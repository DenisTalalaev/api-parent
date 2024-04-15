package by.salary.serviceuser.service;

import by.salary.serviceuser.entities.*;
import by.salary.serviceuser.exceptions.NotEnoughtPermissionsException;
import by.salary.serviceuser.exceptions.OrganisationNotFoundException;
import by.salary.serviceuser.exceptions.UserNotFoundException;
import by.salary.serviceuser.model.OrganisationRequestDTO;
import by.salary.serviceuser.model.OrganisationResponseDTO;
import by.salary.serviceuser.model.UserResponseDTO;
import by.salary.serviceuser.repository.AuthorityRepository;
import by.salary.serviceuser.repository.OrganisationRepository;
import by.salary.serviceuser.repository.PermissionRepository;
import by.salary.serviceuser.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.swing.plaf.OptionPaneUI;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrganisationService {

    private OrganisationRepository organisationRepository;

    private PermissionRepository permissionRepository;

    private AuthorityRepository authorityRepository;

    private UserRepository userRepository;
    private WebClient.Builder webClientBuilder;

    @Autowired
    public OrganisationService(OrganisationRepository organisationRepository, UserRepository userRepository, WebClient.Builder webClientBuilder, PermissionRepository permissionRepository, AuthorityRepository authorityRepository) {
        this.permissionRepository = permissionRepository;
        this.webClientBuilder = webClientBuilder;
        this.organisationRepository = organisationRepository;
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;

        for (AuthorityEnum authority : AuthorityEnum.values()) {
            if (!authorityRepository.existsByAuthority(authority.name())) {
                authorityRepository.save(new Authority(authority));
            }
        }


    }


    public List<OrganisationResponseDTO> getAllOrganisations() {
        List<OrganisationResponseDTO> organisations = new ArrayList<>();
        organisationRepository.findAll().forEach(organisation -> {
            organisations.add(new OrganisationResponseDTO(organisation));
        });
        return organisations;
    }


    public OrganisationResponseDTO getOneOrganisation(BigInteger id) {
        Optional<Organisation> optionalOrganisation = organisationRepository.findById(id);
        if (optionalOrganisation.isEmpty()) {
            throw new OrganisationNotFoundException("Organisation with id " + id + " not found", HttpStatus.NOT_FOUND);
        }
        return new OrganisationResponseDTO(optionalOrganisation.get());
    }

    private BigInteger createNewAgreementId() {
        return new BigInteger(
                Objects.requireNonNull(webClientBuilder.build()
                        .post()
                        .uri("lb://service-agreement/agreements/createdefaultagreement")
                        .retrieve()
                        .bodyToMono(String.class)
                        .block())
        );
    }

    public OrganisationResponseDTO createOrganisation(OrganisationRequestDTO organisationRequestDTO,
                                                      String email) {
        Optional<User> directorOpt = userRepository.findByUserEmail(email);
        if (directorOpt.isEmpty()) {
            throw new UserNotFoundException("User with email " + email + " not found", HttpStatus.NOT_FOUND);
        }
        User director = directorOpt.get();

        Organisation organisation = new Organisation(organisationRequestDTO);
        organisation.setAgreementId(createNewAgreementId());
        organisationRepository.save(organisation);

        director.setOrganisation(organisation);

        organisation.setDirector(director);

        director.setUserFirstName(organisationRequestDTO.getDirectorFirstName());
        director.setUserSurname(organisationRequestDTO.getDirectorSurname());
        director.setUserSecondName(organisationRequestDTO.getDirectorSecondName());

        director.getAuthorities().add(authorityRepository.findByAuthority(new Authority(AuthorityEnum.ADMINISTRATOR).getAuthority()).get());

        if (!permissionRepository.existsByName(new Permission(PermissionsEnum.ALL_PERMISSIONS).getName())) {
            permissionRepository.save(new Permission(PermissionsEnum.ALL_PERMISSIONS));
        }
        director.addPermission(permissionRepository.findByName(new Permission(PermissionsEnum.ALL_PERMISSIONS).getName()).get());
        userRepository.save(director);
        return new OrganisationResponseDTO(organisation);
    }


    public void deleteOrganisation(BigInteger id) {
        if (!organisationRepository.existsById(id)) {
            throw new OrganisationNotFoundException("Organisation with id " + id + " not found", HttpStatus.NOT_FOUND);
        }
        organisationRepository.deleteById(id);
    }


    public List<UserResponseDTO> getOrganisationUsers(BigInteger id) {
        Optional<Organisation> optionalOrganisation = organisationRepository.findById(id);
        if (optionalOrganisation.isEmpty()) {
            throw new OrganisationNotFoundException("Organisation with id " + id + " not found", HttpStatus.NOT_FOUND);
        }
        List<UserResponseDTO> users = new ArrayList<>();
        optionalOrganisation.get().getUsers().forEach(user -> {
            users.add(new UserResponseDTO(user));
        });
        return users;
    }

    public OrganisationResponseDTO updateOrganisation(OrganisationRequestDTO organisationRequestDTO, String email) {
        Optional<User> directorOpt = userRepository.findByUserEmail(email);
        if (directorOpt.isEmpty()) {
            throw new UserNotFoundException("User with email " + email + " not found", HttpStatus.NOT_FOUND);
        }
        User director = directorOpt.get();
        if (!Permission.isPermitted(director, PermissionsEnum.REDACT_ORGANISATION_INFO)) {
            throw new NotEnoughtPermissionsException("Not enough permissions to perform this action", HttpStatus.FORBIDDEN);
        }
        Optional<Organisation> optionalOrganisation = organisationRepository.findById(director.getOrganisation().getId());
        Organisation organisation = optionalOrganisation.get();
        organisation.update(organisationRequestDTO);
        organisationRepository.save(organisation);
        return new OrganisationResponseDTO(organisation);
    }
}

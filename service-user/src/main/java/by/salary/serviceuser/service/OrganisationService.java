package by.salary.serviceuser.service;

import by.salary.serviceuser.entities.Organisation;
import by.salary.serviceuser.entities.User;
import by.salary.serviceuser.model.OrganisationRequestDTO;
import by.salary.serviceuser.model.OrganisationResponseDTO;
import by.salary.serviceuser.model.UserResponseDTO;
import by.salary.serviceuser.repository.OrganisationRepository;
import by.salary.serviceuser.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OrganisationService {

    private OrganisationRepository organisationRepository;
    private UserRepository userRepository;
    private WebClient.Builder webClientBuilder;

    @Autowired
    public OrganisationService(OrganisationRepository organisationRepository, UserRepository userRepository, WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
        this.organisationRepository = organisationRepository;
        this.userRepository = userRepository;
    }


    public List<OrganisationResponseDTO> getAllOrganisations() {

        List<OrganisationResponseDTO> organisations = new ArrayList<>();
        organisationRepository.findAll().forEach(organisation -> {
            organisations.add(new OrganisationResponseDTO(organisation));
        });
        return organisations;
    }


    public OrganisationResponseDTO getOneOrganisation(BigInteger id) {
        return new OrganisationResponseDTO(organisationRepository.findById(id).get());
    }

    private String getEmail(){
        return "hotspot.by@gmail.com";
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
        User director = userRepository.findByUserEmail(email).get();
        Organisation organisation = new Organisation(organisationRequestDTO, director);
        director.setOrganisation(organisation);
        userRepository.save(director);
        organisation.setAgreementId(createNewAgreementId());
        return new OrganisationResponseDTO(organisationRepository.save(organisation));
    }


    public OrganisationResponseDTO updateOrganisation(OrganisationRequestDTO organisationRequestDTO) {
        Organisation organisation = organisationRepository.findById(organisationRequestDTO.getId()).get();
        organisation.update(organisationRequestDTO,
                organisationRequestDTO.getDirectorId() == null ?
                        null :
                        userRepository.findById(organisationRequestDTO.getDirectorId()).get()
                );
        return new OrganisationResponseDTO(organisationRepository.save(organisation));
    }


    public void deleteOrganisation(BigInteger id) {
        organisationRepository.deleteById(id);
    }


    public List<UserResponseDTO> getOrganisationUsers(BigInteger id) {
        List<UserResponseDTO> users = new ArrayList<>();
        organisationRepository.findById(id).get().getUsers().forEach(user -> {
            users.add(new UserResponseDTO(user));
        });
        return users;
    }
}

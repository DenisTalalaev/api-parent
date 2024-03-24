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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganisationService {

    private OrganisationRepository organisationRepository;
    private UserRepository userRepository;

    @Autowired
    public OrganisationService(OrganisationRepository organisationRepository, UserRepository userRepository) {
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


    public OrganisationResponseDTO createOrganisation(OrganisationRequestDTO organisationRequestDTO) {
        User director = userRepository.save(new User());
        OrganisationResponseDTO organisationResponseDTO = new OrganisationResponseDTO(
                organisationRepository.save(new Organisation(organisationRequestDTO,
                        userRepository.findById(director.getId()).get()
                ))
        );
        director.setOrganisation(organisationRepository.findById(organisationResponseDTO.getId()).get());
        userRepository.save(director);
        return organisationResponseDTO;
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

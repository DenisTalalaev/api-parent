package by.salary.serviceuser.service;

import by.salary.serviceuser.entities.Organisation;
import by.salary.serviceuser.model.OrganisationRequestDTO;
import by.salary.serviceuser.model.OrganisationResponseDTO;
import by.salary.serviceuser.repository.OrganisationRepository;
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

    @Autowired
    private OrganisationRepository organisationRepository;


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
        return new OrganisationResponseDTO(organisationRepository.save(new Organisation(organisationRequestDTO)));
    }


    public OrganisationResponseDTO updateOrganisation(OrganisationRequestDTO organisationRequestDTO) {
        Organisation organisation = organisationRepository.findById(organisationRequestDTO.getId()).get();
        if (organisationRepository.existsById(organisationRequestDTO.getId())) {
            organisation.update(organisationRequestDTO);
        }
        return new OrganisationResponseDTO(organisationRepository.save(organisation));
    }


    public void deleteOrganisation(BigInteger id) {
        organisationRepository.deleteById(id);
    }


}

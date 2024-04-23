package by.salary.serviceuser.service;

import by.salary.serviceuser.entities.Permission;
import by.salary.serviceuser.entities.PermissionsEnum;
import by.salary.serviceuser.entities.User;
import by.salary.serviceuser.entities.UserAgreement;
import by.salary.serviceuser.exceptions.CustomValidationException;
import by.salary.serviceuser.exceptions.NotEnoughtPermissionsException;
import by.salary.serviceuser.exceptions.UserNotFoundException;
import by.salary.serviceuser.model.*;
import by.salary.serviceuser.model.mail.MailRequestDTO;
import by.salary.serviceuser.model.mail.MailResponseDTO;
import by.salary.serviceuser.model.mail.MailType;
import by.salary.serviceuser.model.user.agreement.UserAgreementRequestDTO;
import by.salary.serviceuser.model.user.agreement.UserAgreementResponseDTO;
import by.salary.serviceuser.repository.UserAgreementsRepository;
import by.salary.serviceuser.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import javax.validation.ValidationException;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Stream;

@Service
public class UserAgreementsService {

    private final UserAgreementsRepository userAgreementsRepository;
    private final UserRepository userRepository;

    private WebClient.Builder webClientBuilder;

    @Autowired
    public UserAgreementsService(UserAgreementsRepository userAgreementsRepository, UserRepository userRepository, WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
        this.userAgreementsRepository = userAgreementsRepository;
        this.userRepository = userRepository;
    }


    public List<UserAgreementResponseDTO> getAllUserAgreements(BigInteger userId, String email, List<Permission> permissions) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User with id " + userId + " not found", HttpStatus.NOT_FOUND);
        }

        if (!userId.equals(userRepository.findByUserEmail(email).get().getId())
                & !Permission.isPermitted(permissions, PermissionsEnum.CRUD_USER_AGREEMENT)
        ) {
            throw new NotEnoughtPermissionsException("You have not enough permissions to perform this action for user with id " + userId, HttpStatus.FORBIDDEN);
        }

        List<UserAgreementResponseDTO> userAgreements = new ArrayList<>();
        userAgreementsRepository.findAll().forEach(userAgreement -> {
            if (userAgreement.getUser().getId().equals(userId)) {
                String[] data = getAgreementState(userAgreement.getAgreementStateId()).split("\n");
                userAgreements.add(new UserAgreementResponseDTO(userAgreement,
                        BigInteger.valueOf(Integer.parseInt(data[0])),
                        data[1],
                        BigInteger.valueOf(Integer.parseInt(data[2])),
                        data[3],
                        data[4]
                ));
            }
        });


        return userAgreements;
    }

    public List<UserAgreementResponseDTO> getAllUserAgreements(BigInteger userId, String email,
                                                               SelectionCriteriaDto selectionCriteriaDto,
                                                               List<Permission> permissions) {

        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            throw new UserNotFoundException("User with id " + userId + " not found", HttpStatus.NOT_FOUND);
        }

        if (!Permission.isPermitted(permissions, PermissionsEnum.CRUD_USER_AGREEMENT)
        ) {
            throw new NotEnoughtPermissionsException("You have not enough permissions to perform this action for user with id " + userId, HttpStatus.FORBIDDEN);
        }

        SelectionCriteria selectionCriteria = null;
        try {
            selectionCriteria = mapToSelectionCriteria(selectionCriteriaDto);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Wrong selection criteria");
        }


        List<UserAgreementResponseDTO> userAgreements = new ArrayList<>();
        try {
            userAgreementsRepository.findWithCriteria(selectionCriteria).forEach(userAgreement -> {
                if (userAgreement.getUser().getId().equals(userId)) {
                    String[] data = getAgreementState(userAgreement.getAgreementStateId()).split("\n");
                    userAgreements.add(new UserAgreementResponseDTO(userAgreement,
                            BigInteger.valueOf(Integer.parseInt(data[0])),
                            data[1],
                            BigInteger.valueOf(Integer.parseInt(data[2])),
                            data[3],
                            data[4]
                    ));
                }
            });
        } catch (ParseException e) {
            throw new CustomValidationException("Wrong selection criteria");
        }

        return userAgreements;
    }

    public void deleteUserAgreement(BigInteger agreementId, String email) {

        Optional<User> optUser = userRepository.findByUserEmail(email);
        if (optUser.isEmpty()) {
            throw new UserNotFoundException("User with email " + email + " not found", HttpStatus.NOT_FOUND);
        }

        if (!Permission.isPermitted(optUser.get(), PermissionsEnum.CRUD_USER_AGREEMENT)) {
            throw new NotEnoughtPermissionsException("You have not enought permissions to perform this action", HttpStatus.FORBIDDEN);
        }
        if (!userRepository.existsByUserEmail(email)) {
            throw new UserNotFoundException("User with email " + email + " not found", HttpStatus.NOT_FOUND);
        }
        if (!userAgreementsRepository.existsById(agreementId)) {
            throw new UserNotFoundException("User agreement with id " + agreementId + " not found", HttpStatus.NOT_FOUND);
        }
        userAgreementsRepository.deleteById(agreementId);
    }


    public UserAgreementResponseDTO createUserAgreement(UserAgreementRequestDTO userAgreementRequestDTO, BigInteger userId, String email) {
        Optional<User> optUser = userRepository.findByUserEmail(email);
        if (optUser.isEmpty()) {
            throw new UserNotFoundException("User with email " + email + " not found", HttpStatus.NOT_FOUND);
        }
        if (!Permission.isPermitted(optUser.get(), PermissionsEnum.CRUD_USER_AGREEMENT)) {
            throw new NotEnoughtPermissionsException("You have not enought permissions to perform this action", HttpStatus.FORBIDDEN);
        }
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new UserNotFoundException("User with id " + userId + " not found", HttpStatus.NOT_FOUND);
        }
        UserAgreement userAgreement = new UserAgreement(
                userAgreementRequestDTO,
                user.get(),
                optUser.get(),
                getAgreementState(userAgreementRequestDTO.getAgreementId()));

        userAgreement.setAgreementStateId(userAgreementRequestDTO.getAgreementId());
        userAgreementsRepository.save(userAgreement);
        String[] data = getAgreementState(userAgreement.getAgreementStateId()).split("\n");
        mail(
                new MailRequestDTO(
                        user.get().getUserEmail(),
                        userAgreement.toString(),
                        MailType.NEW_PAYMENT
                ));

        return new UserAgreementResponseDTO(userAgreement,
                BigInteger.valueOf(Integer.parseInt(data[0])),
                data[1],
                BigInteger.valueOf(Integer.parseInt(data[2])),
                data[3],
                data[4]
        );
    }

    private String getAgreementState(BigInteger id) {

        return Objects.requireNonNull(webClientBuilder
                .build()
                .get()
                .uri("lb://service-agreement/agreements/getagreementstate/" + id)
                .retrieve()
                .bodyToMono(String.class)
                .block());

    }

    public Optional<MailResponseDTO> mail(MailRequestDTO mailRequestDTO) {
        try {
            Optional<MailResponseDTO> response = webClientBuilder.build()
                    .post()
                    .uri("lb://service-mail/mail")
                    .body(Mono.just(mailRequestDTO), MailRequestDTO.class)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, resp -> Mono.empty())
                    .bodyToMono(MailResponseDTO.class)
                    .blockOptional();
            return response;
        } catch (Exception e) {
            return Optional.empty();
        }
    }


    private SelectionCriteria mapToSelectionCriteria(SelectionCriteriaDto selectionCriteriaDto) {
        SelectionCriteria selectionCriteria = new SelectionCriteria();

        if (selectionCriteriaDto.getFilter() != null) {
            Map<String, Map<FilterCriteria.FilterCriteriaType, String[]>> filter = new HashMap<>();
            for (var filterEntry : selectionCriteriaDto.getFilter().entrySet()) {
                Map<FilterCriteria.FilterCriteriaType, String[]> value = new HashMap<>();
                for (var filterTypeEntry : filterEntry.getValue().entrySet()) {
                    value.put(FilterCriteria.FilterCriteriaType.valueOfString(filterTypeEntry.getKey()), filterTypeEntry.getValue());
                }
                filter.put(filterEntry.getKey(), value);
            }
            selectionCriteria.setFilter(filter);
        }

        if (selectionCriteriaDto.getOrder() != null) {
            Map<String, OrderCriteria.OrderCriteriaType> order = new LinkedHashMap<>();
            for (var orderEntry : selectionCriteriaDto.getOrder().entrySet()) {
                order.put(orderEntry.getKey(), OrderCriteria.OrderCriteriaType.valueOfString(orderEntry.getValue()));
            }
            selectionCriteria.setOrder(order);
        }

        if (selectionCriteriaDto.getPagination() != null) {
            Map<PaginationCriteria.PaginationType, String> pagination = new HashMap<>();
            for (var paginationEntry : selectionCriteriaDto.getPagination().entrySet()) {
                pagination.put(PaginationCriteria.PaginationType.valueOfString(paginationEntry.getKey()), paginationEntry.getValue());
            }
            selectionCriteria.setPagination(pagination);
        }


        return selectionCriteria;
    }
}

package by.salary.authorizationserver.service;

import by.salary.authorizationserver.model.dto.MailRequestDTO;
import by.salary.authorizationserver.model.dto.MailResponseDTO;
import by.salary.authorizationserver.repository.MailRepository;
import jakarta.ws.rs.InternalServerErrorException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class MailService {

    MailRepository mailRepository;
    public boolean checkEmail(MailRequestDTO mailRequestDTO) {
        return mailRepository.checkEmail(mailRequestDTO);
    }

    public void sendMessage(MailRequestDTO mailRequestDTO, String jwt){

        Optional<MailResponseDTO> response = mailRepository.sendMessage(mailRequestDTO, jwt);

        if (response.orElseThrow(() -> new InternalServerErrorException("Mail server error while sending message"))
                .getStatus().isError()) {
            throw new InternalServerErrorException(response.get().getMessage());
        }

    }
}

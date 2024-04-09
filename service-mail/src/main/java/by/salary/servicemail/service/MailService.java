package by.salary.servicemail.service;

import by.salary.servicemail.exceptions.MailSendingException;
import by.salary.servicemail.model.MailRequestDTO;
import by.salary.servicemail.model.MailResponseDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Collections;

@Service
public class MailService {

    
    private JavaMailSender emailSender;
    WebClient.Builder webClientBuilder;

    public MailService(JavaMailSender emailSender, WebClient.Builder webClientBuilder) {
        this.emailSender = emailSender;
        this.webClientBuilder = webClientBuilder;
    }

    public MailResponseDTO sendMail(MailRequestDTO mailRequestDTO, String userEmail) {
        if(!isValidEmailAddress(mailRequestDTO.getMailTo()) || !isValidEmailAddress(userEmail)){
            throw new MailSendingException("Invalid email address", HttpStatus.BAD_REQUEST);
        }
        return new MailResponseDTO(send(mailRequestDTO.getMailTo(), mailRequestDTO.getMessage()));
    }

    public MailResponseDTO broadcastMail(MailRequestDTO mailRequestDTO, String userEmail) {
        getAllMails(userEmail).forEach(mail -> send(mail, mailRequestDTO.getMessage()));
        return new MailResponseDTO(true);
    }

    public boolean send(String mail, String code){
        if(!isValidEmailAddress(mail)){
            throw new MailSendingException("Invalid email address", HttpStatus.BAD_REQUEST);
        }
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(mail);
            helper.setFrom("no-reply@salary.by");
            helper.setSubject("Подтверждение регистрации");
            String htmlBody = formatMessage(code);
            helper.setText(htmlBody, true);
            emailSender.send(message);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String formatMessage(String code) {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Подтверждение регистрации на salary.by</title>\n" +
                "</head>\n" +
                "<body style=\"font-family: Arial, sans-serif;\">\n" +
                "\n" +
                "<table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" bgcolor=\"#f0f0f0\">\n" +
                "    <tr>\n" +
                "        <td align=\"center\" style=\"padding: 40px 0;\">\n" +
                "            <img src=\"https://yourdomain.com/logo.png\" alt=\"Salary.by\" width=\"150\">\n" +
                "        </td>\n" +
                "    </tr>\n" +
                "</table>\n" +
                "\n" +
                "<table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" bgcolor=\"#ffffff\">\n" +
                "    <tr>\n" +
                "        <td style=\"padding: 40px 20px;\">\n" +
                "            <h2 style=\"color: #333333;\">Добро пожаловать на salary.by!</h2>\n" +
                "            <p style=\"font-size: 16px; line-height: 1.5; color: #666666;\">Благодарим вас за регистрацию на нашем портале. Чтобы завершить процесс регистрации, пожалуйста, используйте следующий код подтверждения:</p>\n" +
                "            <div style=\"background-color: #f5f5f5; padding: 20px; border-radius: 8px; margin-top: 20px;\">\n" +
                "                <h3 style=\"font-size: 24px; color: #333333; margin: 0;\">Код подтверждения:</h3>\n" +
                "                <p style=\"font-size: 32px; color: #007bff; margin-top: 10px;\">" + code + "</p>\n" +
                "            </div>\n" +
                "            <p style=\"font-size: 16px; line-height: 1.5; color: #666666;\">Пожалуйста, не передавайте данный код никому, включая сотрудников компании.</p>\n" +
                "            <p style=\"font-size: 16px; line-height: 1.5; color: #666666;\">Если у вас возникли какие-либо вопросы, не стесняйтесь связаться с нами по адресу <a href=\"mailto:help@salary.by\" style=\"color: #007bff;\">help@salary.by</a>.</p>\n" +
                "        </td>\n" +
                "    </tr>\n" +
                "</table>\n" +
                "\n" +
                "<table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" bgcolor=\"#f0f0f0\">\n" +
                "    <tr>\n" +
                "        <td align=\"center\" style=\"padding: 20px 0;\">\n" +
                "            <p style=\"font-size: 14px; color: #666666;\">Вы получили это сообщение, так как зарегистрировались на сайте salary.by. Если это были не вы, пожалуйста, проигнорируйте это письмо.</p>\n" +
                "        </td>\n" +
                "    </tr>\n" +
                "</table>\n" +
                "\n" +
                "</body>\n" +
                "</html>";
    }


    public static boolean isValidEmailAddress(String email) {
        EmailValidator validator = new EmailValidator();
        return validator.isValid(email, null);
    }

    private ArrayList<String> getAllMails(String userEmail) {
        ArrayList<String> mails = new ArrayList<>();
        Collections.addAll(mails, webClientBuilder.build()
                .post()
                .uri("lb://service-invitation/users/getallmails/" + userEmail)
                .retrieve()
                .bodyToMono(String.class).block().split("\n"));
        if(mails.isEmpty()){
            throw new MailSendingException("There are no users to send mail", HttpStatus.NOT_FOUND);
        }
        if(mails.size() == 1 && mails.get(0).equals("nopermissions")){
            throw new MailSendingException("You have not enough permissions to perform this action", HttpStatus.FORBIDDEN);
        }
        return mails;
    }


    public void checkMail(MailRequestDTO mailRequestDTO) {
        if (!isValidEmailAddress(mailRequestDTO.getMailTo())) {
            throw new MailSendingException("Invalid email address", HttpStatus.NOT_FOUND);
        }
    }
}

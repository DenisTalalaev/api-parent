package by.salary.servicemail.service;

import by.salary.servicemail.exceptions.MailSendingException;
import by.salary.servicemail.model.MailRequestDTO;
import by.salary.servicemail.model.MailResponseDTO;
import by.salary.servicemail.model.MailType;
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
        return new MailResponseDTO(send(mailRequestDTO.getMailType(), mailRequestDTO.getMailTo(), mailRequestDTO.getMessage()));
    }

    public MailResponseDTO broadcastMail(MailRequestDTO mailRequestDTO, String userEmail) {
        switch (mailRequestDTO.getMailTo()){

        }
        return new MailResponseDTO(true);
    }

    public boolean send(MailType mailType, String mail, String code){
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

    public MailResponseDTO resetPassword(MailRequestDTO mailRequestDTO) {
        if (!isValidEmailAddress(mailRequestDTO.getMailTo())) {
            throw new MailSendingException("Invalid email address", HttpStatus.NOT_FOUND);
        }
        return new MailResponseDTO(send(
                mailRequestDTO.getMailType(),
                mailRequestDTO.getMailTo(),
                formatPasswordResetMessage(mailRequestDTO.getMessage())
        ));
    }

    private String formatPaymentNotification(String paymentDetails) {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Уведомление о поступлении новой выплаты на Salary.by</title>\n" +
                "</head>\n" +
                "<body style=\"font-family: Arial, sans-serif;\">\n" +
                "\n" +
                "<table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" bgcolor=\"#ffffff\">\n" +
                "    <tr>\n" +
                "        <td style=\"padding: 40px 20px;\">\n" +
                "            <h2 style=\"color: #333333;\">Поступление новой выплаты</h2>\n" +
                "            <p style=\"font-size: 16px; line-height: 1.5; color: #666666;\">Уважаемый пользователь,</p>\n" +
                "            <p style=\"font-size: 16px; line-height: 1.5; color: #666666;\">Получено новое платежное уведомление:</p>\n" +
                "            <div style=\"background-color: #f5f5f5; padding: 20px; border-radius: 8px; margin-top: 20px;\">\n" +
                "                <p style=\"font-size: 16px; color: #333333; margin: 0;\">Детали платежа:</p>\n" +
                "                <p style=\"font-size: 16px; color: #666666; margin-top: 10px;\">" + paymentDetails + "</p>\n" +
                "            </div>\n" +
                "            <p style=\"font-size: 16px; line-height: 1.5; color: #666666;\">Если у вас возникли вопросы, не стесняйтесь связаться с нами по адресу <a href=\"mailto:help@salary.by\" style=\"color: #007bff;\">help@salary.by</a>.</p>\n" +
                "        </td>\n" +
                "    </tr>\n" +
                "</table>\n" +
                "\n" +
                "<table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" bgcolor=\"#f0f0f0\">\n" +
                "    <tr>\n" +
                "        <td align=\"center\" style=\"padding: 20px 0;\">\n" +
                "            <p style=\"font-size: 14px; color: #666666;\">Вы получили это уведомление, так как зарегистрированы на сайте Salary.by.</p>\n" +
                "        </td>\n" +
                "    </tr>\n" +
                "</table>\n" +
                "\n" +
                "</body>\n" +
                "</html>";
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

    private String formatPasswordResetMessage(String code) {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Восстановление пароля на Salary.by</title>\n" +
                "</head>\n" +
                "<body style=\"font-family: Arial, sans-serif;\">\n" +
                "\n" +
                "<table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" bgcolor=\"#ffffff\">\n" +
                "    <tr>\n" +
                "        <td style=\"padding: 40px 20px;\">\n" +
                "            <h2 style=\"color: #333333;\">Восстановление пароля на Salary.by</h2>\n" +
                "            <p style=\"font-size: 16px; line-height: 1.5; color: #666666;\">Для восстановления пароля вашей учетной записи, используйте следующий код:</p>\n" +
                "            <div style=\"background-color: #f5f5f5; padding: 20px; border-radius: 8px; margin-top: 20px;\">\n" +
                "                <h3 style=\"font-size: 24px; color: #333333; margin: 0;\">Код восстановления пароля:</h3>\n" +
                "                <p style=\"font-size: 32px; color: #007bff; margin-top: 10px;\">" + code + "</p>\n" +
                "            </div>\n" +
                "            <p style=\"font-size: 16px; line-height: 1.5; color: #666666;\">Пожалуйста, не передавайте данный код никому, включая сотрудников компании.</p>\n" +
                "            <p style=\"font-size: 16px; line-height: 1.5; color: #666666;\">Если вы не запрашивали восстановление пароля, проигнорируйте это сообщение.</p>\n" +
                "            <p style=\"font-size: 16px; line-height: 1.5; color: #666666;\">Если у вас возникли какие-либо вопросы, свяжитесь с нами по адресу <a href=\"mailto:help@salary.by\" style=\"color: #007bff;\">help@salary.by</a>.</p>\n" +
                "        </td>\n" +
                "    </tr>\n" +
                "</table>\n" +
                "\n" +
                "<table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" bgcolor=\"#f0f0f0\">\n" +
                "    <tr>\n" +
                "        <td align=\"center\" style=\"padding: 20px 0;\">\n" +
                "            <p style=\"font-size: 14px; color: #666666;\">Вы получили это сообщение, так как запросили восстановление пароля на сайте Salary.by. Если это были не вы, пожалуйста, проигнорируйте это письмо.</p>\n" +
                "        </td>\n" +
                "    </tr>\n" +
                "</table>\n" +
                "\n" +
                "</body>\n" +
                "</html>";
    }

    private String formatCollectiveAgreementNotification(String changes) {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Уведомление об изменении коллективного договора на Salary.by</title>\n" +
                "</head>\n" +
                "<body style=\"font-family: Arial, sans-serif;\">\n" +
                "\n" +
                "<table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" bgcolor=\"#ffffff\">\n" +
                "    <tr>\n" +
                "        <td style=\"padding: 40px 20px;\">\n" +
                "            <h2 style=\"color: #333333;\">Изменение коллективного договора</h2>\n" +
                "            <p style=\"font-size: 16px; line-height: 1.5; color: #666666;\">Уважаемый пользователь,</p>\n" +
                "            <p style=\"font-size: 16px; line-height: 1.5; color: #666666;\">Хотим сообщить вам об изменениях в коллективном договоре:</p>\n" +
                "            <div style=\"background-color: #f5f5f5; padding: 20px; border-radius: 8px; margin-top: 20px;\">\n" +
                "                <p style=\"font-size: 16px; color: #333333; margin: 0;\">Изменения:</p>\n" +
                "                <p style=\"font-size: 16px; color: #666666; margin-top: 10px;\">" + changes + "</p>\n" +
                "            </div>\n" +
                "            <p style=\"font-size: 16px; line-height: 1.5; color: #666666;\">Если у вас возникли вопросы, не стесняйтесь связаться с нами по адресу <a href=\"mailto:help@salary.by\" style=\"color: #007bff;\">help@salary.by</a>.</p>\n" +
                "        </td>\n" +
                "    </tr>\n" +
                "</table>\n" +
                "\n" +
                "<table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" bgcolor=\"#f0f0f0\">\n" +
                "    <tr>\n" +
                "        <td align=\"center\" style=\"padding: 20px 0;\">\n" +
                "            <p style=\"font-size: 14px; color: #666666;\">Вы получили это уведомление, так как зарегистрированы на сайте Salary.by.</p>\n" +
                "        </td>\n" +
                "    </tr>\n" +
                "</table>\n" +
                "\n" +
                "</body>\n" +
                "</html>";
    }

    private String formatTwoFactorAuthCode(String code) {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Код подтверждения входа на Salary.by</title>\n" +
                "</head>\n" +
                "<body style=\"font-family: Arial, sans-serif;\">\n" +
                "\n" +
                "<table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" bgcolor=\"#ffffff\">\n" +
                "    <tr>\n" +
                "        <td style=\"padding: 40px 20px;\">\n" +
                "            <h2 style=\"color: #333333;\">Код подтверждения входа</h2>\n" +
                "            <p style=\"font-size: 16px; line-height: 1.5; color: #666666;\">Уважаемый пользователь,</p>\n" +
                "            <p style=\"font-size: 16px; line-height: 1.5; color: #666666;\">Для завершения входа на вашу учетную запись введите следующий код подтверждения:</p>\n" +
                "            <div style=\"background-color: #f5f5f5; padding: 20px; border-radius: 8px; margin-top: 20px;\">\n" +
                "                <h3 style=\"font-size: 24px; color: #333333; margin: 0;\">Код подтверждения:</h3>\n" +
                "                <p style=\"font-size: 32px; color: #007bff; margin-top: 10px;\">" + code + "</p>\n" +
                "            </div>\n" +
                "            <p style=\"font-size: 16px; line-height: 1.5; color: #666666;\">Пожалуйста, не передавайте данный код никому.</p>\n" +
                "        </td>\n" +
                "    </tr>\n" +
                "</table>\n" +
                "\n" +
                "<table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" bgcolor=\"#f0f0f0\">\n" +
                "    <tr>\n" +
                "        <td align=\"center\" style=\"padding: 20px 0;\">\n" +
                "            <p style=\"font-size: 14px; color: #666666;\">Это сообщение было отправлено вам в связи с попыткой входа на сайт Salary.by. Если это были не вы, проигнорируйте это сообщение.</p>\n" +
                "        </td>\n" +
                "    </tr>\n" +
                "</table>\n" +
                "\n" +
                "</body>\n" +
                "</html>";
    }

    private String formatEmailChangeNotification(String confirmationLink) {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Изменение почтового адреса на Salary.by</title>\n" +
                "</head>\n" +
                "<body style=\"font-family: Arial, sans-serif;\">\n" +
                "\n" +
                "<table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" bgcolor=\"#ffffff\">\n" +
                "    <tr>\n" +
                "        <td style=\"padding: 40px 20px;\">\n" +
                "            <h2 style=\"color: #333333;\">Изменение почтового адреса</h2>\n" +
                "            <p style=\"font-size: 16px; line-height: 1.5; color: #666666;\">Уважаемый пользователь,</p>\n" +
                "            <p style=\"font-size: 16px; line-height: 1.5; color: #666666;\">Ваш почтовый адрес был изменен. Для завершения процесса изменения, пожалуйста, подтвердите новый адрес, перейдя по следующей ссылке:</p>\n" +
                "            <div style=\"background-color: #f5f5f5; padding: 20px; border-radius: 8px; margin-top: 20px;\">\n" +
                "                <a href=\"" + confirmationLink + "\" style=\"font-size: 16px; color: #007bff; text-decoration: none;\">Подтвердить изменение почтового адреса</a>\n" +
                "            </div>\n" +
                "            <p style=\"font-size: 16px; line-height: 1.5; color: #666666;\">Если вы не выполняли это действие, проигнорируйте это уведомление.</p>\n" +
                "        </td>\n" +
                "    </tr>\n" +
                "</table>\n" +
                "\n" +
                "<table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" bgcolor=\"#f0f0f0\">\n" +
                "    <tr>\n" +
                "        <td align=\"center\" style=\"padding: 20px 0;\">\n" +
                "            <p style=\"font-size: 14px; color: #666666;\">Это сообщение было отправлено вам в связи с изменением почтового адреса на сайте Salary.by.</p>\n" +
                "        </td>\n" +
                "    </tr>\n" +
                "</table>\n" +
                "\n" +
                "</body>\n" +
                "</html>";
    }


    public MailResponseDTO newPayment(MailRequestDTO mailRequestDTO) {
        if (!isValidEmailAddress(mailRequestDTO.getMailTo())) {
            throw new MailSendingException("Invalid email address", HttpStatus.NOT_FOUND);
        }
        return new MailResponseDTO(send(
                mailRequestDTO.getMailType(),
                mailRequestDTO.getMailTo(),
                formatPaymentNotification(mailRequestDTO.getMessage())
        ));
    }


    public MailResponseDTO agreementChange(MailRequestDTO mailRequestDTO) {
        if (!isValidEmailAddress(mailRequestDTO.getMailTo())) {
            throw new MailSendingException("Invalid email address", HttpStatus.NOT_FOUND);
        }
        return new MailResponseDTO(send(
                mailRequestDTO.getMailType(),
                mailRequestDTO.getMailTo(),
                formatCollectiveAgreementNotification(mailRequestDTO.getMessage())
        ));
    }

    public MailResponseDTO _2FAMail(MailRequestDTO mailRequestDTO) {
        if (!isValidEmailAddress(mailRequestDTO.getMailTo())) {
            throw new MailSendingException("Invalid email address", HttpStatus.NOT_FOUND);
        }
        return new MailResponseDTO(send(
                mailRequestDTO.getMailType(),
                mailRequestDTO.getMailTo(),
                formatTwoFactorAuthCode(mailRequestDTO.getMessage())
        ));
    }

    public MailResponseDTO mail(MailRequestDTO mailRequestDTO) {
        switch (mailRequestDTO.getMailType()){
            case NEW_PAYMENT:
                return newPayment(mailRequestDTO);
            case AGREEMENT_CHANGE:
                return agreementChange(mailRequestDTO);
            case _2FA:
                return _2FAMail(mailRequestDTO);
            case CHANGE_EMAIL:
                return changeEmail(mailRequestDTO);
            case RESET_PASSWORD:
                return resetPassword(mailRequestDTO);
            default:
                throw new MailSendingException("Invalid mail type", HttpStatus.NOT_FOUND);
        }
    }

    private MailResponseDTO changeEmail(MailRequestDTO mailRequestDTO) {
        if (!isValidEmailAddress(mailRequestDTO.getMailTo())) {
            throw new MailSendingException("Invalid email address", HttpStatus.NOT_FOUND);
        }
        return new MailResponseDTO(send(
                mailRequestDTO.getMailType(),
                mailRequestDTO.getMailTo(),
                formatEmailChangeNotification(mailRequestDTO.getMessage())
        ));
    }


}

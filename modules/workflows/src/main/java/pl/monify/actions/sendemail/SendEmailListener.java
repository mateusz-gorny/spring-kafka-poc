package pl.monify.actions.sendemail;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Properties;

@Component
public class SendEmailListener {

    private static final Logger log = LoggerFactory.getLogger(SendEmailListener.class);
    private final KafkaTemplate<Object, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private static final String RESPONSE_TOPIC = "workflow.action.response";
    private static final String TOPIC = "workflow.action.response";

    public SendEmailListener(
            KafkaTemplate<Object, String> kafkaTemplate,
            ObjectMapper objectMapper
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(
            topics = TOPIC,
            groupId = "action.email.consumer.group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handle(ActionExecutionRequest request) {
        try {
            kafkaTemplate.send(RESPONSE_TOPIC, objectMapper.writeValueAsString(execute(request)));
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize message: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private ActionExecutionResult execute(ActionExecutionRequest req) {
        Map<String, Object> input = req.input();
        List<String> toList = (List<String>) input.get("to");
        String subject = (String) input.get("subject");
        String body = (String) input.get("body");

        // Placeholder – in real case fetch from credentialService
        String username = "your@gmail.com";
        String password = "your-password";

        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            for (String to : toList) {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(username));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
                message.setSubject(subject);
                message.setText(body);
                Transport.send(message);
            }

            return new ActionExecutionResult(
                    req.actionInstanceId(),
                    true,
                    "Email sent successfully",
                    Map.of("recipients", toList, "status", "sent")
            );

        } catch (Exception e) {
            log.error("Failed to send email: {}", e.getMessage(), e);
            return new ActionExecutionResult(
                    req.actionInstanceId(),
                    false,
                    "Error: " + e.getMessage(),
                    Map.of()
            );
        }
    }
}

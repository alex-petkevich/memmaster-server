package at.abcdef.memmaster.controllers;

import at.abcdef.memmaster.config.ApplicationProperties;
import at.abcdef.memmaster.controllers.dto.ContactDTO;
import at.abcdef.memmaster.controllers.dto.MessageResponseDTO;
import at.abcdef.memmaster.util.SendMailUtil;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contact")
public class ContactController {

    private static final Logger log = LoggerFactory.getLogger(ContactController.class);

    private final SendMailUtil sendMailUtil;
    private final ApplicationProperties applicationProperties;

    public ContactController(SendMailUtil sendMailUtil, ApplicationProperties applicationProperties) {
        this.sendMailUtil = sendMailUtil;
        this.applicationProperties = applicationProperties;
    }

    @PostMapping
    public ResponseEntity<MessageResponseDTO> sendContactForm(@Valid @RequestBody ContactDTO contactDTO) {
        log.info("Contact form received: type={}, subject={}", contactDTO.getQuestionType(), contactDTO.getSubject());

        String to = applicationProperties.getGeneral().getContactEmail();
        String subject = "[" + contactDTO.getQuestionType() + "] " + contactDTO.getSubject();
        String content = "<h3>Contact Form Submission</h3>"
                + "<p><strong>Question Type:</strong> " + contactDTO.getQuestionType() + "</p>"
                + "<p><strong>Subject:</strong> " + contactDTO.getSubject() + "</p>"
                + "<p><strong>Comment:</strong></p>"
                + "<p>" + contactDTO.getComment().replace("\n", "<br/>") + "</p>";

        sendMailUtil.send(to, subject, content, false, true);

        return ResponseEntity.ok(new MessageResponseDTO("Message sent successfully"));
    }
}


package tech.staffjoy.mail.dto;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailRequest {

    @NotBlank(message = "Please provide an email")
    private String to;
    @NotBlank(message = "Please provide a subject")
    private String subject;
    @NotBlank(message = "Please provide a valid body")
    private String htmlBody;
    private String name;

}

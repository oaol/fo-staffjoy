package tech.staffjoy.account.dto;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;

import org.springframework.util.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tech.staffjoy.common.validation.PhoneNumber;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetOrCreateRequest {
    private String name;
    @Email(message = "Invalid email")
    private String email;
    @PhoneNumber
    private String phoneNumber;

    @AssertTrue(message = "Empty request")
    private boolean isValidRequest() {
        return StringUtils.hasText(name) || StringUtils.hasText(email) || StringUtils.hasText(phoneNumber);
    }
}

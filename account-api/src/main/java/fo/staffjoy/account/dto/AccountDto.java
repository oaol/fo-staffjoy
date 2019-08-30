package fo.staffjoy.account.dto;

import java.time.Instant;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import fo.staffjoy.common.validation.PhoneNumber;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountDto {
    @NotBlank
    private String id;
    private String name;
    @Email(message = "Invalid email")
    private String email;
    private boolean confirmedAndActive;
    @NotNull
    private Instant memberSince;
    private boolean support;
    @PhoneNumber
    private String phoneNumber;
    @NotEmpty
    private String photoUrl;
}

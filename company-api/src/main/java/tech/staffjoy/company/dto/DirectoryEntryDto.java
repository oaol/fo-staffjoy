package tech.staffjoy.company.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tech.staffjoy.common.validation.PhoneNumber;

// directory
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DirectoryEntryDto {
    @NotBlank
    private String userId;
    @NotBlank
    private String internalId;
    @NotBlank
    private String companyId;
    // coming from account
    @NotBlank
    @Builder.Default
    private String name = "";
    @NotBlank
    @Email
    private String email;
    private boolean confirmedAndActive;
    @NotBlank
    @PhoneNumber
    private String phoneNumber;
    private String photoUrl;
}

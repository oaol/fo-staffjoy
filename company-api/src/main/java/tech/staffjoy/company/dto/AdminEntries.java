package tech.staffjoy.company.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminEntries {
    private String companyId;
    @Builder.Default
    private List<DirectoryEntryDto> admins = new ArrayList<DirectoryEntryDto>();
}

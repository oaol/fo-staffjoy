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
public class Association {
    private DirectoryEntryDto account;
    @Builder.Default
    private List<TeamDto> teams = new ArrayList<TeamDto>();
    private Boolean admin;
}

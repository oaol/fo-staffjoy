package tech.staffjoy.company.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminOfList {
    private String userId;
    @Builder.Default
    private List<CompanyDto> companies = new ArrayList<CompanyDto>();
}

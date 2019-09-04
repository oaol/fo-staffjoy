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
public class AssociationList {
    @Builder.Default
    private List<Association> accounts = new ArrayList<>();
    private int limit;
    private int offset;
}

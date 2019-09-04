package tech.staffjoy.company.dto;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tech.staffjoy.common.validation.DayOfWeek;
import tech.staffjoy.common.validation.Group1;
import tech.staffjoy.common.validation.Group2;
import tech.staffjoy.common.validation.Timezone;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompanyDto {
    @NotBlank(groups = {Group1.class})
    private String id;
    @NotBlank(groups = {Group1.class, Group2.class})
    private String name;
    private boolean archived;
    @Timezone(groups = {Group1.class, Group2.class})
    @NotBlank(groups = {Group1.class, Group2.class})
    private String defaultTimezone;
    @DayOfWeek(groups = {Group1.class, Group2.class})
    @NotBlank(groups = {Group1.class, Group2.class})
    private String defaultDayWeekStarts;
}

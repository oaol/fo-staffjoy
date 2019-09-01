package tech.staffjoy.web.view;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Page {
    private String title;
    private String description;
    private String templateName;
    private String cssId;
    @Builder.Default
    private String version = "3.0";
}

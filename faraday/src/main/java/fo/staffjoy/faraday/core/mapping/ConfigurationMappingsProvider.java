package fo.staffjoy.faraday.core.mapping;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.autoconfigure.web.ServerProperties;

import fo.staffjoy.faraday.core.http.HttpClientProvider;
import fo.staffjoy.faraday.properties.FaradayProperties;
import fo.staffjoy.faraday.properties.MappingProperties;

public class ConfigurationMappingsProvider extends MappingsProvider {

    public ConfigurationMappingsProvider(
            ServerProperties serverProperties,
            FaradayProperties faradayProperties,
            MappingsValidator mappingsValidator,
            HttpClientProvider httpClientProvider
    ) {
        super(serverProperties, faradayProperties,
                mappingsValidator, httpClientProvider);
    }


    @Override
    protected boolean shouldUpdateMappings(HttpServletRequest request) {
        return false;
    }

    @Override
    protected List<MappingProperties> retrieveMappings() {
        return faradayProperties.getMappings().stream()
                .map(MappingProperties::copy)
                .collect(Collectors.toList());
    }
}

package tech.staffjoy.faraday.core.filter;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

import lombok.extern.slf4j.Slf4j;
import tech.staffjoy.common.env.EnvConfig;

@Slf4j
public class NakedDomainFilter extends OncePerRequestFilter {

    private final EnvConfig envConfig;
    private static final String DEFAULT_SERVICE = "www";

    public NakedDomainFilter(EnvConfig envConfig) {
        this.envConfig = envConfig;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // if you're hitting naked domain - go to www
        // e.g. staffjoy.xyz/foo?true=1 should redirect to www.staffjoy.xyz/foo?true=1
        if (envConfig.getExternalApex().equals(request.getServerName())) {
            // It's hitting naked domain - redirect to www
            log.info("hitting naked domain - redirect to www");
            String scheme = "http";
            if (!envConfig.isDebug()) {
                scheme = "https";
            }
            try {
                URI redirectUrl = new URI(scheme,
                        null,
                        DEFAULT_SERVICE + "." + envConfig.getExternalApex(),
                        request.getServerPort(),
                        "/login/", null, null);
                response.sendRedirect(redirectUrl.toString());
            } catch (URISyntaxException e) {
                log.error("fail to build redirect url", e);
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }
}

package fo.staffjoy.faraday.core.trace;

import static org.apache.commons.lang3.StringUtils.trimToEmpty;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoggingTraceInterceptor implements TraceInterceptor {

    @Override
    public void onRequestReceived(String traceId, IncomingRequest request) {
        log.info("Incoming HTTP request received:", "traceId", traceId,
                "method", request.getMethod(), "host", request.getHost(),
                "uri", request.getUri(), "headers", request.getHeaders());
    }

    @Override
    public void onNoMappingFound(String traceId, IncomingRequest request) {
        log.info("No mapping found for incoming HTTP request: ", "traceId", traceId,
                "method", request.getMethod(), "host", request.getHost(),
                "uri", request.getUri(), "headers", request.getHeaders());
    }

    @Override
    public void onForwardStart(String traceId, ForwardRequest request) {
        log.info("Forwarding HTTP request started: ", "traceId", traceId,
                "mappingName", trimToEmpty(request.getMappingName()),
                "method", request.getMethod(), "host", request.getHost(),
                "uri", request.getUri(), "body", request.getBody(),
                "headers", request.getHeaders());
    }

    @Override
    public void onForwardError(String traceId, Throwable error) {
        log.error("Forwarding HTTP request failed: ", "traceId", traceId, error);
    }

    @Override
    public void onForwardComplete(String traceId, ReceivedResponse response) {
        log.info("Forwarded HTTP response received: ", "traceId", traceId,
                "status", response.getStatus(), "body", response.getBodyAsString(),
                "headers", response.getHeaders());
    }
}

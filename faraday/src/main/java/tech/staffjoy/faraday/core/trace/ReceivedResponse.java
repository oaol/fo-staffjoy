package tech.staffjoy.faraday.core.trace;

import static tech.staffjoy.faraday.core.utils.BodyConverter.convertBodyToString;

import org.springframework.http.HttpStatus;

public class ReceivedResponse extends HttpEntity {

    protected HttpStatus status;
    protected byte[] body;

    public HttpStatus getStatus() { return status; }
    protected void setStatus(HttpStatus status) { this.status = status; }

    public String getBodyAsString() { return convertBodyToString(body); }

    public byte[] getBody() { return body; }

    protected void setBody(byte[] body) { this.body = body; }

}

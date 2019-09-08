package tech.staffjoy.sms.client;

import javax.validation.Valid;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import tech.staffjoy.common.auth.AuthConstant;
import tech.staffjoy.sms.SmsConstant;
import tech.staffjoy.sms.dto.SmsRequest;

@FeignClient(name = SmsConstant.SERVICE_NAME, path = "/v1", url = "${staffjoy.sms-service-endpoint}")
public interface SmsClient {
    @PostMapping(path = "/queue-send")
    ResponseEntity<String> send(@RequestHeader(AuthConstant.AUTHORIZATION_HEADER) String authz, @RequestBody @Valid SmsRequest smsRequest);
}

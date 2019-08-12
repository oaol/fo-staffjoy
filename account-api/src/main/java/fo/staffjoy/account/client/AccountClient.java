package fo.staffjoy.account.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import fo.staffjoy.account.AccountConstant;
import fo.staffjoy.account.dto.TestDto;

@FeignClient(name = AccountConstant.SERVICE_NAME, path = "/account", url = "${staffjoy.account-service-endpoint}")
// TODO Client side validation can be enabled as needed
// @Validated
public interface AccountClient {
    // @RequestParam 必须写，否则会被认定为 post 请求
    @GetMapping(path = "/test")
    ResponseEntity<TestDto> test(@RequestParam("name") String name);

}

package fo.staffjoy.feign.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fo.staffjoy.account.client.AccountClient;
import fo.staffjoy.account.dto.TestDto;

@RestController
@RequestMapping("/feign")
public class TestController {

    @Autowired
    private AccountClient accountClient;
    
    @GetMapping("test")
    public TestDto hello(String name) {
        return accountClient.test(name);
    }
}

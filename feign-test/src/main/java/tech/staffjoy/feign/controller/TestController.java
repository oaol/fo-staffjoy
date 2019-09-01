package tech.staffjoy.feign.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tech.staffjoy.account.client.AccountClient;
import tech.staffjoy.account.dto.TestDto;

@RestController
@RequestMapping("/feign")
public class TestController {

    @Autowired
    private AccountClient accountClient;
    
    @GetMapping("test")
    public ResponseEntity<TestDto> hello(String name) {
        ResponseEntity<TestDto> test = accountClient.test(name);
        return test;
    }
}

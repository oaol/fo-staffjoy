package tech.staffjoy.bot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tech.staffjoy.bot.dto.GreetingRequest;
import tech.staffjoy.bot.service.GreetingService;


@RestController
@RequestMapping(value = "/v1")
@Validated
public class GreetingController {

    @Autowired
    private GreetingService greetingService;

    @PostMapping(value = "/sms-greeting")
    ResponseEntity<String> sendSmsGreeting(@RequestBody @Validated GreetingRequest request) {
        greetingService.greeting(request.getUserId());
        return ResponseEntity.ok("greeting sent");
    }
}

package tech.staffjoy.bot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tech.staffjoy.bot.dto.OnboardWorkerRequest;
import tech.staffjoy.bot.service.OnBoardingService;

@RestController
@RequestMapping(value = "/v1")
@Validated
public class OnBoardingController {
    @Autowired
    private OnBoardingService onBoardingService;

    @PostMapping(value = "/onboard-worker")
    public ResponseEntity<String> onboardWorker(@RequestBody @Validated OnboardWorkerRequest request) {
        onBoardingService.onboardWorker(request);
        return ResponseEntity.ok("onboarded worker");
    }

}

package tech.staffjoy.bot.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import tech.staffjoy.bot.BotConstant;
import tech.staffjoy.bot.dto.AlertChangedShiftRequest;
import tech.staffjoy.bot.dto.AlertNewShiftRequest;
import tech.staffjoy.bot.dto.AlertNewShiftsRequest;
import tech.staffjoy.bot.dto.AlertRemovedShiftRequest;
import tech.staffjoy.bot.dto.AlertRemovedShiftsRequest;
import tech.staffjoy.bot.dto.GreetingRequest;
import tech.staffjoy.bot.dto.OnboardWorkerRequest;


@FeignClient(name = BotConstant.SERVICE_NAME, path = "/v1", url = "${staffjoy.bot-service-endpoint}")
public interface BotClient {

    @PostMapping(path="sms-greeting")
    ResponseEntity<String> sendSmsGreeting(@RequestBody @Validated GreetingRequest request);

    @PostMapping(path="onboard-worker")
    ResponseEntity<String> onboardWorker(@RequestBody @Validated OnboardWorkerRequest request);

    @PostMapping(path="alert-new-shift")
    ResponseEntity<String> alertNewShift(@RequestBody @Validated AlertNewShiftRequest request);

    @PostMapping(path="alert-new-shifts")
    ResponseEntity<String> alertNewShifts(@RequestBody @Validated AlertNewShiftsRequest request);

    @PostMapping(path="alert-removed-shift")
    ResponseEntity<String> alertRemovedShift(@RequestBody @Validated AlertRemovedShiftRequest request);

    @PostMapping(path="alert-removed-shifts")
    ResponseEntity<String> alertRemovedShifts(@RequestBody @Validated AlertRemovedShiftsRequest request);

    @PostMapping(path="alert-changed-shifts")
    ResponseEntity<String> alertChangedShift(@RequestBody @Validated AlertChangedShiftRequest request);
}

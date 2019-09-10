package tech.staffjoy.bot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tech.staffjoy.bot.dto.AlertChangedShiftRequest;
import tech.staffjoy.bot.dto.AlertNewShiftRequest;
import tech.staffjoy.bot.dto.AlertNewShiftsRequest;
import tech.staffjoy.bot.dto.AlertRemovedShiftRequest;
import tech.staffjoy.bot.dto.AlertRemovedShiftsRequest;
import tech.staffjoy.bot.service.AlertService;


@RestController
@RequestMapping(value = "/v1")
@Validated
public class AlertController {

    @Autowired
    private AlertService alertService;

    @PostMapping(value = "alert-new-shift")
    public ResponseEntity<String> alertNewShift(@RequestBody @Validated AlertNewShiftRequest request) {
        alertService.alertNewShift(request);
        return ResponseEntity.ok("new shift alerted");
    }

    @PostMapping(value = "alert-new-shifts")
    public ResponseEntity<String> alertNewShifts(@RequestBody @Validated AlertNewShiftsRequest request) {
        alertService.alertNewShifts(request);
        return ResponseEntity.ok("new shifts alerted");
    }

    @PostMapping(value = "alert-removed-shift")
    public ResponseEntity<String> alertRemovedShift(@RequestBody @Validated AlertRemovedShiftRequest request) {
        alertService.alertRemovedShift(request);
        return ResponseEntity.ok("removed shift alerted");
    }

    @PostMapping(value = "alert-removed-shifts")
    public ResponseEntity<String> alertRemovedShifts(@RequestBody @Validated AlertRemovedShiftsRequest request) {
        alertService.alertRemovedShifts(request);
        return ResponseEntity.ok("removed shifts alerted");
    }

    @PostMapping(value = "alert-changed-shifts")
    public ResponseEntity<String> alertChangedShifts(@RequestBody @Validated AlertChangedShiftRequest request) {
        alertService.alertChangedShift(request);
        return ResponseEntity.ok("changed shifts alerted");

    }

}

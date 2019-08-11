package fo.staffjoy.account.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fo.staffjoy.account.dto.TestDto;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping
    public TestDto test(@RequestParam String name) {
        TestDto testDto = new  TestDto();
        testDto.setHello(name);
        return testDto;
    }
}

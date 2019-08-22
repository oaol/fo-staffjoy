package fo.staffjoy.faraday.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("test")
public class TestController {

    @PostMapping
    public String test() {
        return "1";
    }
}

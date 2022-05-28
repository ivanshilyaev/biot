package by.bsu.biot.controller;

import by.bsu.biot.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class SwitchController {

    private final ClientService clientService;

    @GetMapping
    public String index(HttpSession session) throws IOException {
        clientService.sendEncryptionKey();
        session.setAttribute("light", true);

        return "index";
    }

    @PostMapping("/led")
    public String switchLamp(HttpSession session) throws IOException {
        if (!(Boolean) session.getAttribute("light")) {
            clientService.turnOn();
            session.setAttribute("light", true);
        } else {
            clientService.turnOff();
            session.setAttribute("light", false);
        }

        return "index";
    }
}

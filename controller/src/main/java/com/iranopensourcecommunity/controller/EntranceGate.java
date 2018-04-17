package com.iranopensourcecommunity.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.api.objects.Update;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class EntranceGate {

    /*
    example url:
    http://localhost:8080/123456789ASDFGH/entrance

    example json data to pass:
    {"update_id": 122232}
    */
    @RequestMapping(value = "/{token}/entrance", method = GET)
    public String hookHandler(@PathVariable String token, @RequestBody Update update) {
        System.out.println(token);
        return update.toString();
    }
}

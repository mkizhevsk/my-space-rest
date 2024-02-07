package com.mk.myspacerest.controller;

import com.mk.myspacerest.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;
}

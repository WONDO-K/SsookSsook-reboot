package com.stillalive.Ssook_BE.diner.controller;

import com.stillalive.Ssook_BE.diner.service.DinerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Diner Controller", description = "식당 API")
@RequestMapping("/api/v1/diner")
@RequiredArgsConstructor
public class DinerController {

    private final DinerService dinerService;
}

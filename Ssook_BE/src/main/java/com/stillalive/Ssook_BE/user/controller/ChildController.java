package com.stillalive.Ssook_BE.user.controller;

import com.stillalive.Ssook_BE.user.service.ChildService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Child Controller", description = "자녀 API")
@RequestMapping("/api/v1/child")
@RequiredArgsConstructor
public class ChildController {

    private final ChildService childService;
}

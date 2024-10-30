package com.stillalive.Ssook_BE.user.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "User Controller", description = "유저 API")
@RequestMapping("/api/v1/user")
public class UserController {
}

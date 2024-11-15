package com.stillalive.Ssook_BE.nut.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class NutScoreResDto {
    private final LocalDate date;
    private final Integer score;
}

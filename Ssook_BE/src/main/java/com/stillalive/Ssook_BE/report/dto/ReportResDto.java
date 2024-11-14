package com.stillalive.Ssook_BE.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
public class ReportResDto {

    private final Integer report_id;
    private final LocalDate end_date;
    private final String diet;
    private final String advice;
}

package com.stillalive.Ssook_BE.report.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ReportListResDto {

    private final List<ReportResDto> reports;
}

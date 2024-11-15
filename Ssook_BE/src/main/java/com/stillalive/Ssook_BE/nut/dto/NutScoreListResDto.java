package com.stillalive.Ssook_BE.nut.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class NutScoreListResDto {

    private final List<NutScoreResDto> score_list;

}

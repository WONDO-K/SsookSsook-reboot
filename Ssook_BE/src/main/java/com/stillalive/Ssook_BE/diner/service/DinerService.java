package com.stillalive.Ssook_BE.diner.service;

import com.stillalive.Ssook_BE.diner.dto.AngelListResDto;
import com.stillalive.Ssook_BE.diner.dto.AngelResDto;
import com.stillalive.Ssook_BE.diner.dto.DinerListResDto;
import com.stillalive.Ssook_BE.diner.dto.DinerResDto;
import com.stillalive.Ssook_BE.diner.repository.DinerRepository;
import com.stillalive.Ssook_BE.exception.SsookException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.stillalive.Ssook_BE.exception.ErrorCode;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DinerService {

    private final DinerRepository dinerRepository;

    public DinerListResDto getDinerList() {
        return DinerListResDto.builder()
                .dinerList(
                        dinerRepository.findAll()
                                .stream()
                                .map(diner -> DinerResDto.builder()
                                        .dinerId(diner.getId())
                                        .name(diner.getName())
                                        .address(diner.getAddress())
                                        .lat(diner.getLat())
                                        .lng(diner.getLng())
                                        .tel(diner.getTel())
                                        .isAngel(diner.getIsAngel())
                                        .build()
                                )
                                .collect(Collectors.toList())
                )
                .totalItems((int) dinerRepository.count())
                .build();
    }

    public AngelListResDto getAngelList() {
        return AngelListResDto.builder()
                .angelList(
                        dinerRepository.findAllByIsAngelTrue()
                                .stream()
                                .map(diner -> AngelResDto.builder()
                                        .dinerId(diner.getId())
                                        .name(diner.getName())
                                        .address(diner.getAddress())
                                        .lat(diner.getLat())
                                        .lng(diner.getLng())
                                        .tel(diner.getTel())
                                        .isAngel(diner.getIsAngel())
                                        .build()
                                )
                                .collect(Collectors.toList())
                )
                .totalItems(dinerRepository.countAllByIsAngelTrue())
                .build();
    }

    public DinerResDto getDinerDetail(Integer dinerId) {
        return dinerRepository.findById(dinerId)
                .map(diner -> DinerResDto.builder()
                        .dinerId(diner.getId())
                        .name(diner.getName())
                        .address(diner.getAddress())
                        .lat(diner.getLat())
                        .lng(diner.getLng())
                        .tel(diner.getTel())
                        .isAngel(diner.getIsAngel())
                        .build()
                )
                .orElseThrow(() -> new SsookException(ErrorCode.DINER_NOT_FOUND));
    }

}

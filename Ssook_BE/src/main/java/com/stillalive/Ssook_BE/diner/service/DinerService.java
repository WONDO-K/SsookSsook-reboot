package com.stillalive.Ssook_BE.diner.service;

import com.stillalive.Ssook_BE.diner.dto.*;
import com.stillalive.Ssook_BE.diner.repository.DinerRepository;
import com.stillalive.Ssook_BE.domain.Diner;
import com.stillalive.Ssook_BE.exception.SsookException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.stillalive.Ssook_BE.exception.ErrorCode;

import java.util.List;
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


    public DinerListResDto getNearbyDiners(NearbyDinerResDto nearbyDinerResDto) {
        Double lat = nearbyDinerResDto.getLat();
        Double lng = nearbyDinerResDto.getLng();
        Float range = nearbyDinerResDto.getRange();

        return DinerListResDto.builder()
                .dinerList(
                        dinerRepository.findNearbyDiners(lat, lng, range)
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
                .totalItems(dinerRepository.findNearbyDiners(lat, lng, range).size())
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

    public DinerMenuListResDto getDinerMenuList(Integer dinerId) {
        return DinerMenuListResDto.builder()
                .dinerMenuList(
                        dinerRepository.findById(dinerId)
                                .map(diner -> diner.getMenus()
                                        .stream()
                                        .map(menu -> DinerMenuResDto.builder()
                                                .menuId(menu.getId())
                                                .name(menu.getName())
                                                .price(menu.getPrice())
                                                .build()
                                        )
                                        .collect(Collectors.toList())
                                )
                                .orElseThrow(() -> new SsookException(ErrorCode.DINER_NOT_FOUND))
                )
                .totalItems(dinerRepository.findById(dinerId).map(diner -> diner.getMenus().size()).orElse(0))
                .build();
    }


//    // 추천 메뉴 판매 식당 목록 조회
//    public DinerListResDto getDinerListByFood(Integer foodId) {
//        return DinerListResDto.builder()
//                .dinerList(
//                        dinerRepository.findDinersByFood(foodId)
//                                .stream()
//                                .map(diner -> DinerResDto.builder()
//                                        .dinerId(diner.getId())
//                                        .name(diner.getName())
//                                        .address(diner.getAddress())
//                                        .lat(diner.getLat())
//                                        .lng(diner.getLng())
//                                        .tel(diner.getTel())
//                                        .isAngel(diner.getIsAngel())
//                                        .build()
//                                )
//                                .collect(Collectors.toList())
//                )
//                .totalItems(dinerRepository.findDinersByFood(foodId).size())
//                .build();
//    }


}

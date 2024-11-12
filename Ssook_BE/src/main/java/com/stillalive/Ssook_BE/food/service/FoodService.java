package com.stillalive.Ssook_BE.food.service;


import com.stillalive.Ssook_BE.domain.ChildHistory;
import com.stillalive.Ssook_BE.menu.repository.RecoMenuRepository;
import com.stillalive.Ssook_BE.nut.service.NutService;
import com.stillalive.Ssook_BE.pay.repository.ChildHistoryRepository;
import com.stillalive.Ssook_BE.pay.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FoodService {

    private final NutService nutService;
    private final PaymentService paymentService;
    private final RecoMenuRepository recoMenuRepository;
    private final ChildHistoryRepository childHistoryRepository;

    public List<String> getRecoFood(Integer childId) {

        // 다음날 기준으로 날짜 설정
        LocalDate date = LocalDate.now().plusDays(1);
        LocalDateTime endDate = date.atStartOfDay();
        LocalDateTime startDate = endDate.minusDays(6);

        // 최근 일주일 동안 먹은 메뉴 리스트
        List<Integer> childHistory_id_list = childHistoryRepository.findByChildIdAndDateRange(childId, startDate, endDate).
                stream().map(ChildHistory::getId).collect(Collectors.toList());

        // 최근 일주일 동안 먹은 메뉴 리스트 조회
        List<String> menuListWeek = paymentService.getMenuListWeek(childHistory_id_list);
//        List<String> menuListWeek = Arrays.asList("초밥","연어초밥");

        // 추천 음식 리스트 조회
        List<String> recoFood = recoMenuRepository.findAllNames();

        // 결과를 저장할 맵 (음식 이름 -> 최소 유사도)
        Map<String, Double> recoFoodMinSimilarity = new HashMap<>();

        for (String recoMenu : recoFood) {

            // 추천 음식 이름 토큰화
            Set<String> recoTokens = new HashSet<>(tokenizeKorean(recoMenu));

            double jaccardSimilarity=0;

            for (String eatenMenu : menuListWeek) {
                // 먹은 음식 이름 토큰화
                Set<String> eatenTokens = new HashSet<>(tokenizeKorean(eatenMenu));

                // 자카드 유사도 계산
                jaccardSimilarity += calculateJaccardSimilarity(recoTokens, eatenTokens);
            }
            recoFoodMinSimilarity.put(recoMenu, jaccardSimilarity);
        }

        // 유사도가 낮은 순으로 정렬
        List<Map.Entry<String, Double>> sortedRecoFood = new ArrayList<>(recoFoodMinSimilarity.entrySet());
        sortedRecoFood.sort(Map.Entry.comparingByValue());

        // 상위 5개 음식 선택
        List<String> recommendedFoods = sortedRecoFood.stream()
                .limit(5)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        return recommendedFoods;
    }

    // 간단한 한국어 토큰화 메서드 (라이브러리 없이)
    private List<String> tokenizeKorean(String text) {
        // 공백 및 특수문자 기준으로 분리
        String[] tokens = text.split("[\\s\\p{Punct}]+");
        return Arrays.asList(tokens);
    }

    // 자카드 유사도 계산 메서드 (부분 일치 고려)
    private double calculateJaccardSimilarity(Set<String> set1, Set<String> set2) {
        if (set1.isEmpty() && set2.isEmpty()) {
            return 1.0; // 둘 다 비어있으면 완전히 동일하다고 판단
        }

        // 교집합과 합집합을 직접 계산
        int intersectionCount = 0;
        Set<String> unionSet = new HashSet<>(set1);
        unionSet.addAll(set2);

        // 교집합 계산 (부분 일치 고려)
        for (String token1 : set1) {
            for (String token2 : set2) {
                if (token1.equals(token2) || token1.contains(token2) || token2.contains(token1)) {
                    intersectionCount++;
                    break; // 한 번 교집합으로 포함된 항목은 중복 계산 방지
                }
            }
        }

        return (double) intersectionCount / unionSet.size();
    }
}



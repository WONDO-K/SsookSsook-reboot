package com.stillalive.Ssook_BE.nut.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stillalive.Ssook_BE.diner.repository.DinerRepository;
import com.stillalive.Ssook_BE.domain.BodyProfile;
import com.stillalive.Ssook_BE.domain.Child;
import com.stillalive.Ssook_BE.domain.Diner;
import com.stillalive.Ssook_BE.domain.NutHistory;
import com.stillalive.Ssook_BE.enums.Gender;
import com.stillalive.Ssook_BE.enums.Meal;
import com.stillalive.Ssook_BE.exception.ErrorCode;
import com.stillalive.Ssook_BE.exception.SsookException;
import com.stillalive.Ssook_BE.nut.dto.DayIntakeNutResDto;
import com.stillalive.Ssook_BE.nut.dto.IntakeNutResDto;
import com.stillalive.Ssook_BE.nut.dto.WeekIntakeNutResDto;
import com.stillalive.Ssook_BE.nut.repository.NutHistoryRepository;
import com.stillalive.Ssook_BE.pay.dto.PaymentReqDto;
import com.stillalive.Ssook_BE.user.repository.BodyProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NutService {

    private final NutHistoryRepository nutHistoryRepository;
    private final DinerRepository dinerRepository;
    private final BodyProfileRepository bodyProfileRepository;

    @Value("${chatgpt.key}")  // application.yml에서 API 키 불러오기
    private String apiKey;  // 발급받은 API 키
    private final String gptApiUrl = "https://api.openai.com/v1/chat/completions";


    // 자녀 영양 섭취
    @Transactional(readOnly = true)
    public IntakeNutResDto getIntakeNut(Integer childId, LocalDate date, Meal mealTime) {
        // 영양 섭취 조회
        NutHistory nutHistory = nutHistoryRepository.findByChild_ChildIdAndEatDateAndMeal(childId, date, mealTime)
                .orElseThrow(() -> new SsookException(ErrorCode.NOT_FOUND_NUT_HISTORY));

        return IntakeNutResDto.builder()
                .nutHistoryId(nutHistory.getId())
                .childId(nutHistory.getChild().getChildId())
                .eatDate(nutHistory.getEatDate())
                .mealTime(nutHistory.getMeal().name())
                .cal(nutHistory.getNutrient().getCal())
                .carb(nutHistory.getNutrient().getCarb())
                .protein(nutHistory.getNutrient().getProtein())
                .fat(nutHistory.getNutrient().getFat())
                .vitA(nutHistory.getNutrient().getVitA())
                .vitC(nutHistory.getNutrient().getVitC())
                .ribof(nutHistory.getNutrient().getRibof())
                .thiam(nutHistory.getNutrient().getThiam())
                .iron(nutHistory.getNutrient().getIron())
                .calcium(nutHistory.getNutrient().getCalcium())
                .build();
    }

    // 자녀 하루 누적 섭취량 조회
    @Transactional(readOnly = true)
    public DayIntakeNutResDto getDailyIntakeNut(Integer childId, LocalDate date) {
        // 하루 누적 섭취량 조회
        List<NutHistory> nutHistories = nutHistoryRepository.findAllByChild_ChildIdAndEatDate(childId, date)
                .orElseThrow(() -> new SsookException(ErrorCode.NOT_FOUND_NUT_HISTORY));

        // 하루 누적 섭취량 계산
        Float dailyCal = 0f;
        Float dailyCarb = 0f;
        Float dailyProtein = 0f;
        Float dailyFat = 0f;
        Float dailyVitA = 0f;
        Float dailyVitC = 0f;
        Float dailyRibof = 0f;
        Float dailyThiam = 0f;
        Float dailyIron = 0f;
        Float dailyCalcium = 0f;

        for (NutHistory nutHistory : nutHistories) {
            dailyCal += nutHistory.getNutrient().getCal() == null ? 0 : nutHistory.getNutrient().getCal();
            dailyCarb += nutHistory.getNutrient().getCarb() == null ? 0 : nutHistory.getNutrient().getCarb();
            dailyProtein += nutHistory.getNutrient().getProtein() == null ? 0 : nutHistory.getNutrient().getProtein();
            dailyFat += nutHistory.getNutrient().getFat() == null ? 0 : nutHistory.getNutrient().getFat();
            dailyVitA += nutHistory.getNutrient().getVitA() == null ? 0 : nutHistory.getNutrient().getVitA();
            dailyVitC += nutHistory.getNutrient().getVitC() == null ? 0 : nutHistory.getNutrient().getVitC();
            dailyRibof += nutHistory.getNutrient().getRibof() == null ? 0 : nutHistory.getNutrient().getRibof();
            dailyThiam += nutHistory.getNutrient().getThiam() == null ? 0 : nutHistory.getNutrient().getThiam();
            dailyIron += nutHistory.getNutrient().getIron() == null ? 0 : nutHistory.getNutrient().getIron();
            dailyCalcium += nutHistory.getNutrient().getCalcium() == null ? 0 : nutHistory.getNutrient().getCalcium();
        }

        return DayIntakeNutResDto.builder()
                .childId(childId)
                .eatDate(date)
                .cal(dailyCal)
                .carb(dailyCarb)
                .protein(dailyProtein)
                .fat(dailyFat)
                .vitA(dailyVitA)
                .vitC(dailyVitC)
                .ribof(dailyRibof)
                .thiam(dailyThiam)
                .iron(dailyIron)
                .calcium(dailyCalcium)
                .build();
    }

    // 자녀 주간 누적 섭취량 조회
    @Transactional(readOnly = true)
    public WeekIntakeNutResDto getWeeklyIntakeNut(Integer childId, LocalDate date) {
        // 주간 누적 섭취량 조회
        List<NutHistory> nutHistories = nutHistoryRepository.findAllByChild_ChildIdAndEatDateBetween(childId, date.minusDays(6), date)
                .orElseThrow(() -> new SsookException(ErrorCode.NOT_FOUND_NUT_HISTORY));

        // 주간 누적 섭취량 계산
        Float weeklyCal = 0f;
        Float weeklyCarb = 0f;
        Float weeklyProtein = 0f;
        Float weeklyFat = 0f;
        Float weeklyVitA = 0f;
        Float weeklyVitC = 0f;
        Float weeklyRibof = 0f;
        Float weeklyThiam = 0f;
        Float weeklyIron = 0f;
        Float weeklyCalcium = 0f;

        for (NutHistory nutHistory : nutHistories) {
            weeklyCal += nutHistory.getNutrient().getCal() == null ? 0 : nutHistory.getNutrient().getCal();
            weeklyCarb += nutHistory.getNutrient().getCarb() == null ? 0 : nutHistory.getNutrient().getCarb();
            weeklyProtein += nutHistory.getNutrient().getProtein() == null ? 0 : nutHistory.getNutrient().getProtein();
            weeklyFat += nutHistory.getNutrient().getFat() == null ? 0 : nutHistory.getNutrient().getFat();
            weeklyVitA += nutHistory.getNutrient().getVitA() == null ? 0 : nutHistory.getNutrient().getVitA();
            weeklyVitC += nutHistory.getNutrient().getVitC() == null ? 0 : nutHistory.getNutrient().getVitC();
            weeklyRibof += nutHistory.getNutrient().getRibof() == null ? 0 : nutHistory.getNutrient().getRibof();
            weeklyThiam += nutHistory.getNutrient().getThiam() == null ? 0 : nutHistory.getNutrient().getThiam();
            weeklyIron += nutHistory.getNutrient().getIron() == null ? 0 : nutHistory.getNutrient().getIron();
            weeklyCalcium += nutHistory.getNutrient().getCalcium() == null ? 0 : nutHistory.getNutrient().getCalcium();
        }

        return WeekIntakeNutResDto.builder()
                .childId(childId)
                .year(date.getYear())
                .month(date.getMonthValue())
                .weekOfMonth(date.get(WeekFields.ISO.weekOfMonth()))
                .cal(weeklyCal)
                .carb(weeklyCarb)
                .protein(weeklyProtein)
                .fat(weeklyFat)
                .vitA(weeklyVitA)
                .vitC(weeklyVitC)
                .ribof(weeklyRibof)
                .thiam(weeklyThiam)
                .iron(weeklyIron)
                .calcium(weeklyCalcium)
                .build();
    }

    // GPT를 이용하여 영양 정보 생성
    public void genrateNutFromGPT(PaymentReqDto paymentReqDto, Child child) {
        // GPT를 이용하여 영양 정보 생성
        Integer diner_id=paymentReqDto.getDinerId();
        Diner diner = dinerRepository.findById(diner_id)
                .orElseThrow(() -> new SsookException(ErrorCode.DINER_NOT_FOUND));
        String menuNames = paymentReqDto.getPayDetails().stream()
                .map(PaymentReqDto.PayDetailDto::getMenuName)
                .collect(Collectors.joining(", "));
        BodyProfile bodyProfile = bodyProfileRepository.findByChild(child)
                .orElseThrow(() -> new SsookException(ErrorCode.NOT_FOUND_BODYPROFILE));

        Integer age=LocalDate.now().getYear()-child.getBday().getYear();
        Gender gender=child.getGender();
        Float height=bodyProfile.getHeight();
        Float weight=bodyProfile.getWeight();
        Integer eer=bodyProfile.getCaloryEer();

        String prompt = "음식점 카테고리:"+"\n" + " 음식점 이름: " + "\n" + "먹은 메뉴들: " + "\n" + "아이의 나이,성별,키,몸무게,EER:" + "\n" + "위와 같은 형태의 정보를 제공하겠다, 해당 아이가 먹을 양을 고려해서, 먹은 메뉴들을 종합접으로 1끼 기준의 영양소를 알려줘" + "\n" + "추가로 해당 음식점에서 제공 할 것 같은 반찬이나 공기밥이 필요한 음식에 대해서 공기밥을 추가해 그에 맞는 영양소도 적절히 생각해서 영양소에 추가해줘" + "\n" + "음식 섭취량은 아이의 신체정보를 통해 예측해" + "\n" + "답변은 아래와 같은 json 형태(단위는 없이)로 주고 다른 답변은 일절 생성하지마 - 답변에서 단위는 생략하돼 칼로리->kcal, vitA->microgram, 탄단지->g, 나머지->mg으로 생각해줘" + "\n" + "{cal: 100, carb: 100, protein: 100, fat: 100, vitA: 100, vitC: 100, ribof: 100, thiam: 100, iron: 100, calcium: 100}";
        String inputText = "음식점 카테고리: " + diner.getCategory() + "\n" + " 음식점 이름: " + diner.getName() + "\n" + "먹은 메뉴들: "+menuNames + "\n" + "아이의 나이,성별(남1,여2),키,몸무게,EER: " + age + "세, " + gender +" , " + height + "cm, " + weight + "kg, " + eer + "kcal";

        log.info("ChatGPT API 요청: {}", inputText);

        // OpenAI API에 보낼 요청 바디 생성
        Map<String, Object> requestPayload = new HashMap<>();
        // ChatGPT API 요청에 필요한 파라미터 설정
        requestPayload.put("model", "gpt-4o");
        requestPayload.put("messages", List.of(
                // ChatGPT API에 보낼 메시지 - 맥락
                Map.of("role", "system", "content", prompt),
                // ChatGPT API에 보낼 메시지 - 사용자 입력
                Map.of("role", "user", "content", inputText)
        ));
        // 응답 길이 설정
        requestPayload.put("max_tokens", 1000);  // 응답의 최대 토큰 수
        requestPayload.put("temperature", 0.2);  // 다음 토큰을 선택할 때 사용할 무작위성 정도

        try {
            // HTTP 요청 생성
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestPayload, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(gptApiUrl, entity, String.class);

            // 응답 처리
            if (response.getStatusCode() == HttpStatus.OK) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.getBody());
                String generatedText = root.path("choices").get(0).path("message").path("content").asText();
//              이부분에서 응답을 받아서 처리하면 됩니다.
                log.info("ChatGPT API 요청 결과: {}", generatedText);


            } else {
                log.info("ChatGPT API 요청 중 오류 발생: {}", response.getStatusCode());

            }
        } catch (Exception e) {
            // 예외 발생 시 오류 메시지 던짐
            log.info("ChatGPT API 요청 중 오류 발생: {}", e.getMessage());
        }

    }

}

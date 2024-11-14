package com.stillalive.Ssook_BE.report.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stillalive.Ssook_BE.domain.BodyProfile;
import com.stillalive.Ssook_BE.domain.Child;
import com.stillalive.Ssook_BE.domain.NutrientRequirement;
import com.stillalive.Ssook_BE.domain.Report;
import com.stillalive.Ssook_BE.exception.ErrorCode;
import com.stillalive.Ssook_BE.exception.SsookException;
import com.stillalive.Ssook_BE.nut.dto.WeekIntakeNutResDto;
import com.stillalive.Ssook_BE.nut.repository.NutrientRequirementRepository;
import com.stillalive.Ssook_BE.nut.service.NutService;
import com.stillalive.Ssook_BE.report.repository.ReportRepository;
import com.stillalive.Ssook_BE.user.repository.BodyProfileRepository;
import com.stillalive.Ssook_BE.user.repository.ChildRepository;
import com.stillalive.Ssook_BE.user.service.ChildService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ReportService {

    private final ReportRepository reportRepository;
    private final ChildRepository childRepository;
    private final NutService nutService;
    private final BodyProfileRepository bodyProfileRepository;
    private final NutrientRequirementRepository nutrientRequirementRepository;

    @Value("${chatgpt.key2}")  // application.yml에서 API 키 불러오기
    private String apiKey;  // 발급받은 API 키
    private final String gptApiUrl = "https://api.openai.com/v1/chat/completions";

//  현재 날짜에서 부터 일주일간에 리포트 기록이 없으면 생성하고 모든 리스트 조회 있으면 모든 리스트 조회
    public void generateReportAndGetList(Integer child_id) {

        Optional<Child> child = childRepository.findById(child_id);
        LocalDate today = LocalDate.now();

//      리포트의 end_date값이 오늘부터 이전 7일까지의 날짜중에 있는 리포트가 있는지 없는지
        boolean isExist = reportRepository.findByChild(child.get()).stream()
                .anyMatch(report -> report.getDate().isAfter(today.minusDays(7)) && report.getDate().isBefore(today));

//      없으면 주간 리포트 생성
        if (!isExist) {
            WeekIntakeNutResDto weekIntakeNutResDto = nutService.getWeeklyIntakeNut(child_id, today);
            BodyProfile bodyProfile = bodyProfileRepository.findByChild(child.get())
                    .orElseThrow(() -> new SsookException(ErrorCode.NOT_FOUND_BODYPROFILE));
            Integer childEer = bodyProfile.getCaloryEer();
            Float intakeCalory = weekIntakeNutResDto.getCal();

            Integer mutiple = Math.round(childEer/intakeCalory);

            Float carb= weekIntakeNutResDto.getCarb()*mutiple;
            Float protein= weekIntakeNutResDto.getProtein()*mutiple;
            Float vitA= weekIntakeNutResDto.getVitA()*mutiple;
            Float vitC= weekIntakeNutResDto.getVitC()*mutiple;
            Float ribof= weekIntakeNutResDto.getRibof()*mutiple;
            Float calcium= weekIntakeNutResDto.getCalcium()*mutiple;
            Float iron= weekIntakeNutResDto.getIron()*mutiple;
            Float thiam = weekIntakeNutResDto.getThiam()*mutiple;

//          영양소
//          아이의 생일로 나이 계산하기
            Integer age = today.getYear() - child.get().getBday().getYear();
            Optional<NutrientRequirement> nutrientRequirement = nutrientRequirementRepository.findByGenderAndAge(child.get().getGender(), age);

//          영양소비율이 80%~120% 사이면 정상
//          영양소비율이 80% 이하면 부족
//          영양소비율이 120% 이상이면 과다
            String carbStatus = carb/nutrientRequirement.get().getCarb() < 0.8 ? "low" : carb/nutrientRequirement.get().getCarb() > 1.2 ? "high" : "normal";
            String proteinStatus = protein/nutrientRequirement.get().getProtein() < 0.8 ? "low" : protein/nutrientRequirement.get().getProtein() > 1.2 ? "high" : "normal";
            String vitAStatus = vitA/nutrientRequirement.get().getVitA() < 0.8 ? "low" : vitA/nutrientRequirement.get().getVitA() > 1.2 ? "high" : "normal";
            String vitCStatus = vitC/nutrientRequirement.get().getVitC() < 0.8 ? "low" : vitC/nutrientRequirement.get().getVitC() > 1.2 ? "high" : "normal";
            String ribofStatus = ribof/nutrientRequirement.get().getRibof() < 0.8 ? "low" : ribof/nutrientRequirement.get().getRibof() > 1.2 ? "high" : "normal";
            String calciumStatus = calcium/nutrientRequirement.get().getCalcium() < 0.8 ? "low" : calcium/nutrientRequirement.get().getCalcium() > 1.2 ? "high" : "normal";
            String ironStatus = iron/nutrientRequirement.get().getIron() < 0.8 ? "low" : iron/nutrientRequirement.get().getIron() > 1.2 ? "high" : "normal";
            String thiamStatus = thiam/nutrientRequirement.get().getThiam() < 0.8 ? "low" : thiam/nutrientRequirement.get().getThiam() > 1.2 ? "high" : "normal";

            String prompt = "각 영양소별로 아이의 일주일 평균 섭취량을 normal, low, high로 구분해서 데이터를 줄게\n"+"해당 데이터를 보고 아이의 한끼 식단을 추천 해주고 전체적인 건강 조언을 해줘\n" +
                    "답변은 다른 답변없이 json형식으로만 답해줘" +
                    "예시 input : {carb : normal, protein : low, vitA : high, vitC : normal, ribof : low, calcium : high, iron : normal, thiam : high}\n" +
                    "예시 output : { diet : '쌀보리밥, 표고부추달걀국, 마파두부, 애호박볶음, 코다리튀김,배추김치', advice : '단백질 섭취를 충분히 하시는 것이 중요합니다. 따라서 식사에서 고단백질 음식들을 추천 드렸습니다. 균형잡힌 식단을 유지하고 규칙적인 운동을 통해 건강을 향상 시키시길 바합니다. 충분한 수분 섭취와 적절한 휴식도 잊지 않으시길 바랍니다. 기온이 떨어짐에 따라 찬 음식 보다는 따뜻하고 열을 유지하는 음식들을 먹는 것을 권해드립니다.'}";
            String inputText = "{carb : "+carbStatus+", protein : "+proteinStatus+", vitA : "+vitAStatus+", vitC : "+vitCStatus+", ribof : "+ribofStatus+", calcium : "+calciumStatus+", iron : "+ironStatus+", thiam : "+thiamStatus+"}";

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

//                  json 형식으로 오는 데이터 파싱하기
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonNode = objectMapper.readTree(generatedText);
                    String diet = jsonNode.get("diet").asText();
                    String advice = jsonNode.get("advice").asText();
//
//                    // json 형식으로 받은 데이터 리포트 생성
                    Report report = Report.builder()
                            .child(child.get())
                            .date(today)
                            .diet(diet)
                            .advice(advice)
                            .build();
                    reportRepository.save(report);



                } else {
                    log.info("ChatGPT API 요청 중 오류 발생: {}", response.getStatusCode());

                }
            } catch (Exception e) {
                // 예외 발생 시 오류 메시지 던짐
                log.info("ChatGPT API 요청 중 오류 발생: {}", e.getMessage());
            }










            genertaeReport(child_id, today);
        }

//        리포트 리스트 조회
        List<Report> reports = reportRepository.findByChild(child.get());

    }

    private void genertaeReport(Integer childId, LocalDate today) {
    }


}

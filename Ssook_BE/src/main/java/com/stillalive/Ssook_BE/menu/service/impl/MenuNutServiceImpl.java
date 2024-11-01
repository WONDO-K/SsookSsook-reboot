package com.stillalive.Ssook_BE.menu.service.impl;

import com.stillalive.Ssook_BE.domain.MenuNut;
import com.stillalive.Ssook_BE.domain.base.Nutrient;
import com.stillalive.Ssook_BE.menu.repository.MenuNutRepository;
import com.stillalive.Ssook_BE.menu.service.MenuNutService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MenuNutServiceImpl implements MenuNutService {

//    private final MenuNutRepository menuNutRepository;
//
//    // TODO: 영양소 분석 AI 모델 서비스 추가해야함
//    private final AiModelService aiModelService;
//
//    @Override
//    public void createMenuNutIfNotExists(String menuName) {
//        menuNutRepository.findByMenuName(menuName).ifPresentOrElse(
//                menuNut -> {
//                    // 메뉴가 이미 존재할 경우 추가 작업 필요 없음
//                },
//                () -> {
//                    Nutrient nutrient = aiModelService.createNutritionalInfo(menuName);
//                    MenuNut newMenuNut = new MenuNut(null, menuName, nutrient);
//                    menuNutRepository.save(newMenuNut);
//                }
//        );
//    }
}

package com.stillalive.Ssook_BE.user.service;

import com.stillalive.Ssook_BE.domain.Child;
import com.stillalive.Ssook_BE.domain.School;
import com.stillalive.Ssook_BE.enums.Gender;
import com.stillalive.Ssook_BE.enums.Progress;
import com.stillalive.Ssook_BE.exception.ErrorCode;
import com.stillalive.Ssook_BE.exception.SsookException;
import com.stillalive.Ssook_BE.user.dto.ChildSignupReqDto;
import com.stillalive.Ssook_BE.user.dto.FamilyReqListResDto;
import com.stillalive.Ssook_BE.user.dto.FamilyReqResDto;
import com.stillalive.Ssook_BE.user.repository.ChildRepository;
import com.stillalive.Ssook_BE.user.repository.FamilyRelationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChildService {

    private final ChildRepository childRepository;
    private final FamilyRelationRepository familyRelationRepository;
    private final SchoolRepository schoolRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public void join(ChildSignupReqDto childSignupReqDto) {

        String name = childSignupReqDto.getName();
        String tel = childSignupReqDto.getTel();
        Date bday = childSignupReqDto.getBday();
        Gender gender = childSignupReqDto.getGender();
        String loginId = childSignupReqDto.getLoginId();
        String password = childSignupReqDto.getPassword();
        Integer schoolId = childSignupReqDto.getSchoolId();

        // 아이디 중복 체크
        if (existsByLoginId(loginId)) {
            throw new SsookException(ErrorCode.DUPLICATE_LOGIN_ID);
        }

        // 전화번호 중복 체크
        if (childRepository.existsByTel(tel)) {
            throw new SsookException(ErrorCode.DUPLICATE_TEL);
        }

        // 해당 학교가 없을 때
        Optional<School> school = schoolRepository.findById(schoolId).or(() -> {
            throw new SsookException(ErrorCode.NOT_FOUND_SCHOOL);
        });

        childRepository.save(Child.builder()
                .name(name)
                .tel(tel)
                .bday(bday)
                .gender(gender)
                .loginId(loginId)
                .password(bCryptPasswordEncoder.encode(password))
                .school(school.get())
                .build());


    }

    public boolean existsByLoginId(String loginId) {
        boolean result = childRepository.existsByLoginId(loginId);
        if (result) {
            // 이미 존재하는 로그인 아이디
            throw new SsookException(ErrorCode.DUPLICATE_LOGIN_ID);
        }
        return result;
    }

    // 가족 신청 목록 조회
    public FamilyReqListResDto getFamilyReqList(Integer childId) {
        List<FamilyReqResDto> list = familyRelationRepository.findByChild_ChildIdAndStatus(childId, Progress.PENDING)
                .stream()
                .map(familyRelation -> FamilyReqResDto.builder()
                        .familyRelationId(familyRelation.getId())
                        .parentName(familyRelation.getParent().getName())
                        .parentTel(familyRelation.getParent().getTel())
                        .requestedAt(familyRelation.getCreatedAt())
                        .build())
                .toList();

        return FamilyReqListResDto.builder()
                .familyReqList(list)
                .build();

    }

}
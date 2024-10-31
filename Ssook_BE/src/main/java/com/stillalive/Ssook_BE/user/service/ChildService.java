package com.stillalive.Ssook_BE.user.service;

import com.stillalive.Ssook_BE.domain.Child;
import com.stillalive.Ssook_BE.domain.School;
import com.stillalive.Ssook_BE.enums.Gender;
import com.stillalive.Ssook_BE.exception.ErrorCode;
import com.stillalive.Ssook_BE.exception.SsookException;
import com.stillalive.Ssook_BE.user.dto.ChildSignupRequestDto;
import com.stillalive.Ssook_BE.user.repository.ChildRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChildService {

    private final ChildRepository childRepository;
    private final SchoolRepository schoolRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public void join(ChildSignupRequestDto childSignupRequestDto) {

        String name = childSignupRequestDto.getName();
        String tel = childSignupRequestDto.getTel();
        Date bday = childSignupRequestDto.getBday();
        Gender gender = childSignupRequestDto.getGender();
        String loginId = childSignupRequestDto.getLoginId();
        String password = childSignupRequestDto.getPassword();
        Integer schoolId = childSignupRequestDto.getSchoolId();

        // 아이디 중복 체크
        if (childRepository.existsByLoginId(loginId)) {
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
}
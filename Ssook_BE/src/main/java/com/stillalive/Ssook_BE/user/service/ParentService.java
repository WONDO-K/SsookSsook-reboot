package com.stillalive.Ssook_BE.user.service;

import com.stillalive.Ssook_BE.domain.Child;
import com.stillalive.Ssook_BE.domain.Parent;
import com.stillalive.Ssook_BE.domain.School;
import com.stillalive.Ssook_BE.enums.Gender;
import com.stillalive.Ssook_BE.exception.ErrorCode;
import com.stillalive.Ssook_BE.exception.SsookException;
import com.stillalive.Ssook_BE.user.dto.ChildSignupRequestDto;
import com.stillalive.Ssook_BE.user.dto.ParentSignupRequestDto;
import com.stillalive.Ssook_BE.user.repository.ParentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ParentService {

    private final ParentRepository parentRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public void join(ParentSignupRequestDto parentSignupRequestDto) {

        String name = parentSignupRequestDto.getName();
        String tel = parentSignupRequestDto.getTel();
        Date bday = parentSignupRequestDto.getBday();
        Gender gender = parentSignupRequestDto.getGender();
        String loginId = parentSignupRequestDto.getLoginId();
        String password = parentSignupRequestDto.getPassword();

        // 아이디 중복 체크
        if (parentRepository.existsByLoginId(loginId)) {
            throw new SsookException(ErrorCode.DUPLICATE_LOGIN_ID);
        }

        // 전화번호 중복 체크
        if (parentRepository.existsByTel(tel)) {
            throw new SsookException(ErrorCode.DUPLICATE_TEL);
        }

        parentRepository.save(Parent.builder()
                .name(name)
                .tel(tel)
                .bday(bday)
                .gender(gender)
                .loginId(loginId)
                .password(bCryptPasswordEncoder.encode(password))
                .build());
    }

}

package com.stillalive.Ssook_BE.user.service;

import com.stillalive.Ssook_BE.domain.Parent;
import com.stillalive.Ssook_BE.enums.Gender;
import com.stillalive.Ssook_BE.exception.ErrorCode;
import com.stillalive.Ssook_BE.exception.SsookException;
import com.stillalive.Ssook_BE.user.dto.ParentSignupReqDto;
import com.stillalive.Ssook_BE.user.repository.ParentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class ParentService {

    private final ParentRepository parentRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public void join(ParentSignupReqDto parentSignupReqDto) {

        String name = parentSignupReqDto.getName();
        String tel = parentSignupReqDto.getTel();
        Date bday = parentSignupReqDto.getBday();
        Gender gender = parentSignupReqDto.getGender();
        String loginId = parentSignupReqDto.getLoginId();
        String password = parentSignupReqDto.getPassword();

        // 아이디 중복 체크
        if (existsByLoginId(loginId)) {
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

    // 아이디 중복 체크
    public boolean existsByLoginId(String loginId) {
        boolean result = parentRepository.existsByLoginId(loginId);
        if (result) {
            // 이미 존재하는 아이디 예외발생
            throw new SsookException(ErrorCode.DUPLICATE_LOGIN_ID);
        }
        return result;
    }

}

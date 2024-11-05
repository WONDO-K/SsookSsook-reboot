package com.stillalive.Ssook_BE.user.dto;

import com.stillalive.Ssook_BE.enums.Gender;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Builder
public class UserInfoResDto {

    private Integer userId;
    private String name;
    private String tel;
    private Date bday;
    private Gender gender;
    private String loginId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isParent;

}

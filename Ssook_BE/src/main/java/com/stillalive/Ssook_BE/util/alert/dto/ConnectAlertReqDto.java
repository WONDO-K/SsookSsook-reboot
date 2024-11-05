package com.stillalive.Ssook_BE.util.alert.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConnectAlertReqDto {

    @NotNull(message = "userId는 필수입니다.")
    int userId;

    @NotNull(message = "isParent는 필수입니다.")
    boolean isParent;
}

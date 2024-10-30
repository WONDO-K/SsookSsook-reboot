package com.stillalive.Ssook_BE.domain.base;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
public class Nutrient {

    private Float cal;
    private Float carb;
    private Float protein;
    private Float fat;
    private Float vitA;
    private Float vitC;
    private Float ribof;
    private Float thiam;
    private Float iron;
    private Float calcium;

}
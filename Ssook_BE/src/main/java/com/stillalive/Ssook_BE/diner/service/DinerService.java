package com.stillalive.Ssook_BE.diner.service;

import com.stillalive.Ssook_BE.diner.repository.DinerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DinerService {

    private final DinerRepository dinerRepository;

}

package com.stillalive.Ssook_BE.user.service;

import com.stillalive.Ssook_BE.user.repository.ParentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParentService {

    private final ParentRepository parentRepository;

}

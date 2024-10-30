package com.stillalive.Ssook_BE.user.service;

import com.stillalive.Ssook_BE.user.repository.ChildRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChildService {

    private final ChildRepository childRepository;

}

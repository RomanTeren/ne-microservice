package com.ne.microservice.controller;

import com.ne.microservice.domain.ServiceStateResponse;
import com.ne.microservice.service.EmulatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/service/state")
public class ServiceStateResource {

    private final EmulatorService emulatorService;

    @GetMapping("/{userId}")
    public ServiceStateResponse getServiceState(@PathVariable("userId") String userId) {
        log.debug("getServiceState: userId: {}", userId);

        return emulatorService.readServiceStates(userId);
    }
}

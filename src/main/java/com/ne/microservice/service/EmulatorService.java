package com.ne.microservice.service;

import com.ne.microservice.config.ApplicationProperties;
import com.ne.microservice.config.ApplicationProperties.EmulatorProperties;
import com.ne.microservice.domain.ServiceStateResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class EmulatorService {

    private static final String CONNECTED = "connected()";
    private static final Pattern DATA_PATTERN = Pattern.compile("data\\(vod (\\S+), hd (\\S+)\\)");
    private static final String DISCONNECT = "disconnect()";
    private static final String DISCONNECTED = "disconnected()";
    private final EmulatorProperties emulatorProperties;

    public EmulatorService(ApplicationProperties applicationProperties) {
        this.emulatorProperties = applicationProperties.getEmulator();
    }

    public ServiceStateResponse readServiceStates(String userId) {
        log.debug("readServiceStates: userId: {}", userId);

        ServiceStateResponse serviceState = null;

        if (connect(emulatorProperties.getLogin(), emulatorProperties.getPassword())) {
            String message = String.format("read(uid %s)", userId);
            String response = sendMessageToSocket(message);
            log.debug("readServiceStates: response from emulator: {}", response);

            Matcher matcher = DATA_PATTERN.matcher(response);
            if (matcher.find()) {
                boolean vodEnabled = "ON".equals(matcher.group(1));
                boolean hdEnabled = "ON".equals(matcher.group(2));

                serviceState =  new ServiceStateResponse(vodEnabled, hdEnabled);
            }
            disconnect();
        }
        return serviceState;
    }

    private boolean connect(String username, String password) {
        log.debug("connect: username: {}", username);

        String message = String.format("connect(l %s, p %s)", username, password);

        String response = sendMessageToSocket(message);

        return CONNECTED.equals(response);
    }

    private boolean disconnect() {
        log.debug("disconnect...");

        String response = sendMessageToSocket(DISCONNECT);

        return DISCONNECTED.equals(response);
    }

    private String sendMessageToSocket(String message) {
        SocketClient socketClient = new SocketClient();
        return socketClient.sendMessage(message);
    }

}

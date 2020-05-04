package com.ne.microservice.service;

import com.ne.microservice.exception.NEMicroserviceException;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@Slf4j
public class SocketClient {

    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public SocketClient() {
        startConnection("ne-emulator", 5555);
    }

    private void startConnection(String ip, int port) {
        try {
            clientSocket = new Socket(ip, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        } catch (IOException e) {
            e.printStackTrace();
            stopConnection();
        }
    }

    public String sendMessage(String msg) {
        try {
            out.println(msg);
            String resp = in.readLine();
            log.debug("sendMessage: msg: {}, response: {}", msg, resp);
            return resp;
        } catch (IOException e) {
            log.error("sendMessage: ", e);
            stopConnection();
            throw new NEMicroserviceException(e.getMessage());
        }
    }

    public void stopConnection() {
        try {
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

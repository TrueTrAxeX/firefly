package org.firefly.server.main;

import org.firefly.server.net.HttpServer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            HttpServer server = new HttpServer(4253, 10000);
            server.addControllerPath("org.firefly.controllers");

            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

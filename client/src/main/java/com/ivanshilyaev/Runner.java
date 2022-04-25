package com.ivanshilyaev;

import java.io.IOException;

public class Runner {

    public static void main(String[] args) {
        try {
            new ClientService().turnOff();
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
}

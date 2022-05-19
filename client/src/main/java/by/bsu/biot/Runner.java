package by.bsu.biot;

import by.bsu.biot.service.ClientService;

import java.io.IOException;

public class Runner {

    static {
        System.loadLibrary("LibraryNative");
    }

    public static void main(String[] args) throws IOException {
        // new ClientService().turnOff();
    }
}

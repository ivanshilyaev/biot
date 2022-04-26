package com.ivanshilyaev;

import com.sun.jna.Library;
import com.sun.jna.Native;

public class Runner {

    public interface exampleDylib extends Library {
        exampleDylib INSTANCE = Native.load("library", exampleDylib.class);

        void bash_f();
    }

    public static void main(String[] args) {
        exampleDylib.INSTANCE.bash_f();

//        try {
//            new ClientService().turnOn();
//        } catch (IOException | InterruptedException e) {
//            System.out.println(e.getMessage());
//        }
    }
}

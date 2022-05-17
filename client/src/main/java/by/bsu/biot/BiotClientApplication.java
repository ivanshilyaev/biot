package by.bsu.biot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BiotClientApplication {

    static {
        System.loadLibrary("LibraryNative");
    }

    public static void main(String[] args) {
        SpringApplication.run(BiotClientApplication.class, args);
    }
}

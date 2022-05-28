package by.bsu.biot.util;

import by.bsu.biot.dto.EncryptionResult;
import by.bsu.biot.service.ClientService;
import by.bsu.biot.service.EncryptionAndHashService;

import java.io.IOException;
import java.util.Arrays;

import static by.bsu.biot.util.HexEncoder.reverseAndDecode;

public class BenchmarkUtil {

    private final EncryptionAndHashService encryptionAndHashService;

    private final ClientService clientService;

    private final byte[] A = reverseAndDecode("B194BAC80A08F53B366D008E584A5DE4");

    private final byte[] K = reverseAndDecode("5BE3D61217B96181FE6786AD716B890B5CB0C0FF33C356B835C405AED8E07F99");

    private byte[] X;

    public BenchmarkUtil() {
        encryptionAndHashService = new EncryptionAndHashService();
        clientService = new ClientService(encryptionAndHashService);
        int l = 256;
        int d = 1;
        encryptionAndHashService.init(l, d, new byte[0]);
    }

    public void runProtocolBenchmark() throws IOException {
        System.out.println("Protocol execution:");
        System.out.println();
        clientService.init();
        clientService.setEncryptionEnabled(true);
        clientService.sendEncryptionKey();
        long sumTime = turnOnAndTurnOff();
        System.out.println();
        System.out.println("full turnOn-turnOff cycle " + sumTime * 0.000_001 + " ms");
        System.out.println();
        System.out.println("------------------------------------------");
        System.out.println();
    }

    private long turnOnAndTurnOff() throws IOException {
        long start = System.nanoTime();
        clientService.turnOn();
        clientService.turnOff();
        long finish = System.nanoTime();
        return finish - start;
    }

    public void runSingleOperationBenchmark() {
        System.out.println("Single operation:");
        System.out.println();
        for (int i = 24; i <= 192; i += 24) {
            X = HexEncoder.decode(HexEncoder.generateRandomHexString(i * 2));
            long sumTime = thousandTimesSingleOperationInvocation();
            System.out.println("X length = " + i + ", 1000 executions in " + sumTime * 0.000_001 + " ms");
        }
        System.out.println();
        System.out.println("------------------------------------------");
        System.out.println();
    }

    private long thousandTimesSingleOperationInvocation() {
        long sumTime = 0;
        for (int i = 0; i < 1000; ++i) {
            sumTime += encryptAndClock(X);
        }
        return sumTime;
    }

    private long encryptAndClock(byte[] X) {
        long start = System.nanoTime();
        EncryptionResult encryptionResult = encryptionAndHashService.authEncrypt(A, K, X);
        byte[] XDecrypted = encryptionAndHashService.authDecrypt(A, K, encryptionResult.getY(), encryptionResult.getT());
        if (!Arrays.equals(X, XDecrypted)) {
            throw new RuntimeException();
        }
        long finish = System.nanoTime();
        return finish - start;
    }
}

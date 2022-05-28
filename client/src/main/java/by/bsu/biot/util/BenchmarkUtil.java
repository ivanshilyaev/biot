package by.bsu.biot.util;

import by.bsu.biot.dto.EncryptionResult;
import by.bsu.biot.service.EncryptionAndHashService;

import java.util.Arrays;

import static by.bsu.biot.util.HexEncoder.reverseAndDecode;

public class BenchmarkUtil {

    private final EncryptionAndHashService service;

    private final byte[] A = reverseAndDecode("B194BAC80A08F53B366D008E584A5DE4");

    private final byte[] K = reverseAndDecode("5BE3D61217B96181FE6786AD716B890B5CB0C0FF33C356B835C405AED8E07F99");

    private final byte[] X = new byte[192];

    public BenchmarkUtil() {
        service = new EncryptionAndHashService();
        int l = 256;
        int d = 1;
        service.init(l, d, new byte[0]);
    }

    public void runBenchmark() {
        long sumTime = 0;
        for (int i = 0; i < 10; ++i) {
            sumTime += encryptAndClock(X);
        }
        System.out.println(sumTime * 0.0000001 + " ms");
    }

    private long encryptAndClock(byte[] X) {
        long start = System.nanoTime();
        EncryptionResult encryptionResult = service.authEncrypt(A, K, X);
        byte[] XDecrypted = service.authDecrypt(A, K, encryptionResult.getY(), encryptionResult.getT());
        if (!Arrays.equals(X, XDecrypted)) {
            throw new RuntimeException();
        }
        long finish = System.nanoTime();
        return finish - start;
    }
}

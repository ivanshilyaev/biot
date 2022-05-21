package by.bsu.biot.service;

import by.bsu.biot.dto.MachineDataType;
import by.bsu.biot.dto.EncryptionResult;
import by.bsu.biot.library.LibraryNative;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class EncryptionAndHashService {

    private static final int N = 1536;
    private static final int N_BYTES = 192;

    // уровень стойкости
    private int l;

    // ёмкость
    private int d;

    // длина буфера
    private int r;

    // текущее смещение в буфере
    private int pos;

    // состояние автомата
    private byte[] S;

    private byte[] I;

    public void init(int l, int d, byte[] I) {
        this.l = l;
        this.d = d;
        this.I = I;
        S = new byte[N_BYTES];
    }

    private void start(byte[] A, byte[] K) {
        // 1.
        if (K.length != 0) {
            r = N - l - d * l / 2;
        }
        // 2.
        else {
            r = N - 2 * d * l;
        }
        // 3.
        pos = 8 * (1 + A.length + K.length);
        // 4.
        S[0] = (byte) ((8 * A.length / 2 + 8 * K.length / 32) % (Math.pow(2, 8)));
        System.arraycopy(A, 0, S, 1, A.length);
        System.arraycopy(K, 0, S, 1 + A.length, K.length);
        // 5.
        for (int i = pos / 8; i < 1472 / 8; ++i) {
            S[i] = 0;
        }
        // 6.
        S[1472 / 8] = (byte) ((l / 4 + d) % (Math.pow(2, 64)));
        for (int i = 1472 / 8 + 1; i < N_BYTES; ++i) {
            S[i] = 0;
        }
    }

    private void commit(MachineDataType type) {
        // 1.
        S[pos / 8] = (byte) (S[pos / 8] ^ Byte.parseByte(type.code, 2));
        // 2.
        S[r / 8] = (byte) (S[r / 8] ^ 128); // 1000 0000
        // 3.
        S = LibraryNative.bash_f(S);
        // 4.
        pos = 0;
    }

    private void absorb(byte[] X) {
        // 1.
        commit(MachineDataType.DATA);
        // 2.
        byte[][] XSplit = split(X, r);
        // 3.
        for (byte[] Xi : XSplit) {
            // 3.1
            pos = Xi.length * 8;
            // 3.2
            for (int i = 0; i < pos / 8; ++i) {
                S[i] = (byte) (S[i] ^ Xi[i]);
            }
            // 3.3
            if (pos == r) {
                S = LibraryNative.bash_f(S);
                pos = 0;
            }
        }
    }

    private byte[] squeeze(int n) {
        // 1.
        commit(MachineDataType.OUT);
        // 2.
        byte[] Y = new byte[0];
        // 3.
        while (8 * Y.length + r <= n) {
            // 3.1
            Y = ArrayUtils.addAll(Y, ArrayUtils.subarray(S, 0, r / 8));
            // 3.2
            S = LibraryNative.bash_f(S);
        }
        // 4.
        pos = n - 8 * Y.length;
        // 5.
        Y = ArrayUtils.addAll(Y, ArrayUtils.subarray(S, 0, pos / 8));
        // 6.
        return Y;
    }

    private byte[] encrypt(byte[] X) {
        // 1.
        commit(MachineDataType.TEXT);
        // 2.
        byte[][] XSplit = split(X, r);
        // 3.
        byte[] Y = new byte[0];
        // 4.
        for (byte[] Xi : XSplit) {
            // 4.1
            pos = Xi.length * 8;
            // 4.2
            for (int i = 0; i < pos / 8; ++i) {
                S[i] = (byte) (S[i] ^ Xi[i]);
            }
            // 4.3
            Y = ArrayUtils.addAll(Y, ArrayUtils.subarray(S, 0, pos / 8));
            // 4.4
            if (pos == r) {
                S = LibraryNative.bash_f(S);
                pos = 0;
            }
        }
        // 5.
        return Y;
    }

    private byte[] decrypt(byte[] Y) {
        // 1.
        commit(MachineDataType.TEXT);
        // 2.
        byte[][] YSplit = split(Y, r);
        // 3.
        byte[] X = new byte[0];
        // 4.
        for (byte[] Yi : YSplit) {
            // 4.1
            pos = Yi.length * 8;
            // 4.2
            byte[] tempS = ArrayUtils.subarray(S, 0, pos / 8);
            for (int i = 0; i < pos / 8; ++i) {
                tempS[i] = (byte) (tempS[i] ^ Yi[i]);
            }
            X = ArrayUtils.addAll(X, tempS);
            // 4.3
            System.arraycopy(Yi, 0, S, 0, pos / 8);
            // 4.4
            if (pos == r) {
                S = LibraryNative.bash_f(S);
                pos = 0;
            }
        }
        // 5.
        return X;
    }

    /**
     * @param A - анонс
     * @param X - сообщение
     * @param n - длина хэша
     * @return хэш-значение Y длины n
     */
    public byte[] hash(byte[] A, byte[] X, int n) {
        // 1.
        start(A, new byte[0]);
        // 2.
        absorb(X);
        // 3.
        byte[] Y = squeeze(n);
        // 4.
        return Y;
    }

    /**
     * @param A - анонс
     * @param K - ключ
     * @param I - ассоциированные данные
     * @param X - сообщение
     * @return зашифрованное сообщение Y и имитовставка T
     */
    public EncryptionResult authEncrypt(byte[] A, byte[] K, byte[] I, byte[] X) {
        // 1.
        start(A, K);
        // 2.1
        absorb(I);
        // 2.2
        byte[] Y = encrypt(X);
        // 2.3
        byte[] T = squeeze(l);
        // 2.4
        return EncryptionResult.builder()
                .Y(Y)
                .T(T)
                .build();
    }

    public EncryptionResult authEncrypt(byte[] A, byte[] K, byte[] X) {
        // 1.
        start(A, K);
        // 2.1
        absorb(I);
        // 2.2
        byte[] Y = encrypt(X);
        // 2.3
        byte[] T = squeeze(l);
        // 2.4
        return EncryptionResult.builder()
                .Y(Y)
                .T(T)
                .build();
    }

    /**
     * @param A - анонс
     * @param K - ключ
     * @param I - ассоциированные данные
     * @param Y - зашифрованное сообщение
     * @param T - имитовставка
     * @return расшифрованное сообщение X
     */
    public byte[] authDecrypt(byte[] A, byte[] K, byte[] I, byte[] Y, byte[] T) {
        // 1.
        start(A, K);
        // 2.1
        absorb(I);
        // 2.2
        byte[] X = decrypt(Y);
        // 2.3
        if (!Arrays.equals(T, squeeze(l))) {
            return null;
        }
        return X;
    }

    private static byte[][] split(byte[] bytes, int r) {
        if (bytes.length * 8 <= r) {
            return new byte[][]{bytes};
        }
        List<byte[]> result = new ArrayList<>();
        int tempPos = 0;
        while (tempPos * 8 + r < bytes.length * 8) {
            result.add(ArrayUtils.subarray(bytes, tempPos, tempPos + r / 8));
            tempPos += r / 8;
        }
        result.add(ArrayUtils.subarray(bytes, tempPos, bytes.length));

        return result.toArray(new byte[0][]);
    }
}

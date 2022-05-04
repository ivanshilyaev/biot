import dto.EncryptionResult;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AuthenticatedEncryptionService {

    private static final int N = 1536;
    private static final int nBytes = 192;

    // уровень стойкости
    private int l;

    // ёмкость
    private int d;

    // анонс, 16 base
    private byte[] A;

    // ключ, 16 base
    private byte[] K;

    private byte[] S = new byte[nBytes];

    private int r;

    private int pos;

    public void start(int ll, int dd, byte[] Aa, byte[] Kk) {
        l = ll;
        d = dd;
        A = Aa;
        K = Kk;
        // 1.
        if (K != null) {
            r = N - l - d * l / 2;
        }
        // 2.
        else {
            r = N - 2 * d * l;
        }
        // 3.
        pos = 8 * (1 + A.length + K.length);
        // 4.
        int U = (int) ((8 * A.length / 2 + 8 * K.length / 32) % (Math.pow(2, 8)));
        S[0] = (byte) (U);
        System.arraycopy(A, 0, S, 1, A.length);
        System.arraycopy(K, 0, S, 1 + A.length, K.length);
        // 5.
        for (int i = pos / 8; i < 1472 / 8; ++i) {
            S[i] = 0;
        }
        // 6.
        U = (int) ((l / 4 + d) % (Math.pow(2, 64)));
        S[1472 / 8] = (byte) (U);
        for (int i = 1472 / 8 + 1; i < nBytes; ++i) {
            S[i] = 0;
        }
    }

    public void commit(AutomateDataTypes type) {
        // 1.
        S[pos / 8] = (byte) (S[pos / 8] ^ Byte.parseByte(type.code, 2));
        // 2.
        S[r / 8] = (byte) (S[r / 8] ^ 128); // 1000 0000
        // 3.
        S = LibraryNative.bash_f(S);
        // 4.
        pos = 0;
    }

    private byte[][] split(byte[] X) {
        if (X.length * 8 <= r) {
            return new byte[][] {X};
        }
        List<byte[]> result = new ArrayList<>();
        int tempPos = 0;
        while (tempPos * 8 + r < X.length * 8) {
            result.add(ArrayUtils.subarray(X, tempPos, tempPos + r / 8));
            tempPos += r / 8;
        }
        result.add(ArrayUtils.subarray(X, tempPos, X.length));

        return result.toArray(new byte[0][]);
    }

    public void absorb(byte[] X) {
        // 1.
        commit(AutomateDataTypes.DATA);
        // 2.
        byte[][] XSplit = split(X);
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

    public byte[] squeeze(int n) {
        // 1.
        commit(AutomateDataTypes.OUT);
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

    public byte[] encrypt(byte[] X) {
        // 1.
        commit(AutomateDataTypes.TEXT);
        // 2.
        byte[][] XSplit = split(X);
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

    public byte[] decrypt(byte[] Y) {
        // 1.
        commit(AutomateDataTypes.TEXT);
        // 2.
        byte[][] YSplit = split(Y);
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

    public EncryptionResult authEncrypt(int l, int d, byte[] A, byte[] K, byte[] I, byte[] X) {
        // 1.
        start(l, d, A, K);
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

    public byte[] authDecrypt(int l, int d, byte[] A, byte[] K, byte[] I, byte[] Y, byte[] T) {
        // 1.
        start(l, d, A, K);
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
}

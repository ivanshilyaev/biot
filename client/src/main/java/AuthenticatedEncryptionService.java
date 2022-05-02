import utils.HexEncoder;

public class AuthenticatedEncryptionService {

    private static final int N = 1536;
    private static final int nBytes = 192;

    // уровень стойкости
    private static int l;

    // ёмкость
    private static int d;

    // анонс, 16 base
    private static byte[] A;

    // ключ, 16 base
    private static byte[] K;

    private static byte[] S = new byte[nBytes];

    private static int r;

    private static int pos;

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
        U = ~U & 0xff; // ?
        S[0] = (byte) (U - 128);
        System.arraycopy(A, 0, S, 1, A.length);
        System.arraycopy(K, 0, S, 1 + A.length, K.length);
        // 5.
        for (int i = pos / 8; i < 1472 / 8; ++i) {
            S[i] = 0;
        }
        // 6.
        U = (int) ((l / 4 + d) % (Math.pow(2, 64)));
        U = ~U & 0xff; // ?
        for (int i = 1472 / 8; i < nBytes - 1; ++i) {
            S[i] = 0;
        }
        S[nBytes - 1] = (byte) (U - 128);
    }

    public void commit(String t) {
        // 1.
        S[pos / 8] = (byte) (S[pos / 8] ^ Byte.parseByte(t + "01", 2));
        // 2.
        byte temp = S[r / 8];
        if (temp % 2 == 0) {
            S[r / 8]++;
        } else {
            S[r / 8]--;
        }
        // 3.
        S = LibraryNative.bash_f(S);
        // 4.
        pos = 0;
    }

    public void absorb(byte[] X) {
        // 1.
        commit(AutomateDataTypes.DATA.code);
        // 2-3.
        if (X.length <= r / 8) {
            pos = 8 * X.length;
            for (int i = 0; i < pos / 8; ++i) {
                S[i] = (byte) (S[i] ^ X[i]);
            }
        }
        else {
            // Только в случае, если входное слово по длине больше 168 байт
            // Имплементировать позже
        }
    }

    // на вход подаётся длина в байтах
    public byte[] squeeze(int n) {
        // 1.
        commit(AutomateDataTypes.OUT.code);
        // 2.
        byte[] Y = new byte[n];
        // 3.
        // Только в случае, если выходное слово по длине больше 168 байт
        // Имплементировать позже
        // 4.
        pos = n * 8;
        // 5.
        System.arraycopy(S, 0, Y, 0, n);
        // 6.
        return Y;
    }

    public byte[] encrypt(byte[] X) {
        // 1.
        commit(AutomateDataTypes.TEXT.code);
        // 3.
        byte[] Y = new byte[X.length];
        // 2-4.
        if (X.length <= r) {
            pos = 8 * X.length;
            for (int i = 0; i < pos / 8; ++i) {
                S[i] = (byte) (S[i] ^ X[i]);
            }
            System.arraycopy(S, 0, Y, 0, X.length);
        }
        else {
            // Только в случае, если входное слово по длине больше 168 байт
            // Имплементировать позже
        }
        // 5.
        return Y;
    }

    public byte[] decrypt(byte[] Y) {
        // 1.
        commit(AutomateDataTypes.TEXT.code);
        // 3.
        byte[] X = new byte[Y.length];
        // 2-4.
        if (Y.length <= r) {
            pos = 8 * Y.length;
            for (int i = 0; i < pos / 8; ++i) {
                X[i] = (byte) (S[i] ^ Y[i]);
                S[i] = Y[i];
            }
        }
        else {
            // Только в случае, если входное слово по длине больше 168 байт
            // Имплементировать позже
        }
        // 5.
        return X;
    }

    public byte[] authEncrypt(int l, int d, byte[] A, byte[] K, byte[] I, byte[] X) {
        // 1.
        start(l, d, A, K);
        // 2.
        absorb(I);
        return encrypt(X);
    }

    public byte[] authDecrypt(int l, int d, byte[] A, byte[] K, byte[] I, byte[] Y) {
        // 1.
        start(l, d, A, K);
        // 2.
        absorb(I);
        return decrypt(Y);
    }
}

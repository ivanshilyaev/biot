import utils.HexEncoder;

public class AuthenticatedEncryptionService {

    private static final int N = 1536;
    private static final int nBytes = 192;

    // уровень стойкости
    private static final int l = 128;

    // ёмкость
    private static final int d = 1;

    // анонс, 16 base
    private static final byte[] A = HexEncoder.decode("1111111111111111");

    // ключ, 16 base
    private static final byte[] K = HexEncoder.decode("12345678123456781234567812345678");

    private static byte[] S = new byte[nBytes];

    private static int r;

    private static int pos;

    public void start() {
        // 1.
        if (K != null) {
            r = (int) (N - l - 0.5 * d * l);
        }
        // 2.
        else {
            r = N - 2 * d * l;
        }
        // 3.
        pos = 8 * (1 + A.length + K.length);
        // 4.
        int U = (int) ((8 * A.length / 2 + 8 * K.length / 32) % (Math.pow(2, 8)));
        S[0] = (byte) (U - 128);
        System.arraycopy(A, 0, S, 1, A.length);
        System.arraycopy(K, 0, S, 1 + A.length, K.length);
        // 5.
        for (int i = pos / 8; i < 1472 / 8; ++i) {
            S[i] = 0;
        }
        // 6.
        U = (int) ((l / 4 + d) % (Math.pow(2, 64)));
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
}

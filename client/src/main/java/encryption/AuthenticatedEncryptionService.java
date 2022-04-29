package encryption;

import utils.HexEncoder;

import java.util.BitSet;

public class AuthenticatedEncryptionService {

    private static final int N = 1536;

    // уровень стойкости
    private static final int l = 128;

    // ёмкость
    private static final int d = 1;

    // анонс, 16 base
    private static final BitSet A = BitSet.valueOf(HexEncoder.decode("11111111"));

    // ключ, 16 base
    private static final BitSet K = BitSet.valueOf(HexEncoder.decode("12345678123456781234567812345678"));

    private static BitSet S = new BitSet(N);

    private static int r;

    private static int pos;

    public void start() {
        // 1.
        if (K != null) {
            r = (int) (1536 - l - 0.5 * d * l);
        }
        // 2.
        else {
            r = 1536 - 2 * d * l;
        }
        // 3. pos <- 8 + |ann| + |key|
        pos = 8 + A.length() + 4 * K.length();
        // 4. s[0..pos) <- <|ann|/2 + |key|/32>_8 || ann || key
        int U = (int) ((A.length() / 2 + K.length() / 32) % (Math.pow(2, 8)));
        String temp = String.format("%8s", Integer.toBinaryString(U)).replaceAll(" ", "0");
        for (int i = 0; i < temp.length(); ++i) {
            S.set(i, temp.charAt(i) == '1');
        }
        for (int i = 0; i < A.length(); ++i) {
            S.set(8 + i, A.get(i));
        }
        for (int i = 0; i < K.length(); ++i) {
            S.set(8 + A.length() + i, K.get(i));
        }
        // 5.
    }
}

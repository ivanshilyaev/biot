import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.ByteArrayUtils.concatenate;
import static utils.HexEncoder.swapAndDecode;

public class AuthEncryptionTest {
    @BeforeAll
    public static void init() {
        System.loadLibrary("LibraryNative");
    }

    @Test
    public void authEncryption() {
        int l = 256;
        int d = 1;
        byte[] A = concatenate(
                swapAndDecode("B194BAC80A08F53B"),
                swapAndDecode("366D008E584A5DE4")
        );
        byte[] K = concatenate(
                swapAndDecode("5BE3D61217B96181"),
                swapAndDecode("FE6786AD716B890B"),
                swapAndDecode("5CB0C0FF33C356B8"),
                swapAndDecode("35C405AED8E07F99")
        );
        String xString = "on";
        byte[] X = xString.getBytes(StandardCharsets.UTF_8);
        byte[] I = concatenate(
                swapAndDecode("E12BDC1AE28257EC"),
                swapAndDecode("703FCCF095EE8DF1"),
                swapAndDecode("C1AB76389FE678CA"),
                swapAndDecode("F7C6F860D5BB9C4F"),
                swapAndDecode("F33C657B637C306A"),
                swapAndDecode("DD4EA7799EB23D31"),
                swapAndDecode("3E")
        );

        AuthenticatedEncryptionService service = new AuthenticatedEncryptionService();
        byte[] Y = service.authEncrypt(l, d, A, K, I, X);
        byte[] resultX = service.authDecrypt(l, d, A, K, I, Y);
        String resultXString = new String(resultX);
        System.out.println(resultXString);

        assertEquals(xString, resultXString);
    }
}

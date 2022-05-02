import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import utils.HexEncoder;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AuthEncryptionTest {
    @BeforeAll
    public static void init() {
        System.loadLibrary("LibraryNative");
    }

    @Test
    public void authEncryption() {
        int l = 256;
        int d = 1;
        byte[] A = HexEncoder.decode("B194BAC80A08F53B366D008E584A5DE4");
        byte[] K = HexEncoder.decode("5BE3D61217B96181FE6786AD716B890B5CB0C0FF33C356B835C405AED8E07F99");
        String xString = "on";
        byte[] X = xString.getBytes(StandardCharsets.UTF_8);
        byte[] I = HexEncoder.decode("E12BDC1AE28257EC703FCCF095EE8DF1C1AB76389FE678CAF7C6F860D5BB9C4F" +
                "F33C657B637C306ADD4EA7799EB23D313E");

        AuthenticatedEncryptionService service = new AuthenticatedEncryptionService();
        byte[] Y = service.authEncrypt(l, d, A, K, I, X);
        byte[] resultX = service.authDecrypt(l, d, A, K, I, Y);
        String resultXString = new String(resultX);
        System.out.println(resultXString);

        assertEquals(xString, resultXString);
    }
}

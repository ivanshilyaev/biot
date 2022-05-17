package by.bsu.biot;

import by.bsu.biot.service.AuthenticatedEncryptionService;
import by.bsu.biot.dto.EncryptionResult;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static by.bsu.biot.util.HexEncoder.encode;
import static by.bsu.biot.util.HexEncoder.reverseAndDecode;

public class AuthEncryptionTest {
    @BeforeAll
    public static void init() {
        System.loadLibrary("LibraryNative");
    }

    // А.6 Аутентифицированное шифрование
    @Test
    public void authEncryptionAndDecryption() {
        int l = 256;
        int d = 1;
        byte[] A = reverseAndDecode("B194BAC80A08F53B366D008E584A5DE4");
        byte[] K = reverseAndDecode("5BE3D61217B96181FE6786AD716B890B5CB0C0FF33C356B835C405AED8E07F99");
        byte[] X = new byte[192];
        byte[] I = reverseAndDecode("E12BDC1AE28257EC703FCCF095EE8DF1C1AB76389FE678CAF7C6F860D5BB9C4F" +
                "F33C657B637C306ADD4EA7799EB23D313E");

        String expectedY = "690673766C3E848CAC7C05169FFB7B7751E52A011040E5602573FAF991044A00" +
                "4329EEF7BED8E6875830A91854D1BD2EDC6FC2FF37851DBAC249DF400A0549EA" +
                "2E0C811D499E1FF1E5E32FAE7F0532FA4051D0F9E300D9B1DBF119AC8CFFC48D" +
                "D3CBF1CA0DBA5DD97481C88DF0BE412785E40988B31585537948B80F5A9C49E0" +
                "8DD684A7DCA871C380DFDC4C4DFBE61F50D2D0FBD24D8B9D32974A347247D001" +
                "BAD5B168440025693967E77394DC088B0ECCFA8D291BA13D44F60B06E2EDB351";
        String expectedT = "CDE5AF6EF9A14B7D0C191B869A6343ED6A4E9AAB4EE00A579E9E682D0EC051E3";

        AuthenticatedEncryptionService service = new AuthenticatedEncryptionService(l, d);

        // шифрование
        EncryptionResult encryptionResult = service.authEncrypt(A, K, I, X);
        String outputY = encode(encryptionResult.getY());
        String outputT = encode(encryptionResult.getT());
        assertEquals(expectedY, outputY);
        assertEquals(expectedT, outputT);

        // расшифрование
        byte[] XDecrypted = service.authDecrypt(A, K, I, encryptionResult.getY(), encryptionResult.getT());
        assertArrayEquals(X, XDecrypted);
    }

    @Test
    public void authEncryptionWithRandomData() {
        int l = 256;
        int d = 1;
        byte[] A = reverseAndDecode("B194BAC80A08F53B366D008E584A5DE4");
        byte[] K = reverseAndDecode("5BE3D61217B96181FE6786AD716B890B5CB0C0FF33C356B835C405AED8E07F99");
        String xString = "off";
        byte[] X = xString.getBytes(StandardCharsets.UTF_8);
        byte[] I = reverseAndDecode("E12BDC1AE28257EC703FCCF095EE8DF1C1AB76389FE678CAF7C6F860D5BB9C4F" +
                "F33C657B637C306ADD4EA7799EB23D313E");

        AuthenticatedEncryptionService service = new AuthenticatedEncryptionService(l, d);
        EncryptionResult encryptionResult = service.authEncrypt(A, K, I, X);

        //
        System.out.println(encode(xString.getBytes()));
        String yString = encode(encryptionResult.getY());
        System.out.println(yString);
        String tString = encode(encryptionResult.getT());
        System.out.println(tString);
        //

        byte[] resultX = service.authDecrypt(A, K, I, encryptionResult.getY(), encryptionResult.getT());
        String resultXString = new String(resultX);

        assertEquals(xString, resultXString);
    }
}

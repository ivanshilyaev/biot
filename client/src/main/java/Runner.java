import dto.EncryptionResult;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static utils.ByteArrayUtils.concatenate;
import static utils.HexEncoder.*;

public class Runner {

    static {
        System.loadLibrary("LibraryNative");
    }

    public static void main(String[] args) throws IOException {
        int l = 256;
        int d = 1;
        byte[] A = concatenate(
                reverseAndDecode("B194BAC80A08F53B"),
                reverseAndDecode("366D008E584A5DE4")
        );
        byte[] K = concatenate(
                reverseAndDecode("5BE3D61217B96181"),
                reverseAndDecode("FE6786AD716B890B"),
                reverseAndDecode("5CB0C0FF33C356B8"),
                reverseAndDecode("35C405AED8E07F99")
        );
        String xString = "onn";
        byte[] X = xString.getBytes(StandardCharsets.UTF_8);
        byte[] I = concatenate(
                reverseAndDecode("E12BDC1AE28257EC"),
                reverseAndDecode("703FCCF095EE8DF1"),
                reverseAndDecode("C1AB76389FE678CA"),
                reverseAndDecode("F7C6F860D5BB9C4F"),
                reverseAndDecode("F33C657B637C306A"),
                reverseAndDecode("DD4EA7799EB23D31"),
                reverseAndDecode("3E")
        );

        AuthenticatedEncryptionService service = new AuthenticatedEncryptionService(l, d);
        EncryptionResult encryptionResult = service.authEncrypt(A, K, I, X);

        new ClientService().switchLamp(
                new String(Base64.getEncoder().encode(encode(encryptionResult.getY()).getBytes())),
                new String(Base64.getEncoder().encode(encode(encryptionResult.getT()).getBytes()))
        );
    }
}

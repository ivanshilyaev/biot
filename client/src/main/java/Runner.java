import dto.EncryptionResult;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static utils.HexEncoder.*;

public class Runner {

    static {
        System.loadLibrary("LibraryNative");
    }

    public static void main(String[] args) throws IOException {
        int l = 256;
        int d = 1;
        byte[] A = new byte[16];
        byte[] K = reverseAndDecode("5BE3D61217B96181FE6786AD716B890B5CB0C0FF33C356B835C405AED8E07F99");
        String xString = "off";
        byte[] X = xString.getBytes(StandardCharsets.UTF_8);
        byte[] I = new byte[49];

        AuthenticatedEncryptionService service = new AuthenticatedEncryptionService(l, d);
        EncryptionResult encryptionResult = service.authEncrypt(A, K, I, X);

        new ClientService().switchLamp(
                new String(Base64.getEncoder().encode(encode(encryptionResult.getY()).getBytes())),
                new String(Base64.getEncoder().encode(encode(encryptionResult.getT()).getBytes()))
        );
    }
}

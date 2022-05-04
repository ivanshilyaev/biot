import static utils.ByteArrayUtils.*;
import static utils.HexEncoder.*;

public class Runner {

    static {
        System.loadLibrary("LibraryNative");
    }

    public static void main(String[] args) {
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
        byte[] X = new byte[192];
        byte[] I = concatenate(
                swapAndDecode("E12BDC1AE28257EC"),
                swapAndDecode("703FCCF095EE8DF1"),
                swapAndDecode("C1AB76389FE678CA"),
                swapAndDecode("F7C6F860D5BB9C4F"),
                swapAndDecode("F33C657B637C306A"),
                swapAndDecode("DD4EA7799EB23D31"),
                //swapAndDecode("3E00000000000000")
                swapAndDecode("3E")
        );

        AuthenticatedEncryptionService service = new AuthenticatedEncryptionService();
        byte[] Y = service.authEncrypt(l, d, A, K, I, X);
        String yString = encode(Y);

        for (int i = 0; i < 384; i += 16) {
            System.out.print(yString.substring(i, i + 16) + " ");
            if ((i + 16) % 64 == 0) {
                System.out.println();
            }
        }
    }
}

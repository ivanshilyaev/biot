import utils.HexEncoder;

public class Runner {

    static {
        System.loadLibrary("LibraryNative");
    }

    public static void main(String[] args) {
        int l = 256;
        int d = 1;
        byte[] A = HexEncoder.decode("B194BAC80A08F53B366D008E584A5DE4");
        byte[] K = HexEncoder.decode("5BE3D61217B96181FE6786AD716B890B5CB0C0FF33C356B835C405AED8E07F99");
        byte[] X = new byte[192];
        byte[] I = HexEncoder.decode("E12BDC1AE28257EC703FCCF095EE8DF1C1AB76389FE678CAF7C6F860D5BB9C4F" +
                "F33C657B637C306ADD4EA7799EB23D313E");

        AuthenticatedEncryptionService service = new AuthenticatedEncryptionService();
        byte[] Y = service.authEncrypt(l, d, A, K, I, X);
        String yString = HexEncoder.encode(Y);

        for (int i = 0; i < 384; i+=16) {
            System.out.print(yString.substring(i, i+16) + " ");
            if ((i+16) % 64 == 0) {
                System.out.println();
            }
        }
    }
}

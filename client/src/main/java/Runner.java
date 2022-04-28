import java.util.Arrays;

public class Runner {

    static {
        System.loadLibrary("LibraryNative");
    }

    private final static char[] HEX = new char[] {
        '0', '1', '2', '3', '4', '5', '6', '7',
        '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };

    /**
     * Convert bytes to a base16 string.
     */
    public static String encode(byte[] byteArray) {
        StringBuffer hexBuffer = new StringBuffer(byteArray.length * 2);
        for (int i = 0; i < byteArray.length; i++)
            for (int j = 1; j >= 0; j--)
                hexBuffer.append(HEX[(byteArray[i] >> (j * 4)) & 0xF]);
        return hexBuffer.toString();
    }

    /**
     * Convert a base16 string into a byte array.
     */
    public static byte[] decode(String s) {
        int len = s.length();
        byte[] r = new byte[len / 2];
        for (int i = 0; i < r.length; i++) {
            int digit1 = s.charAt(i * 2), digit2 = s.charAt(i * 2 + 1);
            if (digit1 >= '0' && digit1 <= '9')
                digit1 -= '0';
            else if (digit1 >= 'A' && digit1 <= 'F')
                digit1 -= 'A' - 10;
            if (digit2 >= '0' && digit2 <= '9')
                digit2 -= '0';
            else if (digit2 >= 'A' && digit2 <= 'F')
                digit2 -= 'A' - 10;

            r[i] = (byte) ((digit1 << 4) + digit2);
        }
        return r;
    }

    public static void main(String[] args) {
        String input =
                "B194BAC80A08F53B366D008E584A5DE48504FA9D1BB6C7AC252E72C202FDCE0D" +
                "5BE3D61217B96181FE6786AD716B890B5CB0C0FF33C356B835C405AED8E07F99" +
                "E12BDC1AE28257EC703FCCF095EE8DF1C1AB76389FE678CAF7C6F860D5BB9C4F" +
                "F33C657B637C306ADD4EA7799EB23D313E98B56E27D3BCCF591E181F4C5AB793" +
                "E9DEE72C8F0C0FA62DDB49F46F73964706075316ED247A3739CBA38303A98BF6" +
                "92BD9B1CE5D141015445FBC95E4D0EF2682080AA227D642F2687F934904055111";
        byte[] array = decode(input);
        byte[] result = LibraryNative.bash_f(array);
        String output = encode(result);

        for (int i = 0; i < 384; i+=16) {
            System.out.print(output.substring(i, i+16) + " ");
            if ((i+16) % 64 == 0) {
                System.out.println();
            }
        }
    }
}

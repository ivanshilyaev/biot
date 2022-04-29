import utils.HexEncoder;

public class Runner {

    static {
        System.loadLibrary("LibraryNative");
    }

    public static void main(String[] args) {
        String input =
                "B194BAC80A08F53B366D008E584A5DE48504FA9D1BB6C7AC252E72C202FDCE0D" +
                "5BE3D61217B96181FE6786AD716B890B5CB0C0FF33C356B835C405AED8E07F99" +
                "E12BDC1AE28257EC703FCCF095EE8DF1C1AB76389FE678CAF7C6F860D5BB9C4F" +
                "F33C657B637C306ADD4EA7799EB23D313E98B56E27D3BCCF591E181F4C5AB793" +
                "E9DEE72C8F0C0FA62DDB49F46F73964706075316ED247A3739CBA38303A98BF6" +
                "92BD9B1CE5D141015445FBC95E4D0EF2682080AA227D642F2687F934904055111";
        byte[] array = HexEncoder.decode(input);
        byte[] result = LibraryNative.bash_f(array);
        String output = HexEncoder.encode(result);

        for (int i = 0; i < 384; i+=16) {
            System.out.print(output.substring(i, i+16) + " ");
            if ((i+16) % 64 == 0) {
                System.out.println();
            }
        }
    }
}

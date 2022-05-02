import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import utils.HexEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BashFTest {

    @BeforeAll
    public static void init() {
        System.loadLibrary("LibraryNative");
    }

    @Test
    public void bashF() {
        String input =
                "B194BAC80A08F53B366D008E584A5DE48504FA9D1BB6C7AC252E72C202FDCE0D" +
                        "5BE3D61217B96181FE6786AD716B890B5CB0C0FF33C356B835C405AED8E07F99" +
                        "E12BDC1AE28257EC703FCCF095EE8DF1C1AB76389FE678CAF7C6F860D5BB9C4F" +
                        "F33C657B637C306ADD4EA7799EB23D313E98B56E27D3BCCF591E181F4C5AB793" +
                        "E9DEE72C8F0C0FA62DDB49F46F73964706075316ED247A3739CBA38303A98BF6" +
                        "92BD9B1CE5D141015445FBC95E4D0EF2682080AA227D642F2687F934904055111";
        String output = "8FE727775EA7F140B95BB6A200CBB28C7F0809C0C0BC68B7DC5AEDC841BD94E4" +
                "03630C301FC255DF5B67DB53EF65E376E8A4D797A6172F2271BA48093173D329" +
                "C3502AC946767326A2891971392D3F7089959F5D61621238655975E00E2132A0" +
                "D5018CEEDB17731CCD88FC50151D37C0D4A3359506AEDC2E6109511E7703AFBB" +
                "014642348D8568AA1A5D9868C4C7E6DFA756B1690C7C2608A2DC136F5997AB8F" +
                "BB3F4D9F033C87CA6070E117F099C4094972ACD9D976214B7CED8E3F8B6E058E";

        byte[] array = HexEncoder.decode(input);
        byte[] result = LibraryNative.bash_f(array);
        String resultString = HexEncoder.encode(result);

        assertEquals(output, resultString);
    }
}

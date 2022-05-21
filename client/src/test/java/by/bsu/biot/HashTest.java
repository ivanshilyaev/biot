package by.bsu.biot;

import by.bsu.biot.service.EncryptionAndHashService;
import by.bsu.biot.util.HexEncoder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HashTest {

    @BeforeAll
    public static void init() {
        System.loadLibrary("LibraryNative");
    }

    // А.5 Хэширование (программируемые алгоритмы)
    @Test
    public void hashTestOne() {
        int l = 128;
        int d = 2;
        int n = 256;
        byte[] A = new byte[0];
        String xString = "B194BAC80A08F53B366D008E584A5DE48504FA9D1BB6C7AC252E72C202FDCE0D" +
                "5BE3D61217B96181FE6786AD716B890B5CB0C0FF33C356B835C405AED8E07F99" +
                "E12BDC1AE28257EC703FCCF095EE8DF1C1AB76389FE678CAF7C6F860D5BB9C4F" +
                "F33C657B637C306ADD4EA7799EB23D313E98B56E27D3BCCF591E181F4C5AB793" +
                "E9DEE72C8F0C0FA62DDB49F46F73964706075316ED247A3739CBA38303A98BF6" +
                "92BD9B1CE5D141015445FBC95E4D0EF2682080AA227D642F2687F934904055111";
        EncryptionAndHashService service = new EncryptionAndHashService();
        service.init(l, d, new byte[0]);
        byte[] X;
        byte[] Y;

        // m = 0;
        X = new byte[0];
        Y = service.hash(A, X, n);
        assertEquals("36FA075EC15721F250B9A641A8CB99A333A9EE7BA8586D0646CBAC3686C03DF3",
                HexEncoder.encode(Y));
        // m = 127
        X = HexEncoder.decode(xString.substring(0, 254));
        Y = service.hash(A, X, n);
        assertEquals("C930FF427307420DA6E4182969AA1FFC3310179B8A0EDB3E20BEC285B568BA17",
                HexEncoder.encode(Y));
        // m = 128
        X = HexEncoder.decode(xString.substring(0, 256));
        Y = service.hash(A, X, n);
        assertEquals("92AD1402C2007191F2F7CFAD6A2F8807BB0C50F73DFF95EF1B8AF08504D54007",
                HexEncoder.encode(Y));
        // m = 150
        X = HexEncoder.decode(xString.substring(0, 300));
        Y = service.hash(A, X, n);
        assertEquals("48DB61832CA1009003BC0D8BDE67893A9DC683C48A5BC23AC884EB4613B480A6",
                HexEncoder.encode(Y));
    }

    @Test
    public void hashTestTwo() {
        int l = 192;
        int d = 1;
        int n = 384;
        byte[] A = new byte[0];
        String xString = "B194BAC80A08F53B366D008E584A5DE48504FA9D1BB6C7AC252E72C202FDCE0D" +
                "5BE3D61217B96181FE6786AD716B890B5CB0C0FF33C356B835C405AED8E07F99" +
                "E12BDC1AE28257EC703FCCF095EE8DF1C1AB76389FE678CAF7C6F860D5BB9C4F" +
                "F33C657B637C306ADD4EA7799EB23D313E98B56E27D3BCCF591E181F4C5AB793" +
                "E9DEE72C8F0C0FA62DDB49F46F73964706075316ED247A3739CBA38303A98BF6" +
                "92BD9B1CE5D141015445FBC95E4D0EF2682080AA227D642F2687F934904055111";
        EncryptionAndHashService service = new EncryptionAndHashService();
        service.init(l, d, new byte[0]);
        byte[] X;
        byte[] Y;

        // m = 143;
        X = HexEncoder.decode(xString.substring(0, 286));
        Y = service.hash(A, X, n);
        assertEquals("6166032D6713D401A6BC687CCFFF2E603287143A84C78D2C62C71551E0E2FB2A" +
                        "F6B799EE33B5DECD7F62F190B1FBB052",
                HexEncoder.encode(Y));
        // m = 144
        X = HexEncoder.decode(xString.substring(0, 288));
        Y = service.hash(A, X, n);
        assertEquals("8D84C82ECD0AB6468CC451CFC5EEB3B298DFD381D200DA69FBED5AE67D26BAD5" +
                        "C727E2652A225BF465993043039E338B",
                HexEncoder.encode(Y));
        // m = 150
        X = HexEncoder.decode(xString.substring(0, 300));
        Y = service.hash(A, X, n);
        assertEquals("47529F9D499AB6AB8AD72B1754C90C39E7DA237BEB16CDFC00FE87934F5AFC11" +
                        "01862DFA50560F062A4DAC859CC13DBC",
                HexEncoder.encode(Y));
    }
}

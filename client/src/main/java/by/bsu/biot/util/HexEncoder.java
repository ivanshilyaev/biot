package by.bsu.biot.util;

import org.apache.commons.lang3.ArrayUtils;

import java.security.SecureRandom;

public class HexEncoder {

    private final static char[] HEX = new char[]{
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };

    /**
     * Convert a byte array into a base16 string
     */
    public static String encode(byte[] byteArray) {
        StringBuilder hexBuffer = new StringBuilder(byteArray.length * 2);
        for (byte b : byteArray)
            for (int j = 1; j >= 0; j--)
                hexBuffer.append(HEX[(b >> (j * 4)) & 0xF]);
        return hexBuffer.toString();
    }

    /**
     * Convert a base16 string into a byte array
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

    /**
     * Reverse string by 8-bit blocks and decode
     */
    public static byte[] reverseAndDecode(String s) {
        StringBuilder eightBitReversed = new StringBuilder();
        for (int i = s.length() - 1; i >= 0; i -= 2) {
            eightBitReversed.append(s, i - 1, i + 1);
        }
        byte[] result = decode(eightBitReversed.toString());
        ArrayUtils.reverse(result);

        return result;
    }

    public static String generateRandomHexString(int n) {
        SecureRandom random = new SecureRandom();
        StringBuilder builder = new StringBuilder();
        while (builder.length() < n) {
            builder.append(Integer.toHexString(random.nextInt()));
        }

        return builder.substring(0, n).toUpperCase();
    }
}

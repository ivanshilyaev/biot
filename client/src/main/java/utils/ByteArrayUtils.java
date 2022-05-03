package utils;

import java.util.Arrays;

public class ByteArrayUtils {

    public static byte[] concatenate(byte[]... arrays) {
        int finalLength = 0;
        for (byte[] array: arrays) {
            finalLength += array.length;
        }

        byte[] dest = null;
        int destPos = 0;

        for (byte[] array: arrays)
        {
            if (dest == null)
            {
                dest = Arrays.copyOf(array, finalLength);
                destPos = array.length;
            }
            else {
                System.arraycopy(array, 0, dest, destPos, array.length);
                destPos += array.length;
            }
        }

        return dest;
    }
}

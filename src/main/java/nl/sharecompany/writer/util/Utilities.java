package nl.sharecompany.writer.util;

import java.nio.ByteBuffer;

public class Utilities {
    private final static int [] powerOf10Table = {1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000} ;
    public static long getTimestampAsMilliseconds(ByteBuffer value) {
        int len = value.remaining();
        if (len == 0) {
            return 0;
        }

        int i = 0;

        long n =0;
        boolean commaFound = false;
        int decimals = 0;
        while(i <  len) {
            if(value.get(i) == 46) { //found decimal point
                commaFound = true;
                i++;
                continue;
            }
            if(commaFound) {
                decimals++;
            }

            n = 10 * n + (value.get(i) - 48);
            i++;
        }

        if(decimals < 3) {
            n = n * powerOf10Table[(3 - decimals)];
        }
        else if (decimals > 3) {
            n  = n / powerOf10Table[(decimals - 3)];
        }

        return n;
    }
}

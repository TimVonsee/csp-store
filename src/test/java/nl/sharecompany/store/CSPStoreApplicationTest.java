package nl.sharecompany.store;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Unit test for simple CSPStoreApplication.
 */
public class CSPStoreApplicationTest
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd-hh:mm:ss");

        // 1523363667.14838
        System.out.println("Time "+sdf.format(new Date(1523363667148L)));


        Iterator<Object[]> rows = new Iterator<Object[]>() {
            int i = 0;
            final Random random = new Random();

            @Override
            public boolean hasNext() {
                return i!=1000000;
            }

            @Override
            public Object[] next() {
                i++;
                return new Object[]{i, String.valueOf(random.nextLong())};
            }
        };

        Object[] r = rows.next();

        System.out.println(r[0] + " " + r[1]);



    }

}

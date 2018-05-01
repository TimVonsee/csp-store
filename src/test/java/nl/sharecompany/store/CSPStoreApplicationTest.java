package nl.sharecompany.store;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

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
    }
}

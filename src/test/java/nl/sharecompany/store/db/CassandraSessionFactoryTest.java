package nl.sharecompany.store.db;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import org.junit.Test;


public class CassandraSessionFactoryTest {

    @Test
    public void connectionTest() throws Exception {
        CassandraFactory client = new CassandraFactory();
        Cluster c = client.build();

        try (Session s = c.connect("fix_db")){
            ResultSet rs = s.execute("SELECT symbol, minute, ts, fix, day FROM raw_fix_by_minute");

            System.out.printf("%-10s\t%-30s\t%-30s%n", "Symbol", "Minute", "FIX");
            for (Row r : rs) {
                System.out.printf("%-10s\t%-30s\t%-30s%n",
                        r.getString("symbol"),
                        r.getTimestamp("minute"),
                        r.getString("fix"));
            }
        }
    }
}
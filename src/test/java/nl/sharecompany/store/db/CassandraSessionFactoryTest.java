package nl.sharecompany.store.db;

import com.datastax.driver.core.*;
import com.datastax.driver.core.utils.UUIDs;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;


public class CassandraSessionFactoryTest {

    @Test
    public void connectionTest() throws Exception {
        CassandraFactory client = new CassandraFactory();
        Cluster c = client.build();

        try (Session s = c.connect("fix_db")){
           // ResultSet rs = s.execute("SELECT symbol, minute, ts, fix, day FROM raw_fix_by_minute");
//
//            System.out.printf("%-10s\t%-30s\t%-30s%n", "Symbol", "Minute", "FIX");
//            for (Row r : rs) {
//                System.out.printf("%-10s\t%-30s\t%-30s%n",
//                        r.getString("symbol"),
//                        r.getTimestamp("minute"),
//                        r.getString("fix"));
//

            final PreparedStatement statement = s.prepare("INSERT INTO bids_by_day (src_id, symbol, day, uts, bid_ts, bid_price, bid_size) VALUES (?, ?, ?, ?, ?, ?, ?);");

            UUID timeuuid = UUIDs.timeBased();

            Calendar now = Calendar.getInstance();
            now.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date tmp = now.getTime();
            now.set(Calendar.HOUR_OF_DAY, 0);
            now.set(Calendar.MINUTE, 0);
            now.set(Calendar.SECOND, 0);
            now.set(Calendar.MILLISECOND, 0);

            Date day = now.getTime();

            System.out.println(day);
            System.out.println(tmp);


            Object[] objs = {"ID", "SYM", day, timeuuid, tmp, "PRICE", "SIZE"};
            BoundStatement bound = statement.bind(objs);
            s.execute(bound);
        }
    }

    @Test
    public void test(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar now = Calendar.getInstance();
        System.out.println(sdf.format(now.getTime()));
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.HOUR_OF_DAY, 0);
        System.out.println(sdf.format(now.getTime()));
        System.out.println(now.getTime());

    }
}
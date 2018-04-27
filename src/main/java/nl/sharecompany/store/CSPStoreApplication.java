package nl.sharecompany.store;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import nl.sharecompany.store.db.CassandraFactory;

/**
 * Hello world!
 *
 */
public class CSPStoreApplication {
    private static final String KEY_SPACE = "fix_db";

    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
    }

    public void run(String[] args) {
        // Setup connection to Cassandra
        CassandraFactory factory = new CassandraFactory();
        Cluster session = factory.build();

        // Setup connection to data feed

        // Storage(cassandra_conn, data_feed_conn)
            // Parsing of data to class
            // Write data to Cassandra
    }
}

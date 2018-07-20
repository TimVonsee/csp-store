package nl.sharecompany.writer;

import com.datastax.driver.core.Cluster;
import nl.sharecompany.ctffeed.dfa.ArrayDFA;
import nl.sharecompany.writer.csp.DfaFactory.DfaFactory;
import nl.sharecompany.writer.storage.CassandraFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * CSPStore Application that handles the data feed and stores it
 *
 */
public class CSPStoreApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(CSPStoreApplication.class);
    private static final int BUFFER_SIZE = 1572864;
    private static final int THREADS = 10;
    private static final int BATCH_LIMIT = 60;

    public static void main( String[] args ) {
        new CSPStoreApplication().run(args);
    }

    public void run(String[] args) {
        // Setup connection to Cassandra
        CassandraFactory factory = new CassandraFactory();
        Cluster cluster = factory.build();

        // Setup DFA factory with connection the cluster
        DfaFactory dfaFactory = new DfaFactory(cluster);

        // Build DFA with configured thread count and max. batch size
        ArrayDFA dfa = dfaFactory.getDfa(DfaFactory.DfaType.FIX, THREADS, BATCH_LIMIT);

        String fileName = args[0];
        byte[] buffer = new byte[BUFFER_SIZE];
        try(FileInputStream data = new FileInputStream(fileName)) {
            int size;
            do{
                size = data.read(buffer, 0, buffer.length);
                dfa.parse(buffer, 0, size); // Buffer is parsed *and* persisted by DFA
            } while (size > 0);
        } catch (FileNotFoundException e) {
            LOGGER.error("File not found", e);
        } catch (IOException e) {
            LOGGER.error("IO Error", e);
        }

        cluster.close();
    }
}

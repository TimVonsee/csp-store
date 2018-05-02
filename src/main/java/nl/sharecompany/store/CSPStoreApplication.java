package nl.sharecompany.store;

import com.datastax.driver.core.Cluster;
import nl.sharecompany.ctffeed.dfa.ArrayDFA;
import nl.sharecompany.store.csp.BidDfaFactory;
import nl.sharecompany.store.csp.command.EndOfBidMessageCommand;
import nl.sharecompany.store.csp.message.Message;
import nl.sharecompany.store.db.CassandraFactory;
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
    private static final String KEY_SPACE = "fix_db";
    private static final int BUFFER_SIZE = 1572864;

    public static void main( String[] args )
    {
        new CSPStoreApplication().run(args);
    }

    public void run(String[] args) {
        // Setup connection to Cassandra
        CassandraFactory factory = new CassandraFactory();
        Cluster cluster = factory.build();

        // TODO - Cassandra health check (see if  key space is there with expected column families)

        // Setup connection to data feed
        Message msg = new Message();
        ArrayDFA bidDFA = new BidDfaFactory(new EndOfBidMessageCommand(msg, 10), msg).create(); // TODO IoC maakt het wat raar

        String fileName = args[0];
        byte[] buffer = new byte[BUFFER_SIZE];
        try(FileInputStream data = new FileInputStream(fileName)) {
            int size;
            do{
                size = data.read(buffer, 0, buffer.length);
                bidDFA.parse(buffer, 0, size);
            } while (size > 0);
        } catch (FileNotFoundException e) {
            LOGGER.error("File not found", e);
        } catch (IOException e) {
            LOGGER.error("IO Error", e);
        }

        cluster.close();
    }
}

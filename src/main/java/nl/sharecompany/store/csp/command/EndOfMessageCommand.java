package nl.sharecompany.store.csp.command;

import nl.sharecompany.pattern.simplecommand.ICommand;
import nl.sharecompany.store.csp.message.Message;
import nl.sharecompany.store.storage.CassandraBulkLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class EndOfMessageCommand implements ICommand {
    private final Logger LOGGER = LoggerFactory.getLogger(EndOfMessageCommand.class);
    private final Message msg;
    private final List<Object[]> microBatch;
    private final int batchLimit;
    private final CassandraBulkLoader bulkLoader;

    public EndOfMessageCommand(Message msg, int batchLimit, CassandraBulkLoader bulkLoader) {
        this.msg = msg;
        this.batchLimit = batchLimit;
        this.microBatch = new ArrayList<>(batchLimit);
        this.bulkLoader = bulkLoader;
    }

    @Override
    public void execute() {
        if(!msg.hasData()) {
            msg.reset();
            return;
        }

        boolean batchLimitReached = microBatch.size() >= batchLimit;
        if(batchLimitReached) {
            // Flush batch to cassandra
            // TODO in principe is het een on eidinge stream maar wat als de limiet nooit bereikt is. Maximum timer die flusht?
            flush();

            msg.reset();
            return;
        }

        Object[] row = {msg.ctfSource, msg.symbol, msg.day, msg.timestamp, msg.price, msg.size};
        microBatch.add(row);
        msg.reset();
    }

    public void flush() {
        this.bulkLoader.insert(microBatch.iterator());
        microBatch.clear();
    }
}

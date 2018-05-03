package nl.sharecompany.store.csp.command;

import nl.sharecompany.pattern.simplecommand.ICommand;
import nl.sharecompany.store.csp.message.Message;
import nl.sharecompany.store.db.BulkLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class EndOfBidMessageCommand implements ICommand {
    private static final String INSERT_BID = "INSERT INTO fix_db.bids_by_day (src_id, symbol, day, uts, bid_ts, bid_price, bid_size) VALUES (?, ?, ?, now(), ?, ?, ?);";
    private final Logger LOGGER = LoggerFactory.getLogger(EndOfBidMessageCommand.class);
    private final Message msg;
    private final List<Object[]> microBatch;
    private final int batchLimit;
    private final BulkLoader bulkLoader;

    public EndOfBidMessageCommand(Message msg, int batchLimit, BulkLoader bulkLoader) {
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
        try {
            this.bulkLoader.ingest(microBatch.iterator(), INSERT_BID);
        } catch (InterruptedException e) {
            LOGGER.error("Error ",e);
        }
        microBatch.clear();
    }
}

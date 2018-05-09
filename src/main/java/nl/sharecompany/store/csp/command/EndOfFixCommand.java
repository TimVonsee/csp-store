package nl.sharecompany.store.csp.command;

import nl.sharecompany.pattern.simplecommand.ICommand;
import nl.sharecompany.store.csp.message.FixMessage;
import nl.sharecompany.store.db.BulkLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EndOfFixCommand implements ICommand {
    private final Logger LOGGER = LoggerFactory.getLogger(EndOfFixCommand.class);
    private final FixMessage msg;
    private final List<Object[]> microBatch;
    private final int batchLimit;
    private final BulkLoader bulkLoader;

    public EndOfFixCommand(FixMessage msg, int batchLimit, BulkLoader bulkLoader) {
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
            flush();
            msg.reset();
            return;
        }

        if(msg.day == null) {
            // Set own received date when missing
            msg.day = new Date();
        }
        Object[] row = {msg.ctfSource, msg.symbol, msg.day, msg.fix};
        microBatch.add(row);

        msg.reset();
    }

    public void flush() {
        this.bulkLoader.insert(microBatch.iterator());
        microBatch.clear();
    }
}

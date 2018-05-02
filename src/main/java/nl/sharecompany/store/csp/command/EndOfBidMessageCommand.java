package nl.sharecompany.store.csp.command;

import nl.sharecompany.pattern.simplecommand.ICommand;
import nl.sharecompany.store.csp.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class EndOfBidMessageCommand implements ICommand {
    private final Logger LOGGER = LoggerFactory.getLogger(EndOfBidMessageCommand.class);
    private final Message msg;
    private final List<String[]> microbatch;
    private final int batchLimit;

    public EndOfBidMessageCommand(Message msg, int batchLimit) {
        this.msg = msg;
        this.batchLimit = batchLimit;
        microbatch = new ArrayList<>(batchLimit);
    }

    @Override
    public void execute() {
        if(!msg.hasData()) {
            msg.reset();
            return;
        }

        boolean batchLimitReached = microbatch.size() >= batchLimit;
        if(batchLimitReached) {
            // Flush batch to cassandra
            // TODO in principe is het een on eidinge stream maar wat als de limiet nooit bereikt is. Maximum timer die flusht?
            System.out.println("Flush: " + microbatch);

            for (String[] params : microbatch){
                for (String s : params){
                    System.out.print(s + ", ");
                }
                System.out.println();
            }

            msg.reset();
            microbatch.clear();
            return;
        }

        String[] row = {msg.ctfSource, msg.symbol, msg.day, "now()", msg.timestamp, msg.price, msg.size};
        microbatch.add(row);

        msg.reset();
    }
}

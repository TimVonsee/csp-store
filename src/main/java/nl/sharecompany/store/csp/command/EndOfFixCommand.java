package nl.sharecompany.store.csp.command;

import nl.sharecompany.pattern.simplecommand.ICommand;
import nl.sharecompany.store.csp.message.FixMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EndOfFixCommand implements ICommand {
    private final Logger LOGGER = LoggerFactory.getLogger(EndOfFixCommand.class);
    private final FixMessage msg;

    public EndOfFixCommand(FixMessage msg) {
        this.msg = msg;
    }

    @Override
    public void execute() {
        if(!msg.hasData()) {
            msg.reset();
            return;
        }

        LOGGER.debug("FIX {}-{} {}", msg.ctfSource, msg.symbol, msg.fix);
    }
}

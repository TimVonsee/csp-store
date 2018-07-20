package nl.sharecompany.writer.csp.transformers;

import nl.sharecompany.writer.csp.message.Message;

public class SymbolTokenHandler extends BaseTokenHandler {
    public SymbolTokenHandler(Message msg) {
        super(msg);
    }

    @Override
    public void apply(Message msg, String value) {
        msg.symbol = value;
    }
}

package nl.sharecompany.store.csp.tokenhandlers;

import nl.sharecompany.store.csp.message.Message;

public class SymbolTokenHandler extends BaseTokenHandler {
    public SymbolTokenHandler(Message msg) {
        super(msg);
    }

    @Override
    public void apply(Message msg, String value) {
        msg.symbol = value;
    }
}

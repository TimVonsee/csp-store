package nl.sharecompany.store.csp.tokenhandlers;

import nl.sharecompany.store.csp.message.Message;

public class PriceTokenHandler extends BaseTokenHandler {
    public PriceTokenHandler(Message msg) {
        super(msg);
    }

    @Override
    public void apply(Message msg, String value) {
        msg.price = value;
    }
}

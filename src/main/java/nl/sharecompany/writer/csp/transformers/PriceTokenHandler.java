package nl.sharecompany.writer.csp.transformers;

import nl.sharecompany.writer.csp.message.Message;

public class PriceTokenHandler extends BaseTokenHandler {
    public PriceTokenHandler(Message msg) {
        super(msg);
    }

    @Override
    public void apply(Message msg, String value) {
        msg.price = value;
    }
}

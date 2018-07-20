package nl.sharecompany.writer.csp.transformers;

import nl.sharecompany.writer.csp.message.Message;

public class SizeTokenHandler extends BaseTokenHandler {

    public SizeTokenHandler(Message msg) {
        super(msg);
    }

    @Override
    public void apply(Message msg, String value) {
        msg.size = value;
    }
}

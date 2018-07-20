package nl.sharecompany.store.csp.transformers;

import nl.sharecompany.store.csp.message.Message;

public class CtfSourceTokenHandler extends BaseTokenHandler {
    public CtfSourceTokenHandler(Message msg) {
        super(msg);
    }

    @Override
    public void apply(Message msg, String value) {
        msg.ctfSource = value;
    }
}

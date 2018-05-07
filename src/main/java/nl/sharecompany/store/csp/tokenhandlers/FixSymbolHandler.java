package nl.sharecompany.store.csp.tokenhandlers;

import nl.sharecompany.pattern.bytebuffercommand.IByteBufferCommand;
import nl.sharecompany.store.csp.message.FixMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class FixSymbolHandler implements IByteBufferCommand {
    private final Logger LOGGER = LoggerFactory.getLogger(BaseTokenHandler.class);
    private final CharBuffer charValue = CharBuffer.allocate(30);
    private final CharsetDecoder decoder = Charset.defaultCharset().newDecoder();
    private final FixMessage msg;

    public FixSymbolHandler(FixMessage msg) {
        this.msg = msg;
    }

    @Override
    public void execute(ByteBuffer value) {
        try {
            decoder.decode(value, charValue, true);
            charValue.flip();

            msg.symbol = charValue.toString();
            charValue.clear();
        } catch(Exception e) {
            LOGGER.warn("Conversion failed. {}, exception: {}", charValue, e);
        }
    }
}

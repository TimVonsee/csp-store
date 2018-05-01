package nl.sharecompany.store.csp;

import nl.sharecompany.ctffeed.dfa.ArrayDFA;
import nl.sharecompany.ctffeed.dfa.IDFA;
import nl.sharecompany.pattern.bytebuffercommand.IByteBufferCommand;
import nl.sharecompany.pattern.factory.IFactory;
import nl.sharecompany.pattern.simplecommand.ICommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class DfaFactory implements IFactory<IDFA> {
    @Override
    public ArrayDFA create() {
        final Map<String, IByteBufferCommand> tokenHandlers = new HashMap<String, IByteBufferCommand>();
        final Message msg = new Message();

        tokenHandlers.put("4", new CtfSourceTokenHandler(msg));
        tokenHandlers.put("5", new SymbolTokenHandler(msg));

        // Bid tokens
        tokenHandlers.put("12", new BidPriceTokenHandler(msg));
        tokenHandlers.put("13", new BidSizeTokenHandler(msg));
        tokenHandlers.put("20", new BidDateTimeTokenHandler(msg));

        return new ArrayDFA(tokenHandlers, new EndOfBidMessageCommand(msg));
    }

    private abstract class BaseTokenHandler implements IByteBufferCommand {

        private final Logger LOGGER = LoggerFactory.getLogger(BaseTokenHandler.class);
        private final CharBuffer charValue = CharBuffer.allocate(30);
        private final CharsetDecoder decoder = Charset.defaultCharset().newDecoder();
        private final Message msg;

        private BaseTokenHandler(Message msg) {
            this.msg = msg;
        }

        @Override
        public void execute(ByteBuffer value) {
            try {
                decoder.decode(value, charValue, true);
                charValue.flip();
                this.apply(msg, charValue.toString());
                charValue.clear();
            } catch(Exception e) {
                LOGGER.warn("Conversion failed. {}, exception: {}", charValue, e);
            }
        }

        abstract void apply(Message msg, String value);
    }

    private class SymbolTokenHandler extends BaseTokenHandler {

        private SymbolTokenHandler(Message msg) {
            super(msg);
        }

        @Override
        void apply(Message msg, String value) {
            msg.symbol = value;
        }
    }

    private class CtfSourceTokenHandler extends BaseTokenHandler {

        private CtfSourceTokenHandler(Message msg) {
            super(msg);
        }

        @Override
        void apply(Message msg, String value) {
            msg.ctfSource = value;
        }
    }

    private class BidPriceTokenHandler extends BaseTokenHandler {

        private BidPriceTokenHandler(Message msg) {
            super(msg);
        }

        @Override
        void apply(Message msg, String value) {
            msg.price = value;
        }
    }

    private class BidSizeTokenHandler extends BaseTokenHandler {

        private BidSizeTokenHandler(Message msg) {
            super(msg);
        }

        @Override
        void apply(Message msg, String value) {
            msg.size = value;
        }
    }

    private class BidDateTimeTokenHandler extends BaseTokenHandler {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd-hh:mm:ss");

        private BidDateTimeTokenHandler(Message msg) {
            super(msg);
        }

        @Override
        void apply(Message msg, String value) {
            msg.day = value;
            msg.timestamp = value;
        }
    }

    public class EndOfBidMessageCommand implements ICommand {
        private final Logger LOGGER = LoggerFactory.getLogger(EndOfBidMessageCommand.class);
        private final Message msg;
        public EndOfBidMessageCommand(Message msg) {
            this.msg = msg;
        }

        @Override
        public void execute() {
            if(msg.hasData()) {
                System.out.println("BID: " + msg);
            }

            msg.reset();
        }
    }

    private class BaseMessage {
        public String ctfSource;
        public String symbol;
        public String day;
    }

    private class Message extends BaseMessage {
        public String price;
        public String size;
        public String timestamp;

        public boolean hasData() {
            return symbol != null && ctfSource != null && day != null && price != null;
        }

        public void reset() {
            symbol = null;
            ctfSource = null;
            day = null;
            timestamp = null;
            price = null;
            size = null;
        }

        @Override
        public String toString() {
            return "ctfSource='" + ctfSource + '\'' +
                    ", symbol='" + symbol + '\'' +
                    ", day='" + day + '\'' +
                    ", price='" + price + '\'' +
                    ", size='" + size + '\'' +
                    ", timestamp='" + timestamp + '\'';
        }
    }

}

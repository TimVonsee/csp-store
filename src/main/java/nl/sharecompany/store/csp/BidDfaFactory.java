package nl.sharecompany.store.csp;

import nl.sharecompany.ctffeed.dfa.ArrayDFA;
import nl.sharecompany.ctffeed.dfa.IDFA;
import nl.sharecompany.pattern.bytebuffercommand.IByteBufferCommand;
import nl.sharecompany.pattern.factory.IFactory;
import nl.sharecompany.store.csp.command.EndOfBidMessageCommand;
import nl.sharecompany.store.csp.message.Message;
import nl.sharecompany.store.util.Token;
import nl.sharecompany.store.util.Utilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.*;

public class BidDfaFactory implements IFactory<IDFA> {

    private final EndOfBidMessageCommand endOfMessageCommand;
    private Message message;

    public BidDfaFactory(EndOfBidMessageCommand endOfMessageCommand, Message message) {
        this.endOfMessageCommand = endOfMessageCommand;
        this.message = message;
    }

    @Override
    public ArrayDFA create() {
        final Map<String, IByteBufferCommand> tokenHandlers = new HashMap<String, IByteBufferCommand>();

        tokenHandlers.put(Token.CTF_SOURCE, new CtfSourceTokenHandler(message));
        tokenHandlers.put(Token.SYMBOL, new SymbolTokenHandler(message));

        // Bid tokens
        tokenHandlers.put(Token.BID_PRICE, new BidPriceTokenHandler(message));
        tokenHandlers.put(Token.BID_SIZE, new BidSizeTokenHandler(message));
        tokenHandlers.put(Token.BID_DATETIME, new BidDateTimeTokenHandler(message));

        return new ArrayDFA(tokenHandlers, endOfMessageCommand);
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

    private class BidDateTimeTokenHandler implements IByteBufferCommand {

        private final Message msg;

        private BidDateTimeTokenHandler(Message msg) {
            this.msg = msg;
        }

        @Override
        public void execute(ByteBuffer value) {
            long lngUnixTime = Utilities.getTimestampAsMilliseconds(value);

            Calendar tmp = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            tmp.setTimeInMillis(lngUnixTime);

            Date timestamp = tmp.getTime();

            tmp.set(Calendar.HOUR_OF_DAY, 0);
            tmp.set(Calendar.MINUTE, 0);
            tmp.set(Calendar.SECOND, 0);
            tmp.set(Calendar.MILLISECOND, 0);

            msg.day = tmp.getTime();
            msg.timestamp = timestamp;
        }
    }
}

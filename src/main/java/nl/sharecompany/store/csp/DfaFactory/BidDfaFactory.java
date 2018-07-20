package nl.sharecompany.store.csp.DfaFactory;

import nl.sharecompany.ctffeed.dfa.ArrayDFA;
import nl.sharecompany.ctffeed.dfa.IDFA;
import nl.sharecompany.pattern.bytebuffercommand.IByteBufferCommand;
import nl.sharecompany.pattern.factory.IFactory;
import nl.sharecompany.store.csp.command.EndOfMessageCommand;
import nl.sharecompany.store.csp.message.Message;
import nl.sharecompany.store.csp.transformers.*;
import nl.sharecompany.store.util.Token;
import java.util.*;

public class BidDfaFactory implements IFactory<IDFA> {

    private final EndOfMessageCommand endOfMessageCommand;
    private final Message message;

    public BidDfaFactory(EndOfMessageCommand endOfMessageCommand, Message message) {
        this.endOfMessageCommand = endOfMessageCommand;
        this.message = message;
    }

    @Override
    public ArrayDFA create() {
        final Map<String, IByteBufferCommand> tokenHandlers = new HashMap<String, IByteBufferCommand>();

        tokenHandlers.put(Token.CTF_SOURCE, new CtfSourceTokenHandler(message));
        tokenHandlers.put(Token.SYMBOL, new SymbolTokenHandler(message));

        // Bid tokens
        tokenHandlers.put(Token.BID_PRICE, new PriceTokenHandler(message));
        tokenHandlers.put(Token.BID_SIZE, new SizeTokenHandler(message));
        tokenHandlers.put(Token.BID_DATETIME, new DateTimeTokenHandler(message));

        return new ArrayDFA(tokenHandlers, endOfMessageCommand);
    }
}

package nl.sharecompany.writer.csp.DfaFactory;

import nl.sharecompany.ctffeed.dfa.ArrayDFA;
import nl.sharecompany.ctffeed.dfa.IDFA;
import nl.sharecompany.pattern.bytebuffercommand.IByteBufferCommand;
import nl.sharecompany.pattern.factory.IFactory;
import nl.sharecompany.writer.csp.command.EndOfMessageCommand;
import nl.sharecompany.writer.csp.message.Message;
import nl.sharecompany.writer.csp.transformers.*;
import nl.sharecompany.writer.util.Token;

import java.util.HashMap;
import java.util.Map;

public class TradeDfaFactory implements IFactory<IDFA> {

    private final EndOfMessageCommand endOfMessageCommand;
    private final Message message;

    public TradeDfaFactory(EndOfMessageCommand endOfMessageCommand, Message message) {
        this.endOfMessageCommand = endOfMessageCommand;
        this.message = message;
    }

    @Override
    public ArrayDFA create() {
        final Map<String, IByteBufferCommand> tokenHandlers = new HashMap<String, IByteBufferCommand>();

        tokenHandlers.put(Token.CTF_SOURCE, new CtfSourceTokenHandler(message));
        tokenHandlers.put(Token.SYMBOL, new SymbolTokenHandler(message));

        // Bid tokens
        tokenHandlers.put(Token.TRADE_PRICE, new PriceTokenHandler(message));
        tokenHandlers.put(Token.TRADE_SIZE, new SizeTokenHandler(message));
        tokenHandlers.put(Token.TRADE_DATETIME, new DateTimeTokenHandler(message));

        return new ArrayDFA(tokenHandlers, endOfMessageCommand);
    }
}

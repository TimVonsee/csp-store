package nl.sharecompany.store.csp;

import nl.sharecompany.ctffeed.dfa.ArrayDFA;
import nl.sharecompany.ctffeed.dfa.IDFA;
import nl.sharecompany.pattern.bytebuffercommand.IByteBufferCommand;
import nl.sharecompany.pattern.factory.IFactory;
import nl.sharecompany.store.csp.command.EndOfMessageCommand;
import nl.sharecompany.store.csp.message.Message;
import nl.sharecompany.store.csp.tokenhandlers.*;
import nl.sharecompany.store.util.Token;

import java.util.*;

public class AskDfaFactory implements IFactory<IDFA> {

    private final EndOfMessageCommand endOfMessageCommand;
    private Message message;

    public AskDfaFactory(EndOfMessageCommand endOfMessageCommand, Message message) {
        this.endOfMessageCommand = endOfMessageCommand;
        this.message = message;
    }

    @Override
    public ArrayDFA create() {
        final Map<String, IByteBufferCommand> tokenHandlers = new HashMap<String, IByteBufferCommand>();

        tokenHandlers.put(Token.CTF_SOURCE, new CtfSourceTokenHandler(message));
        tokenHandlers.put(Token.SYMBOL, new SymbolTokenHandler(message));

        // Ask token handlers
        tokenHandlers.put(Token.ASK_PRICE, new PriceTokenHandler(message));
        tokenHandlers.put(Token.ASK_SIZE, new SizeTokenHandler(message));
        tokenHandlers.put(Token.ASK_DATETIME, new DateTimeTokenHandler(message));

        return new ArrayDFA(tokenHandlers, endOfMessageCommand);
    }


}

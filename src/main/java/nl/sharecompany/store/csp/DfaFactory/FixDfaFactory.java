package nl.sharecompany.store.csp.DfaFactory;

import nl.sharecompany.ctffeed.dfa.ArrayDFA;
import nl.sharecompany.ctffeed.dfa.IDFA;
import nl.sharecompany.pattern.bytebuffercommand.IByteBufferCommand;
import nl.sharecompany.pattern.factory.IFactory;
import nl.sharecompany.store.csp.command.EndOfFixCommand;
import nl.sharecompany.store.csp.message.FixMessage;
import nl.sharecompany.store.csp.tokenhandlers.FixCtfSourceHandler;
import nl.sharecompany.store.csp.tokenhandlers.FixHandler;
import nl.sharecompany.store.csp.tokenhandlers.FixSymbolHandler;
import nl.sharecompany.store.util.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class FixDfaFactory implements IFactory<IDFA> {
    private final Logger LOGGER = LoggerFactory.getLogger(FixDfaFactory.class);
    private final EndOfFixCommand endOfFixCommand;
    private FixMessage message;

    public FixDfaFactory(EndOfFixCommand endOfFixCommand, FixMessage message) {
        this.endOfFixCommand = endOfFixCommand;
        this.message = message;
    }

    @Override
    public ArrayDFA create() {
        final Map<String, IByteBufferCommand> tokenHandlers = new HashMap<String, IByteBufferCommand>();

        tokenHandlers.put(Token.CTF_SOURCE, new FixCtfSourceHandler(message));
        tokenHandlers.put(Token.SYMBOL, new FixSymbolHandler(message));

        // Read Token properties file from resources folder
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream tokensFile = classloader.getResourceAsStream("tokens.properties");

        Properties tokens = new Properties();
        try {
            tokens.load(tokensFile);
            tokens.forEach((tn, tv)-> tokenHandlers.put((String) tn, new FixHandler((String) tv, message)));
        } catch (IOException e) {
            LOGGER.error("Could not open file", e);
        }

        return new ArrayDFA(tokenHandlers, endOfFixCommand);
    }
}

package nl.sharecompany.writer.csp.DfaFactory;

import nl.sharecompany.ctffeed.dfa.ArrayDFA;
import nl.sharecompany.ctffeed.dfa.IDFA;
import nl.sharecompany.pattern.bytebuffercommand.IByteBufferCommand;
import nl.sharecompany.pattern.factory.IFactory;
import nl.sharecompany.writer.csp.command.EndOfFixCommand;
import nl.sharecompany.writer.csp.message.FixMessage;
import nl.sharecompany.writer.csp.transformers.*;
import nl.sharecompany.writer.util.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class FixDfaFactory implements IFactory<IDFA> {
    public static final String TOKEN_FILE = "tokens.properties";
    private final Logger LOGGER = LoggerFactory.getLogger(FixDfaFactory.class);
    private final EndOfFixCommand endOfFixCommand;
    private final FixMessage message;

    public FixDfaFactory(EndOfFixCommand endOfFixCommand, FixMessage message) {
        this.endOfFixCommand = endOfFixCommand;
        this.message = message;
    }

    @Override
    public ArrayDFA create() {
        final Map<String, IByteBufferCommand> tokenHandlers = new HashMap<String, IByteBufferCommand>();

        tokenHandlers.put(Token.CTF_SOURCE, new FixCtfSourceHandler(message));
        tokenHandlers.put(Token.SYMBOL, new FixSymbolHandler(message));
        tokenHandlers.put(Token.ACTIVITY_DATETIME, new FixDateTimeTokenHandler(message));

        // Read Token properties file from resources folder
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream tokensFile = classloader.getResourceAsStream(TOKEN_FILE);

        Properties tokens = new Properties();
        try {
            tokens.load(tokensFile);
            tokens.forEach((tn, tv)-> tokenHandlers.put((String) tn, new FixHandler((String) tv, message)));
        } catch (IOException e) {
            LOGGER.error("Could not open token file", e);
        }

        return new ArrayDFA(tokenHandlers, endOfFixCommand);
    }
}

package nl.sharecompany.store.csp;

import nl.sharecompany.pattern.buffers.SerializableByteBuffer;
import nl.sharecompany.pattern.bytebuffercommand.IByteBufferCommand;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class TokenHandler implements IByteBufferCommand {
    private final String token;
    private final CharBuffer charValue = CharBuffer.allocate(130);
    private final static CharsetDecoder decoder = Charset.forName("US-ASCII").newDecoder();
    private final static Charset ascii = Charset.forName("US-ASCII");

    private final SerializableByteBuffer tokenKey;

    public TokenHandler(String token) {
        this.token = token;
        tokenKey = SerializableByteBuffer.allocate(token.length());
        tokenKey.put(token.getBytes(ascii)).flip();
    }


    public void execute(ByteBuffer value) {
        charValue.clear();
        try {
            decoder.decode(value, charValue, true);
            charValue.flip();
            System.out.print(token + "=" + charValue + "|");

        }
        catch (RuntimeException e) {
            e.printStackTrace();
        }
    }
}

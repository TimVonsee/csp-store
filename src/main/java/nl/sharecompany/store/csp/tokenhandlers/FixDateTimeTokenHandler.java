package nl.sharecompany.store.csp.tokenhandlers;

import nl.sharecompany.pattern.bytebuffercommand.IByteBufferCommand;
import nl.sharecompany.store.csp.message.BaseMessage;
import nl.sharecompany.store.util.Utilities;

import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.TimeZone;

public class FixDateTimeTokenHandler implements IByteBufferCommand {
    private final BaseMessage msg;

    public FixDateTimeTokenHandler(BaseMessage message) {
        this.msg = message;
    }

    @Override
    public void execute(ByteBuffer byteBuffer) {
        long lngUnixTime = Utilities.getTimestampAsMilliseconds(byteBuffer);

        Calendar tmp = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        tmp.setTimeInMillis(lngUnixTime);

        tmp.set(Calendar.HOUR_OF_DAY, 0);
        tmp.set(Calendar.MINUTE, 0);
        tmp.set(Calendar.SECOND, 0);
        tmp.set(Calendar.MILLISECOND, 0);

        msg.day = tmp.getTime();
    }
}

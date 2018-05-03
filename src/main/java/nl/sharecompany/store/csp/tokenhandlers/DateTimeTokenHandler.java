package nl.sharecompany.store.csp.tokenhandlers;

import nl.sharecompany.pattern.bytebuffercommand.IByteBufferCommand;
import nl.sharecompany.store.csp.message.Message;
import nl.sharecompany.store.util.Utilities;

import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateTimeTokenHandler implements IByteBufferCommand {
    private final Message msg;

    public DateTimeTokenHandler(Message message) {
        this.msg = message;
    }

    @Override
    public void execute(ByteBuffer byteBuffer) {
        long lngUnixTime = Utilities.getTimestampAsMilliseconds(byteBuffer);

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
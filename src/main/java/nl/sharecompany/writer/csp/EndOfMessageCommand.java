package nl.sharecompany.writer.csp;

import nl.sharecompany.pattern.simplecommand.IParamCommand;

public class EndOfMessageCommand implements IParamCommand {


    @Override
    public void execute(Object o) {
        System.out.println(" [END] " + o);
    }
}

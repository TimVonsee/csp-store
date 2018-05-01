package nl.sharecompany.store.csp;

import nl.sharecompany.pattern.simplecommand.IParamCommand;

public class EndOfMessageCommand implements IParamCommand {


    @Override
    public void execute(Object o) {
        System.out.println(" [END] " + o);
    }
}

package phrase.towerClans.action;

import phrase.towerClans.action.context.Context;
import phrase.towerClans.action.context.impl.StringContext;
import phrase.towerClans.action.impl.ConsoleAction;
import phrase.towerClans.action.impl.MessageAction;
import phrase.towerClans.action.impl.OpenAction;

public enum ActionType {

    CONSOLE(new ConsoleAction(), StringContext.class),
    MESSAGE(new MessageAction(), StringContext.class),
    OPEN(new OpenAction(), StringContext.class);

    private final Action<?> action;
    private final Class<? extends Context> context;

    ActionType(Action<?> action, Class<? extends Context> context) {
        this.action = action;
        this.context = context;
    }

    public Class<? extends Context> getContext() {
        return context;
    }

    public <C extends Context> Action<C> getAction() {
        return (Action<C>) action;
    }

}

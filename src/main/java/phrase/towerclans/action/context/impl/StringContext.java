package phrase.towerclans.action.context.impl;

import phrase.towerclans.action.context.Context;
import phrase.towerclans.util.Utils;

public record StringContext(String message) implements Context {
    public static StringContext validate(String message) {
        return new StringContext(Utils.COLORIZER.colorize(message));
    }
}

package phrase.towerClans.action.context.impl;

import phrase.towerClans.action.context.Context;
import phrase.towerClans.util.Utils;

public record StringContext(String message) implements Context {

    public static StringContext validate(String message) {
        return new StringContext(Utils.COLORIZER.colorize(message));
    }

}

package phrase.towerclans.util.colorizer.impl;

import phrase.towerclans.util.colorizer.ColorizerProvider;

public class MiniMessageProvider extends ColorizerProvider {
    public MiniMessageProvider() {
        super(new MiniMessageService());
    }

    @Override
    public String colorize(String message) {
        return getColorizer().colorize(message);
    }
}

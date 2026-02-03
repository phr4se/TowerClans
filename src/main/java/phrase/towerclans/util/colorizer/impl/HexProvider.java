package phrase.towerclans.util.colorizer.impl;

import phrase.towerclans.util.colorizer.ColorizerProvider;

public class HexProvider extends ColorizerProvider {

    public HexProvider() {
        super(new HexService());
    }

    @Override
    public String colorize(String message) {
        return getColorizer().colorize(message);
    }
}

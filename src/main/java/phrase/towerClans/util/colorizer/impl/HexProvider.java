package phrase.towerClans.util.colorizer.impl;

import phrase.towerClans.util.colorizer.ColorizerProvider;

public class HexProvider extends ColorizerProvider {

    public HexProvider() {
        super(new HexService());
    }

    @Override
    public String colorize(String message) {
        return getColorizer().colorize(message);
    }
}

package phrase.towerClans.utils.colorizer.impl;

import phrase.towerClans.utils.colorizer.ColorizerProvider;

public class HexProvider extends ColorizerProvider {

    public HexProvider() {
        super(new HexService());
    }

    @Override
    public String colorize(String message) {
        return getColorizer().colorize(message);
    }
}

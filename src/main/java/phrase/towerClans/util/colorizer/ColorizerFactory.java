package phrase.towerClans.util.colorizer;

import phrase.towerClans.util.colorizer.impl.*;

public class ColorizerFactory {

    public static ColorizerProvider getProvider(ColorizerType colorizerType) {
        switch (colorizerType) {
            case HEX:
                return new HexProvider();
            default:
                return null;

        }

    }

}

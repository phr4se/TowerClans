package phrase.towerClans.utils.colorizer;

import phrase.towerClans.utils.colorizer.impl.*;

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

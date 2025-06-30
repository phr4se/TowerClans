package phrase.towerClans.util.colorizer;

import phrase.towerClans.util.colorizer.impl.*;

public class ColorizerFactory {

    public static ColorizerProvider getProvider(ColorizerType colorizerType) {
        return switch (colorizerType) {
            case HEX -> new HexProvider();
        };

    }

}

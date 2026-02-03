package phrase.towerclans.util.colorizer;

import phrase.towerclans.util.colorizer.impl.*;

public class ColorizerFactory {

    public static ColorizerProvider getProvider(ColorizerType colorizerType) {
        return switch (colorizerType) {
            case HEX -> new HexProvider();
        };

    }

}

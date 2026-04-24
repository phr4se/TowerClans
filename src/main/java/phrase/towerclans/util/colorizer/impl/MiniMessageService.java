package phrase.towerclans.util.colorizer.impl;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import phrase.towerclans.util.colorizer.ColorizerService;

public class MiniMessageService implements ColorizerService {
    @Override
    public String colorize(String message) {
        return LegacyComponentSerializer.legacySection().serialize(MiniMessage.miniMessage().deserialize(message));
    }
}

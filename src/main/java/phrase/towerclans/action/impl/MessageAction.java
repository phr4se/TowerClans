package phrase.towerclans.action.impl;

import org.bukkit.entity.Player;
import phrase.towerclans.action.Action;
import phrase.towerclans.action.context.impl.StringContext;
import phrase.towerclans.util.Utils;

public class MessageAction implements Action<StringContext> {
    @Override
    public void execute(Player player, StringContext context) {
        Utils.sendMessage(player, context.message());
    }
}

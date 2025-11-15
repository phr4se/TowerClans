package phrase.towerClans.action.impl;

import org.bukkit.entity.Player;
import phrase.towerClans.action.Action;
import phrase.towerClans.action.context.impl.StringContext;
import phrase.towerClans.util.Utils;

public class MessageAction implements Action<StringContext> {

    @Override
    public void execute(Player player, StringContext context) {
        Utils.sendMessage(player, context.message());
    }

}

package phrase.towerClans.action;

import org.bukkit.entity.Player;
import phrase.towerClans.action.context.Context;

public interface Action<C extends Context> {

    void execute(Player player, C context);

}

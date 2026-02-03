package phrase.towerclans.action;

import org.bukkit.entity.Player;
import phrase.towerclans.action.context.Context;

public interface Action<C extends Context> {
    void execute(Player player, C context);
}

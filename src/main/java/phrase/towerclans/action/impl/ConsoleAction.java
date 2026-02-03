package phrase.towerclans.action.impl;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import phrase.towerclans.action.Action;
import phrase.towerclans.action.context.impl.StringContext;

public class ConsoleAction implements Action<StringContext> {
    @Override
    public void execute(Player player, StringContext context) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), context.message());
    }
}

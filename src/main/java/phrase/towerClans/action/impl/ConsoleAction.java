package phrase.towerClans.action.impl;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import phrase.towerClans.action.Action;
import phrase.towerClans.action.context.impl.StringContext;

public class ConsoleAction implements Action<StringContext> {

    @Override
    public void execute(Player player, StringContext context) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), context.message());
    }

}

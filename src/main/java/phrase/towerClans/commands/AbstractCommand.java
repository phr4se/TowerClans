package phrase.towerClans.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import phrase.towerClans.Plugin;

public abstract class AbstractCommand implements CommandExecutor {

    public AbstractCommand(String command) {
        if(Plugin.instance.getCommand(command) == null) {
            Plugin.instance.getLogger().severe("Команда " + command + " не найдена");
            return;
        }

        Plugin.instance.getCommand(command).setExecutor(this);

    }

    public abstract void execute(CommandSender commandSender, Command command, String label, String[] args);

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        execute(commandSender, command, s, strings);
        return true;
    }
}

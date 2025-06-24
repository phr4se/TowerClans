package phrase.towerClans.command;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import phrase.towerClans.Plugin;
import phrase.towerClans.config.Config;

public class CommandMapper {

    private final Plugin plugin;

    public CommandMapper(Plugin plugin) {
        this.plugin = plugin;
    }

    public CommandResult mapCommand(Player player, String label, String[] args) {

        CommandDescription commandDescription = CommandLogger.getCommands().get(label.toLowerCase());

        if(commandDescription == null) return new CommandResult(Config.getMessages().unknownCommand(), CommandResult.ResultStatus.UNKNOWN_COMMAND);

        if(!player.hasPermission(commandDescription.getPermission())) return new CommandResult(Config.getMessages().noPermission() ,CommandResult.ResultStatus.NO_PERMISSION);

        CommandHandler commandHandler = commandDescription.getCommandHandler();
        if(commandHandler == null) return new CommandResult(Config.getMessages().error(), CommandResult.ResultStatus.ERROR);

        boolean success = commandHandler.handler(player, args);

        if(!success) return new CommandResult(null,CommandResult.ResultStatus.INCORRECT_ARGUMENTS);

        return new CommandResult(null,CommandResult.ResultStatus.SUCCESS);

    }

}

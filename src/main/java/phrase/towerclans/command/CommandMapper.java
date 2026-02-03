package phrase.towerclans.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import phrase.towerclans.config.Config;

public class CommandMapper {
    private final CommandLogger commandLogger;

    public CommandMapper(CommandLogger commandLogger) {
        this.commandLogger = commandLogger;
    }

    public CommandResult mapCommand(Player player, String label, String[] args) {
        CommandDescription commandDescription = commandLogger.getCommands().get(label.toLowerCase());
        if (commandDescription == null)
            return new CommandResult(Config.getMessages().unknownCommand(), CommandResult.ResultStatus.UNKNOWN_COMMAND);
        if (!player.hasPermission(commandDescription.getPermission()))
            return new CommandResult(Config.getMessages().noPermission(), CommandResult.ResultStatus.NO_PERMISSION);
        CommandHandler commandHandler = commandDescription.getCommandHandler();
        if (commandHandler == null)
            return new CommandResult(Config.getMessages().error(), CommandResult.ResultStatus.ERROR);
        boolean success = commandHandler.handler(player, args);
        if (!success) return new CommandResult(null, CommandResult.ResultStatus.INCORRECT_ARGUMENTS);
        return new CommandResult(null, CommandResult.ResultStatus.SUCCESS);
    }

    public void mapCommand(CommandSender sender, String label, String[] args) {
        CommandDescription commandDescription = commandLogger.getCommands().get(label.toLowerCase());
        if (commandDescription == null) return;
        CommandHandler commandHandler = commandDescription.getCommandHandler();
        if (commandHandler == null) return;
        commandHandler.handler(sender, args);
    }
}

package phrase.towerClans.command;


public class CommandDescription {

    private final String permission;
    private final CommandHandler commandHandler;

    public CommandDescription(String permission, CommandHandler commandHandler) {
        this.permission = permission;
        this.commandHandler = commandHandler;
    }

    public String getPermission() {
        return permission;
    }

    public CommandHandler getCommandHandler() {
        return commandHandler;
    }
}

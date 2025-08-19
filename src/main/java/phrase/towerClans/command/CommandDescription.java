package phrase.towerClans.command;


public class CommandDescription {

    public enum CommandType {

        WITH_CLAN,
        WITHOUT_CLAN,
        AND,
        ADMIN

    }

    private final String permission;
    private final CommandHandler commandHandler;
    private final CommandType commandType;

    public CommandDescription(String permission, CommandHandler commandHandler, CommandType commandType) {
        this.permission = permission;
        this.commandHandler = commandHandler;
        this.commandType = commandType;
    }

    public String getPermission() {
        return permission;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public CommandHandler getCommandHandler() {
        return commandHandler;
    }
}

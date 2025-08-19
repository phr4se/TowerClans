package phrase.towerClans.command;

public class CommandResult {

    public enum ResultStatus {

        SUCCESS,
        UNKNOWN_COMMAND,
        INCORRECT_ARGUMENTS,
        NO_PERMISSION,
        ERROR

    }

    private final ResultStatus resultStatus;
    private final String message;

    public CommandResult(String message, ResultStatus resultStatus) {
        this.message = message;
        this.resultStatus = resultStatus;
    }

    public String getMessage() {
        return message;
    }

    public ResultStatus getResultStatus() {
        return resultStatus;
    }
}

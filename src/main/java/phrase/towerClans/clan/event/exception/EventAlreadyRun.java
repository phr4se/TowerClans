package phrase.towerClans.clan.event.exception;

public class EventAlreadyRun extends RuntimeException {
    public EventAlreadyRun(String message) {
        super(message);
    }
}

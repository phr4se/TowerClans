package phrase.towerclans.clan.event.exception;

public class EventAlreadyRun extends RuntimeException {
    public EventAlreadyRun(String message) {
        super(message);
    }
}

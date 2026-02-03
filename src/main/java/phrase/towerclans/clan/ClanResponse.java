package phrase.towerclans.clan;

public class ClanResponse {
    public enum ResponseType {
        SUCCESS,
        FAILURE
    }

    private final String message;
    private final ResponseType responseType;

    public ClanResponse(String message, ResponseType responseType) {
        this.message = message;
        this.responseType = responseType;
    }

    public boolean isSuccess() {
        if (getResponseType() == ResponseType.SUCCESS) return true;
        return false;
    }

    public String getMessage() {
        return message;
    }

    public ResponseType getResponseType() {
        return responseType;
    }
}

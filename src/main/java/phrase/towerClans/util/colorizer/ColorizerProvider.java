package phrase.towerClans.util.colorizer;

public abstract class ColorizerProvider {

    private final ColorizerService colorizerService;

    public ColorizerProvider(ColorizerService colorizerService) {
        this.colorizerService = colorizerService;
    }

    public abstract String colorize(String message);

    protected ColorizerService getColorizer() {
        return colorizerService;
    }

}

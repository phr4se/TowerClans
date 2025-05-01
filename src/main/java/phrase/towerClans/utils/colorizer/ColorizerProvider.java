package phrase.towerClans.utils.colorizer;

public abstract class ColorizerProvider {

    private final ColorizerService colorizer;

    public ColorizerProvider(ColorizerService colorizer) {
        this.colorizer = colorizer;
    }

    public abstract String colorize(String message);

    protected ColorizerService getColorizer() {
        return colorizer;
    }

}

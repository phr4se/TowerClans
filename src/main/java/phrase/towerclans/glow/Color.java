package phrase.towerclans.glow;

public class Color {
    private final String key;
    private final org.bukkit.Color color;

    Color(String key, int red, int green, int blue) {
        this.key = key;
        this.color = org.bukkit.Color.fromRGB(red, green, blue);
    }

    public String getKey() {
        return key;
    }

    public org.bukkit.Color getColor() {
        return color;
    }
}

package phrase.towerclans.glow;

import org.bukkit.configuration.ConfigurationSection;
import phrase.towerclans.config.Config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ColorManager {
    public static Color COLOR;
    private final Map<String, Color> colors = new HashMap<>();

    public void initialize() {
        ConfigurationSection configurationSection = Config.getFile("colors.yml").getConfigurationSection("colors");
        for (String key : configurationSection.getKeys(false)) {
            final int red = configurationSection.getInt(key + ".red");
            final int green = configurationSection.getInt(key + ".green");
            final int blue = configurationSection.getInt(key + ".blue");
            colors.put(key, new Color(key, red, green, blue));
        }
        COLOR = getRandomColor();
    }

    public Color getColor(String key) {
        return colors.get(key);
    }

    public Color getRandomColor() {
        final List<Color> colors = this.colors.values().stream().toList();
        if (colors.isEmpty()) return null;
        return colors.get(colors.size() - 1);
    }
}

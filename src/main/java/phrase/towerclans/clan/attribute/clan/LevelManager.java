package phrase.towerclans.clan.attribute.clan;

import org.bukkit.configuration.ConfigurationSection;
import phrase.towerclans.Plugin;
import phrase.towerclans.config.Config;

import java.util.HashMap;
import java.util.Map;

public class LevelManager {
    public record Level(int level, int xp, int maximumBalance, int maximumMembers, int availableSlots) {
    }

    private final Map<Integer, Level> levels;
    private int xpForMurder;
    private int countLevels;

    public LevelManager(Plugin plugin) {
        this.levels = new HashMap<>();
        initialize(plugin);
    }

    public int getXpLevel(int level) {
        if (!levels.containsKey(level)) return -1;
        return levels.get(level).xp();
    }

    public int getLevelMaximumBalance(int level) {
        return levels.get(level).maximumBalance();
    }

    public int getLevelMaximumMembers(int level) {
        return levels.get(level).maximumMembers();
    }

    public int getAvailableSlots(int level) {
        return levels.get(level).availableSlots();
    }

    public int getXpForMurder() {
        return xpForMurder;
    }

    public int getCountLevels() {
        return countLevels;
    }

    private void initialize(Plugin plugin) {
        this.xpForMurder = Config.getSettings().xpForMurder();
        final ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("settings.level");
        countLevels = 0;
        for (String key : configurationSection.getKeys(false)) {
            final int level = configurationSection.getInt(key + ".level");
            final int xp = configurationSection.getInt(key + ".xp");
            final int maximumBalance = configurationSection.getInt(key + ".maximum_balance");
            final int maximumMembers = configurationSection.getInt(key + ".maximum_members");
            final int availableSlots = configurationSection.getInt(key + ".available");
            levels.put(level, new Level(countLevels, xp, maximumBalance, maximumMembers, availableSlots));
            countLevels++;
        }
    }
}

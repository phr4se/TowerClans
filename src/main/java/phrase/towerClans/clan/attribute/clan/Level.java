package phrase.towerClans.clan.attribute.clan;

import org.bukkit.configuration.ConfigurationSection;
import phrase.towerClans.Plugin;
import phrase.towerClans.config.Config;

import java.util.HashMap;
import java.util.Map;

public record Level(int level, int xp, int maximumBalance, int maximumMembers, int availableSlots) {

    private static Map<Integer, Level> LEVELS;

    private static int xpForMurder;
    private static int countLevels;

    public static int getXpLevel(int level) {
        if(!LEVELS.containsKey(level)) return -1;
        return LEVELS.get(level).xp();
    }

    public static int getLevelMaximumBalance(int level) {
        return LEVELS.get(level).maximumBalance();
    }

    public static int getLevelMaximumMembers(int level) {
        return LEVELS.get(level).maximumMembers();
    }

    public static int getAvailableSlots(int level) {
        return LEVELS.get(level).availableSlots();
    }

    public static int getXpForMurder() {
        return xpForMurder;
    }


    public static int getCountLevels() {
        return countLevels;
    }

    public static void initialize(Plugin plugin) {
        LEVELS = new HashMap<>();
        xpForMurder = Config.getSettings().xpForMurder();

        ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("settings.level");

        countLevels = 0;

        for (String key : configurationSection.getKeys(false)) {

            int level = configurationSection.getInt(key + ".level");
            int xp = configurationSection.getInt(key + ".xp");
            int maximumBalance = configurationSection.getInt(key + ".maximum_balance");
            int maximumMembers = configurationSection.getInt(key + ".maximum_members");
            int availableSlots = configurationSection.getInt(key + ".available");
            LEVELS.put(level, new Level(countLevels, xp, maximumBalance, maximumMembers, availableSlots));
            countLevels++;

        }

    }
}

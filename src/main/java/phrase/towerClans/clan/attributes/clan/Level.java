package phrase.towerClans.clan.attributes.clan;

import org.bukkit.configuration.ConfigurationSection;
import phrase.towerClans.Plugin;

import java.util.HashMap;
import java.util.Map;

public class Level {

    public static Map<Integer, Level> levels = new HashMap<>();

    private final int level;
    private final int xp;
    private final int maximumBalance;
    private final int maximumMembers;
    private final int availableSlots;

    public Level(int level, int xp, int maximumBalance, int maximumMembers, int availableSlots) {
        this.level = level;
        this.xp = xp;
        this.maximumBalance = maximumBalance;
        this.maximumMembers = maximumMembers;
        this.availableSlots = availableSlots;
    }

    public int getLevel() {
        return level;
    }

    public int getXp() {
        return xp;
    }

    public int getMaximumBalance() {
        return maximumBalance;
    }

    public int getMaximumMembers() {
        return maximumMembers;
    }

    public int getAvailableSlots() { return availableSlots; }

    public static int getXpLevel(int level) {
        return levels.get(level).getXp();
    }

    public static int getLevelMaximumBalance(int level) {
        return levels.get(level).getMaximumBalance();
    }

    public static int getLevelMaximumMembers(int level) {
        return levels.get(level).getMaximumMembers();
    }

    public static int getAvailableSlots(int level) { return levels.get(level).getAvailableSlots(); }

    public static void intialize(Plugin plugin) {

        ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("settings.level");

        int id = 1;

        for(String key : configurationSection.getKeys(false)) {

            int level = configurationSection.getInt(key + ".level");
            int xp = configurationSection.getInt(key + ".xp");
            int maximumBalance = configurationSection.getInt(key + ".maximum_balance");
            int maximumMembers = configurationSection.getInt(key + ".maximum_members");
            int availableSlots = configurationSection.getInt(key + ".available");
            levels.put(id, new Level(level, xp, maximumBalance, maximumMembers, availableSlots));
            id++;

        }

    }
}

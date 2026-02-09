package phrase.towerclans.clan.attribute.clan;

import org.bukkit.configuration.ConfigurationSection;
import phrase.towerclans.TowerClans;

import java.util.Arrays;

public enum RankType {
    UNDEFINED(0),
    LEADER(1),
    DEPUTY(2),
    MEMBER(3);
    private String name;
    private final int id;

    RankType(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public static void initialize(TowerClans plugin) {
        final ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("settings");
        RankType.LEADER.setName(configurationSection.getString("rank-leader-name"));
        RankType.DEPUTY.setName(configurationSection.getString("rank-deputy-name"));
        RankType.MEMBER.setName(configurationSection.getString("rank-member-name"));
    }

    public static RankType getRank(int id) {
        return Arrays.stream(RankType.values()).filter(rank -> rank.getId() == id).findFirst().orElseGet(() -> RankType.UNDEFINED);
    }
}

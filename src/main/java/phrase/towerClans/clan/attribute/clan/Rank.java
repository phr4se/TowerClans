package phrase.towerClans.clan.attribute.clan;

import org.bukkit.configuration.ConfigurationSection;
import phrase.towerClans.Plugin;

import java.util.Arrays;

public class Rank {

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

        public static RankType getRank(int id) {
            return Arrays.stream(RankType.values()).filter(rank -> rank.getId() == id).findFirst().orElseGet(() -> RankType.UNDEFINED);
        }
    }

    public static void initialize(Plugin plugin) {
        ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("settings.rank");

        RankType.LEADER.setName(configurationSection.getString("leader"));
        RankType.DEPUTY.setName(configurationSection.getString("deputy"));
        RankType.MEMBER.setName(configurationSection.getString("member"));
    }
}

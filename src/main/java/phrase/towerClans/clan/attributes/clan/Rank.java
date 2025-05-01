package phrase.towerClans.clan.attributes.clan;

import org.bukkit.configuration.ConfigurationSection;
import phrase.towerClans.Plugin;

public class Rank {

    public enum RankType {
        LEADER(""),
        DEPUTY(""),
        MEMBER("");

        private String name;

        RankType(String name) {
            this.name = name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public static void intialize(Plugin plugin) {
        ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("settings.rank");

        RankType.LEADER.setName(configurationSection.getString("leader"));
        RankType.DEPUTY.setName(configurationSection.getString("deputy"));
        RankType.MEMBER.setName(configurationSection.getString("member"));

    }
}

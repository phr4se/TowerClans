package phrase.towerclans.serializable;

import phrase.towerclans.clan.attribute.player.StatsManager;

public class StatsSerializable {
    public static String statsToString(StatsManager.Stats stats) {
        return stats.getKills() + ":" + stats.getDeaths();
    }

    public static StatsManager.Stats stringToStats(String data) {
        String[] strings = data.split(":");
        return new StatsManager.Stats(Integer.parseInt(strings[0]), Integer.parseInt(strings[1]));
    }
}

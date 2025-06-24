package phrase.towerClans.serializable;

import phrase.towerClans.clan.attribute.player.Stats;

public class StatsSerializable {

    public static String statsToString(Stats stats) {
        return stats.getKills() + ":" + stats.getDeaths();
    }

    public static Stats stringToStats(String data) {
        String[] strings = data.split(":");
        return new Stats(Integer.parseInt(strings[0]), Integer.parseInt(strings[1]));
    }

}

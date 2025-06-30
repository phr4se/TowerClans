package phrase.towerClans.clan.attribute.player;

import phrase.towerClans.clan.entity.ModifiedPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Stats {

    public static final Map<UUID, Stats> PLAYERS = new HashMap<>();

    private int kills;
    private int deaths;

    public Stats(int kills, int deaths) {
        this.kills = kills;
        this.deaths = deaths;
    }

    public static int getKillsMembers(Map<ModifiedPlayer, String> members) {

        int kills = 0;

        for(Map.Entry<ModifiedPlayer, String> entry : members.entrySet()) {

           Stats playerStats = PLAYERS.get(entry.getKey().getPlayerUUID());
           kills += playerStats.getKills();

        }

        return kills;

    }

    public static int getDeathsMembers(Map<ModifiedPlayer, String> members) {

        int deaths = 0;

        for(Map.Entry<ModifiedPlayer, String> entry : members.entrySet()) {

            Stats playerStats = PLAYERS.get(entry.getKey().getPlayerUUID());
            deaths += playerStats.getDeaths();


        }

        return deaths;

    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }
}

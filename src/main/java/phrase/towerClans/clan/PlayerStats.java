package phrase.towerClans.clan;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerStats {

    public static final Map<UUID, PlayerStats> PLAYERS = new HashMap<>();

    private int kills;
    private int deaths;

    public PlayerStats(int kills, int deaths) {
        this.kills = kills;
        this.deaths = deaths;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getKills() {
        return kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public static int getKillsMembers(Map<ModifiedPlayer, String> members) {

        int kills = 0;

        for(Map.Entry<ModifiedPlayer, String> entry : members.entrySet()) {

           PlayerStats playerStats = PLAYERS.get(entry.getKey().getPlayer().getUniqueId());
           kills += playerStats.getKills();


        }

        return kills;

    }

    public static int getDeathMembers(Map<ModifiedPlayer, String> members) {

        int deaths = 0;

        for(Map.Entry<ModifiedPlayer, String> entry : members.entrySet()) {

            PlayerStats playerStats = PLAYERS.get(entry.getKey().getPlayer().getUniqueId());
            deaths += playerStats.getDeaths();


        }

        return deaths;

    }


}

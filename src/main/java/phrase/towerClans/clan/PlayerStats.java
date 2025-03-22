package phrase.towerClans.clan;

import java.util.HashMap;
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


}

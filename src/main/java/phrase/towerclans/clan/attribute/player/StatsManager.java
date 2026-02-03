package phrase.towerclans.clan.attribute.player;

import phrase.towerclans.clan.entity.ModifiedPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StatsManager {
    public static class Stats {
        private int kills;
        private int deaths;

        public Stats(int kills, int deaths) {
            this.kills = kills;
            this.deaths = deaths;
        }

        public int getKills() {
            return kills;
        }

        public void setKills(int kills) {
            this.kills = kills;
        }

        public int getDeaths() {
            return deaths;
        }

        public void setDeaths(int deaths) {
            this.deaths = deaths;
        }
    }

    private final Map<UUID, Stats> players;

    public StatsManager() {
        this.players = new HashMap<>();
    }

    public int getKillsMembers(Map<ModifiedPlayer, String> members) {
        int kills = 0;
        for (Map.Entry<ModifiedPlayer, String> entry : members.entrySet()) {
            Stats playerStats = players.computeIfAbsent(entry.getKey().getPlayerUUID(), k -> new Stats(0, 0));
            kills += playerStats.getKills();
        }
        return kills;
    }

    public int getDeathsMembers(Map<ModifiedPlayer, String> members) {
        int deaths = 0;
        for (Map.Entry<ModifiedPlayer, String> entry : members.entrySet()) {
            Stats playerStats = players.computeIfAbsent(entry.getKey().getPlayerUUID(), k -> new Stats(0, 0));
            deaths += playerStats.getDeaths();
        }
        return deaths;
    }

    public Map<UUID, Stats> getPlayers() {
        return players;
    }
}

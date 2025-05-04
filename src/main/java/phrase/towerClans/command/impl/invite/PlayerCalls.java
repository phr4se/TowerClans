package phrase.towerClans.command.impl.invite;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerCalls {

    private static final Map<UUID, UUID> PLAYERS = new HashMap<>();

    public static void addPlayers(UUID targetPlayer, UUID player) {
        PLAYERS.put(targetPlayer, player);
    }

    public static UUID removePlayers(UUID targetPlayer) {
        if(!PLAYERS.containsKey(targetPlayer)) return null;

        return PLAYERS.remove(targetPlayer);
    }

    public static void removeQuitPlayers(UUID player) {
        for (Map.Entry<UUID, UUID> entry : PLAYERS.entrySet()) {

            if(entry.getKey().equals(player) || entry.getValue().equals(player)) PlayerCalls.removePlayers(entry.getKey());

        }
    }

}

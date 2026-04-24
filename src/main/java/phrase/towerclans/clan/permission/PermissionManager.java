package phrase.towerclans.clan.permission;

import phrase.towerclans.clan.entity.ModifiedPlayer;

import java.util.*;

public class PermissionManager {
    private final Map<UUID, Permission> players = new HashMap<>();
    private int permissionRowIndex = -1;

    public Permission getPermissionsPlayer(ModifiedPlayer type) {
        return players.compute(type.getPlayerUUID(), (k, v) -> (v == null) ? new Permission(new ArrayList<>(), 0) : v);
    }

    public Map<UUID, Permission> getPlayers() {
        return players;
    }

    public void setPermissionRowIndex(int permissionRowIndex) {
        this.permissionRowIndex = permissionRowIndex;
    }

    public int getPermissionRowIndex() {
        return permissionRowIndex;
    }
}

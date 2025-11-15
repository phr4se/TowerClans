package phrase.towerClans.clan.permission;

import org.bukkit.configuration.ConfigurationSection;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.entity.ModifiedPlayer;
import phrase.towerClans.config.Config;

import java.util.*;

public class Permission {

    public static final Map<ModifiedPlayer, Permission> PLAYERS = new HashMap<>();
    public static int permissionRowIndex = -1;

    private final List<PermissionType> permissionTypes;
    private int currentPermission;

    public Permission(List<PermissionType> permissionTypes, int currentPermission) {
        this.permissionTypes = permissionTypes;
        this.currentPermission = currentPermission;
    }

    public void setPermissionPlayer(PermissionType permissionType) {
        getPermissionTypes().add(permissionType);
    }

    public void setPermissionsPlayer(PermissionType... permissionTypes) {
        for (PermissionType permissionType : permissionTypes) setPermissionPlayer(permissionType);
    }

    public void clearPermissionPlayer(PermissionType permissionType) {
       getPermissionTypes().remove(permissionType);
    }

    public void clearPermissionsPlayer(PermissionType... permissionTypes) {
        for (PermissionType permissionType : permissionTypes) clearPermissionPlayer(permissionType);
    }

    public static Permission getPermissionsPlayer(ModifiedPlayer modifiedPlayer) {
        return PLAYERS.compute(modifiedPlayer, (k, v) -> (v == null) ? new Permission(new ArrayList<>(), 0) : v);
    }

    public List<PermissionType> getPermissionTypes() {
        return permissionTypes;
    }

    public int getCurrentPermission() {
        return currentPermission;
    }

    public void setCurrentPermission(int currentPermission) {
        this.currentPermission = currentPermission;
    }

    public boolean hasNextPermission(Plugin plugin) {
        ConfigurationSection configurationSection = Config.getFile(plugin, "menus/menu-clan-members.yml").getConfigurationSection("menu_clan_members");
        return currentPermission < configurationSection.getStringList("permission").size() - 1;
    }
    
}

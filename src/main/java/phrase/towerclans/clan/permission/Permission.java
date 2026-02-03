package phrase.towerclans.clan.permission;

import org.bukkit.configuration.ConfigurationSection;
import phrase.towerclans.config.Config;

import java.util.List;

public class Permission {
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

    public List<PermissionType> getPermissionTypes() {
        return permissionTypes;
    }

    public int getCurrentPermission() {
        return currentPermission;
    }

    public void setCurrentPermission(int currentPermission) {
        this.currentPermission = currentPermission;
    }

    public boolean hasNextPermission() {
        final ConfigurationSection configurationSection = Config.getFile("menus/menu-clan-members.yml").getConfigurationSection("menu_clan_members");
        return currentPermission < configurationSection.getStringList("permission").size() - 1;
    }
}

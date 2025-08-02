package phrase.towerClans.clan.event.privilege;

import org.bukkit.entity.Player;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.event.privilege.impl.EssentialsX;
import phrase.towerClans.clan.event.privilege.impl.Vanilla;

public class PrivilegeManager {

    private static Privilege privilege;

    public static void setPrivilege(String type, Plugin plugin) {
        switch (type) {
            case "Essentials" -> {
                privilege = new EssentialsX();
                privilege.initialize(plugin);
            }
            default -> {
                privilege = new Vanilla();
                privilege.initialize(plugin);
            }
        }

        for(PrivilegeType privilegeType : PrivilegeType.values()) privilegeType.initialize(privilege);
    }

    public static boolean hasPrivilege(Player player) {
        for(PrivilegeType privilegeType : PrivilegeType.values()) {
            if(!privilegeType.getPrivilegeChecker().hasPrivilege(player)) continue;
            return true;
        }

        return false;
    }

    public static void disablePrivilege(Player player) {
        for(PrivilegeType privilegeType : PrivilegeType.values()) {
            privilegeType.getPrivilegeDisabler().disablePrivilege(player);
        }
    }

}

package phrase.towerClans.clan.event.privilege;

import org.bukkit.entity.Player;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.event.privilege.impl.EssentialsX;
import phrase.towerClans.clan.event.privilege.impl.Vanilla;

import java.util.ArrayList;
import java.util.List;

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

    public static List<PrivilegeType> transformation(List<String> list) {
        List<PrivilegeType> transformationList = new ArrayList<>();
        for(String string : list) {
            transformationList.add(PrivilegeType.valueOf(string));
        }
        return transformationList;
    }

    public static boolean hasPrivilege(Player player, List<PrivilegeType> disablePrivilegeType) {
        for(PrivilegeType privilegeType : PrivilegeType.values()) {
            if(!privilegeType.getPrivilegeChecker().hasPrivilege(player) && !disablePrivilegeType.contains(privilegeType)) continue;
            return true;
        }

        return false;
    }

    public static void disablePrivilege(Player player, List<PrivilegeType> disablePrivilegeType) {
        for(PrivilegeType privilegeType : PrivilegeType.values()) {
            if(disablePrivilegeType.contains(privilegeType)) privilegeType.getPrivilegeDisabler().disablePrivilege(player);
        }
    }

}

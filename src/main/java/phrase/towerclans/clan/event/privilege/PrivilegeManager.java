package phrase.towerclans.clan.event.privilege;

import org.bukkit.entity.Player;
import phrase.towerclans.Plugin;
import phrase.towerclans.clan.event.privilege.impl.EssentialsX;
import phrase.towerclans.clan.event.privilege.impl.Vanilla;

import java.util.ArrayList;
import java.util.List;

public class PrivilegeManager {
    public void setPrivilege(String type, Plugin plugin) {
        Privilege privilege;
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
        for (PrivilegeType privilegeType : PrivilegeType.values()) privilegeType.initialize(privilege);
    }

    public List<PrivilegeType> transformation(List<String> list) {
        List<PrivilegeType> transformationList = new ArrayList<>();
        for (String string : list) {
            transformationList.add(PrivilegeType.valueOf(string));
        }
        return transformationList;
    }

    public boolean hasPrivilege(Player player, List<PrivilegeType> disablePrivilegeType) {
        for (PrivilegeType privilegeType : PrivilegeType.values()) {
            if (!privilegeType.getPrivilegeChecker().hasPrivilege(player) && !disablePrivilegeType.contains(privilegeType))
                continue;
            return true;
        }
        return false;
    }

    public void disablePrivilege(Player player, List<PrivilegeType> disablePrivilegeType) {
        for (PrivilegeType privilegeType : PrivilegeType.values()) {
            if (disablePrivilegeType.contains(privilegeType))
                privilegeType.getPrivilegeDisabler().disablePrivilege(player);
        }
    }
}

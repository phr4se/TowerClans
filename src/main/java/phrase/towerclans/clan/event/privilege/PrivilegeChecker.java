package phrase.towerclans.clan.event.privilege;

import org.bukkit.entity.Player;

public interface PrivilegeChecker {
    boolean hasPrivilege(Player player);
}

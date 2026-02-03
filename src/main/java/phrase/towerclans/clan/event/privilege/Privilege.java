package phrase.towerclans.clan.event.privilege;

import phrase.towerclans.Plugin;

public interface Privilege {
    void initialize(Plugin plugin);
    PrivilegeChecker flyChecker();
    PrivilegeDisabler flyDisabler();
    PrivilegeChecker godChecker();
    PrivilegeDisabler godDisabler();
    PrivilegeChecker vanishChecker();
    PrivilegeDisabler vanishDisabler();
}

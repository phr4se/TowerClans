package phrase.towerclans.clan.event.privilege;

import phrase.towerclans.TowerClans;

public interface Privilege {
    void initialize(TowerClans plugin);
    PrivilegeChecker flyChecker();
    PrivilegeDisabler flyDisabler();
    PrivilegeChecker godChecker();
    PrivilegeDisabler godDisabler();
    PrivilegeChecker vanishChecker();
    PrivilegeDisabler vanishDisabler();
}

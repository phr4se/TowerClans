package phrase.towerClans.clan.event.privilege;

public enum PrivilegeType {

    FLY,
    VANISH,
    GOD;

    private PrivilegeChecker privilegeChecker;
    private PrivilegeDisabler privilegeDisabler;

    public void initialize(Privilege privilege) {

        switch (this) {
            case FLY -> {
                privilegeChecker = privilege.flyChecker();
                privilegeDisabler = privilege.flyDisabler();
            }
            case GOD -> {
                privilegeChecker = privilege.godChecker();
                privilegeDisabler = privilege.godDisabler();
            }
            case VANISH -> {
                privilegeChecker = privilege.vanishChecker();
                privilegeDisabler = privilege.vanishDisabler();
            }
        }

    };

    public PrivilegeChecker getPrivilegeChecker() {
        return privilegeChecker;
    }

    public PrivilegeDisabler getPrivilegeDisabler() {
        return privilegeDisabler;
    }
}

package phrase.towerclans.clan.event.privilege.impl;

import phrase.towerclans.Plugin;
import phrase.towerclans.clan.event.privilege.Privilege;
import phrase.towerclans.clan.event.privilege.PrivilegeChecker;
import phrase.towerclans.clan.event.privilege.PrivilegeDisabler;

public class Vanilla implements Privilege {
    @Override
    public void initialize(Plugin plugin) {
        setupFlyChecker();
        setupFlyDisabler();
        setupGodChecker();
        setupGodDisabler();
        setupVanishChecker();
        setupVanishDisabler();
    }

    private PrivilegeChecker flyChecker;
    private PrivilegeDisabler flyDisabler;

    private void setupFlyChecker() {
        flyChecker = player -> player.isFlying();
    }

    private void setupFlyDisabler() {
        flyDisabler = player -> {
            player.setAllowFlight(false);
            player.setFlying(false);
        };
    }

    @Override
    public PrivilegeChecker flyChecker() {
        return flyChecker;
    }

    @Override
    public PrivilegeDisabler flyDisabler() {
        return flyDisabler;
    }

    private PrivilegeChecker godChecker;
    private PrivilegeDisabler godDisabler;

    private void setupGodChecker() {
        godChecker = player -> player.isInvulnerable();
    }

    private void setupGodDisabler() {
        godDisabler = player -> player.setInvulnerable(false);
    }

    @Override
    public PrivilegeChecker godChecker() {
        return godChecker;
    }

    @Override
    public PrivilegeDisabler godDisabler() {
        return godDisabler;
    }

    private PrivilegeChecker vanishChecker;
    private PrivilegeDisabler vanishDisabler;

    private void setupVanishChecker() {
        vanishChecker = player -> player.isInvisible();
    }

    private void setupVanishDisabler() {
        vanishDisabler = player -> player.setInvisible(false);
    }

    @Override
    public PrivilegeChecker vanishChecker() {
        return vanishChecker;
    }

    @Override
    public PrivilegeDisabler vanishDisabler() {
        return vanishDisabler;
    }
}

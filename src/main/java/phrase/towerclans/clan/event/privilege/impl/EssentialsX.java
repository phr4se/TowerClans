package phrase.towerclans.clan.event.privilege.impl;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import phrase.towerclans.TowerClans;
import phrase.towerclans.clan.event.privilege.Privilege;
import phrase.towerclans.clan.event.privilege.PrivilegeChecker;
import phrase.towerclans.clan.event.privilege.PrivilegeDisabler;

public class EssentialsX implements Privilege {
    private Essentials essentials;

    @Override
    public void initialize(TowerClans plugin) {
        essentials = (Essentials) plugin.getServer().getPluginManager().getPlugin("Essentials");
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
        godChecker = player -> {
            User user = essentials.getUser(player);
            return user.isGodModeEnabled();
        };
    }

    private void setupGodDisabler() {
        godDisabler = player -> {
            User user = essentials.getUser(player);
            user.setGodModeEnabled(false);
        };
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
        vanishChecker = player -> {
            User user = essentials.getUser(player);
            return user.isVanished();
        };
    }

    private void setupVanishDisabler() {
        vanishDisabler = player -> {
            User user = essentials.getUser(player);
            user.setVanished(false);
        };
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

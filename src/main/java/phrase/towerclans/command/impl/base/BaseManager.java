package phrase.towerclans.command.impl.base;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import phrase.towerclans.clan.Clan;
import phrase.towerclans.command.impl.base.region.RegionChecker;
import phrase.towerclans.command.impl.base.region.impl.Vanilla;
import phrase.towerclans.command.impl.base.region.impl.WorldGuard;
import phrase.towerclans.config.Config;
import phrase.towerclans.util.Utils;

import java.util.HashMap;
import java.util.Map;

public class BaseManager {
    private final Map<Clan, Location> bases = new HashMap<>();

    private RegionChecker regionChecker;

    public void setRegionChecker(String type) {
        switch (type.toLowerCase()) {
            case "worldguard" -> this.regionChecker = new WorldGuard();
            default -> this.regionChecker = new Vanilla();
        };
    }

    public void setRegionChecker(RegionChecker regionChecker) {
        this.regionChecker = regionChecker;
    }

    public void setBase(Clan clan, Player player, Location location) {
        if(location == null) {
            setBase(clan, null);
            return;
        }
        if(player == null) {
            setBase(clan, location);
            return;
        }
        if(Config.getSettings().blackListWorlds().contains(location.getWorld().getName())) {
            Utils.sendMessage(player, Config.getCommandMessages().blackListWorld());
            return;
        }
        boolean success = regionChecker.containsRegion(player);
        if(player.hasPermission("towerclans.regionchecker.bypass")) {
            success = true;
        }
        if(success) {
            setBase(clan, location);
            Utils.sendMessage(player, Config.getCommandMessages().setBase());
        } else Utils.sendMessage(player, Config.getCommandMessages().notSetBaseAlienRegion());
    }

    private void setBase(Clan clan, Location location) {
        if (bases.containsKey(clan)) {
            bases.replace(clan, bases.get(clan), location);
            return;
        }
        bases.put(clan, location);
    }

    public Location getBase(Clan clan) {
        return bases.get(clan);
    }

    public void removeClan(Clan clan) {
        bases.remove(clan);
    }

}

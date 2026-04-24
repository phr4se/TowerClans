package phrase.towerclans.command.impl.base.region.impl;

import org.bukkit.entity.Player;
import phrase.towerclans.command.impl.base.region.RegionChecker;

public class Vanilla implements RegionChecker {
    @Override
    public boolean containsRegion(Player player) {
        return true;
    }
}

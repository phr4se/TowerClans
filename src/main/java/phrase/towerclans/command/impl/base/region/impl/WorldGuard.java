package phrase.towerclans.command.impl.base.region.impl;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import phrase.towerclans.command.impl.base.region.RegionChecker;

import java.util.Set;

public class WorldGuard implements RegionChecker {
    private final RegionContainer regionContainer;

    public WorldGuard() {
        this.regionContainer = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getRegionContainer();
    }

    @Override
    public boolean containsRegion(Player player) {
        Location location = player.getLocation();
        RegionManager regionManager = regionContainer.get(BukkitAdapter.adapt(location.getWorld()));
        Set<ProtectedRegion> protectedRegionSet = regionManager.getApplicableRegions(BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ())).getRegions();
        return protectedRegionSet.isEmpty() || protectedRegionSet.stream().anyMatch(protectedRegion -> protectedRegion.getOwners().contains(player.getUniqueId()));
    }
}

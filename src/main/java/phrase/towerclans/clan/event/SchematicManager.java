package phrase.towerclans.clan.event;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import phrase.towerclans.Plugin;
import phrase.towerclans.config.Config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SchematicManager {
    private final Plugin plugin;
    private final File file;
    private final Map<Location, Material> blocks;
    private final List<String> regionFlagsName;
    private ProtectedCuboidRegion protectedCuboidRegion;
    private Location pos1;
    private Location pos2;

    public SchematicManager(Plugin plugin, File file, List<String> regionFlagsName) {
        this.plugin = plugin;
        this.file = file;
        this.blocks = new HashMap<>();
        this.regionFlagsName = regionFlagsName;
    }

    public void setSchematic(Location pos1) {
        this.pos1 = pos1;
        try (EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(pos1.getWorld()))) {
            Clipboard clipboard = ClipboardFormats.findByFile(file).getReader(new FileInputStream(file)).read();
            BlockVector3 dimensions = clipboard.getDimensions();
            pos2 = new Location(pos1.getWorld(), pos1.getBlockX() + dimensions.getBlockX() - 1, pos1.getBlockY() + dimensions.getBlockY(), pos1.getBlockZ() + dimensions.getBlockZ() - 1);
            this.protectedCuboidRegion = new ProtectedCuboidRegion(UUID.randomUUID().toString(), BlockVector3.at(pos1.getBlockX(), pos1.getBlockY(), pos1.getBlockZ()), BlockVector3.at(pos2.getBlockX(), pos2.getBlockY(), pos2.getBlockZ()));
            RegionFlag.setRegionFlags(this.protectedCuboidRegion, regionFlagsName);
            WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(pos1.getWorld())).addRegion(protectedCuboidRegion);
            Operation operation = new ClipboardHolder(clipboard).createPaste(editSession).to(BlockVector3.at(pos1.getBlockX(), pos1.getBlockY(), pos1.getBlockZ())).build();
            saveBlocks();
            Operations.complete(operation);
            editSession.flushSession();
        } catch (IOException | WorldEditException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveBlocks() {
        World world = pos1.getWorld();
        for (int x = pos1.getBlockX(); x <= pos2.getBlockX(); x++) {
            for (int y = pos1.getBlockY(); y <= pos2.getBlockY(); y++) {
                for (int z = pos1.getBlockZ(); z <= pos2.getBlockZ(); z++) {
                    Location location = new Location(world, x, y, z);
                    Block block = location.getBlock();
                    blocks.put(location, block.getType());
                    block.setType(Material.AIR);
                }
            }
        }
    }

    public void regenerationBlocks() {
        ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("settings.event.capture");
        boolean teleport = configurationSection.getBoolean("teleport");
        World world = pos1.getWorld();
        for (int x = pos1.getBlockX(); x <= pos2.getBlockX(); x++) {
            for (int y = pos1.getBlockY(); y <= pos2.getBlockY(); y++) {
                for (int z = pos1.getBlockZ(); z <= pos2.getBlockZ(); z++) {
                    Location location = new Location(world, x, y, z);
                    if (teleport && !world.getNearbyEntities(location, 1, 1, 1).isEmpty()) {
                        for (Entity entity : world.getNearbyEntities(location, 1, 1, 1)) {
                            if (entity instanceof Player player) {
                                player.teleport(new Location(world, x, pos2.getBlockY() + 3, z));
                                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 100, 0));
                            }
                        }
                    }
                    Material material = blocks.get(location);
                    location.getBlock().setType(material);
                }
            }
        }
        WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world)).removeRegion(protectedCuboidRegion.getId());
    }

    public Location getPos2() {
        return pos2;
    }

    public boolean existsSchematic() {
        return file.exists();
    }

    public boolean schematicDamaged() {
        return ClipboardFormats.findByFile(file) == null;
    }
}

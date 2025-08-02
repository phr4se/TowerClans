package phrase.towerClans.clan.event;

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
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import phrase.towerClans.Plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SchematicManager {

    private static final Map<Location, Material> BLOCKS = new HashMap<>();

    private File file;
    private final Plugin plugin;

    public SchematicManager(Plugin plugin) {
        file = new File(plugin.getPath());
        this.plugin = plugin;
    }

    public void setSchematic(World world, int minX, int maxX, int minY, int maxY, int minZ, int maxZ){

        saveBlocks(world, minX, maxX, minY, maxY, minZ, maxZ);

        try(EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().world(BukkitAdapter.adapt(world)).build()) {
            Clipboard clipboard = ClipboardFormats.findByFile(file).getReader(new FileInputStream(file)).read();

            Operation operation = new ClipboardHolder(clipboard).createPaste(editSession).to(BlockVector3.at(minX, minY, minZ)).build();

            Operations.complete(operation);
            editSession.flushSession();
        } catch (IOException | WorldEditException e) {
            throw new RuntimeException(e);
        }


    }

    private void saveBlocks(World world, int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
        BLOCKS.clear();

        int height = maxY - minY;

        for(int x = minX; x <= maxX; x++) {

            for(int y = minY - height; y <= maxY; y++) {

                for (int z = minZ; z <= maxZ; z++) {

                    Location location = new Location(world, x, y, z);
                    Material material = location.getBlock().getType();
                    BLOCKS.put(location, material);
                    if(material == Material.AIR) continue;
                    location.getBlock().setType(Material.AIR);

                }

            }

        }

    }

    public void regenerationBlocks(World world, int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {

        ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("settings.event.capture");

        boolean teleport = configurationSection.getBoolean("teleport");

        int height = maxY - minY;

        for(int x = minX; x <= maxX; x++) {

            for(int y = minY - height; y <= maxY; y++) {

                for (int z = minZ; z <= maxZ; z++) {
                    Location location = new Location(world, x, y, z);

                    if(teleport && !world.getNearbyEntities(location, 1, 1, 1).isEmpty()) {
                        for(Entity entity : world.getNearbyEntities(location, 1, 1, 1)) {

                            if(entity instanceof Player player) {
                                player.teleport(new Location(world, x, maxY + 3, z));
                                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 100, 0));
                            }

                        }

                    }

                    Material material = BLOCKS.get(location);
                    location.getBlock().setType(material);
                }


            }

        }

    }

    public boolean existsSchematic() {
        return file.exists();
    }

    public boolean schematicDamaged() {
        return ClipboardFormats.findByFile(file) == null;
    }

}

package phrase.towerclans.glow;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.Equipment;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEquipment;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import phrase.towerclans.TowerClans;
import phrase.towerclans.clan.AbstractClan;
import phrase.towerclans.clan.entity.ModifiedPlayer;
import phrase.towerclans.clan.impl.clan.ClanImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GlowManager {
    private final Map<UUID, Map<ModifiedPlayer, String>> players = new HashMap<>();
    private final TowerClans plugin;

    public GlowManager(TowerClans plugin) {
        this.plugin = plugin;
    }

    public void addPlayer(Player player) {
        ModifiedPlayer o = ModifiedPlayer.get(player);
        ClanImpl clan = (ClanImpl) o.getClan();
        if(clan == null) return;
        UUID playerUUID = player.getUniqueId();
        players.put(playerUUID, clan.getMembers());
        for(Map.Entry<ModifiedPlayer, String> entry : players.get(playerUUID).entrySet()) {
            ModifiedPlayer o1 = entry.getKey();
            Player target = o1.getPlayer();
            if(target != null) {
                if(player.getUniqueId().equals(target.getUniqueId())) continue;
                new BukkitRunnable() {
                    final Color color = ((AbstractClan) o1.getClan()).getColor().getColor();
                    @Override
                    public void run() {
                        PacketEvents.getAPI().getPlayerManager().sendPacket(player, getPacket(target.getEntityId(), phrase.towerclans.glow.Equipment.getEquipment(target, color, new ItemStack(Material.LEATHER_HELMET),
                                new ItemStack(Material.LEATHER_CHESTPLATE),
                                new ItemStack(Material.LEATHER_LEGGINGS),
                                new ItemStack(Material.LEATHER_BOOTS))));
                    }
                }.runTaskAsynchronously(plugin);
            }
        }
    }

    public void removePlayer(Player player) {
        UUID playerUUID = player.getUniqueId();
        for(Map.Entry<ModifiedPlayer, String> entry : players.remove(playerUUID).entrySet()) {
            ModifiedPlayer o1 = entry.getKey();
            Player target = o1.getPlayer();
            if(target != null) {
                if(player.getUniqueId().equals(target.getUniqueId())) continue;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        PacketEvents.getAPI().getPlayerManager().sendPacket(player, getPacket(target.getEntityId(), phrase.towerclans.glow.Equipment.getDefaultEquipment(target)));
                    }
                }.runTaskAsynchronously(plugin);
            }
        }
    }

    public void updatePlayer(Player target, Map<ModifiedPlayer, String> players) {
        if(isEnableForPlayer(target)) addPlayer(target);
        ModifiedPlayer o = ModifiedPlayer.get(target);
        Color color = ((AbstractClan)o.getClan()).getColor().getColor();
        for(ModifiedPlayer modifiedPlayer : players.keySet()) {
            Player player = modifiedPlayer.getPlayer();
            if(target.getUniqueId().equals(player.getUniqueId())) continue;
            if(!isEnableForPlayer(player)) continue;
            new BukkitRunnable() {
                @Override
                public void run() {
                    PacketEvents.getAPI().getPlayerManager().sendPacket(player, getPacket(target.getEntityId(),phrase.towerclans.glow.Equipment.getEquipment(target, color, new ItemStack(Material.LEATHER_HELMET),
                            new ItemStack(Material.LEATHER_CHESTPLATE),
                            new ItemStack(Material.LEATHER_LEGGINGS),
                            new ItemStack(Material.LEATHER_BOOTS))));
                }
            }.runTaskAsynchronously(plugin);
        }
    }

    public void actionsDefaultPlayer(Player target, Map<ModifiedPlayer, String> players) {
        if(isEnableForPlayer(target)) removePlayer(target);
        for(ModifiedPlayer modifiedPlayer : players.keySet()) {
            Player player = modifiedPlayer.getPlayer();
            if(target.getUniqueId().equals(player.getUniqueId())) continue;
            if(!isEnableForPlayer(player)) continue;
            new BukkitRunnable() {
                @Override
                public void run() {
                    PacketEvents.getAPI().getPlayerManager().sendPacket(player, getPacket(target.getEntityId(), phrase.towerclans.glow.Equipment.getDefaultEquipment(target)));
                }
            }.runTaskAsynchronously(plugin);
        }
    }

    public boolean isEnableForPlayer(Player player) {
        UUID playerUUID = player.getUniqueId();
        return players.containsKey(playerUUID);
    }

    public WrapperPlayServerEntityEquipment getPacket(int entityId, List<Equipment> equipment) {
        return new WrapperPlayServerEntityEquipment(entityId, equipment);
    }

    public boolean isTargetForPlayer(Player player, Player target) {
        UUID playerUUID = player.getUniqueId();
        if(!players.containsKey(playerUUID)) return false;
        ModifiedPlayer o = ModifiedPlayer.get(target);
        return players.get(playerUUID).keySet().stream().anyMatch(o1 -> o1.getPlayerUUID().equals(o.getPlayerUUID()));
    }

}

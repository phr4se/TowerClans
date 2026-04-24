package phrase.towerclans.glow;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEquipment;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import phrase.towerclans.TowerClans;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import phrase.towerclans.clan.AbstractClan;
import phrase.towerclans.clan.entity.ModifiedPlayer;
import phrase.towerclans.clan.impl.clan.ClanImpl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class GlowPacketListener implements PacketListener {
    private final TowerClans plugin;
    private final GlowManager glowManager;

    public GlowPacketListener(TowerClans plugin) {
        this.plugin = plugin;
        this.glowManager = plugin.getGlowManager();
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        PacketTypeCommon packetTypeCommon = event.getPacketType();
        if (packetTypeCommon != PacketType.Play.Server.SPAWN_PLAYER && packetTypeCommon != PacketType.Play.Server.ENTITY_EQUIPMENT)
            return;
        int entityId = getEntityId(packetTypeCommon, event);
        if (entityId == -1) return;
        Player player = event.getPlayer();
        Entity entity = SpigotConversionUtil.getEntityById(player.getWorld(), entityId);
        if (!(entity instanceof Player target)) return;
        ModifiedPlayer o = ModifiedPlayer.get(player);
        ModifiedPlayer o1 = ModifiedPlayer.get(target);
        ClanImpl c = (ClanImpl) o.getClan();
        ClanImpl c1 = (ClanImpl) o1.getClan();
        if (c == null || c1 == null) return;
        final Color color = ((AbstractClan) o1.getClan()).getColor().getColor();
        if(!glowManager.isTargetForPlayer(player, target)) return;
        if (packetTypeCommon == PacketType.Play.Server.ENTITY_EQUIPMENT) {
            Object buffer = getBuffer(event, Equipment.getEquipment(target, color, new ItemStack(Material.LEATHER_HELMET),
                    new ItemStack(Material.LEATHER_CHESTPLATE),
                    new ItemStack(Material.LEATHER_LEGGINGS),
                    new ItemStack(Material.LEATHER_BOOTS)));
            event.setByteBuf(buffer);
        } else {
            new BukkitRunnable() {
                @Override
                public void run() {
                    PacketEvents.getAPI().getPlayerManager().sendPacket(player, new WrapperPlayServerEntityEquipment(target.getEntityId(), Equipment.getEquipment(target, color, new ItemStack(Material.LEATHER_HELMET),
                            new ItemStack(Material.LEATHER_CHESTPLATE),
                            new ItemStack(Material.LEATHER_LEGGINGS),
                            new ItemStack(Material.LEATHER_BOOTS))));
                }
            }.runTaskAsynchronously(plugin);
        }
    }

    public int getEntityId(PacketTypeCommon packetTypeCommon, PacketSendEvent packetSendEvent) {
        Class<? extends PacketWrapper<?>> clazz = packetTypeCommon.getWrapperClass();
        if (clazz == null) return -1;
        try {
            Constructor<? extends PacketWrapper<?>> constructor = clazz.getDeclaredConstructor(PacketSendEvent.class);
            PacketWrapper<?> packetWrapper = constructor.newInstance(packetSendEvent);
            Method method = clazz.getMethod("getEntityId");
            return (int) method.invoke(packetWrapper);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            return -1;
        }
    }

    public Object getBuffer(PacketSendEvent packetSendEvent, List<com.github.retrooper.packetevents.protocol.player.Equipment> equipment) {
        WrapperPlayServerEntityEquipment wrapperPlayServerEntityEquipment = new WrapperPlayServerEntityEquipment(packetSendEvent);
        wrapperPlayServerEntityEquipment.setEquipment(equipment);
        wrapperPlayServerEntityEquipment.write();
        return wrapperPlayServerEntityEquipment.buffer;
    }
}

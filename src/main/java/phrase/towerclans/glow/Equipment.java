package phrase.towerclans.glow;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;

import java.util.ArrayList;
import java.util.List;

public class Equipment {
    public enum Equip {
        HELMET(EquipmentSlot.HEAD, com.github.retrooper.packetevents.protocol.player.EquipmentSlot.HELMET),
        CHEST_PLATE(EquipmentSlot.CHEST, com.github.retrooper.packetevents.protocol.player.EquipmentSlot.CHEST_PLATE),
        LEGGINGS(EquipmentSlot.LEGS, com.github.retrooper.packetevents.protocol.player.EquipmentSlot.LEGGINGS),
        BOOTS(EquipmentSlot.FEET, com.github.retrooper.packetevents.protocol.player.EquipmentSlot.BOOTS);
        public final EquipmentSlot bukkitSlot;
        public final com.github.retrooper.packetevents.protocol.player.EquipmentSlot packetSlot;

        Equip(EquipmentSlot bukkitSlot, com.github.retrooper.packetevents.protocol.player.EquipmentSlot packetSlot) {
            this.bukkitSlot = bukkitSlot;
            this.packetSlot = packetSlot;
        }
    }

    public static List<com.github.retrooper.packetevents.protocol.player.Equipment> getEquipment(Player player, Color color, ItemStack... itemStacks) {
        List<com.github.retrooper.packetevents.protocol.player.Equipment> equipment = new ArrayList<>();
        EntityEquipment entityEquipment = player.getEquipment();
        Equip[] equips = Equip.values();
        for (int i = 0; i < itemStacks.length; i++) {
            if(entityEquipment.getItem(equips[i].bukkitSlot).getType() == Material.AIR) {
                equipment.add(new com.github.retrooper.packetevents.protocol.player.Equipment(equips[i].packetSlot, SpigotConversionUtil.fromBukkitItemStack(new ItemStack(Material.AIR))));
                continue;
            }
            ItemStack itemStack = itemStacks[i];
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta instanceof LeatherArmorMeta leatherArmorMeta) {
                leatherArmorMeta.setColor(color);
            }
            itemStack.setItemMeta(itemMeta);
            equipment.add(new com.github.retrooper.packetevents.protocol.player.Equipment(equips[i].packetSlot, SpigotConversionUtil.fromBukkitItemStack(itemStack)));
        }
        return equipment;
    }

    public static List<com.github.retrooper.packetevents.protocol.player.Equipment> getDefaultEquipment(Player player) {
        List<com.github.retrooper.packetevents.protocol.player.Equipment> equipment = new ArrayList<>();
        EntityEquipment entityEquipment = player.getEquipment();
        for(Equip equip : Equip.values()) {
            ItemStack itemStack = entityEquipment.getItem(equip.bukkitSlot);
            if(itemStack.getType() == Material.AIR) continue;
            equipment.add(new com.github.retrooper.packetevents.protocol.player.Equipment(equip.packetSlot, SpigotConversionUtil.fromBukkitItemStack(itemStack)));
        }
        return equipment;
    }
}

package phrase.towerClans.glow;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.Plugin;
import phrase.towerClans.clan.entity.ModifiedPlayer;
import phrase.towerClans.clan.impl.ClanImpl;
import org.bukkit.Color;

import java.util.ArrayList;
import java.util.List;

public class GlowPacketListener extends PacketAdapter {

    public GlowPacketListener(Plugin plugin, PacketType... types) {
        super(plugin, types);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        Player sender = null;
        Player receiver = event.getPlayer();
        PacketContainer container = event.getPacket();

        int entityId = event.getPacket().getIntegers().read(0);

        for(Player player : plugin.getServer().getOnlinePlayers()) {
            if(player.getEntityId() != entityId) continue;
            sender = player;
            break;
        }

        if(sender == null) return;

        ModifiedPlayer o1 = ModifiedPlayer.get(sender);
        ModifiedPlayer o2 = ModifiedPlayer.get(receiver);

        if(isMember(o1, o2) && sender != receiver && Glow.isEnableForPlayer(o2)) {

            ClanImpl clan = (ClanImpl) o2.getClan();
            Glow.LeatherColor color = clan.getColor();

            List<Pair<EnumWrappers.ItemSlot, ItemStack>> list = new ArrayList<>();
            LeatherArmorMeta leatherArmorMeta;

            PlayerInventory playerInventory = sender.getInventory();
            ItemStack head;
            if(playerInventory.getHelmet() != null && playerInventory.getHelmet().getType() != Material.AIR) {
                head = new ItemStack(Material.LEATHER_HELMET);
                leatherArmorMeta = (LeatherArmorMeta) head.getItemMeta();
                leatherArmorMeta.setColor(Color.fromRGB(color.getR(), color.getG(), color.getB()));
                head.setItemMeta(leatherArmorMeta);
            } else head = playerInventory.getHelmet();
            ItemStack chest;
            if(playerInventory.getChestplate() != null && playerInventory.getChestplate().getType() != Material.AIR) {
                chest = new ItemStack(Material.LEATHER_CHESTPLATE);
                leatherArmorMeta = (LeatherArmorMeta) chest.getItemMeta();
                leatherArmorMeta.setColor(Color.fromRGB(color.getR(), color.getG(), color.getB()));
                chest.setItemMeta(leatherArmorMeta);
            } else chest = playerInventory.getChestplate();
            ItemStack legs;
            if(playerInventory.getLeggings() != null && playerInventory.getLeggings().getType() != Material.AIR) {
                legs = new ItemStack(Material.LEATHER_LEGGINGS);
                leatherArmorMeta = (LeatherArmorMeta) legs.getItemMeta();
                leatherArmorMeta.setColor(Color.fromRGB(color.getR(), color.getG(), color.getB()));
                legs.setItemMeta(leatherArmorMeta);
            } else legs = playerInventory.getLeggings();
            ItemStack feet;
            if(playerInventory.getBoots() != null && playerInventory.getBoots().getType() != Material.AIR) {
                feet = new ItemStack(Material.LEATHER_BOOTS);
                leatherArmorMeta = (LeatherArmorMeta) feet.getItemMeta();
                leatherArmorMeta.setColor(Color.fromRGB(color.getR(), color.getG(), color.getB()));
                feet.setItemMeta(leatherArmorMeta);
            } else feet = sender.getInventory().getBoots();
            ItemStack mainhand = sender.getInventory().getItemInMainHand();
            ItemStack offhand = sender.getInventory().getItemInOffHand();

            list.add(new Pair<>(EnumWrappers.ItemSlot.HEAD, head));
            list.add(new Pair<>(EnumWrappers.ItemSlot.CHEST, chest));
            list.add(new Pair<>(EnumWrappers.ItemSlot.LEGS, legs));
            list.add(new Pair<>(EnumWrappers.ItemSlot.FEET, feet));
            list.add(new Pair<>(EnumWrappers.ItemSlot.OFFHAND, offhand));
            list.add(new Pair<>(EnumWrappers.ItemSlot.MAINHAND, mainhand));
            container.getSlotStackPairLists().write(0, list);
        } else {
            List<Pair<EnumWrappers.ItemSlot, ItemStack>> list = new ArrayList<>();

            PlayerInventory playerInventory = sender.getInventory();
            list.add(new Pair<>(EnumWrappers.ItemSlot.HEAD, playerInventory.getHelmet()));
            list.add(new Pair<>(EnumWrappers.ItemSlot.CHEST, playerInventory.getChestplate()));
            list.add(new Pair<>(EnumWrappers.ItemSlot.LEGS, playerInventory.getLeggings()));
            list.add(new Pair<>(EnumWrappers.ItemSlot.FEET, playerInventory.getBoots()));
            list.add(new Pair<>(EnumWrappers.ItemSlot.OFFHAND, playerInventory.getItemInOffHand()));
            list.add(new Pair<>(EnumWrappers.ItemSlot.MAINHAND, playerInventory.getItemInMainHand()));

            container.getSlotStackPairLists().write(0, list);
        }

    }

    private boolean isMember(ModifiedPlayer o1, ModifiedPlayer o2) {
        if(o1.getClan() == null || o2.getClan() == null) return false;
        return ((ClanImpl) o1.getClan()).getName().equals(((ClanImpl) o2.getClan()).getName());
    }

}

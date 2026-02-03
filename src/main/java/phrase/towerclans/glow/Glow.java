package phrase.towerclans.glow;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import phrase.towerclans.clan.entity.ModifiedPlayer;
import phrase.towerclans.clan.impl.clan.ClanImpl;

import java.util.*;

public class Glow {
    public enum LeatherColor {
        RED(0, 255, 0, 0),
        GREEN(1, 0, 255, 0),
        BLUE(2, 0, 0, 255);
        private final int id;
        private final int r;
        private final int g;
        private final int b;

        LeatherColor(int id, int r, int g, int b) {
            this.id = id;
            this.r = r;
            this.g = g;
            this.b = b;
        }

        public int getId() {
            return id;
        }

        public int getR() {
            return r;
        }

        public int getG() {
            return g;
        }

        public int getB() {
            return b;
        }

        public static LeatherColor getLeaherColor(int id) {
            for (LeatherColor leatherColor : LeatherColor.values()) {
                if (leatherColor.getId() == id) return leatherColor;
            }
            return null;
        }
    }

    private final static Set<UUID> PLAYERS = new HashSet<>();

    public static void enableForPlayer(ModifiedPlayer modifiedPlayer) {
        PLAYERS.add(modifiedPlayer.getPlayerUUID());
        changeForPlayer(modifiedPlayer, true);
    }

    public static void disableForPlayer(ModifiedPlayer modifiedPlayer) {
        PLAYERS.remove(modifiedPlayer.getPlayerUUID());
        changeForPlayer(modifiedPlayer, true);
    }

    public static boolean isEnableForPlayer(ModifiedPlayer modifiedPlayer) {
        return PLAYERS.contains(modifiedPlayer.getPlayerUUID());
    }

    public static void changeForPlayer(ModifiedPlayer modifiedPlayer, boolean isReceiver) {
        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
        if (clan == null) return;
        Map<ModifiedPlayer, String> players = clan.getMembers();
        if (!isReceiver) {
            Player sender = modifiedPlayer.getPlayer();
            if (sender == null) return;
            ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
            PacketContainer container = protocolManager.createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
            container.getIntegers().write(0, sender.getEntityId());
            PlayerInventory playerInventory = sender.getInventory();
            List<Pair<EnumWrappers.ItemSlot, ItemStack>> list = new ArrayList<>();
            ItemStack head = playerInventory.getHelmet();
            ItemStack chest = playerInventory.getChestplate();
            ItemStack legs = playerInventory.getLeggings();
            ItemStack feet = playerInventory.getBoots();
            ItemStack mainhand = playerInventory.getItemInMainHand();
            ItemStack offhand = playerInventory.getItemInOffHand();
            list.add(new Pair<>(EnumWrappers.ItemSlot.HEAD, head));
            list.add(new Pair<>(EnumWrappers.ItemSlot.CHEST, chest));
            list.add(new Pair<>(EnumWrappers.ItemSlot.LEGS, legs));
            list.add(new Pair<>(EnumWrappers.ItemSlot.FEET, feet));
            list.add(new Pair<>(EnumWrappers.ItemSlot.OFFHAND, offhand));
            list.add(new Pair<>(EnumWrappers.ItemSlot.MAINHAND, mainhand));
            container.getSlotStackPairLists().write(0, list);
            protocolManager.broadcastServerPacket(container);
            return;
        }
        Player receiver = modifiedPlayer.getPlayer();
        for (Map.Entry<ModifiedPlayer, String> entry : players.entrySet()) {
            ModifiedPlayer key = entry.getKey();
            Player sender = key.getPlayer();
            if (sender == null) continue;
            ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
            PacketContainer container = protocolManager.createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
            container.getIntegers().write(0, sender.getEntityId());
            PlayerInventory playerInventory = sender.getInventory();
            List<Pair<EnumWrappers.ItemSlot, ItemStack>> list = new ArrayList<>();
            ItemStack head = playerInventory.getHelmet();
            ItemStack chest = playerInventory.getChestplate();
            ItemStack legs = playerInventory.getLeggings();
            ItemStack feet = playerInventory.getBoots();
            ItemStack mainhand = playerInventory.getItemInMainHand();
            ItemStack offhand = playerInventory.getItemInOffHand();
            list.add(new Pair<>(EnumWrappers.ItemSlot.HEAD, head));
            list.add(new Pair<>(EnumWrappers.ItemSlot.CHEST, chest));
            list.add(new Pair<>(EnumWrappers.ItemSlot.LEGS, legs));
            list.add(new Pair<>(EnumWrappers.ItemSlot.FEET, feet));
            list.add(new Pair<>(EnumWrappers.ItemSlot.OFFHAND, offhand));
            list.add(new Pair<>(EnumWrappers.ItemSlot.MAINHAND, mainhand));
            container.getSlotStackPairLists().write(0, list);
            protocolManager.sendServerPacket(receiver, container);
        }
    }

    public static void changeForPlayer(ModifiedPlayer modifiedPlayer, boolean isReceiver, Map<ModifiedPlayer, String> players) {
        if (!isReceiver) {
            Player sender = modifiedPlayer.getPlayer();
            if (sender == null) return;
            ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
            PacketContainer packetContainer = protocolManager.createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
            packetContainer.getIntegers().write(0, sender.getEntityId());
            PlayerInventory playerInventory = sender.getInventory();
            List<Pair<EnumWrappers.ItemSlot, ItemStack>> list = new ArrayList<>();
            ItemStack head = playerInventory.getHelmet();
            ItemStack chest = playerInventory.getChestplate();
            ItemStack legs = playerInventory.getLeggings();
            ItemStack feet = playerInventory.getBoots();
            ItemStack mainhand = playerInventory.getItemInMainHand();
            ItemStack offhand = playerInventory.getItemInOffHand();
            list.add(new Pair<>(EnumWrappers.ItemSlot.HEAD, head));
            list.add(new Pair<>(EnumWrappers.ItemSlot.CHEST, chest));
            list.add(new Pair<>(EnumWrappers.ItemSlot.LEGS, legs));
            list.add(new Pair<>(EnumWrappers.ItemSlot.FEET, feet));
            list.add(new Pair<>(EnumWrappers.ItemSlot.OFFHAND, offhand));
            list.add(new Pair<>(EnumWrappers.ItemSlot.MAINHAND, mainhand));
            packetContainer.getSlotStackPairLists().write(0, list);
            protocolManager.broadcastServerPacket(packetContainer);
            return;
        }
        Player receiver = modifiedPlayer.getPlayer();
        for (Map.Entry<ModifiedPlayer, String> entry : players.entrySet()) {
            ModifiedPlayer key = entry.getKey();
            Player sender = key.getPlayer();
            if (sender == null) continue;
            ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
            PacketContainer packetContainer = protocolManager.createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
            packetContainer.getIntegers().write(0, modifiedPlayer.getPlayer().getEntityId());
            PlayerInventory playerInventory = sender.getInventory();
            List<Pair<EnumWrappers.ItemSlot, ItemStack>> list = new ArrayList<>();
            ItemStack head = playerInventory.getHelmet();
            ItemStack chest = playerInventory.getChestplate();
            ItemStack legs = playerInventory.getLeggings();
            ItemStack feet = playerInventory.getBoots();
            ItemStack mainhand = playerInventory.getItemInMainHand();
            ItemStack offhand = playerInventory.getItemInOffHand();
            list.add(new Pair<>(EnumWrappers.ItemSlot.HEAD, head));
            list.add(new Pair<>(EnumWrappers.ItemSlot.CHEST, chest));
            list.add(new Pair<>(EnumWrappers.ItemSlot.LEGS, legs));
            list.add(new Pair<>(EnumWrappers.ItemSlot.FEET, feet));
            list.add(new Pair<>(EnumWrappers.ItemSlot.OFFHAND, offhand));
            list.add(new Pair<>(EnumWrappers.ItemSlot.MAINHAND, mainhand));
            packetContainer.getSlotStackPairLists().write(0, list);
            protocolManager.sendServerPacket(receiver, packetContainer);
        }
    }
}

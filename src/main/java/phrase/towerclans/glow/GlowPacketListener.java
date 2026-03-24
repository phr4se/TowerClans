package phrase.towerclans.glow;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.Plugin;
import phrase.towerclans.clan.Clan;
import phrase.towerclans.clan.entity.ModifiedPlayer;
import phrase.towerclans.clan.impl.clan.ClanImpl;
import org.bukkit.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GlowPacketListener extends PacketAdapter {
    public static final Map<Integer, Player> CACHE;
    public static final Map<String, Map<EnumWrappers.ItemSlot, ItemStack>> CACHE_ARMOR;

    static {
        CACHE = new HashMap<>();
        CACHE_ARMOR = new HashMap<>();
    }

    public GlowPacketListener(Plugin plugin, PacketType... types) {
        super(plugin, types);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        final Player receiver = event.getPlayer();
        final PacketContainer container = event.getPacket();
        final Player sender = CACHE.get(event.getPacket().getIntegers().read(0));
        if (sender == null) return;
        final ModifiedPlayer o1 = ModifiedPlayer.get(sender);
        final ModifiedPlayer o2 = ModifiedPlayer.get(receiver);
        if (isMember(o1, o2) && sender != receiver && Glow.isEnableForPlayer(o2)) {
            final ClanImpl clan = (ClanImpl) o2.getClan();
            final Map<EnumWrappers.ItemSlot, ItemStack> map = setupArmor(clan);
            final PlayerInventory playerInventory = sender.getInventory();
            final List<Pair<EnumWrappers.ItemSlot, ItemStack>> pairs = new ArrayList<>();
            final ItemStack head;
            if (playerInventory.getHelmet() != null) head = map.get(EnumWrappers.ItemSlot.HEAD);
            else head = playerInventory.getHelmet();
            final ItemStack chest;
            if (playerInventory.getChestplate() != null) chest = map.get(EnumWrappers.ItemSlot.CHEST);
            else chest = playerInventory.getChestplate();
            final ItemStack legs;
            if (playerInventory.getLeggings() != null) legs = map.get(EnumWrappers.ItemSlot.LEGS);
            else legs = playerInventory.getLeggings();
            final ItemStack feet;
            if (playerInventory.getBoots() != null) feet = map.get(EnumWrappers.ItemSlot.FEET);
            else feet = sender.getInventory().getBoots();
            final ItemStack mainHand = sender.getInventory().getItemInMainHand();
            final ItemStack offhand = sender.getInventory().getItemInOffHand();
            pairs.add(new Pair<>(EnumWrappers.ItemSlot.HEAD, head));
            pairs.add(new Pair<>(EnumWrappers.ItemSlot.CHEST, chest));
            pairs.add(new Pair<>(EnumWrappers.ItemSlot.LEGS, legs));
            pairs.add(new Pair<>(EnumWrappers.ItemSlot.FEET, feet));
            pairs.add(new Pair<>(EnumWrappers.ItemSlot.OFFHAND, offhand));
            pairs.add(new Pair<>(EnumWrappers.ItemSlot.MAINHAND, mainHand));
            container.getSlotStackPairLists().write(0, pairs);
        } else container.getSlotStackPairLists().write(0, setupDefaultArmor(sender));
    }

    private Map<EnumWrappers.ItemSlot, ItemStack> setupArmor(Clan clan) {
        ClanImpl target = (ClanImpl) clan;
        if(CACHE_ARMOR.containsKey(target.getName())) return CACHE_ARMOR.get(target.getName());
        final Map<EnumWrappers.ItemSlot, ItemStack> map = new HashMap<>();
        final Glow.LeatherColor color = target.getColor();
        LeatherArmorMeta leatherArmorMeta;
        final ItemStack head = new ItemStack(Material.LEATHER_HELMET);
        leatherArmorMeta = (LeatherArmorMeta) head.getItemMeta();
        leatherArmorMeta.setColor(Color.fromRGB(color.getR(), color.getG(), color.getB()));
        head.setItemMeta(leatherArmorMeta);
        map.put(EnumWrappers.ItemSlot.HEAD, head);
        final ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE);
        leatherArmorMeta = (LeatherArmorMeta) chest.getItemMeta();
        leatherArmorMeta.setColor(Color.fromRGB(color.getR(), color.getG(), color.getB()));
        chest.setItemMeta(leatherArmorMeta);
        map.put(EnumWrappers.ItemSlot.CHEST, chest);
        final ItemStack legs = new ItemStack(Material.LEATHER_LEGGINGS);
        leatherArmorMeta = (LeatherArmorMeta) legs.getItemMeta();
        leatherArmorMeta.setColor(Color.fromRGB(color.getR(), color.getG(), color.getB()));
        legs.setItemMeta(leatherArmorMeta);
        map.put(EnumWrappers.ItemSlot.LEGS, legs);
        final ItemStack feet = new ItemStack(Material.LEATHER_BOOTS);
        leatherArmorMeta = (LeatherArmorMeta) feet.getItemMeta();
        leatherArmorMeta.setColor(Color.fromRGB(color.getR(), color.getG(), color.getB()));
        feet.setItemMeta(leatherArmorMeta);
        map.put(EnumWrappers.ItemSlot.FEET, feet);
        CACHE_ARMOR.put(target.getName(), map);
        return map;
    }

    private List<Pair<EnumWrappers.ItemSlot, ItemStack>> setupDefaultArmor(Player player) {
        final List<Pair<EnumWrappers.ItemSlot, ItemStack>> pairs = new ArrayList<>();
        PlayerInventory playerInventory = player.getInventory();
        pairs.add(new Pair<>(EnumWrappers.ItemSlot.HEAD, playerInventory.getHelmet()));
        pairs.add(new Pair<>(EnumWrappers.ItemSlot.CHEST, playerInventory.getChestplate()));
        pairs.add(new Pair<>(EnumWrappers.ItemSlot.LEGS, playerInventory.getLeggings()));
        pairs.add(new Pair<>(EnumWrappers.ItemSlot.FEET, playerInventory.getBoots()));
        pairs.add(new Pair<>(EnumWrappers.ItemSlot.OFFHAND, playerInventory.getItemInOffHand()));
        pairs.add(new Pair<>(EnumWrappers.ItemSlot.MAINHAND, playerInventory.getItemInMainHand()));
        return pairs;
    }

    private boolean isMember(ModifiedPlayer o1, ModifiedPlayer o2) {
        if (o1.getClan() == null || o2.getClan() == null) return false;
        return ((ClanImpl) o1.getClan()).getName().equals(((ClanImpl) o2.getClan()).getName());
    }
}

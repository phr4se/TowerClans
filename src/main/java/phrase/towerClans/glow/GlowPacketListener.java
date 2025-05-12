package phrase.towerClans.glow;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import phrase.towerClans.clan.entity.ModifiedPlayer;

import java.util.ArrayList;
import java.util.List;

public class GlowPacketListener extends PacketAdapter {

    public GlowPacketListener(Plugin plugin, PacketType... types) {
        super(plugin, types);
    }

    @Override
    public void onPacketSending(PacketEvent e) {
        Player sender = null; // отправитель пакета
        Player receiver = e.getPlayer(); // получатель пакета
        PacketContainer container = e.getPacket();

        int en = e.getPacket().getIntegers().read(0);

        // fixme нужно переделать
        // todo не обязательно
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (en==p.getEntityId()) {
                sender = p;
            }
        }

        ModifiedPlayer modifiedSender = ModifiedPlayer.get(sender);
        ModifiedPlayer modifiedReceiver = ModifiedPlayer.get(receiver);

        if (sender != null
                && Glow.getEnabledPlayers().contains(modifiedReceiver)
                && Glow.isMember(modifiedSender, modifiedReceiver)
                && receiver != sender) {

            List<Pair<EnumWrappers.ItemSlot, ItemStack>> list = new ArrayList<>();
            ItemStack head = new ItemStack(Material.LEATHER_HELMET);
            ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE);
            ItemStack legs = new ItemStack(Material.LEATHER_LEGGINGS);
            ItemStack feet = new ItemStack(Material.LEATHER_BOOTS);
            ItemStack mainhand = sender.getInventory().getItemInMainHand();
            ItemStack offhand = sender.getInventory().getItemInOffHand();
            Glow.LeatherColor color = modifiedReceiver.getSelectedColor();

            color.setColor(head);
            color.setColor(chest);
            color.setColor(legs);
            color.setColor(feet);

            list.add(new Pair<>(EnumWrappers.ItemSlot.HEAD, head));
            list.add(new Pair<>(EnumWrappers.ItemSlot.CHEST, chest));
            list.add(new Pair<>(EnumWrappers.ItemSlot.LEGS, legs));
            list.add(new Pair<>(EnumWrappers.ItemSlot.FEET, feet));
            list.add(new Pair<>(EnumWrappers.ItemSlot.OFFHAND, offhand));
            list.add(new Pair<>(EnumWrappers.ItemSlot.MAINHAND, mainhand));

            container.getSlotStackPairLists().write(0,list);

            e.setPacket(container);
        }
    }

}


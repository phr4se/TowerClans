package phrase.towerClans.glow;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.Clan;
import phrase.towerClans.clan.entity.ModifiedPlayer;
import phrase.towerClans.event.ClanGlowDisableEvent;
import phrase.towerClans.event.ClanGlowEnableEvent;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

public class Glow {
    private static final HashSet<ModifiedPlayer> enabledGlowing = new HashSet<>();

    /**
     * @param modifiedPlayer игрок которому нужно включить подсветку тимейтов
     * @param runnable не обязательно, можно использовать для отправки сообщения
     */
    public static void enableForPlayer(@NonNull ModifiedPlayer modifiedPlayer, @Nullable Runnable... runnable) {
        Player player = modifiedPlayer.getPlayer();

        if (player == null) {
            return;
        }
        ClanGlowEnableEvent event = new ClanGlowEnableEvent(modifiedPlayer);
        Bukkit.getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            enabledGlowing.add(modifiedPlayer);

            broadcastChange();

        }

        executeRunnable(runnable);
    }

    public static boolean isGlowEnabled(ModifiedPlayer modifiedPlayer) {
        return enabledGlowing.contains(modifiedPlayer);
    }

    /**
     * @param modifiedPlayer игрок которому нужно отключить подсветку тимейтов
     * @param runnable не обязательно, можно использовать для отправки сообщения
     */
    public static void disableForPlayer(@NonNull ModifiedPlayer modifiedPlayer, @Nullable Runnable... runnable) {

        ClanGlowDisableEvent event = new ClanGlowDisableEvent(modifiedPlayer);
        Bukkit.getPluginManager().callEvent(event);

        if (enabledGlowing.contains(modifiedPlayer) && !event.isCancelled()) {

            enabledGlowing.remove(modifiedPlayer);
            broadcastChange();

        }

        executeRunnable(runnable);
    }

    protected static void executeRunnable(Runnable... runnable) {
        if (runnable != null) {
            Optional<Runnable> run = Arrays.stream(runnable).findFirst();
            run.ifPresent(Runnable::run);
        }
    }

    public static boolean isMember(ModifiedPlayer sender, ModifiedPlayer receiver) {
        Clan senderClan = sender.getClan();
        Clan receiverClan = receiver.getClan();

        if (senderClan == null || receiverClan == null) return false;

        return senderClan.equals(receiverClan);
    }

    public static HashSet<ModifiedPlayer> getEnabledPlayers() {
        return enabledGlowing;
    }

    /**
     * Уведомляет сервер о том, что игрок сменил броню,
     * нужно чтобы броня начала отображаться.
     */
    public static void broadcastChange() {
        PacketContainer container = ProtocolLibrary.getProtocolManager()
                .createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);

        ProtocolLibrary.getProtocolManager()
                .broadcastServerPacket(container);
    }

    public static final class LeatherColor {
        private final int r;
        private final int g;
        private final int b;

        public LeatherColor(int r, int g, int b) {
            this.r = r;
            this.g = g;
            this.b = b;
        }

        public void setColor(ItemStack item) {
            LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
            meta.setColor(Color.fromRGB(r,g,b));
            meta.addEnchant(Enchantment.DURABILITY,1,true);
            meta.getPersistentDataContainer().set(Plugin.getNamespacedKey(),PersistentDataType.STRING, "armor");
            item.setItemMeta(meta);
        }

        @Override
        public String toString() {
            return r+","+g+","+b;
        }
    }
}

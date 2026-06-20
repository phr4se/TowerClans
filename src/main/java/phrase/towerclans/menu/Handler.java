package phrase.towerclans.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import phrase.towerclans.clan.entity.ModifiedPlayer;

import java.util.Set;

public interface Handler {
    default void handleClick(ClickType clickType, Player player, PersistentDataContainer persistentDataContainer, Class<? extends Cancellable> clazz, Object object, Object... args) {}
    default void handleDrag(Set<Integer> rawSlots, Class<? extends Cancellable> clazz, Object object) {}
    default void handleClose(ModifiedPlayer modifiedPlayer, ItemStack[] contents) {}
    default void handleOpen(ModifiedPlayer modifiedPlayer) {}
    default boolean allowFastMoving(ItemStack itemStack, int slot) {
        return true;
    }
}

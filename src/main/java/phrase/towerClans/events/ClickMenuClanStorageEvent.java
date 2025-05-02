package phrase.towerClans.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import phrase.towerClans.clan.Clan;

public class ClickMenuClanStorageEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Clan clan;
    private final Player player;
    private final Inventory storage;
    private final InventoryClickEvent event;

    public ClickMenuClanStorageEvent(Clan clan, Player player, Inventory storage, InventoryClickEvent event) {
        this.clan = clan;
        this.player = player;
        this.storage = storage;
        this.event = event;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public Clan getClan() {
        return clan;
    }

    public Player getPlayer() {
        return player;
    }

    public Inventory getStorage() {
        return storage;
    }

    public ItemStack getCurrentItem() {
        return event.getCurrentItem();
    }

    @Override
    public boolean isCancelled() {
        return event.isCancelled();
    }

    @Override
    public void setCancelled(boolean b) {
        event.setCancelled(b);
    }
}

package phrase.towerClans.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import phrase.towerClans.clan.Clan;

public class CloseMenuClanStorageEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Clan clan;
    private final Player player;
    private final Inventory storage;

    public CloseMenuClanStorageEvent(Clan clan, Player player, Inventory storage) {
        this.clan = clan;
        this.player = player;
        this.storage = storage;
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
}

package phrase.towerClans.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import phrase.towerClans.clan.entity.ModifiedPlayer;

public class ClickMenuClanMembersEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final ModifiedPlayer modifiedPlayer;
    private final InventoryClickEvent event;

    public ClickMenuClanMembersEvent(ModifiedPlayer modifiedPlayer, InventoryClickEvent event) {
        this.modifiedPlayer = modifiedPlayer;
        this.event = event;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public ModifiedPlayer getModifiedPlayer() {
        return modifiedPlayer;
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

package phrase.towerClans.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import phrase.towerClans.clan.entity.ModifiedPlayer;

public class ClanGlowDisableEvent extends Event implements Cancellable {

    // for paper
    private static final HandlerList HANDLER_LIST = new HandlerList();
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    private final ModifiedPlayer player;
    private boolean cancelled = false;

    public ClanGlowDisableEvent(ModifiedPlayer player) {
        this.player = player;
    }

    public ModifiedPlayer getPlayer() {
        return player;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }
}

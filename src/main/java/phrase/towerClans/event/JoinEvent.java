package phrase.towerClans.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import phrase.towerClans.clan.entity.ModifiedPlayer;

public class JoinEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final ModifiedPlayer modifiedPlayer;

    public JoinEvent(ModifiedPlayer modifiedPlayer) {
        this.modifiedPlayer = modifiedPlayer;
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

}

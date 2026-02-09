package phrase.towerclans.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import phrase.towerclans.clan.entity.ModifiedPlayer;

public class ClanJoinEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final ModifiedPlayer modifiedPlayer;

    public ClanJoinEvent(ModifiedPlayer modifiedPlayer) {
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

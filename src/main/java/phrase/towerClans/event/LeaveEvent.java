package phrase.towerClans.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import phrase.towerClans.clan.Clan;
import phrase.towerClans.clan.entity.ModifiedPlayer;

public class LeaveEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Clan clan;
    private final ModifiedPlayer modifiedPlayer;

    public LeaveEvent(Clan clan, ModifiedPlayer modifiedPlayer) {
        this.clan = clan;
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

    public Clan getClan() {
        return clan;
    }
}

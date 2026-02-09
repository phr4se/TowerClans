package phrase.towerclans.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import phrase.towerclans.clan.AbstractClan;
import phrase.towerclans.clan.Clan;

public class ClanLevelUpEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final AbstractClan clan;

    public ClanLevelUpEvent(AbstractClan clan) {
        this.clan = clan;
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
}

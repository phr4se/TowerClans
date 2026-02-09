package phrase.towerclans.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;
import phrase.towerclans.clan.AbstractClan;
import phrase.towerclans.clan.Clan;
import phrase.towerclans.clan.entity.ModifiedPlayer;

public class ClanPvpEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private final AbstractClan clan;
    private final ModifiedPlayer attacker;
    private final ModifiedPlayer defender;
    private final EntityDamageByEntityEvent event;

    public ClanPvpEvent(AbstractClan clan, ModifiedPlayer attacker, ModifiedPlayer defender, EntityDamageByEntityEvent event) {
        this.clan = clan;
        this.attacker = attacker;
        this.defender = defender;
        this.event = event;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public ModifiedPlayer getAttacker() {
        return attacker;
    }

    public ModifiedPlayer getDefender() {
        return defender;
    }

    public Clan getClan() {
        return clan;
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

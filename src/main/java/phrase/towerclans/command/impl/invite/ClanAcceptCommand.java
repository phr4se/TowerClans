package phrase.towerclans.command.impl.invite;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import phrase.towerclans.TowerClans;
import phrase.towerclans.clan.ClanResponse;
import phrase.towerclans.clan.entity.ModifiedPlayer;
import phrase.towerclans.clan.impl.clan.ClanImpl;
import phrase.towerclans.command.CommandHandler;
import phrase.towerclans.config.Config;
import phrase.towerclans.event.ClanJoinEvent;
import phrase.towerclans.util.Utils;

import java.util.UUID;

public class ClanAcceptCommand implements CommandHandler {
    private final TowerClans plugin;

    public ClanAcceptCommand(TowerClans plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean handler(Player player, String[] args) {
        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);
        UUID senderPlayer = PlayerCalls.removePlayer(player.getUniqueId());
        if (senderPlayer == null) {
            Utils.sendMessage(player, Config.getCommandMessages().notInvited());
            return true;
        }
        if (modifiedPlayer.getClan() != null) {
            Utils.sendMessage(player, Config.getCommandMessages().inClan());
            return true;
        }
        ModifiedPlayer senderModifiedPlayer = ModifiedPlayer.get(Bukkit.getPlayer(senderPlayer));
        ClanImpl clan = (ClanImpl) senderModifiedPlayer.getClan();
        modifiedPlayer.setClan(clan);
        ClanResponse clanResponse = clan.invite(modifiedPlayer);
        plugin.getServer().getPluginManager().callEvent(new ClanJoinEvent(modifiedPlayer));
        if (clanResponse.isSuccess()) {
            Utils.sendMessage(player, Config.getCommandMessages().acceptInvited());
            Utils.sendMessage(Bukkit.getPlayer(senderPlayer), Config.getCommandMessages().playerAcceptInvited());
            return true;
        } else {
            if (clanResponse.getMessage() != null) {
                Utils.sendMessage(Bukkit.getPlayer(senderPlayer), clanResponse.getMessage());
            }
        }
        return true;
    }
}

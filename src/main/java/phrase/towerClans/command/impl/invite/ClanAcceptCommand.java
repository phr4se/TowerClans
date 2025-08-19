package phrase.towerClans.command.impl.invite;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.ClanResponse;
import phrase.towerClans.clan.entity.ModifiedPlayer;
import phrase.towerClans.clan.impl.ClanImpl;
import phrase.towerClans.command.CommandHandler;
import phrase.towerClans.config.Config;
import phrase.towerClans.event.JoinEvent;
import phrase.towerClans.util.Utils;

import java.util.UUID;

public class ClanAcceptCommand implements CommandHandler {

    private final Plugin plugin;

    public ClanAcceptCommand(Plugin plugin) {
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

        plugin.getServer().getPluginManager().callEvent(new JoinEvent(modifiedPlayer));

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

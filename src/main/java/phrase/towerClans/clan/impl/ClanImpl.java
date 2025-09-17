package phrase.towerClans.clan.impl;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.*;
import phrase.towerClans.clan.attribute.clan.Level;
import phrase.towerClans.clan.attribute.clan.Rank;
import phrase.towerClans.clan.entity.ModifiedPlayer;
import phrase.towerClans.clan.permission.Permission;
import phrase.towerClans.clan.permission.PermissionType;
import phrase.towerClans.command.impl.base.Base;
import phrase.towerClans.config.Config;
import phrase.towerClans.glow.Glow;
import phrase.towerClans.gui.*;
import phrase.towerClans.gui.impl.MenuClanMembersProvider;
import phrase.towerClans.util.Utils;

import java.util.*;

public class ClanImpl extends AbstractClan {

    private static final Map<String, ClanImpl> CLANS = new HashMap<>();
    private final Plugin plugin;

    public ClanImpl(String name, Plugin plugin) {
        super(name);

        CLANS.put(name, this);

        this.plugin = plugin;
    }

    public ClanImpl(String name, ModifiedPlayer modifiedPlayer, Plugin plugin) {
        super(name);

        modifiedPlayer.setClan(this);
        getMembers().put(modifiedPlayer, Rank.RankType.LEADER.getName());
        CLANS.put(name, this);
        Base.setBase(this, null);
        Permission.getPermissionsPlayer(modifiedPlayer).setPermissionsPlayer(PermissionType.PERMISSION, PermissionType.WITHDRAW, PermissionType.STORAGE, PermissionType.BASE, PermissionType.KICK, PermissionType.PVP, PermissionType.GLOW, PermissionType.INVITE);

        this.plugin = plugin;
    }

    @Override
    public ClanResponse invite(ModifiedPlayer modifiedPlayer) {
        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
        if (clan.getMembers().containsKey(modifiedPlayer)) return new ClanResponse(Config.getCommandMessages().inClan(), ClanResponse.ResponseType.FAILURE);
        int maximumMembers = Level.getLevelMaximumMembers(clan.getLevel());
        if ((clan.getMembers().size() + 1) > maximumMembers) return new ClanResponse(Config.getCommandMessages().noPlaceInClan(), ClanResponse.ResponseType.FAILURE);
        clan.getMembers().put(modifiedPlayer, Rank.RankType.MEMBER.getName());

        String message = Config.getCommandMessages().notificationInvited().replace("%player%", modifiedPlayer.getPlayer().getName());

        chat(message);

        return new ClanResponse(null, ClanResponse.ResponseType.SUCCESS);
    }

    @Override
    public ClanResponse kick(ModifiedPlayer modifiedPlayer) {
        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
        if (!clan.getMembers().containsKey(modifiedPlayer))
            return new ClanResponse(Config.getCommandMessages().playerNotInYourselfClan(), ClanResponse.ResponseType.FAILURE);
        clan.getMembers().remove(modifiedPlayer);

        String message = Config.getCommandMessages().notificationKicked().replace("%player%", (modifiedPlayer.getPlayer() == null) ? Bukkit.getOfflinePlayer(modifiedPlayer.getPlayerUUID()).getName() : modifiedPlayer.getPlayer().getName());
        chat(message);

        return new ClanResponse(null, ClanResponse.ResponseType.SUCCESS);
    }

    @Override
    public ClanResponse invest(ModifiedPlayer modifiedPlayer, int amount) {
        if(amount <= 0) return new ClanResponse(Config.getCommandMessages().incorrectArguments(), ClanResponse.ResponseType.FAILURE);
        if (plugin.getEconomy().getBalance(modifiedPlayer.getPlayer()) < amount) return new ClanResponse(Config.getCommandMessages().notEnough().replace("%amount%", String.valueOf(amount)), ClanResponse.ResponseType.FAILURE);
        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
        int maximumBalance = Level.getLevelMaximumBalance(clan.getLevel());
        if ((clan.getBalance() + amount) > maximumBalance) return new ClanResponse(Config.getCommandMessages().noPlaceCurrencyInClan(), ClanResponse.ResponseType.FAILURE);
        plugin.getEconomy().withdrawPlayer(modifiedPlayer.getPlayer(), amount);
        clan.setBalance(clan.getBalance() + amount);

        String message = Config.getCommandMessages().notificationPut().replace("%player%", modifiedPlayer.getPlayer().getName()).replace("%amount%", String.valueOf(amount));
        chat(message);

        return new ClanResponse(null, ClanResponse.ResponseType.SUCCESS);
    }

    @Override
    public ClanResponse withdraw(ModifiedPlayer modifiedPlayer, int amount) {
        if(amount <= 0) return new ClanResponse(Config.getCommandMessages().incorrectArguments(), ClanResponse.ResponseType.FAILURE);
        if (getBalance() < amount) return new ClanResponse(Config.getCommandMessages().notCurrencyInClan(), ClanResponse.ResponseType.FAILURE);

        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
        plugin.getEconomy().depositPlayer(modifiedPlayer.getPlayer(), amount);
        clan.setBalance(clan.getBalance() - amount);

        String message = Config.getCommandMessages().notificationWithdraw().replace("%player%", modifiedPlayer.getPlayer().getName()).replace("%amount%", String.valueOf(amount));
        chat(message);

        return new ClanResponse(null, ClanResponse.ResponseType.SUCCESS);
    }

    @Override
    public ClanResponse leave(ModifiedPlayer modifiedPlayer) {
        if (!getMembers().containsKey(modifiedPlayer)) return new ClanResponse(Config.getCommandMessages().notInClan(), ClanResponse.ResponseType.FAILURE);
        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
        clan.getMembers().remove(modifiedPlayer);

        String message = Config.getCommandMessages().notificationLeave().replace("%player%", modifiedPlayer.getPlayer().getName());
        chat(message);

        return new ClanResponse(null, ClanResponse.ResponseType.SUCCESS);
    }

    @Override
    public ClanResponse rank(ModifiedPlayer modifiedPlayer, int id) {
        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
        switch (Rank.RankType.getRank(id)) {
            case LEADER: return new ClanResponse(Config.getCommandMessages().notGiveRankLeader(), ClanResponse.ResponseType.FAILURE);
            case DEPUTY: {
                Permission.getPermissionsPlayer(modifiedPlayer).setPermissionsPlayer(PermissionType.INVITE, PermissionType.KICK, PermissionType.PVP, PermissionType.KICK, PermissionType.STORAGE);
                clan.getMembers().compute(modifiedPlayer, (k, v) -> Rank.RankType.DEPUTY.getName());
            }
                        break;
            case MEMBER: {
                Permission.getPermissionsPlayer(modifiedPlayer).clearPermissionsPlayer(PermissionType.INVITE, PermissionType.KICK, PermissionType.PVP, PermissionType.KICK, PermissionType.STORAGE, PermissionType.BASE, PermissionType.GLOW, PermissionType.WITHDRAW, PermissionType.PERMISSION);
                clan.getMembers().compute(modifiedPlayer, (k, v) -> Rank.RankType.MEMBER.getName());
            }
                        break;
            case UNDEFINED: return new ClanResponse(Config.getCommandMessages().rankNoExists(), ClanResponse.ResponseType.FAILURE);
        }

        String message = Config.getCommandMessages().notificationRank().replace("%player%", modifiedPlayer.getPlayer().getName()).replace("%rank%", (id == 2) ? Rank.RankType.DEPUTY.getName() : Rank.RankType.MEMBER.getName());
        chat(message);

        return new ClanResponse(null, ClanResponse.ResponseType.SUCCESS);
    }

    @Override
    public ClanResponse disband(ModifiedPlayer modifiedPlayer) {
        if (!getMembers().get(modifiedPlayer).equals(Rank.RankType.LEADER.getName())) return new ClanResponse(Config.getCommandMessages().notLeader(), ClanResponse.ResponseType.FAILURE);

        if(modifiedPlayer.hasPermission(PermissionType.PERMISSION)) Permission.getPermissionsPlayer(modifiedPlayer).clearPermissionPlayer(PermissionType.PERMISSION);

        String message = Config.getCommandMessages().notificationDisband();
        chat(message);

        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
        for (Map.Entry<ModifiedPlayer, String> entry : clan.getMembers().entrySet()) {
            entry.getKey().setClan(null);
            Player player = entry.getKey().getPlayer();
            if(player == null) continue;
            if(Glow.isEnableForPlayer(entry.getKey())) Glow.disableForPlayer(entry.getKey());
        }

        CLANS.remove(clan.getName());
        return new ClanResponse(null, ClanResponse.ResponseType.SUCCESS);
    }

    @Override
    public void showMenu(ModifiedPlayer modifiedPlayer, MenuType menuType) {
        MenuProvider menuProvider = MenuFactory.getProvider(menuType);
        if(menuProvider == null) {
            modifiedPlayer.getPlayer().closeInventory();
            return;
        }

        Player player = modifiedPlayer.getPlayer();

        if (menuProvider.menuPages()) {
            Inventory menu = menuProvider.getMenu(modifiedPlayer, ((ClanImpl) modifiedPlayer.getClan()), plugin);
            List<ItemStack> players = ((Pages) menuProvider).getContents(modifiedPlayer, ((ClanImpl) modifiedPlayer.getClan()), plugin);
            MenuPages menuPages = ((Pages) menuProvider).register(modifiedPlayer.getPlayerUUID(), new MenuPages(0, players, menu));
            player.openInventory(menuPages.getPage(menuPages.getCurrentPage()));
        } else player.openInventory(menuProvider.getMenu(modifiedPlayer, ((ClanImpl) modifiedPlayer.getClan()), plugin));


    }

    @Override
    public void glow(ModifiedPlayer modifiedPlayer, Plugin plugin) {

        if(!Glow.isEnableForPlayer(modifiedPlayer)) {
            Glow.enableForPlayer(modifiedPlayer);
            Utils.sendMessage(modifiedPlayer.getPlayer(), Config.getCommandMessages().enableGlow());
            return;
        }

        Glow.disableForPlayer(modifiedPlayer);
        Utils.sendMessage(modifiedPlayer.getPlayer(), Config.getCommandMessages().disableGlow());

    }

    @Override
    public void chat(String message) {

        for(Map.Entry<ModifiedPlayer, String> entry : getMembers().entrySet()) {
            Player player = entry.getKey().getPlayer();
            if(player == null) return;
            Utils.sendMessage(player, message);
        }

    }

    public static Map<String, ClanImpl> getClans() {
        return Collections.unmodifiableMap(CLANS);
    }

}
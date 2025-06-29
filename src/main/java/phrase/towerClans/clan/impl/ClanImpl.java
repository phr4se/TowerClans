package phrase.towerClans.clan.impl;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.*;
import phrase.towerClans.clan.attribute.clan.Level;
import phrase.towerClans.clan.attribute.clan.Rank;
import phrase.towerClans.clan.entity.ModifiedPlayer;
import phrase.towerClans.config.Config;
import phrase.towerClans.glow.Glow;
import phrase.towerClans.gui.MenuFactory;
import phrase.towerClans.gui.MenuPages;
import phrase.towerClans.gui.MenuProvider;
import phrase.towerClans.gui.MenuType;
import phrase.towerClans.gui.impl.MenuClanMembersProvider;
import phrase.towerClans.util.Utils;

import java.util.*;

public class ClanImpl extends AbstractClan {

    private static final Map<String, ClanImpl> CLANS = new HashMap<>();
    private final Plugin plugin;

    public ClanImpl(String name, Plugin plugin) {
        super(name);
        this.plugin = plugin;
    }

    @Override
    public ClanResponse invite(ModifiedPlayer modifiedPlayer) {
        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
        if (clan.getMembers().containsKey(modifiedPlayer)) return new ClanResponse(Config.getCommandMessages().inClan(), ClanResponse.ResponseType.FAILURE);
        int maximumMembers = Level.getLevelMaximumMembers(clan.getLevel());
        if ((clan.getMembers().size() + 1) > maximumMembers) return new ClanResponse(Config.getCommandMessages().noPlaceInClan(), ClanResponse.ResponseType.FAILURE);
        clan.getMembers().put(modifiedPlayer, Rank.RankType.MEMBER.getName());

        for (Map.Entry<ModifiedPlayer, String> entry : clan.getMembers().entrySet()) {
            String string = Config.getCommandMessages().notificationInvited().replace("%player%", modifiedPlayer.getPlayer().getName());
            Utils.sendMessage(entry.getKey().getPlayer(), string);
        }

        return new ClanResponse(null, ClanResponse.ResponseType.SUCCESS);
    }

    @Override
    public ClanResponse kick(ModifiedPlayer modifiedPlayer) {
        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
        if (!clan.getMembers().containsKey(modifiedPlayer)) return new ClanResponse(Config.getCommandMessages().playerNotInYourselfClan(), ClanResponse.ResponseType.FAILURE);
        clan.getMembers().remove(modifiedPlayer);

        for (Map.Entry<ModifiedPlayer, String> entry : clan.getMembers().entrySet()) {
            String string = Config.getCommandMessages().notificationWithdraw().replace("%player%", modifiedPlayer.getPlayer().getName());
            Utils.sendMessage(entry.getKey().getPlayer(), string);
        }

        return new ClanResponse(null, ClanResponse.ResponseType.SUCCESS);
    }

    @Override
    public ClanResponse invest(ModifiedPlayer modifiedPlayer, int amount) {
        if (plugin.getEconomy().getBalance(modifiedPlayer.getPlayer()) < amount) return new ClanResponse(Config.getCommandMessages().notEnough(), ClanResponse.ResponseType.FAILURE);
        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
        int maximumBalance = Level.getLevelMaximumBalance(clan.getLevel());
        if ((clan.getBalance() + amount) > maximumBalance) return new ClanResponse(Config.getCommandMessages().noPlaceCurrencyInClan(), ClanResponse.ResponseType.FAILURE);
        plugin.getEconomy().withdrawPlayer(modifiedPlayer.getPlayer(), amount);
        clan.setBalance(clan.getBalance() + amount);

        for (Map.Entry<ModifiedPlayer, String> entry : clan.getMembers().entrySet()) {
            String string = Config.getCommandMessages().notificationPut().replace("%player%", modifiedPlayer.getPlayer().getName()).replace("%amount%", String.valueOf(amount));
            Utils.sendMessage(entry.getKey().getPlayer(), string);
        }

        return new ClanResponse(null, ClanResponse.ResponseType.SUCCESS);
    }

    @Override
    public ClanResponse withdraw(ModifiedPlayer modifiedPlayer, int amount) {
        if (getBalance() < amount) return new ClanResponse(Config.getCommandMessages().notInClan(), ClanResponse.ResponseType.FAILURE);

        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
        plugin.getEconomy().depositPlayer(modifiedPlayer.getPlayer(), amount);
        clan.setBalance(clan.getBalance() - amount);

        for (Map.Entry<ModifiedPlayer, String> entry : getMembers().entrySet()) {
            String string = Config.getCommandMessages().notificationWithdraw().replace("%player%", modifiedPlayer.getPlayer().getName()).replace("%amount%", String.valueOf(amount));
            Utils.sendMessage(entry.getKey().getPlayer(), string);
        }

        return new ClanResponse(null, ClanResponse.ResponseType.SUCCESS);
    }

    @Override
    public ClanResponse leave(ModifiedPlayer modifiedPlayer) {
        if (!getMembers().containsKey(modifiedPlayer)) return new ClanResponse(Config.getCommandMessages().notInClan(), ClanResponse.ResponseType.FAILURE);
        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
        clan.getMembers().remove(modifiedPlayer);

        for (Map.Entry<ModifiedPlayer, String> entry : clan.getMembers().entrySet()) {
            String string = Config.getCommandMessages().notificationLeave().replace("%player%", modifiedPlayer.getPlayer().getName());
            Utils.sendMessage(entry.getKey().getPlayer(), string);
        }

        return new ClanResponse(null, ClanResponse.ResponseType.SUCCESS);
    }

    @Override
    public ClanResponse rank(ModifiedPlayer modifiedPlayer, int id) {
        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
        switch (Rank.RankType.getRank(id)) {
            case LEADER: return new ClanResponse(Config.getCommandMessages().notGiveRankLeader(), ClanResponse.ResponseType.FAILURE);
            case DEPUTY: clan.getMembers().replace(modifiedPlayer, getMembers().get(modifiedPlayer), Rank.RankType.DEPUTY.getName());
                        break;
            case MEMBER: clan.getMembers().replace(modifiedPlayer, getMembers().get(modifiedPlayer), Rank.RankType.MEMBER.getName());
                        break;
            case UNDEFINED: return new ClanResponse(Config.getCommandMessages().rankNoExists(), ClanResponse.ResponseType.FAILURE);
        }

        for (Map.Entry<ModifiedPlayer, String> entry : clan.getMembers().entrySet()) {
            String string = Config.getCommandMessages().notificationRank().replace("%player%", modifiedPlayer.getPlayer().getName()).replace("%rank%", (id == 2) ? Rank.RankType.DEPUTY.getName() : Rank.RankType.MEMBER.getName());
            Utils.sendMessage(entry.getKey().getPlayer(), string);
        }

        return new ClanResponse(null, ClanResponse.ResponseType.SUCCESS);
    }

    @Override
    public ClanResponse disband(ModifiedPlayer modifiedPlayer) {
        if (!getMembers().get(modifiedPlayer).equals(Rank.RankType.LEADER.getName())) return new ClanResponse(Config.getCommandMessages().notLeader(), ClanResponse.ResponseType.FAILURE);

        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
        for (Map.Entry<ModifiedPlayer, String> entry : clan.getMembers().entrySet()) {
            entry.getKey().setClan(null);
            Utils.sendMessage(entry.getKey().getPlayer(), Config.getCommandMessages().notificationDisband());
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

        if (menuType == MenuType.MENU_CLAN_MEMBERS) {
            MenuClanMembersProvider menuClanMembersProvider = (MenuClanMembersProvider) menuProvider;
            Inventory menu = menuProvider.getMenu(((ClanImpl) modifiedPlayer.getClan()), plugin);
            List<ItemStack> players = menuClanMembersProvider.getPlayers(((ClanImpl) modifiedPlayer.getClan()), plugin);
            MenuPages menuPages = menuClanMembersProvider.register(modifiedPlayer.getPlayerUUID(), new MenuPages(0, players, menu));
            modifiedPlayer.getPlayer().openInventory(menuPages.getPage(menuPages.getCurrentPage()));
        } else modifiedPlayer.getPlayer().openInventory(menuProvider.getMenu(((ClanImpl) modifiedPlayer.getClan()), plugin));


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

    public static Map<String, ClanImpl> getClans() {
        return CLANS;
    }

}
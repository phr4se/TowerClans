package phrase.towerClans.clan.impl;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.*;
import phrase.towerClans.clan.attribute.clan.Level;
import phrase.towerClans.clan.attribute.clan.Rank;
import phrase.towerClans.clan.entity.ModifiedPlayer;
import phrase.towerClans.glow.Glow;
import phrase.towerClans.gui.MenuFactory;
import phrase.towerClans.gui.MenuPages;
import phrase.towerClans.gui.MenuProvider;
import phrase.towerClans.gui.MenuType;
import phrase.towerClans.gui.impl.MenuClanMembersProvider;
import phrase.towerClans.util.ChatUtil;

import java.util.*;

public class ClanImpl extends AbstractClan {

    private static final Map<String, ClanImpl> CLANS = new HashMap<>();
    private final Plugin plugin;
    private final ChatUtil chatUtil;

    public ClanImpl(String name, Plugin plugin) {
        super(name);
        this.plugin = plugin;
        chatUtil = new ChatUtil(plugin);
    }

    @Override
    public ClanResponse invite(ModifiedPlayer modifiedPlayer) {
        ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("message.command.invite.accept");
        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
        if (clan.getMembers().containsKey(modifiedPlayer)) return new ClanResponse(configurationSection.getString("you_are_in_a_clan"), ClanResponse.ResponseType.FAILURE);
        int maximumMembers = Level.getLevelMaximumMembers(clan.getLevel());
        if ((clan.getMembers().size() + 1) > maximumMembers) return new ClanResponse(configurationSection.getString("there_is_no_place_in_the_clan"), ClanResponse.ResponseType.FAILURE);
        configurationSection = plugin.getConfig().getConfigurationSection("message.command.invite");
        clan.getMembers().put(modifiedPlayer, Rank.RankType.MEMBER.getName());

        for (Map.Entry<ModifiedPlayer, String> entry : clan.getMembers().entrySet()) {
            String string = configurationSection.getString("notification_of_the_invitation").replace("%player%", modifiedPlayer.getPlayer().getName());
            chatUtil.sendMessage(entry.getKey().getPlayer(), string);
        }

        return new ClanResponse(null, ClanResponse.ResponseType.SUCCESS);
    }

    @Override
    public ClanResponse kick(ModifiedPlayer modifiedPlayer) {
        ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("message.command.kick");
        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
        if (!clan.getMembers().containsKey(modifiedPlayer)) return new ClanResponse(configurationSection.getString("the_player_is_not_in_the_clan"), ClanResponse.ResponseType.FAILURE);
        clan.getMembers().remove(modifiedPlayer);

        for (Map.Entry<ModifiedPlayer, String> entry : clan.getMembers().entrySet()) {
            String string = configurationSection.getString("notification_of_exclusion").replace("%player%", modifiedPlayer.getPlayer().getName());
            chatUtil.sendMessage(entry.getKey().getPlayer(), string);
        }

        return new ClanResponse(null, ClanResponse.ResponseType.SUCCESS);
    }

    @Override
    public ClanResponse invest(ModifiedPlayer modifiedPlayer, int amount) {
        ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("message.command.invest");
        if (plugin.economy.getBalance(modifiedPlayer.getPlayer()) < amount) return new ClanResponse(configurationSection.getString("you_don't_have_enough"), ClanResponse.ResponseType.FAILURE);
        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
        int maximumBalance = Level.getLevelMaximumBalance(clan.getLevel());
        if ((clan.getBalance() + amount) > maximumBalance) return new ClanResponse(configurationSection.getString("there_is_no_place_in_the_clan"), ClanResponse.ResponseType.FAILURE);
        plugin.economy.withdrawPlayer(modifiedPlayer.getPlayer(), amount);
        clan.setBalance(clan.getBalance() + amount);

        for (Map.Entry<ModifiedPlayer, String> entry : clan.getMembers().entrySet()) {
            String string = configurationSection.getString("notification_of_investment").replace("%player%", modifiedPlayer.getPlayer().getName()).replace("%amount%", String.valueOf(amount));
            chatUtil.sendMessage(entry.getKey().getPlayer(), string);
        }

        return new ClanResponse(null, ClanResponse.ResponseType.SUCCESS);
    }

    @Override
    public ClanResponse withdraw(ModifiedPlayer modifiedPlayer, int amount) {
        ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("message.command.withdraw");
        if (getBalance() < amount) return new ClanResponse(configurationSection.getString("not_in_the_clan"), ClanResponse.ResponseType.FAILURE);

        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
        plugin.economy.depositPlayer(modifiedPlayer.getPlayer(), amount);
        clan.setBalance(clan.getBalance() - amount);

        for (Map.Entry<ModifiedPlayer, String> entry : getMembers().entrySet()) {
            String string = configurationSection.getString("notification_of_withdrawal").replace("%player%", modifiedPlayer.getPlayer().getName()).replace("%amount%", String.valueOf(amount));
            chatUtil.sendMessage(entry.getKey().getPlayer(), string);
        }

        return new ClanResponse(null, ClanResponse.ResponseType.SUCCESS);
    }

    @Override
    public ClanResponse leave(ModifiedPlayer modifiedPlayer) {
        ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("message.command.leave");
        if (!getMembers().containsKey(modifiedPlayer)) return new ClanResponse(configurationSection.getString("you're_not_in_the_clan)"), ClanResponse.ResponseType.FAILURE);
        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
        clan.getMembers().remove(modifiedPlayer);

        for (Map.Entry<ModifiedPlayer, String> entry : clan.getMembers().entrySet()) {
            String string = configurationSection.getString("notification_of_exclusion").replace("%player%", modifiedPlayer.getPlayer().getName());
            chatUtil.sendMessage(entry.getKey().getPlayer(), string);
        }

        return new ClanResponse(null, ClanResponse.ResponseType.SUCCESS);
    }

    @Override
    public ClanResponse rank(ModifiedPlayer modifiedPlayer, int id) {
        ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("message.command.rank");
        if (id == 1) return new ClanResponse(configurationSection.getString("you_can't_give_out_a_leader_rank"), ClanResponse.ResponseType.FAILURE);
        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
        if (id == 2) clan.getMembers().replace(modifiedPlayer, getMembers().get(modifiedPlayer), Rank.RankType.DEPUTY.getName());
        else if (id == 3)
            clan.getMembers().replace(modifiedPlayer, getMembers().get(modifiedPlayer), Rank.RankType.MEMBER.getName());
        else return new ClanResponse(configurationSection.getString("this_rank_does_not_exist"), ClanResponse.ResponseType.FAILURE);
        ConfigurationSection configSection = plugin.getConfig().getConfigurationSection("message.command.rank");

        for (Map.Entry<ModifiedPlayer, String> entry : clan.getMembers().entrySet()) {
            String string = configSection.getString("notification_of_rank").replace("%player%", modifiedPlayer.getPlayer().getName()).replace("%rank%", (id == 2) ? Rank.RankType.DEPUTY.getName() : Rank.RankType.MEMBER.getName());
            chatUtil.sendMessage(entry.getKey().getPlayer(), string);
        }

        return new ClanResponse(null, ClanResponse.ResponseType.SUCCESS);
    }

    @Override
    public ClanResponse disband(ModifiedPlayer modifiedPlayer) {
        ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("message.command.disband");
        if (!getMembers().get(modifiedPlayer).equals(Rank.RankType.LEADER.getName())) return new ClanResponse(configurationSection.getString("you_are_not_a_leader"), ClanResponse.ResponseType.FAILURE);

        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
        for (Map.Entry<ModifiedPlayer, String> entry : clan.getMembers().entrySet()) {
            entry.getKey().setClan(null);

            chatUtil.sendMessage(entry.getKey().getPlayer(), configurationSection.getString("notification_of_disband"));
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

        ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("message.command.glow");

        if(!Glow.isEnableForPlayer(modifiedPlayer)) {
            Glow.enableForPlayer(modifiedPlayer);
            chatUtil.sendMessage(modifiedPlayer.getPlayer(), configurationSection.getString("you_have_enabled_glow"));
            return;
        }

        Glow.disableForPlayer(modifiedPlayer);
        chatUtil.sendMessage(modifiedPlayer.getPlayer(), configurationSection.getString("you_have_disabled_glow"));

    }

    public static Map<String, ClanImpl> getClans() {
        return CLANS;
    }

}
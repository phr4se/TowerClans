package phrase.towerClans.commands.impls;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.AbstractClan;
import phrase.towerClans.clan.ModifiedPlayer;
import phrase.towerClans.clan.impls.ClanImpl;
import phrase.towerClans.commands.AbstractCommand;
import phrase.towerClans.utils.ChatUtil;

import java.util.*;

public class ClanCommand extends AbstractCommand {

    private static final Map<UUID, UUID> PLAYERS = new HashMap<>();
    private final ChatUtil chatUtil = new ChatUtil();

    public ClanCommand(String command) {
        super(command);
    }

    @Override
    public void execute(CommandSender commandSender, Command command, String label, String[] args) {

        if (!(commandSender instanceof Player)) return;

        Player player = (Player) commandSender;

        ConfigurationSection configSection = Plugin.getInstance().getConfig().getConfigurationSection("message");

        if (!player.hasPermission("towerclans.command")) {
            chatUtil.sendMessage(player, configSection.getString("you_don't_have_permission"));
            return;
        }

        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);

        if (args.length < 1) {

            if (modifiedPlayer.getClan() == null) {
                List<String> list = configSection.getStringList("a_player_without_a_clan");
                for (String string : list) {
                    chatUtil.sendMessage(player, string);
                }
                return;
            }

            List<String> list = configSection.getStringList("a_player_with_a_clan");
            for (String string : list) {
                chatUtil.sendMessage(player, string);
            }
            return;
        }

        if (args[0].equalsIgnoreCase("chat")) {

            configSection = Plugin.getInstance().getConfig().getConfigurationSection("message.command.chat");

            if (modifiedPlayer.getClan() == null) {
                chatUtil.sendMessage(player, configSection.getString("you're_not_in_the_clan"));
                return;
            }

            ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();

            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                stringBuilder.append(args[i]).append(" ");
            }

            String string = configSection.getString("message_format").replace("%player%", modifiedPlayer.getPlayer().getName()).replace("%rank%", clan.getMembers().get(modifiedPlayer)).replace("%message%", stringBuilder.toString());
            for (Map.Entry<ModifiedPlayer, String> entry : clan.getMembers().entrySet()) {
                chatUtil.sendMessage(entry.getKey().getPlayer(), string);
            }

            return;
        }

        if (args[0].equalsIgnoreCase("create")) {

            configSection = Plugin.getInstance().getConfig().getConfigurationSection("message.command.create");

            if (modifiedPlayer.getClan() != null) {
                chatUtil.sendMessage(player, configSection.getString("you_are_in_a_clan"));
                return;
            }

            if (args.length < 2) {
                chatUtil.sendMessage(player, configSection.getString("usage_command"));
                return;
            }

            configSection = Plugin.getInstance().getConfig().getConfigurationSection("settings");

            int amount = configSection.getInt("the_cost_of_creating_a_clan");

            configSection = Plugin.getInstance().getConfig().getConfigurationSection("message.command.create");

            if (ClanImpl.getClans().containsKey(args[1])) {
                chatUtil.sendMessage(player, configSection.getString("a_clan_with_that_name_already_exists"));
                return;
            }

            if (Plugin.getInstance().economy.getBalance(player) < amount) {
                String string = configSection.getString("you_don't_have_enough").replace("%amount%", String.valueOf(amount - Plugin.getInstance().economy.getBalance(player)));
                chatUtil.sendMessage(player, string);
                return;
            }


            Plugin.getInstance().economy.withdrawPlayer(player, amount);
            String name = args[1];

            ClanImpl clan = new ClanImpl(name);
            modifiedPlayer.setClan(clan);
            clan.getMembers().put(modifiedPlayer, AbstractClan.RankType.LEADER.getName());
            ClanImpl.getClans().put(args[1], clan);

            chatUtil.sendMessage(player, configSection.getString("you_have_created_a_clan"));

            return;
        }

        if (args[0].equalsIgnoreCase("menu")) {

            configSection = Plugin.getInstance().getConfig().getConfigurationSection("message.command.menu");

            if (modifiedPlayer.getClan() == null) {
                chatUtil.sendMessage(player, configSection.getString("you're_not_in_the_clan"));
                return;
            }

            ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
            clan.showMenu(modifiedPlayer, ClanImpl.MenuType.MENU_CLAN.getId());
            chatUtil.sendMessage(player, configSection.getString("you_have_opened_the_clan_menu"));

            return;
        }

        if (args[0].equalsIgnoreCase("invite")) {

            configSection = Plugin.getInstance().getConfig().getConfigurationSection("message.command.invite");

            if (modifiedPlayer.getClan() == null) {
                chatUtil.sendMessage(player, configSection.getString("you're_not_in_the_clan"));
                return;
            }

            ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();

            if (!clan.getMembers().get(modifiedPlayer).equals(AbstractClan.RankType.LEADER.getName()) && !clan.getMembers().get(modifiedPlayer).equals(AbstractClan.RankType.DEPUTY.getName())) {
                chatUtil.sendMessage(player, configSection.getString("you_don't_have_permission"));
                return;
            }

            if (args.length < 2) {
                chatUtil.sendMessage(player, configSection.getString("usage_command"));
                return;
            }

            String name = args[1];

            Player targetPlayer = Bukkit.getPlayer(name);

            if (targetPlayer == null) {
                chatUtil.sendMessage(player, configSection.getString("message.command.invite.the_player_was_not_found"));
                return;
            }

            PLAYERS.put(targetPlayer.getUniqueId(), player.getUniqueId());
            chatUtil.sendMessage(player, configSection.getString("you_have_sent_a_request_to_join_the_clan"));
            chatUtil.sendMessage(targetPlayer, configSection.getString("you_have_received_a_request_to_join_the_clan"));

            return;
        }

        if (args[0].equalsIgnoreCase("accept")) {

            configSection = Plugin.getInstance().getConfig().getConfigurationSection("message.command.invite.accept");

            UUID senderPlayer = PLAYERS.remove(player.getUniqueId());

            if (senderPlayer == null) {
                chatUtil.sendMessage(player, configSection.getString("has_anyone_sent_you_a_request_to_join_clan"));
                return;
            }

            if (modifiedPlayer.getClan() != null) {
                chatUtil.sendMessage(player, configSection.getString("you_are_in_a_clan"));
                return;
            }

            ModifiedPlayer senderModifiedPlayer = ModifiedPlayer.get(Bukkit.getPlayer(senderPlayer));
            ClanImpl clan = (ClanImpl) senderModifiedPlayer.getClan();

            modifiedPlayer.setClan(clan);
            boolean b = clan.invite(modifiedPlayer);

            if (b) {
                chatUtil.sendMessage(player, configSection.getString("have_you_accepted_the_request_to_join_the_clan"));
                chatUtil.sendMessage(Bukkit.getPlayer(senderPlayer), configSection.getString("the_player_accepted_the_request_to_join_the_clan"));
                return;
            } else {
                chatUtil.sendMessage(player, configSection.getString("message.command.invite.accept.you_are_in_a_clan"));
            }

            return;
        }

        if (args[0].equalsIgnoreCase("cancel")) {

            configSection = Plugin.getInstance().getConfig().getConfigurationSection("message.command.invite.cancel");

            UUID senderPlayer = PLAYERS.remove(player.getUniqueId());

            if (senderPlayer == null) {
                chatUtil.sendMessage(player, configSection.getString("has_anyone_sent_you_a_request_to_join_clan"));
                return;
            }

            chatUtil.sendMessage(player, configSection.getString("you_rejected_the_request_to_join_the_clan"));
            chatUtil.sendMessage(Bukkit.getPlayer(senderPlayer), configSection.getString("the_player_rejected_the_request_to_join_the_clan"));

            return;
        }

        if (args[0].equalsIgnoreCase("kick")) {

            configSection = Plugin.getInstance().getConfig().getConfigurationSection("message.command.kick");

            if (modifiedPlayer.getClan() == null) {
                chatUtil.sendMessage(player, configSection.getString("you're_not_in_the_clan"));
                return;
            }

            ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();

            if (!clan.getMembers().get(modifiedPlayer).equals(AbstractClan.RankType.LEADER.getName()) && !clan.getMembers().get(modifiedPlayer).equals(AbstractClan.RankType.DEPUTY.getName())) {
                chatUtil.sendMessage(player, configSection.getString("you_don't_have_permission"));
                return;
            }

            if (args.length < 2) {
                chatUtil.sendMessage(player, configSection.getString("usage_command"));
                return;
            }


            String name = args[1];

            Player targetPlayer = Bukkit.getPlayer(name);

            if (targetPlayer == null) {
                chatUtil.sendMessage(player, configSection.getString("the_player_was_not_found"));
                return;
            }

            ModifiedPlayer targetModifiedPlayer = ModifiedPlayer.get(targetPlayer);

            if (clan.getMembers().get(modifiedPlayer).equals("Лидер")) {
                chatUtil.sendMessage(player, configSection.getString("you_cannot_leave_the_clan"));
                return;
            }

            boolean b = clan.kick(targetModifiedPlayer);

            if (b) {
                chatUtil.sendMessage(player, configSection.getString("you_kicked_a_player_from_the_clan"));
                chatUtil.sendMessage(targetPlayer, configSection.getString("you_were_kicked_out_of_the_clan"));
                return;
            } else {
                chatUtil.sendMessage(player, configSection.getString("the_player_is_not_in_the_clan"));
            }

            return;
        }

        if (args[0].equalsIgnoreCase("invest")) {

            configSection = Plugin.getInstance().getConfig().getConfigurationSection("message.command.invest");

            if (modifiedPlayer.getClan() == null) {
                chatUtil.sendMessage(player, configSection.getString("you're_not_in_the_clan"));
                return;
            }

            ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();

            if (args.length < 2) {
                chatUtil.sendMessage(player, configSection.getString("usage_command"));
                return;
            }


            int amount = 0;
            try {
                amount = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                chatUtil.sendMessage(player, configSection.getString("usage_command"));
                return;
            }

            boolean b = clan.invest(modifiedPlayer, amount);
            if (b) {
                chatUtil.sendMessage(player, configSection.getString("you_put_it_in_the_clan"));
                return;
            } else {
                chatUtil.sendMessage(player, configSection.getString("you_don't_have_enough"));
            }

            return;
        }

        if (args[0].equalsIgnoreCase("withdraw")) {

            configSection = Plugin.getInstance().getConfig().getConfigurationSection("message.command.withdraw");

            if (modifiedPlayer.getClan() == null) {
                chatUtil.sendMessage(player, configSection.getString("you're_not_in_the_clan"));
                return;
            }

            ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();

            if (!clan.getMembers().get(modifiedPlayer).equals(AbstractClan.RankType.LEADER.getName()) && !clan.getMembers().get(modifiedPlayer).equals(AbstractClan.RankType.DEPUTY.getName())) {
                chatUtil.sendMessage(player, configSection.getString("you_don't_have_permission"));
                return;
            }

            if (args.length < 2) {
                chatUtil.sendMessage(player, configSection.getString("usage_command"));
                return;
            }

            int amount = 0;
            try {
                amount = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                chatUtil.sendMessage(player, configSection.getString("usage_command"));
                return;
            }

            boolean b = clan.withdraw(modifiedPlayer, amount);
            if (b) {
                chatUtil.sendMessage(player, configSection.getString("you_have_withdrawn_from_the_clan"));
                return;
            } else {
                chatUtil.sendMessage(player, configSection.getString("not_in_the_clan"));
            }

            return;
        }

        if (args[0].equalsIgnoreCase("leave")) {

            configSection = Plugin.getInstance().getConfig().getConfigurationSection("message.command.leave");

            if (modifiedPlayer.getClan() == null) {
                chatUtil.sendMessage(player, configSection.getString("you're_not_in_the_clan"));
                return;
            }

            ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();

            if (clan.getMembers().get(modifiedPlayer).equals("Лидер")) {
                chatUtil.sendMessage(player, configSection.getString("you_cannot_leave_the_clan"));
                return;
            }

            boolean b = clan.leave(modifiedPlayer);

            if (b) {
                chatUtil.sendMessage(player, configSection.getString("you_have_left_the_clan"));
                return;
            } else {
                chatUtil.sendMessage(player, configSection.getString("you're_not_in_the_clan"));
            }

            return;
        }

        if (args[0].equalsIgnoreCase("pvp")) {

            configSection = Plugin.getInstance().getConfig().getConfigurationSection("message.command.pvp");

            if (modifiedPlayer.getClan() == null) {
                chatUtil.sendMessage(player, configSection.getString("you're_not_in_the_clan"));
                return;
            }

            ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();

            if (!clan.getMembers().get(modifiedPlayer).equals(AbstractClan.RankType.LEADER.getName()) && !clan.getMembers().get(modifiedPlayer).equals(AbstractClan.RankType.DEPUTY.getName())) {
                chatUtil.sendMessage(player, configSection.getString("you_don't_have_permission"));
                return;
            }

            if (clan.isPvp()) {
                clan.setPvp(false);
                chatUtil.sendMessage(player, configSection.getString("you_have_disabled_pvp_in_the_clan"));
                return;
            } else {
                clan.setPvp(true);
                chatUtil.sendMessage(player, configSection.getString("you_have_enabled_pvp_in_the_clan"));
            }

            return;
        }

        if (args[0].equalsIgnoreCase("rank")) {

            configSection = Plugin.getInstance().getConfig().getConfigurationSection("message.command.rank");

            if (modifiedPlayer.getClan() == null) {
                chatUtil.sendMessage(player, configSection.getString("message.command.rank.you're_not_in_the_clan"));
                return;
            }

            ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();

            if (!clan.getMembers().get(modifiedPlayer).equals(AbstractClan.RankType.LEADER.getName())) {
                chatUtil.sendMessage(player, configSection.getString("you_don't_have_permission"));
                return;
            }

            if (args.length < 3) {
                chatUtil.sendMessage(player, configSection.getString("usage_command"));
                return;
            }

            String name = args[1];
            Player targetPlayer;
            try {
                targetPlayer = Objects.requireNonNull(Bukkit.getPlayer(name));
            } catch (NullPointerException e) {
                chatUtil.sendMessage(player, configSection.getString("the_player_is_offline"));
                return;
            }
            ModifiedPlayer targetModifierPlayer = ModifiedPlayer.get(targetPlayer);

            int id = 0;
            try {
                id = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                chatUtil.sendMessage(player, configSection.getString("usage_command"));
            }

            boolean b = clan.rank(targetModifierPlayer, id);

            if (b) {
                String string = configSection.getString("you_have_set_the_player's_rank").replace("%player%", targetPlayer.getName()).replace("%rank%", (id == 2) ? AbstractClan.RankType.DEPUTY.getName() : AbstractClan.RankType.MEMBER.getName());
                chatUtil.sendMessage(player, string);
                string = configSection.getString("you_have_been_set_a_rank").replace("%player%", player.getName()).replace("%rank%", (id == 2) ? AbstractClan.RankType.DEPUTY.getName() : AbstractClan.RankType.MEMBER.getName());
                chatUtil.sendMessage(player, string);
                return;
            } else {
                chatUtil.sendMessage(player, configSection.getString("usage_command"));
            }

            return;

        }

        if (args[0].equalsIgnoreCase("disband")) {

            configSection = Plugin.getInstance().getConfig().getConfigurationSection("message.command.disband");

            if (modifiedPlayer.getClan() == null) {
                chatUtil.sendMessage(player, configSection.getString("you're_not_in_the_clan"));
                return;
            }

            ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();

            boolean b = clan.disband(modifiedPlayer, clan);

            if (b) {
                chatUtil.sendMessage(player, configSection.getString("you_deleted_the_clan"));
                return;
            } else {
                chatUtil.sendMessage(player, configSection.getString("you_are_not_a_leader"));
            }


            return;

        }

        if (args[0].equalsIgnoreCase("reload")) {

            configSection = Plugin.getInstance().getConfig().getConfigurationSection("message.command.reload");

            if (!player.hasPermission("towerclans.reload")) {
                chatUtil.sendMessage(player, configSection.getString("message.command.reload.you_don't_have_permission"));
                return;
            }

            Plugin.getInstance().reloadConfig();
            chatUtil.sendMessage(player, configSection.getString("message.command.reload.you_have_reloaded_the_config"));

            return;
        }
    }

    public static Map<UUID, UUID> getPlayers() {
        return PLAYERS;
    }


}

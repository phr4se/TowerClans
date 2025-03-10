package phrase.towerClans.commands.impls;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.AbstractClan;
import phrase.towerClans.clan.ModifiedPlayer;
import phrase.towerClans.clan.impls.ClanImpl;
import phrase.towerClans.commands.AbstractCommand;
import phrase.towerClans.utils.ChatUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ClanCommand extends AbstractCommand {

    private static final Map<Player, Player> players = new HashMap<>();

    public ClanCommand(String command) {
        super(command);
    }

    @Override
    public void execute(CommandSender commandSender, Command command, String label, String[] args) {

        if(!label.equals("clan")) return;

        if(!(commandSender instanceof Player)) return;

        Player player = (Player) commandSender;
        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);

        if(args.length < 1) {

            if(modifiedPlayer.getClan() == null) {
                List<String> list = Plugin.instance.getConfig().getStringList("message.a_player_without_a_clan");
                for(String string : list) {
                    ChatUtil.sendMessage(player, string);
                }
                return;
            }

            List<String> list = Plugin.instance.getConfig().getStringList("message.a_player_with_a_clan");
            for(String string : list) {
                ChatUtil.sendMessage(player, string);
            }
            return;
        }

        if(args[0].equalsIgnoreCase("chat")) {

            if(modifiedPlayer.getClan() == null) {
                ChatUtil.sendMessage(player, Plugin.instance.getConfig().getString("message.command.chat.you're_not_in_the_clan"));
                return;
            }

            ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();

            StringBuilder stringBuilder = new StringBuilder();
            for(int i = 1; i < args.length; i++) {
                stringBuilder.append(args[i]).append(" ");
            }

            String string = Plugin.instance.getConfig().getString("message.command.chat.message_format").replace("%player%", modifiedPlayer.getPlayer().getName()).replace("%rank%", clan.getMembers().get(modifiedPlayer)).replace("%message%", stringBuilder.toString());
            for(Map.Entry<ModifiedPlayer, String> entry : clan.getMembers().entrySet()) {
                ChatUtil.sendMessage(entry.getKey().getPlayer(), string);
            }

            return;
        }

        if(args[0].equalsIgnoreCase("create")) {

            if(modifiedPlayer.getClan() != null) {
                ChatUtil.sendMessage(player, Plugin.instance.getConfig().getString("message.command.create.you_are_in_a_clan"));
                return;
            }

            if(args.length < 2) {
                ChatUtil.sendMessage(player, Plugin.instance.getConfig().getString("message.command.create.usage_command"));
                return;
            }

            int amount = Plugin.instance.getConfig().getInt("settings.the_cost_of_creating_a_clan");

            if(ClanImpl.clans.containsKey(args[1])) {
                ChatUtil.sendMessage(player, Plugin.instance.getConfig().getString("message.command.create.a_clan_with_that_name_already_exists"));
                return;
            }

            if(Plugin.instance.economy.getBalance(player) < amount) {
                String string = Plugin.instance.getConfig().getString("message.command.create.you_don't_have_enough").replace("%amount%", String.valueOf(amount - Plugin.instance.economy.getBalance(player)));
                ChatUtil.sendMessage(player, string);
                return;
            }


            Plugin.instance.economy.withdrawPlayer(player, amount);
            String name = args[1];

            ClanImpl clan = new ClanImpl(name);
            modifiedPlayer.setClan(clan);
            clan.getMembers().put(modifiedPlayer, AbstractClan.RankType.LEADER.getName());
            ClanImpl.clans.put(args[1], clan);

            ChatUtil.sendMessage(player, Plugin.instance.getConfig().getString("message.command.create.you_have_created_a_clan"));

            return;
        }

        if(args[0].equalsIgnoreCase("menu")) {


            if (modifiedPlayer.getClan() == null) {
                ChatUtil.sendMessage(player, Plugin.instance.getConfig().getString("message.command.menu.you're_not_in_the_clan"));
                return;
            }

            ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
            clan.showMenu(modifiedPlayer, AbstractClan.MenuType.MENU_CLAN.getId());
            ChatUtil.sendMessage(player, Plugin.instance.getConfig().getString("message.command.menu.you_have_opened_the_clan_menu"));

            return;
        }

        if(args[0].equalsIgnoreCase("invite")) {

            if(modifiedPlayer.getClan() == null) {
                ChatUtil.sendMessage(player, Plugin.instance.getConfig().getString("message.command.invite.you're_not_in_the_clan"));
                return;
            }

            ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();

            if(!clan.getMembers().get(modifiedPlayer).equals(AbstractClan.RankType.LEADER.getName()) && !clan.getMembers().get(modifiedPlayer).equals(AbstractClan.RankType.DEPUTY.getName())) {
                ChatUtil.sendMessage(player, Plugin.instance.getConfig().getString("message.command.invite.you_don't_have_permission"));
                return;
            }

            if(args.length < 2) {
                ChatUtil.sendMessage(player, Plugin.instance.getConfig().getString("message.command.invite.usage_command"));
                return;
            }

            String name = args[1];

            Player targetPlayer = Bukkit.getPlayer(name);

            if(targetPlayer == null) {
                ChatUtil.sendMessage(player, Plugin.instance.getConfig().getString("message.command.invite.the_player_was_not_found"));
                return;
            }

            players.put(targetPlayer, player);
            ChatUtil.sendMessage(player, Plugin.instance.getConfig().getString("message.command.invite.you_have_sent_a_request_to_join_the_clan"));
            ChatUtil.sendMessage(targetPlayer, Plugin.instance.getConfig().getString("message.command.invite.you_have_received_a_request_to_join_the_clan"));

            return;
        }

        if(args[0].equalsIgnoreCase("accept")) {

            Player senderPlayer = players.remove(player);

            if(senderPlayer == null) {
                ChatUtil.sendMessage(player, Plugin.instance.getConfig().getString("message.command.invite.accept.has_anyone_sent_you_a_request_to_join_clan"));
                return;
            }

            if(modifiedPlayer.getClan() != null) {
                ChatUtil.sendMessage(player, Plugin.instance.getConfig().getString("message.command.invite.accept.you_are_in_a_clan"));
                return;
            }

            ModifiedPlayer senderModifiedPlayer = ModifiedPlayer.get(senderPlayer);
            ClanImpl clan = (ClanImpl) senderModifiedPlayer.getClan();

            modifiedPlayer.setClan(clan);
            boolean b = clan.invite(modifiedPlayer);

            if(b) {
                ChatUtil.sendMessage(player, Plugin.instance.getConfig().getString("message.command.invite.accept.have_you_accepted_the_request_to_join_the_clan"));
                ChatUtil.sendMessage(senderPlayer, Plugin.instance.getConfig().getString("message.command.invite.accept.the_player_accepted_the_request_to_join_the_clan"));
                return;
            } else {
                ChatUtil.sendMessage(player, Plugin.instance.getConfig().getString("message.command.invite.accept.you_are_in_a_clan"));
            }

            return;
        }

        if(args[0].equalsIgnoreCase("cancel")) {

            Player senderPlayer = players.remove(player);

            if(senderPlayer == null) {
                ChatUtil.sendMessage(player, Plugin.instance.getConfig().getString("message.command.invite.cancel.has_anyone_sent_you_a_request_to_join_clan"));
                return;
            }

            ChatUtil.sendMessage(player, Plugin.instance.getConfig().getString("message.command.invite.cancel.you_rejected_the_request_to_join_the_clan"));
            ChatUtil.sendMessage(senderPlayer, Plugin.instance.getConfig().getString("message.command.invite.cancel.the_player_rejected_the_request_to_join_the_clan"));

            return;
        }

        if(args[0].equalsIgnoreCase("kick")) {

            if(modifiedPlayer.getClan() == null) {
                ChatUtil.sendMessage(player, Plugin.instance.getConfig().getString("message.command.kick.you're_not_in_the_clan"));
                return;
            }

            ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();

            if(!clan.getMembers().get(modifiedPlayer).equals(AbstractClan.RankType.LEADER.getName()) && !clan.getMembers().get(modifiedPlayer).equals(AbstractClan.RankType.DEPUTY.getName())) {
                ChatUtil.sendMessage(player, Plugin.instance.getConfig().getString("message.command.kick.you_don't_have_permission"));
                return;
            }

            if(args.length < 2) {
                ChatUtil.sendMessage(player, Plugin.instance.getConfig().getString("message.command.kick.usage_command"));
                return;
            }


            String name = args[1];

            Player targetPlayer = Bukkit.getPlayer(name);

            if(targetPlayer == null) {
                ChatUtil.sendMessage(player, Plugin.instance.getConfig().getString("message.command.kick.the_player_was_not_found"));
                return;
            }

            ModifiedPlayer targetModifiedPlayer = ModifiedPlayer.get(targetPlayer);

            if(clan.getMembers().get(modifiedPlayer).equals("Лидер")) {
                ChatUtil.sendMessage(player, Plugin.instance.getConfig().getString("message.command.kick.you_cannot_leave_the_clan"));
                return;
            }

            boolean b = clan.kick(targetModifiedPlayer);

            if (b) {
                ChatUtil.sendMessage(player, Plugin.instance.getConfig().getString("message.command.kick.you_kicked_a_player_from_the_clan"));
                ChatUtil.sendMessage(targetPlayer, Plugin.instance.getConfig().getString("message.command.kick.you_were_kicked_out_of_the_clan"));
                return;
            } else {
                ChatUtil.sendMessage(player, Plugin.instance.getConfig().getString("message.command.kick.the_player_is_not_in_the_clan"));
            }

            return;
        }

        if(args[0].equalsIgnoreCase("invest")) {

            if(modifiedPlayer.getClan() == null) {
                ChatUtil.sendMessage(player, Plugin.instance.getConfig().getString("message.command.invest.you're_not_in_the_clan"));
                return;
            }

            ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();

            if(args.length < 2) {
                ChatUtil.sendMessage(player, Plugin.instance.getConfig().getString("message.command.invest.usage_command"));
                return;
            }


            int amount = 0;
            try {
                amount = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                ChatUtil.sendMessage(player, Plugin.instance.getConfig().getString("message.command.invest.usage_command"));
                return;
            }

            boolean b = clan.invest(modifiedPlayer, amount);
            if(b) {
                ChatUtil.sendMessage(player, Plugin.instance.getConfig().getString("message.command.invest.you_put_it_in_the_clan"));
                return;
            } else {
                ChatUtil.sendMessage(player, Plugin.instance.getConfig().getString("message.command.invest.you_don't_have_enough"));
            }

            return;
        }

        if(args[0].equalsIgnoreCase("withdraw")) {

            if(modifiedPlayer.getClan() == null) {
                ChatUtil.sendMessage(player, Plugin.instance.getConfig().getString("message.command.withdraw.you're_not_in_the_clan"));
                return;
            }

            ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();

            if(!clan.getMembers().get(modifiedPlayer).equals(AbstractClan.RankType.LEADER.getName()) && !clan.getMembers().get(modifiedPlayer).equals(AbstractClan.RankType.DEPUTY.getName())) {
                ChatUtil.sendMessage(player, Plugin.instance.getConfig().getString("message.command.withdraw.you_don't_have_permission"));
                return;
            }

            if(args.length < 2) {
                ChatUtil.sendMessage(player, Plugin.instance.getConfig().getString("message.command.withdraw.usage_command"));
                return;
            }

            int amount = 0;
            try {
                amount = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                ChatUtil.sendMessage(player, Plugin.instance.getConfig().getString("message.command.withdraw.usage_command"));
                return;
            }

            boolean b = clan.withdraw(modifiedPlayer, amount);
            if(b) {
                ChatUtil.sendMessage(player, Plugin.instance.getConfig().getString("message.command.withdraw.you_have_withdrawn_from_the_clan"));
                return;
            } else {
                ChatUtil.sendMessage(player, Plugin.instance.getConfig().getString("message.command.withdraw.not_in_the_clan"));
            }

            return;
        }

        if(args[0].equalsIgnoreCase("leave")) {

            if(modifiedPlayer.getClan() == null) {
                ChatUtil.sendMessage(player, Plugin.instance.getConfig().getString("message.command.leave.you're_not_in_the_clan"));
                return;
            }

            ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();

            if(clan.getMembers().get(modifiedPlayer).equals("Лидер")) {
                ChatUtil.sendMessage(player, Plugin.instance.getConfig().getString("message.command.leave.you_cannot_leave_the_clan"));
                return;
            }

            boolean b = clan.leave(modifiedPlayer);

            if(b) {
                ChatUtil.sendMessage(player, Plugin.instance.getConfig().getString("message.command.leave.you_have_left_the_clan"));
                return;
            } else {
                ChatUtil.sendMessage(player, Plugin.instance.getConfig().getString("message.command.leave.you're_not_in_the_clan"));
            }

            return;
        }

        if(args[0].equalsIgnoreCase("pvp")) {

            if(modifiedPlayer.getClan() == null) {
                ChatUtil.sendMessage(player, Plugin.instance.getConfig().getString("message.command.pvp.you're_not_in_the_clan"));
                return;
            }

            ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();

            if(!clan.getMembers().get(modifiedPlayer).equals(AbstractClan.RankType.LEADER.getName()) && !clan.getMembers().get(modifiedPlayer).equals(AbstractClan.RankType.DEPUTY.getName())) {
                ChatUtil.sendMessage(player, Plugin.instance.getConfig().getString("message.command.pvp.you_don't_have_permission"));
                return;
            }

            if(clan.isPvp()) {
                clan.setPvp(false);
                ChatUtil.sendMessage(player, Plugin.instance.getConfig().getString("message.command.pvp.you_have_disabled_pvp_in_the_clan"));
                return;
            } else {
                clan.setPvp(true);
                ChatUtil.sendMessage(player, Plugin.instance.getConfig().getString("message.command.pvp.you_have_enabled_pvp_in_the_clan"));
            }

            return;
        }

        if(args[0].equalsIgnoreCase("rank")) {

            if(modifiedPlayer.getClan() == null) {
                ChatUtil.sendMessage(player, Plugin.instance.getConfig().getString("message.command.rank.you're_not_in_the_clan"));
                return;
            }

            ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();

            if(!clan.getMembers().get(modifiedPlayer).equals(AbstractClan.RankType.LEADER.getName())) {
                ChatUtil.sendMessage(player, Plugin.instance.getConfig().getString("message.command.rank.you_don't_have_permission"));
                return;
            }

            if(args.length < 3) {
                ChatUtil.sendMessage(player, Plugin.instance.getConfig().getString("message.command.rank.usage_command"));
                return;
            }

            String name = args[1];
            Player targetPlayer;
            try {
                targetPlayer = Objects.requireNonNull(Bukkit.getPlayer(name));
            } catch (NullPointerException e) {
                ChatUtil.sendMessage(player, Plugin.instance.getConfig().getString("message.command.rank.the_player_is_offline"));
                return;
            }
            ModifiedPlayer targetModifierPlayer = ModifiedPlayer.get(targetPlayer);

            int id = 0;
            try {
                id = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                ChatUtil.sendMessage(player, Plugin.instance.getConfig().getString("message.command.rank.usage_command"));
            }

            boolean b = clan.rank(targetModifierPlayer, id);

            if(b) {
                String string = Plugin.instance.getConfig().getString("message.command.rank.you_have_set_the_player's_rank").replace("%player%", targetPlayer.getName()).replace("%rank%", (id == 2) ? AbstractClan.RankType.DEPUTY.getName() : AbstractClan.RankType.MEMBER.getName());
                ChatUtil.sendMessage(player, string);
                string = Plugin.instance.getConfig().getString("message.command.rank.you_have_been_set_a_rank").replace("%player%", player.getName()).replace("%rank%", (id == 2) ? AbstractClan.RankType.DEPUTY.getName() : AbstractClan.RankType.MEMBER.getName());
                ChatUtil.sendMessage(player, string);
                return;
            } else {
                ChatUtil.sendMessage(player, "message.command.rank.usage_command");
            }

            return;

        }

    }


}

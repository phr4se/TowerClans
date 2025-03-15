package phrase.towerClans.commands;

import phrase.towerClans.commands.impls.chat.ClanChatCommand;
import phrase.towerClans.commands.impls.create.ClanCreateCommand;
import phrase.towerClans.commands.impls.disband.ClanDisbandCommand;
import phrase.towerClans.commands.impls.invite.ClanAcceptCommand;
import phrase.towerClans.commands.impls.invite.ClanCancelCommand;
import phrase.towerClans.commands.impls.invest.ClanInvestCommand;
import phrase.towerClans.commands.impls.invite.ClanInviteCommand;
import phrase.towerClans.commands.impls.kick.ClanKickCommand;
import phrase.towerClans.commands.impls.menu.ClanMenuCommand;
import phrase.towerClans.commands.impls.pvp.ClanPvpCommand;
import phrase.towerClans.commands.impls.rank.ClanRankCommand;
import phrase.towerClans.commands.impls.reload.ClanReloadCommand;
import phrase.towerClans.commands.impls.withdraw.ClanWithdrawCommand;

import java.util.HashMap;
import java.util.Map;

public class CommandLogger {

    private static final Map<String, CommandDescription> commands = new HashMap<>();

    public CommandLogger() {
        intialize();
    }

    public static Map<String, CommandDescription> getCommands() {
        return commands;
    }

    private void intialize() {
        commands.put("create", new CommandDescription("towerclans.create", new ClanCreateCommand()));
        commands.put("disband", new CommandDescription("towerclans.disband", new ClanDisbandCommand()));
        commands.put("invest", new CommandDescription("towerclans.invest", new ClanInvestCommand()));
        commands.put("withdraw", new CommandDescription("towerclans.withdraw", new ClanWithdrawCommand()));
        commands.put("invite", new CommandDescription("towerclans.invite", new ClanInviteCommand()));
        commands.put("kick", new CommandDescription("towerclans.kick", new ClanKickCommand()));
        commands.put("accept", new CommandDescription("towerclans.accept", new ClanAcceptCommand()));
        commands.put("cancel", new CommandDescription("towerclans.cancel", new ClanCancelCommand()));
        commands.put("reload", new CommandDescription("towerclans.reload", new ClanReloadCommand()));
        commands.put("leave", new CommandDescription("towerclans.leave", new ClanPvpCommand()));
        commands.put("chat", new CommandDescription("towerclans.disband", new ClanChatCommand()));
        commands.put("menu", new CommandDescription("towerclans.menu", new ClanMenuCommand()));
        commands.put("pvp", new CommandDescription("towerclans.pvp", new ClanPvpCommand()));
        commands.put("rank", new CommandDescription("towerclans.rank", new ClanRankCommand()));
    }

}

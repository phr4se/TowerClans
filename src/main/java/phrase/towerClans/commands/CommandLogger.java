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

    private static final Map<String, CommandDescription> COMMANDS = new HashMap<>();

    public CommandLogger() {
        intialize();
    }

    public static Map<String, CommandDescription> getCommands() {
        return COMMANDS;
    }

    private void intialize() {
        COMMANDS.put("create", new CommandDescription("towerclans.create", new ClanCreateCommand()));
        COMMANDS.put("disband", new CommandDescription("towerclans.disband", new ClanDisbandCommand()));
        COMMANDS.put("invest", new CommandDescription("towerclans.invest", new ClanInvestCommand()));
        COMMANDS.put("withdraw", new CommandDescription("towerclans.withdraw", new ClanWithdrawCommand()));
        COMMANDS.put("invite", new CommandDescription("towerclans.invite", new ClanInviteCommand()));
        COMMANDS.put("kick", new CommandDescription("towerclans.kick", new ClanKickCommand()));
        COMMANDS.put("accept", new CommandDescription("towerclans.accept", new ClanAcceptCommand()));
        COMMANDS.put("cancel", new CommandDescription("towerclans.cancel", new ClanCancelCommand()));
        COMMANDS.put("reload", new CommandDescription("towerclans.reload", new ClanReloadCommand()));
        COMMANDS.put("leave", new CommandDescription("towerclans.leave", new ClanPvpCommand()));
        COMMANDS.put("chat", new CommandDescription("towerclans.disband", new ClanChatCommand()));
        COMMANDS.put("menu", new CommandDescription("towerclans.menu", new ClanMenuCommand()));
        COMMANDS.put("pvp", new CommandDescription("towerclans.pvp", new ClanPvpCommand()));
        COMMANDS.put("rank", new CommandDescription("towerclans.rank", new ClanRankCommand()));
    }

}

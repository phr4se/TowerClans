package phrase.towerClans.command;

import phrase.towerClans.Plugin;
import phrase.towerClans.command.impl.base.ClanBaseCommand;
import phrase.towerClans.command.impl.base.ClanDelBaseCommand;
import phrase.towerClans.command.impl.base.ClanSetBaseCommand;
import phrase.towerClans.command.impl.chat.ClanChatCommand;
import phrase.towerClans.command.impl.create.ClanCreateCommand;
import phrase.towerClans.command.impl.disband.ClanDisbandCommand;
import phrase.towerClans.command.impl.info.ClanInfoCommand;
import phrase.towerClans.command.impl.invite.ClanAcceptCommand;
import phrase.towerClans.command.impl.invite.ClanCancelCommand;
import phrase.towerClans.command.impl.invest.ClanInvestCommand;
import phrase.towerClans.command.impl.invite.ClanInviteCommand;
import phrase.towerClans.command.impl.kick.ClanKickCommand;
import phrase.towerClans.command.impl.leave.ClanLeaveCommand;
import phrase.towerClans.command.impl.menu.ClanMenuCommand;
import phrase.towerClans.command.impl.pvp.ClanPvpCommand;
import phrase.towerClans.command.impl.rank.ClanRankCommand;
import phrase.towerClans.command.impl.reload.ClanReloadCommand;
import phrase.towerClans.command.impl.stats.ClanStatsCommand;
import phrase.towerClans.command.impl.storage.ClanStorageCommand;
import phrase.towerClans.command.impl.top.ClanTopCommand;
import phrase.towerClans.command.impl.withdraw.ClanWithdrawCommand;

import java.util.HashMap;
import java.util.Map;

public class CommandLogger {

    private static final Map<String, CommandDescription> COMMANDS = new HashMap<>();

    private final Plugin plugin;

    public CommandLogger(Plugin plugin) {
        this.plugin = plugin;
        intialize();
    }

    public static Map<String, CommandDescription> getCommands() {
        return COMMANDS;
    }

    private void intialize() {
        COMMANDS.put("create", new CommandDescription("towerclans.create", new ClanCreateCommand(plugin)));
        COMMANDS.put("disband", new CommandDescription("towerclans.disband", new ClanDisbandCommand(plugin)));
        COMMANDS.put("invest", new CommandDescription("towerclans.invest", new ClanInvestCommand(plugin)));
        COMMANDS.put("withdraw", new CommandDescription("towerclans.withdraw", new ClanWithdrawCommand(plugin)));
        COMMANDS.put("invite", new CommandDescription("towerclans.invite", new ClanInviteCommand(plugin)));
        COMMANDS.put("kick", new CommandDescription("towerclans.kick", new ClanKickCommand(plugin)));
        COMMANDS.put("accept", new CommandDescription("towerclans.accept", new ClanAcceptCommand(plugin)));
        COMMANDS.put("cancel", new CommandDescription("towerclans.cancel", new ClanCancelCommand(plugin)));
        COMMANDS.put("reload", new CommandDescription("towerclans.reload", new ClanReloadCommand(plugin)));
        COMMANDS.put("leave", new CommandDescription("towerclans.leave", new ClanLeaveCommand(plugin)));
        COMMANDS.put("chat", new CommandDescription("towerclans.disband", new ClanChatCommand(plugin)));
        COMMANDS.put("menu", new CommandDescription("towerclans.menu", new ClanMenuCommand(plugin)));
        COMMANDS.put("pvp", new CommandDescription("towerclans.pvp", new ClanPvpCommand(plugin)));
        COMMANDS.put("rank", new CommandDescription("towerclans.rank", new ClanRankCommand(plugin)));
        COMMANDS.put("stats", new CommandDescription("towerclans.stats", new ClanStatsCommand(plugin)));
        COMMANDS.put("info", new CommandDescription("towerclans.info", new ClanInfoCommand(plugin)));
        COMMANDS.put("top", new CommandDescription("towerclans.top", new ClanTopCommand(plugin)));
        COMMANDS.put("base", new CommandDescription("towerclans.base", new ClanBaseCommand(plugin)));
        COMMANDS.put("setbase", new CommandDescription("towerclans.setbase", new ClanSetBaseCommand(plugin)));
        COMMANDS.put("delbase", new CommandDescription("towerclans.delbase", new ClanDelBaseCommand(plugin)));
        COMMANDS.put("storage", new CommandDescription("towerclans.storage", new ClanStorageCommand(plugin)));
    }

}

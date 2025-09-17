package phrase.towerClans.command;

import phrase.towerClans.Plugin;
import phrase.towerClans.command.impl.base.ClanBaseCommand;
import phrase.towerClans.command.impl.base.ClanDelBaseCommand;
import phrase.towerClans.command.impl.base.ClanSetBaseCommand;
import phrase.towerClans.command.impl.chat.ClanChatCommand;
import phrase.towerClans.command.impl.create.ClanCreateCommand;
import phrase.towerClans.command.impl.disband.ClanDisbandCommand;
import phrase.towerClans.command.impl.event.ClanEventCommand;
import phrase.towerClans.command.impl.glow.ClanGlowCommand;
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

import java.util.Collections;
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
        return Collections.unmodifiableMap(COMMANDS);
    }

    private void intialize() {
        COMMANDS.put("create", new CommandDescription("towerclans.create", new ClanCreateCommand(plugin), CommandDescription.CommandType.WITHOUT_CLAN));
        COMMANDS.put("disband", new CommandDescription("towerclans.disband", new ClanDisbandCommand(), CommandDescription.CommandType.WITH_CLAN));
        COMMANDS.put("invest", new CommandDescription("towerclans.invest", new ClanInvestCommand(), CommandDescription.CommandType.WITH_CLAN));
        COMMANDS.put("withdraw", new CommandDescription("towerclans.withdraw", new ClanWithdrawCommand(), CommandDescription.CommandType.WITH_CLAN));
        COMMANDS.put("invite", new CommandDescription("towerclans.invite", new ClanInviteCommand(plugin), CommandDescription.CommandType.WITH_CLAN));
        COMMANDS.put("kick", new CommandDescription("towerclans.kick", new ClanKickCommand(plugin), CommandDescription.CommandType.WITH_CLAN));
        COMMANDS.put("accept", new CommandDescription("towerclans.accept", new ClanAcceptCommand(plugin), CommandDescription.CommandType.WITHOUT_CLAN));
        COMMANDS.put("cancel", new CommandDescription("towerclans.cancel", new ClanCancelCommand(), CommandDescription.CommandType.WITHOUT_CLAN));
        COMMANDS.put("reload", new CommandDescription("towerclans.reload", new ClanReloadCommand(plugin), CommandDescription.CommandType.ADMIN));
        COMMANDS.put("leave", new CommandDescription("towerclans.leave", new ClanLeaveCommand(plugin), CommandDescription.CommandType.WITH_CLAN));
        COMMANDS.put("chat", new CommandDescription("towerclans.disband", new ClanChatCommand(), CommandDescription.CommandType.WITH_CLAN));
        COMMANDS.put("menu", new CommandDescription("towerclans.menu", new ClanMenuCommand(), CommandDescription.CommandType.WITH_CLAN));
        COMMANDS.put("pvp", new CommandDescription("towerclans.pvp", new ClanPvpCommand(), CommandDescription.CommandType.WITH_CLAN));
        COMMANDS.put("rank", new CommandDescription("towerclans.rank", new ClanRankCommand(), CommandDescription.CommandType.WITH_CLAN));
        COMMANDS.put("stats", new CommandDescription("towerclans.stats", new ClanStatsCommand(), CommandDescription.CommandType.WITH_CLAN));
        COMMANDS.put("info", new CommandDescription("towerclans.info", new ClanInfoCommand(), CommandDescription.CommandType.WITH_CLAN));
        COMMANDS.put("top", new CommandDescription("towerclans.top", new ClanTopCommand(), CommandDescription.CommandType.AND));
        COMMANDS.put("base", new CommandDescription("towerclans.base", new ClanBaseCommand(), CommandDescription.CommandType.WITH_CLAN));
        COMMANDS.put("setbase", new CommandDescription("towerclans.setbase", new ClanSetBaseCommand(), CommandDescription.CommandType.WITH_CLAN));
        COMMANDS.put("delbase", new CommandDescription("towerclans.delbase", new ClanDelBaseCommand(), CommandDescription.CommandType.WITH_CLAN));
        COMMANDS.put("storage", new CommandDescription("towerclans.storage", new ClanStorageCommand(), CommandDescription.CommandType.WITH_CLAN));
        COMMANDS.put("glow", new CommandDescription("towerclans.glow", new ClanGlowCommand(plugin), CommandDescription.CommandType.WITH_CLAN));
        COMMANDS.put("event", new CommandDescription("towerclans.event", new ClanEventCommand(plugin), CommandDescription.CommandType.ADMIN));
    }

}

package phrase.towerclans.command;

import phrase.towerclans.Plugin;
import phrase.towerclans.command.impl.base.ClanBaseCommand;
import phrase.towerclans.command.impl.base.ClanDelBaseCommand;
import phrase.towerclans.command.impl.base.ClanSetBaseCommand;
import phrase.towerclans.command.impl.chat.ClanChatCommand;
import phrase.towerclans.command.impl.create.ClanCreateCommand;
import phrase.towerclans.command.impl.disband.ClanDisbandCommand;
import phrase.towerclans.command.impl.event.ClanEventCommand;
import phrase.towerclans.command.impl.glow.ClanGlowCommand;
import phrase.towerclans.command.impl.info.ClanInfoCommand;
import phrase.towerclans.command.impl.invite.ClanAcceptCommand;
import phrase.towerclans.command.impl.invite.ClanCancelCommand;
import phrase.towerclans.command.impl.invest.ClanInvestCommand;
import phrase.towerclans.command.impl.invite.ClanInviteCommand;
import phrase.towerclans.command.impl.kick.ClanKickCommand;
import phrase.towerclans.command.impl.leave.ClanLeaveCommand;
import phrase.towerclans.command.impl.menu.ClanMenuCommand;
import phrase.towerclans.command.impl.pvp.ClanPvpCommand;
import phrase.towerclans.command.impl.rank.ClanRankCommand;
import phrase.towerclans.command.impl.reload.ClanReloadCommand;
import phrase.towerclans.command.impl.stats.ClanStatsCommand;
import phrase.towerclans.command.impl.storage.ClanStorageCommand;
import phrase.towerclans.command.impl.top.ClanTopCommand;
import phrase.towerclans.command.impl.withdraw.ClanWithdrawCommand;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CommandLogger {
    private final Map<String, CommandDescription> commands;
    private final Plugin plugin;

    public CommandLogger(Plugin plugin) {
        this.commands = new HashMap<>();
        this.plugin = plugin;
        initialize();
    }

    public Map<String, CommandDescription> getCommands() {
        return Collections.unmodifiableMap(commands);
    }

    private void initialize() {
        commands.put("create", new CommandDescription("towerclans.create", new ClanCreateCommand(plugin), CommandDescription.CommandType.WITHOUT_CLAN));
        commands.put("disband", new CommandDescription("towerclans.disband", new ClanDisbandCommand(), CommandDescription.CommandType.WITH_CLAN));
        commands.put("invest", new CommandDescription("towerclans.invest", new ClanInvestCommand(), CommandDescription.CommandType.WITH_CLAN));
        commands.put("withdraw", new CommandDescription("towerclans.withdraw", new ClanWithdrawCommand(), CommandDescription.CommandType.WITH_CLAN));
        commands.put("invite", new CommandDescription("towerclans.invite", new ClanInviteCommand(plugin), CommandDescription.CommandType.WITH_CLAN));
        commands.put("kick", new CommandDescription("towerclans.kick", new ClanKickCommand(plugin), CommandDescription.CommandType.WITH_CLAN));
        commands.put("accept", new CommandDescription("towerclans.accept", new ClanAcceptCommand(plugin), CommandDescription.CommandType.WITHOUT_CLAN));
        commands.put("cancel", new CommandDescription("towerclans.cancel", new ClanCancelCommand(), CommandDescription.CommandType.WITHOUT_CLAN));
        commands.put("reload", new CommandDescription("towerclans.reload", new ClanReloadCommand(plugin), CommandDescription.CommandType.ADMIN));
        commands.put("leave", new CommandDescription("towerclans.leave", new ClanLeaveCommand(plugin), CommandDescription.CommandType.WITH_CLAN));
        commands.put("chat", new CommandDescription("towerclans.disband", new ClanChatCommand(), CommandDescription.CommandType.WITH_CLAN));
        commands.put("menu", new CommandDescription("towerclans.menu", new ClanMenuCommand(), CommandDescription.CommandType.WITH_CLAN));
        commands.put("pvp", new CommandDescription("towerclans.pvp", new ClanPvpCommand(), CommandDescription.CommandType.WITH_CLAN));
        commands.put("rank", new CommandDescription("towerclans.rank", new ClanRankCommand(), CommandDescription.CommandType.WITH_CLAN));
        commands.put("stats", new CommandDescription("towerclans.stats", new ClanStatsCommand(plugin), CommandDescription.CommandType.WITH_CLAN));
        commands.put("info", new CommandDescription("towerclans.info", new ClanInfoCommand(plugin), CommandDescription.CommandType.WITH_CLAN));
        commands.put("top", new CommandDescription("towerclans.top", new ClanTopCommand(plugin), CommandDescription.CommandType.AND));
        commands.put("base", new CommandDescription("towerclans.base", new ClanBaseCommand(), CommandDescription.CommandType.WITH_CLAN));
        commands.put("setbase", new CommandDescription("towerclans.setbase", new ClanSetBaseCommand(), CommandDescription.CommandType.WITH_CLAN));
        commands.put("delbase", new CommandDescription("towerclans.delbase", new ClanDelBaseCommand(), CommandDescription.CommandType.WITH_CLAN));
        commands.put("storage", new CommandDescription("towerclans.storage", new ClanStorageCommand(), CommandDescription.CommandType.WITH_CLAN));
        commands.put("glow", new CommandDescription("towerclans.glow", new ClanGlowCommand(plugin), CommandDescription.CommandType.WITH_CLAN));
        commands.put("event", new CommandDescription("towerclans.event", new ClanEventCommand(plugin), CommandDescription.CommandType.ADMIN));
    }
}

package phrase.towerclans;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import phrase.towerclans.clan.ClanManager;
import phrase.towerclans.clan.attribute.clan.RankType;
import phrase.towerclans.clan.attribute.clan.StorageManager;
import phrase.towerclans.clan.attribute.player.StatsManager;
import phrase.towerclans.clan.entity.ModifiedPlayer;
import phrase.towerclans.clan.event.Event;
import phrase.towerclans.clan.event.privilege.PrivilegeManager;
import phrase.towerclans.clan.impl.clan.ClanImpl;
import phrase.towerclans.clan.impl.manager.ClanManagerImpl;
import phrase.towerclans.command.CommandLogger;
import phrase.towerclans.command.CommandMapper;
import phrase.towerclans.command.CommandResult;
import phrase.towerclans.command.impl.ClanTabCompleter;
import phrase.towerclans.config.Config;
import phrase.towerclans.database.DatabaseManager;
import phrase.towerclans.glow.GlowPacketListener;
import phrase.towerclans.gui.MenuPages;
import phrase.towerclans.listener.ClanListener;
import phrase.towerclans.listener.PlayerListener;
import phrase.towerclans.util.Placeholder;
import phrase.towerclans.util.Utils;

import java.sql.SQLException;
import java.util.logging.Logger;

public final class TowerClans extends JavaPlugin implements CommandExecutor {
    private static TowerClans instance;
    private final CommandLogger commandLogger = new CommandLogger(this);
    private final CommandMapper commandMapper = new CommandMapper(commandLogger);
    private final StatsManager statsManager = new StatsManager();
    private final PrivilegeManager privilegeManager = new PrivilegeManager();
    private Economy economy;
    private DatabaseManager databaseManager;
    private ClanManager<ClanImpl> clanManager;

    @Override
    public void onEnable() {
        instance = this;
        Logger logger = getLogger();
        PluginManager pluginManager = Bukkit.getPluginManager();
        saveDefaultConfig();
        if (!pluginManager.isPluginEnabled("WorldEdit") && !pluginManager.isPluginEnabled("WorldGuard")) {
            logger.severe("WorldEdit и WorldGuard не найден. Плагин будет выключен");
            pluginManager.disablePlugin(this);
            return;
        }
        Config.plugin = getInstance();
        Config.createFiles("menus/menu-clan-main.yml", "menus/menu-clan-members.yml", "menus/menu-clan-level.yml", "menus/menu-clan-storage.yml", "menus/menu-clan-glow.yml", "messages.yml", "levels.yml", "event-capture.yml", "menu-pages.yml");
        Config.setupMessages(Config.getFile("messages.yml"));
        Config.setupSettings(getConfig());
        clanManager = new ClanManagerImpl();
        databaseManager = new DatabaseManager(Config.getSettings().databaseType(), this);
        StorageManager.initialize();
        MenuPages.initialize();
        RankType.initialize(this);
        ModifiedPlayer.plugin = getInstance();
        if (!setupEconomy()) {
            logger.severe("Vault не найден. Плагин будет выключен");
            pluginManager.disablePlugin(this);
            return;
        }
        databaseManager.getDatabase().loadClans();
        databaseManager.getDatabase().loadPlayers();
        databaseManager.getDatabase().loadPermissions();
        PluginCommand pluginCommand = getCommand("clan");
        pluginCommand.setExecutor(this);
        pluginCommand.setTabCompleter(new ClanTabCompleter(this, commandLogger));
        if (pluginManager.isPluginEnabled("PlaceholderAPI")) new Placeholder(this).register();
        pluginManager.registerEvents(new PlayerListener(this), this);
        pluginManager.registerEvents(new ClanListener(this), this);
        if (!pluginManager.isPluginEnabled("ProtocolLib")) {
            logger.severe("ProtocolLib не найден. Плагин будет выключен");
            pluginManager.disablePlugin(this);
            return;
        }
        ProtocolLibrary.getProtocolManager().addPacketListener(new GlowPacketListener(this, PacketType.Play.Server.ENTITY_EQUIPMENT));
        privilegeManager.setPrivilege(Config.getSettings().type(), this);
        new BukkitRunnable() {
            @Override
            public void run() {
                databaseManager.getDatabase().saveClans();
                databaseManager.getDatabase().savePlayers();
                databaseManager.getDatabase().savePermissions();
            }
        }.runTaskTimerAsynchronously(this, 0L, 1200L);
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> registeredServiceProvider = getServer().getServicesManager().getRegistration(Economy.class);
        if (registeredServiceProvider == null) {
            return false;
        }
        economy = registeredServiceProvider.getProvider();
        return true;
    }

    @Override
    public void onDisable() {
        databaseManager.getDatabase().saveClans();
        databaseManager.getDatabase().savePlayers();
        databaseManager.getDatabase().savePermissions();
        try {
            databaseManager.shutdown();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (Event.isRunningEvent()) Event.getRunningEvent(Event.EventType.CAPTURE).endEvent();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            if (sender instanceof ConsoleCommandSender) {
                commandMapper.mapCommand(sender, args[0], args);
                return true;
            }
            Utils.sendMessage(sender, Config.getMessages().notPlayer());
            return true;
        }
        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);
        if (args.length < 1) {
            if (modifiedPlayer.getClan() == null) {
                Config.getMessages().notInClan().forEach(string -> Utils.sendMessage(player, string));
                return true;
            }
            Config.getMessages().inClan().forEach(string -> Utils.sendMessage(player, string));
            return true;
        }
        CommandResult commandResult = commandMapper.mapCommand(player, args[0], args);
        if (!(commandResult.getResultStatus().equals(CommandResult.ResultStatus.SUCCESS))) {
            if (commandResult.getMessage() != null) {
                Utils.sendMessage(sender, commandResult.getMessage());
            }
            return true;
        }
        return true;
    }

    public ClanManager<ClanImpl> getClanManager() {
        return clanManager;
    }

    public StatsManager getStatsManager() {
        return statsManager;
    }

    public Economy getEconomy() {
        return economy;
    }

    public PrivilegeManager getPrivilegeManager() {
        return privilegeManager;
    }

    public static TowerClans getInstance() {
        return instance;
    }
}

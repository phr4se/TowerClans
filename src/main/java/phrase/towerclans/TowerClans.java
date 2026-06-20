package phrase.towerclans;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import phrase.towerclans.clan.ClanManager;
import phrase.towerclans.clan.attribute.clan.RankType;
import phrase.towerclans.clan.attribute.clan.ClanImplStorage;
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
import phrase.towerclans.command.impl.base.BaseManager;
import phrase.towerclans.config.Config;
import phrase.towerclans.database.Database;
import phrase.towerclans.database.DatabaseManager;
import phrase.towerclans.glow.ColorManager;
import phrase.towerclans.glow.GlowManager;
import phrase.towerclans.glow.GlowPacketListener;
import phrase.towerclans.menu.PaginatedMenu;
import phrase.towerclans.listener.ClanListener;
import phrase.towerclans.listener.PlayerListener;
import phrase.towerclans.util.Placeholder;
import phrase.towerclans.util.Utils;
import phrase.towerclans.util.colorizer.ColorizerFactory;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public final class TowerClans extends JavaPlugin implements CommandExecutor {
    private static TowerClans instance;
    private final StatsManager statsManager = new StatsManager();
    private final PrivilegeManager privilegeManager = new PrivilegeManager();
    private final ColorManager colorManager = new ColorManager();
    private CommandLogger commandLogger;
    private CommandMapper commandMapper;
    private BaseManager baseManager;
    private GlowManager glowManager;
    private Economy economy;
    private DatabaseManager databaseManager;
    private ClanManager<ClanImpl> clanManager;
    private Database database;

    @Override
    public void onEnable() {
        instance = this;
        PluginManager pluginManager = Bukkit.getPluginManager();
        if (!setupEconomy()) {
            pluginManager.disablePlugin(this);
            return;
        }
        saveDefaultConfig();
        Config.plugin = getInstance();
        Config.setLanguage(Config.getDefaultFile("choose-language.yml"));
        Config.createFiles("menus/menu-clan-main.yml", "menus/menu-clan-members.yml", "menus/menu-clan-level.yml", "menus/menu-clan-storage.yml", "menus/menu-clan-glow.yml", "messages.yml", "levels.yml", "event-capture.yml", "menu-pages.yml", "colors.yml");
        Config.setupSettings(getConfig());
        baseManager = new BaseManager();
        Utils.colorizer = ColorizerFactory.getProvider(Config.getSettings().colorizerType());
        Config.setupMessages(Config.getFile("messages.yml"));
        clanManager = new ClanManagerImpl(this);
        colorManager.initialize();
        ClanImplStorage.initialize();
        databaseManager = new DatabaseManager(Config.getSettings().databaseType(), this);
        CommandLogger commandLogger = new CommandLogger(this);
        commandMapper = new CommandMapper(commandLogger);
        glowManager = new GlowManager(this);
        this.database = databaseManager.getDatabase();
        database.loadAll();
        PaginatedMenu.initialize(this);
        RankType.initialize(this);
        ModifiedPlayer.plugin = getInstance();
        ModifiedPlayer.clanManager = clanManager;
        PacketEvents.getAPI().init();
        PacketEvents.getAPI().getEventManager().registerListener(new GlowPacketListener(this), PacketListenerPriority.NORMAL);
        PluginCommand pluginCommand = getCommand("clan");
        pluginCommand.setExecutor(this);
        pluginCommand.setTabCompleter(new ClanTabCompleter(this, commandLogger));
        if (pluginManager.isPluginEnabled("PlaceholderAPI")) new Placeholder(this).register();
        pluginManager.registerEvents(new PlayerListener(this), this);
        pluginManager.registerEvents(new ClanListener(this), this);
        privilegeManager.setPrivilege(Config.getSettings().type(), this);
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
        if(database != null) {
            database.saveAll();
            for (CompletableFuture<Void> cf : databaseManager.getTasks()) if (!cf.isDone()) cf.join();
            databaseManager.getTasks().clear();
            try {
                databaseManager.shutdown();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
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

    public BaseManager getBaseManager() {
        return baseManager;
    }

    public GlowManager getGlowManager() {
        return glowManager;
    }

    public ColorManager getColorManager() {
        return colorManager;
    }

    public Database getDatabase() {
        return database;
    }

    public CommandLogger getCommandLogger() {
        return commandLogger;
    }

    public static TowerClans getInstance() {
        return instance;
    }
}

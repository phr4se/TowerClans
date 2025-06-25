package phrase.towerClans;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import phrase.towerClans.clan.attribute.clan.Level;
import phrase.towerClans.clan.attribute.clan.Rank;
import phrase.towerClans.clan.entity.ModifiedPlayer;
import phrase.towerClans.clan.attribute.clan.Storage;
import phrase.towerClans.clan.event.TimeChecker;
import phrase.towerClans.command.CommandLogger;
import phrase.towerClans.command.CommandMapper;
import phrase.towerClans.command.CommandResult;
import phrase.towerClans.config.Config;
import phrase.towerClans.database.DatabaseMananger;
import phrase.towerClans.glow.GlowPacketListener;
import phrase.towerClans.gui.MenuPages;
import phrase.towerClans.listener.ClanListener;
import phrase.towerClans.listener.PlayerListener;
import phrase.towerClans.util.Placeholder;
import phrase.towerClans.util.UpdateChecker;
import phrase.towerClans.util.Utils;

import java.io.File;
import java.sql.SQLException;
import java.util.logging.Logger;


public final class Plugin extends JavaPlugin implements CommandExecutor {

    private CommandMapper commandMapper;
    private Economy economy;
    private String path;
    private DatabaseMananger databaseMananger;

    @Override
    public void onEnable() {

        Logger logger = getLogger();
        PluginManager pluginManager = Bukkit.getPluginManager();

        saveDefaultConfig();

        if(!UpdateChecker.check().equals(getDescription().getVersion())) logger.severe("Вы используете устаревшую версию плагина");

        if(!pluginManager.isPluginEnabled("WorldEdit") && !pluginManager.isPluginEnabled("WorldGuard")) {
            logger.severe("WorldEdit и WorldGuard не найден. Плагин будет выключен");
            pluginManager.disablePlugin(this);
            return;
        }

        Config.setupMessages(YamlConfiguration.loadConfiguration(getMessagesFile()));
        Config.setupSettings(getConfig());

        databaseMananger = new DatabaseMananger(Config.getSettings().databaseType(), this);

        initializeSchematicPath();
        commandMapper = new CommandMapper(this);

        Level.initialize(this);
        Storage.initialize(this);
        Rank.initialize(this);
        MenuPages.initialize(this);
        new CommandLogger(this);

        if (!setupEconomy()) {
            logger.severe("Vault не найден. Плагин будет выключен");
            pluginManager.disablePlugin(this);
            return;
        }

        databaseMananger.getDatabase().loadClans();
        databaseMananger.getDatabase().loadPlayers();

        getCommand("clan").setExecutor(this);

        if (pluginManager.isPluginEnabled("PlaceholderAPI")) new Placeholder().register();

        pluginManager.registerEvents(new PlayerListener(this), this);
        pluginManager.registerEvents(new ClanListener(this), this);

        if(!setupProtocolLib()) {
            logger.severe("ProtocolLib не найден. Плагин будет выключен");
            pluginManager.disablePlugin(this);
            return;
        }
        ProtocolLibrary.getProtocolManager().addPacketListener(new GlowPacketListener(this, PacketType.Play.Server.ENTITY_EQUIPMENT));

        TimeChecker timeChecker = new TimeChecker(this);
        timeChecker.start();
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> registeredServiceProvider = getServer().getServicesManager().getRegistration(Economy.class);

        if (registeredServiceProvider == null) {
            return false;
        }

        economy = registeredServiceProvider.getProvider();
        return true;
    }

    private boolean setupProtocolLib() {
        return getServer().getPluginManager().getPlugin("ProtocolLib") != null;
    }

    private void initializeSchematicPath() {
        File schematicFolder = new File(getDataFolder() + "/schematics");
        if(!schematicFolder.exists()) schematicFolder.mkdirs();
        path = schematicFolder.getPath() + "/" + getConfig().getString("settings.event.capture.schematic_name") + ".schem";
    }

    public File getMessagesFile() {
        File messagesFile = new File(getDataFolder(), "messages.yml");

        if (!messagesFile.exists()) {
            saveResource("messages.yml", false);
        }

        return messagesFile;
    }

    @Override
    public void onDisable() {
        databaseMananger.getDatabase().saveClans();
        databaseMananger.getDatabase().savePlayers();
        try {
            databaseMananger.shutdown();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Utils.sendMessage(sender, Config.getMessages().notPlayer());
            return true;
        }
        Player player = (Player) sender;
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

    public String getPath() {
        return path;
    }

    public Economy getEconomy() {
        return economy;
    }

    public DatabaseMananger getDatabaseMananger() {
        return databaseMananger;
    }
}

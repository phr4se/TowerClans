package phrase.towerClans;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import phrase.towerClans.clan.impls.ClanImpl;
import phrase.towerClans.commands.impls.ClanCommand;
import phrase.towerClans.config.ConfigManager;
import phrase.towerClans.listeners.EventListener;
import phrase.towerClans.placeholders.PluginPlaceholder;
import phrase.towerClans.utils.ChatUtil;


public final class Plugin extends JavaPlugin {

    private ConfigManager configManager;
    private static Plugin instance;
    public Economy economy;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        configManager = new ConfigManager();

        if(!setupEconomy()) {
            getLogger().severe("Vault не найден. Плагин будет выключен");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        configManager.loadData();

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) new PluginPlaceholder().register();

        new ClanCommand("clan");
        getServer().getPluginManager().registerEvents(new EventListener(), this);

    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> registeredServiceProvider = getServer().getServicesManager().getRegistration(Economy.class);

        if(registeredServiceProvider == null) {
            return false;
        }

        economy = registeredServiceProvider.getProvider();
        return true;
    }

    @Override
    public void onDisable() {
        configManager.saveData();
    }

    public static Plugin getInstance() {
        return instance;
    }
}

package phrase.towerClans;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import phrase.towerClans.clan.impls.ClanImpl;
import phrase.towerClans.commands.impls.ClanCommand;
import phrase.towerClans.placeholders.PluginPlaceholder;


public final class Plugin extends JavaPlugin {

    public static Plugin instance;
    public Economy economy;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        
        if(!setupEconomy()) {
            getLogger().severe("Vault не найден. Плагин будет выключен");
            getServer().getPluginManager().disablePlugin(this);
        }
        ClanImpl.loadData();

        new PluginPlaceholder().register();

        new ClanCommand("clan");
        getServer().getPluginManager().registerEvents(new ClanImpl(), this);

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
        ClanImpl.saveData();
    }
}

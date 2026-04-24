package phrase.towerclans.clan.impl.manager;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import phrase.towerclans.TowerClans;
import phrase.towerclans.clan.ClanManager;
import phrase.towerclans.clan.ClanResponse;
import phrase.towerclans.clan.attribute.clan.LevelManager;
import phrase.towerclans.clan.entity.ModifiedPlayer;
import phrase.towerclans.clan.impl.clan.ClanImpl;
import phrase.towerclans.clan.permission.PermissionManager;
import phrase.towerclans.config.Config;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ClanManagerImpl implements ClanManager<ClanImpl> {
    private final TowerClans plugin;
    private final Map<String, ClanImpl> clans;
    private final PermissionManager permissionManager = new PermissionManager();
    private final LevelManager levelManager;
    private final int min;
    private final int max;
    private final int amount;
    private final Economy economy;

    public ClanManagerImpl(TowerClans plugin) {
        this.plugin = plugin;
        this.clans = new HashMap<>();
        this.levelManager = new LevelManager();
        this.min = Config.getSettings().minSizeClanName();
        this.max = Config.getSettings().maxSizeClanName();
        this.amount = Config.getSettings().costCreatingClan();
        this.economy = plugin.getEconomy();
    }

    @Override
    public ClanResponse createClan(ModifiedPlayer modifiedPlayer, String name) {
        if (modifiedPlayer.getClan() != null) return new ClanResponse(Config.getCommandMessages().inClan(), ClanResponse.ResponseType.FAILURE);
        if (!(name.length() < max && name.length() > min)) return new ClanResponse(Config.getMessages().clanNameLimit().replace("%min%", String.valueOf(min)).replace("%max%", String.valueOf(max)), ClanResponse.ResponseType.FAILURE);
        if (!name.matches(Config.getSettings().regex()) || clanName(name, Config.getSettings().badWords())) return new ClanResponse(Config.getMessages().clanNameBadWord(), ClanResponse.ResponseType.FAILURE);
        if (existsClan(name)) return new ClanResponse(Config.getCommandMessages().clanNameExists(), ClanResponse.ResponseType.FAILURE);
        Player player = modifiedPlayer.getPlayer();
        double balance = economy.getBalance(player);
        if (balance < amount) return new ClanResponse(Config.getCommandMessages().notEnough().replace("%amount%", String.valueOf(amount - (int) balance)), ClanResponse.ResponseType.FAILURE);
        economy.withdrawPlayer(player, amount);
        ClanImpl target = new ClanImpl(name, modifiedPlayer, plugin);
        plugin.getDatabase().saveClan(target);
        return new ClanResponse(Config.getCommandMessages().creatingClan(), ClanResponse.ResponseType.SUCCESS);
    }

    @Override
    public void addClan(String name, ClanImpl clan) {
        this.clans.put(name, clan);
    }

    @Override
    public void removeClan(String name) {
        this.clans.remove(name);
    }

    @Override
    public ClanImpl getClan(String name) {
        return this.clans.get(name);
    }

    @Override
    public PermissionManager getPermissionManager() {
        return permissionManager;
    }

    @Override
    public LevelManager getLevelManager() {
        return levelManager;
    }

    @Override
    public Map<String, ClanImpl> getClans() {
        return Collections.unmodifiableMap(clans);
    }

    @Override
    public ClanImpl[] values() {
        return clans.values().toArray(new ClanImpl[0]);
    }

    @Override
    public boolean existsClan(String name) {
        return clans.containsKey(name);
    }

    @Override
    public Set<String> keySet() {
        return clans.keySet();
    }

    @Override
    public Set<Map.Entry<String, ClanImpl>> entrySet() {
        return clans.entrySet();
    }

    @Override
    public boolean clanName(String clanName, List<String> badWords) {
        if (badWords == null || badWords.isEmpty()) return false;
        return Pattern.compile(badWords.stream()
                .map(Pattern::quote)
                .map(word -> "\\b" + word + "\\b")
                .collect(Collectors.joining("|"))).matcher(clanName).matches();
    }
}

package phrase.towerclans.clan.entity;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import phrase.towerclans.TowerClans;
import phrase.towerclans.clan.Clan;
import phrase.towerclans.clan.impl.clan.ClanImpl;
import phrase.towerclans.clan.permission.PermissionType;
import phrase.towerclans.gui.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class ModifiedPlayer {
    public static TowerClans plugin;
    private final UUID playerUUID;
    private Clan clan;

    public ModifiedPlayer(UUID playerUUID, Clan clan) {
        this.playerUUID = playerUUID;
        this.clan = clan;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(playerUUID);
    }

    public static ModifiedPlayer get(Player player) {
        if (player == null) return null;
        for (Map.Entry<String, ClanImpl> clan : plugin.getClanManager().getClans().entrySet()) {
            for (Map.Entry<ModifiedPlayer, String> entry : clan.getValue().getMembers().entrySet()) {
                if (!entry.getKey().getPlayerUUID().equals(player.getUniqueId())) continue;
                return new ModifiedPlayer(player.getUniqueId(), clan.getValue());
            }
        }
        return new ModifiedPlayer(player.getUniqueId(), null);
    }

    public static ModifiedPlayer get(OfflinePlayer offlinePlayer) {
        if (offlinePlayer == null) return null;
        for (Map.Entry<String, ClanImpl> clan : plugin.getClanManager().getClans().entrySet()) {
            for (Map.Entry<ModifiedPlayer, String> entry : clan.getValue().getMembers().entrySet()) {
                if (!entry.getKey().getPlayerUUID().equals(offlinePlayer.getUniqueId())) continue;
                return new ModifiedPlayer(offlinePlayer.getUniqueId(), clan.getValue());
            }
        }
        return new ModifiedPlayer(offlinePlayer.getUniqueId(), null);
    }

    public Clan getClan() {
        return clan;
    }

    public void setClan(Clan clan) {
        this.clan = clan;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public boolean hasPermission(PermissionType permissionType) {
        return plugin.getClanManager().getPermissionManager().getPermissionsPlayer(this).getPermissionTypes().contains(permissionType);
    }

    public void showMenu(MenuType menuType) {
        MenuProvider menuProvider = MenuFactory.getProvider(menuType);
        if (menuProvider == null) {
            this.getPlayer().closeInventory();
            return;
        }
        Player player = this.getPlayer();
        if (menuProvider.menuPages()) {
            Inventory menu = menuProvider.getMenu(this, ((ClanImpl) this.getClan()), plugin);
            List<ItemStack> players = ((Pages) menuProvider).getContents(this, ((ClanImpl) this.getClan()), plugin);
            MenuPages menuPages = ((Pages) menuProvider).register(this.getPlayerUUID(), new MenuPages(0, players, menu));
            player.openInventory(menuPages.getPage(menuPages.getCurrentPage()));
        } else
            player.openInventory(menuProvider.getMenu(this, ((ClanImpl) this.getClan()), plugin));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModifiedPlayer that = (ModifiedPlayer) o;
        return Objects.equals(playerUUID, that.playerUUID) && Objects.equals(clan, that.clan);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerUUID, clan);
    }
}

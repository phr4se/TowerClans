package phrase.towerclans.database.impl;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import phrase.towerclans.TowerClans;
import phrase.towerclans.clan.AbstractClan;
import phrase.towerclans.clan.ClanManager;
import phrase.towerclans.clan.attribute.clan.RankType;
import phrase.towerclans.clan.attribute.player.StatsManager;
import phrase.towerclans.clan.entity.ModifiedPlayer;
import phrase.towerclans.clan.impl.clan.ClanImpl;
import phrase.towerclans.clan.permission.Permission;
import phrase.towerclans.clan.permission.PermissionManager;
import phrase.towerclans.clan.permission.PermissionType;
import phrase.towerclans.command.impl.base.BaseManager;
import phrase.towerclans.config.Config;
import phrase.towerclans.database.Database;
import phrase.towerclans.database.DatabaseManager;
import phrase.towerclans.glow.ColorManager;
import phrase.towerclans.serializable.*;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class MySQL implements Database {
    private final TowerClans plugin;
    private final DatabaseManager databaseManager;
    private final BaseManager baseManager;
    private final StatsManager statsManager;
    private final ClanManager<ClanImpl> clanManager;
    private final PermissionManager playerPermissionManager;
    private final ColorManager colorManager;

    public MySQL(TowerClans plugin, DatabaseManager databaseManager) {
        this.plugin = plugin;
        this.databaseManager = databaseManager;
        this.baseManager = plugin.getBaseManager();
        this.statsManager = plugin.getStatsManager();
        this.clanManager = plugin.getClanManager();
        this.playerPermissionManager = clanManager.getPermissionManager();
        this.colorManager = plugin.getColorManager();
    }

    @Override
    public void createTable() {
        final int maxSizeClanName = Config.getSettings().maxSizeClanName();
        try (Connection connection = databaseManager.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS towerclans_clan (clan VARCHAR(" + maxSizeClanName + ") PRIMARY KEY, lvl INTEGER, xp INTEGER, bal INTEGER, pvp INTEGER, members TEXT, home TEXT, storage TEXT, colorKey VARCHAR(8))");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS towerclans_player (player VARCHAR(36) PRIMARY KEY, stats TEXT, permission TEXT, clan VARCHAR(" + maxSizeClanName + "), rank VARCHAR(36), FOREIGN KEY (clan) REFERENCES towerclans_clan (clan) ON DELETE SET NULL ON UPDATE CASCADE)");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveClan(ClanImpl clan) {
        CompletableFuture<Void> cf = CompletableFuture.runAsync(() -> {
            try (Connection connection = databaseManager.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO towerclans_clan (clan, lvl, xp, bal, pvp, home, storage, colorKey) VALUES (?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE lvl = VALUES(lvl), xp = VALUES(xp), bal = VALUES(bal), pvp = VALUES(pvp), home = VALUES(home), storage = VALUES(storage), colorKey = VALUES(colorKey)")) {
                preparedStatement.setString(1, clan.getName());
                preparedStatement.setInt(2, clan.getLevel());
                preparedStatement.setInt(3, clan.getXp());
                preparedStatement.setInt(4, clan.getBalance());
                preparedStatement.setBoolean(5, clan.isPvp());
                preparedStatement.setString(6, (baseManager.getBase(clan) == null) ? LocationSerializable.EMPTY : LocationSerializable.locationToString(baseManager.getBase(clan)));
                preparedStatement.setString(7, InventorySerializable.inventoryToBase64(clan.getClanImplStorage().getInventory()));
                preparedStatement.setString(8, clan.getColor().getKey());
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        databaseManager.getTasks().add(cf);
    }

    @Override
    public void removeClan(String clan) {
        CompletableFuture<Void> cf = CompletableFuture.runAsync(() -> {
            try (Connection connection = databaseManager.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM towerclans_clan WHERE clan = ?")) {
                preparedStatement.setString(1, clan);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        databaseManager.getTasks().add(cf);
    }

    @Override
    public void saveClans() {
        for (ClanImpl clan : clanManager.values()) saveClan(clan);
    }

    @Override
    public void loadClans() {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM towerclans_clan")) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                final String clan = resultSet.getString(1);
                final ClanImpl target = new ClanImpl(clan, plugin);
                final int lvl = resultSet.getInt(2);
                target.setLevel(lvl);
                final int xp = resultSet.getInt(3);
                target.setXp(xp);
                final int bal = resultSet.getInt(4);
                target.setBalance(bal);
                final boolean pvp = resultSet.getBoolean(5);
                target.setPvp(pvp);
                final Location home = LocationSerializable.stringToLocation(resultSet.getString(6));
                baseManager.setBase(target, null, home);
                final Inventory inventory = InventorySerializable.base64ToInventory(resultSet.getString(7));
                target.getClanImplStorage().getInventory().setContents(inventory.getContents());
                final String colorKey = resultSet.getString(8);
                target.setColor(colorManager.getColor(colorKey));
                clanManager.addClan(clan, target);
            }
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void savePlayer(ModifiedPlayer modifiedPlayer) {
        final UUID playerUUID = modifiedPlayer.getPlayerUUID();
        CompletableFuture<Void> cf = CompletableFuture.runAsync(() -> {
            try (Connection connection = databaseManager.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO towerclans_player (player, stats, permission, clan, rank) VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE stats = VALUES(stats), permission = VALUES(permission), rank = VALUES(rank)")) {
                preparedStatement.setString(1, playerUUID.toString());
                preparedStatement.setString(2, StatsSerializable.statsToString(statsManager.getPlayers().get(playerUUID)));
                preparedStatement.setString(3, PermissionSerializable.permissionToString(playerPermissionManager.getPermissionsPlayer(modifiedPlayer)));
                ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
                if (clan != null) {
                    preparedStatement.setString(4, clan.getName());
                    preparedStatement.setString(5, clan.getMembers().get(modifiedPlayer));
                } else {
                    preparedStatement.setNull(4, Types.VARCHAR);
                    preparedStatement.setString(5, RankType.UNDEFINED.getName());
                }
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        databaseManager.getTasks().add(cf);
    }

    @Override
    public void savePlayers() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);
            savePlayer(modifiedPlayer);
        }
    }

    @Override
    public void loadPlayers() {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM towerclans_player")) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                UUID playerUUID = UUID.fromString(resultSet.getString(1));
                StatsManager.Stats stats = StatsSerializable.stringToStats(resultSet.getString(2));
                statsManager.getPlayers().put(playerUUID, stats);
                final List<PermissionType> permissionTypes = PermissionSerializable.stringToPermission(resultSet.getString(3));
                playerPermissionManager.getPlayers().put((Bukkit.getPlayer(playerUUID) == null) ? Bukkit.getOfflinePlayer(playerUUID).getUniqueId() : playerUUID, new Permission(permissionTypes, 0));
                String clan = resultSet.getString(4);
                ModifiedPlayer modifiedPlayer = new ModifiedPlayer(playerUUID, null);
                AbstractClan abstractClan = clanManager.getClan(clan);
                if (abstractClan == null) continue;
                modifiedPlayer.setClan(clanManager.getClan(clan));
                String rank = resultSet.getString(5);
                abstractClan.getMembers().put(modifiedPlayer, rank);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveAll() {
        saveClans();
        savePlayers();
    }

    @Override
    public void loadAll() {
        loadClans();
        loadPlayers();
    }
}

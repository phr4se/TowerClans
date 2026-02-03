package phrase.towerclans.database.impl;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.Inventory;
import phrase.towerclans.Plugin;
import phrase.towerclans.clan.ClanDataConverter;
import phrase.towerclans.clan.attribute.player.StatsManager;
import phrase.towerclans.clan.entity.ModifiedPlayer;
import phrase.towerclans.clan.impl.clan.ClanImpl;
import phrase.towerclans.clan.permission.Permission;
import phrase.towerclans.clan.permission.PermissionManager;
import phrase.towerclans.clan.permission.PermissionType;
import phrase.towerclans.command.impl.base.Base;
import phrase.towerclans.config.Config;
import phrase.towerclans.database.Database;
import phrase.towerclans.database.DatabaseManager;
import phrase.towerclans.glow.Glow;
import phrase.towerclans.serializable.InventorySerializable;
import phrase.towerclans.serializable.ListStringSerializable;
import phrase.towerclans.serializable.PermissionSerializable;
import phrase.towerclans.serializable.StatsSerializable;

import java.io.IOException;
import java.sql.*;
import java.util.*;

public class SQLite implements Database {
    private final DatabaseManager databaseMananger;
    private final Plugin plugin;

    public SQLite(DatabaseManager databaseMananger, Plugin plugin) {
        this.databaseMananger = databaseMananger;
        this.plugin = plugin;
    }

    @Override
    public void initTable() {
        int max = Config.getSettings().maxSizeClanName();
        String sqlClans = "CREATE TABLE IF NOT EXISTS clans (name VARCHAR(" + max + ") PRIMARY KEY, level INTEGER, xp INTEGER, balance INTEGER, x DOUBLE, y DOUBLE, z DOUBLE, world VARCHAR(255), storage TEXT, members TEXT, pvp INTEGER, id INTEGER);";
        String sqlPlayers = "CREATE TABLE IF NOT EXISTS players (name VARCHAR(36) PRIMARY KEY, stats TEXT);";
        String sqlPermissions = "CREATE TABLE IF NOT EXISTS permissions (name VARCHAR(36) PRIMARY KEY, permission TEXT)";
        try (Connection connection = databaseMananger.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(sqlClans);
            statement.executeUpdate(sqlPlayers);
            statement.executeUpdate(sqlPermissions);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveClans() {
        String check = "SELECT name FROM clans";
        String save = "INSERT INTO clans (name, level, xp, balance, x, y, z, world, storage, members, pvp, id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String update = "UPDATE clans SET level = ?, xp = ?, balance = ?, x = ?, y = ?, z = ?, world = ?, storage = ?, members = ?, pvp = ?, id = ? WHERE name = ?";
        String delete = "DELETE FROM clans WHERE name = ?";
        try (Connection connection = databaseMananger.getConnection();
             Statement statementCheck = connection.createStatement();
             PreparedStatement preparedStatementSave = connection.prepareStatement(save);
             PreparedStatement preparedStatementUpdate = connection.prepareStatement(update);
             PreparedStatement preparedStatementDelete = connection.prepareStatement(delete)
        ) {
            List<String> existingClanNames = new ArrayList<>();
            ResultSet resultSet = statementCheck.executeQuery(check);
            while (resultSet.next()) if (!plugin.getClanManager().getClans().containsKey(resultSet.getString(1))) {
                preparedStatementDelete.setString(1, resultSet.getString(1));
                preparedStatementDelete.addBatch();
            } else existingClanNames.add(resultSet.getString(1));
            for (ClanImpl clan : plugin.getClanManager().getClans().values()) {
                boolean exists = existingClanNames.contains(clan.getName());
                if (!exists) {
                    preparedStatementSave.setString(1, clan.getName());
                    preparedStatementSave.setInt(2, clan.getLevel());
                    preparedStatementSave.setInt(3, clan.getXp());
                    preparedStatementSave.setInt(4, clan.getBalance());
                    Location location = Base.getBase(clan);
                    if (location == null) {
                        preparedStatementSave.setNull(5, Types.DOUBLE);
                        preparedStatementSave.setNull(6, Types.DOUBLE);
                        preparedStatementSave.setNull(7, Types.DOUBLE);
                        preparedStatementSave.setNull(8, Types.VARCHAR);
                    } else {
                        preparedStatementSave.setDouble(5, location.getX());
                        preparedStatementSave.setDouble(6, location.getY());
                        preparedStatementSave.setDouble(7, location.getZ());
                        preparedStatementSave.setString(8, location.getWorld().getName());
                    }
                    preparedStatementSave.setString(9, InventorySerializable.inventoryToBase64(clan.getStorageManager().getInventory()));
                    preparedStatementSave.setString(10, ListStringSerializable.listToString(ClanDataConverter.mapToList(clan.getMembers())));
                    preparedStatementSave.setBoolean(11, clan.isPvp());
                    preparedStatementSave.setInt(12, clan.getColor().getId());
                    preparedStatementSave.addBatch();
                } else {
                    preparedStatementUpdate.setInt(1, clan.getLevel());
                    preparedStatementUpdate.setInt(2, clan.getXp());
                    preparedStatementUpdate.setInt(3, clan.getBalance());
                    Location location = Base.getBase(clan);
                    if (location == null) {
                        preparedStatementUpdate.setNull(4, Types.DOUBLE);
                        preparedStatementUpdate.setNull(5, Types.DOUBLE);
                        preparedStatementUpdate.setNull(6, Types.DOUBLE);
                        preparedStatementUpdate.setNull(7, Types.VARCHAR);
                    } else {
                        preparedStatementUpdate.setDouble(4, location.getX());
                        preparedStatementUpdate.setDouble(5, location.getY());
                        preparedStatementUpdate.setDouble(6, location.getZ());
                        preparedStatementUpdate.setString(7, location.getWorld().getName());
                    }
                    preparedStatementUpdate.setString(8, InventorySerializable.inventoryToBase64(clan.getStorageManager().getInventory()));
                    preparedStatementUpdate.setString(9, ListStringSerializable.listToString(ClanDataConverter.mapToList(clan.getMembers())));
                    preparedStatementUpdate.setBoolean(10, clan.isPvp());
                    preparedStatementUpdate.setInt(11, clan.getColor().getId());
                    preparedStatementUpdate.setString(12, clan.getName());
                    preparedStatementUpdate.addBatch();
                }
            }
            preparedStatementSave.executeBatch();
            preparedStatementUpdate.executeBatch();
            preparedStatementDelete.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void loadClans() {
        String sql = "SELECT * FROM clans";
        try (Connection connection = databaseMananger.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String name = resultSet.getString(1);
                ClanImpl clan = new ClanImpl(name, plugin);
                int level = resultSet.getInt(2);
                clan.setLevel(level);
                int xp = resultSet.getInt(3);
                clan.setXp(xp);
                int balance = resultSet.getInt(4);
                clan.setBalance(balance);
                double x = resultSet.getDouble(5);
                boolean xIsNull = resultSet.wasNull();
                double y = resultSet.getDouble(6);
                boolean yIsNull = resultSet.wasNull();
                double z = resultSet.getDouble(7);
                boolean zIsNull = resultSet.wasNull();
                String worldName = resultSet.getString(8);
                boolean worldIsNull = resultSet.wasNull();
                if (xIsNull && yIsNull && zIsNull && worldIsNull) {
                    Base.setBase(clan, null);
                } else {
                    World world = Bukkit.getWorld(worldName);
                    Location location = new Location(world, x, y, z);
                    Base.setBase(clan, location);
                }
                boolean pvp = resultSet.getBoolean(11);
                clan.setPvp(pvp);
                int id = resultSet.getInt(12);
                clan.setColor(Glow.LeatherColor.getLeaherColor(id));
                Inventory storage = InventorySerializable.base64ToInventory(resultSet.getString(9));
                clan.getStorageManager().getInventory().setContents(storage.getContents());
                Map<ModifiedPlayer, String> members = ClanDataConverter.listToMap(ListStringSerializable.stringToList(resultSet.getString(10)), clan);
                clan.setMembers(members);
            }
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void savePlayers() {
        String check = "SELECT name FROM players";
        String save = "INSERT INTO players (name, stats) VALUES (?, ?)";
        String update = "UPDATE players SET stats = ? WHERE name = ?";
        try (Connection connection = databaseMananger.getConnection();
             PreparedStatement preparedStatementSave = connection.prepareStatement(save);
             Statement statementCheck = connection.createStatement();
             PreparedStatement preparedStatementUpdate = connection.prepareStatement(update)) {
            ResultSet resultSet = statementCheck.executeQuery(check);
            List<String> existingPlayerNames = new ArrayList<>();
            while (resultSet.next()) existingPlayerNames.add(resultSet.getString(1));
            for (Map.Entry<UUID, StatsManager.Stats> entry : plugin.getStatsManager().getPlayers().entrySet()) {
                String uuid = entry.getKey().toString();
                boolean exists = existingPlayerNames.contains(uuid);
                if (!exists) {
                    preparedStatementSave.setString(1, uuid);
                    preparedStatementSave.setString(2, StatsSerializable.statsToString(entry.getValue()));
                    preparedStatementSave.addBatch();
                } else {
                    preparedStatementUpdate.setString(1, StatsSerializable.statsToString(entry.getValue()));
                    preparedStatementUpdate.setString(2, uuid);
                    preparedStatementUpdate.addBatch();
                }
            }
            preparedStatementSave.executeBatch();
            preparedStatementUpdate.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void loadPlayers() {
        String sql = "SELECT * FROM players";
        try (Connection connection = databaseMananger.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                UUID uuid = UUID.fromString(resultSet.getString(1));
                StatsManager.Stats stats = StatsSerializable.stringToStats(resultSet.getString(2));
                plugin.getStatsManager().getPlayers().put(uuid, stats);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void savePermissions() {
        String check = "SELECT * FROM permissions";
        String save = "INSERT INTO permissions (name, permission) VALUES(?,?)";
        String update = "UPDATE permissions SET permission = ? WHERE name = ?";
        try (Connection connection = databaseMananger.getConnection();
             PreparedStatement preparedStatementSave = connection.prepareStatement(save);
             Statement statementCheck = connection.createStatement();
             PreparedStatement preparedStatementUpdate = connection.prepareStatement(update)) {
            ResultSet resultSet = statementCheck.executeQuery(check);
            List<String> existingPlayerNames = new ArrayList<>();
            while (resultSet.next()) existingPlayerNames.add(resultSet.getString(1));
            for (Map.Entry<ModifiedPlayer, Permission> entry : plugin.getClanManager().getPermissionManager().getPlayers().entrySet()) {
                String uuid = entry.getKey().getPlayerUUID().toString();
                boolean exists = existingPlayerNames.contains(uuid);
                if (!exists) {
                    preparedStatementSave.setString(1, uuid);
                    preparedStatementSave.setString(2, PermissionSerializable.permissionToString(entry.getValue()));
                    preparedStatementSave.addBatch();
                } else {
                    preparedStatementUpdate.setString(1, PermissionSerializable.permissionToString(entry.getValue()));
                    preparedStatementUpdate.setString(2, uuid);
                    preparedStatementUpdate.addBatch();
                }
            }
            preparedStatementSave.executeBatch();
            preparedStatementUpdate.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void loadPermissions() {
        String sql = "SELECT * FROM permissions";
        try (Connection connection = databaseMananger.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                UUID uuid = UUID.fromString(resultSet.getString(1));
                List<PermissionType> permissionTypes = PermissionSerializable.stringToPermission(resultSet.getString(2));
                plugin.getClanManager().getPermissionManager().getPlayers().put(ModifiedPlayer.get((Bukkit.getPlayer(uuid) == null) ? Bukkit.getOfflinePlayer(uuid) : Bukkit.getPlayer(uuid)), new Permission(permissionTypes, 0));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

package phrase.towerClans.database.impl;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.Inventory;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.ClanDataConverter;
import phrase.towerClans.clan.attribute.player.Stats;
import phrase.towerClans.clan.entity.ModifiedPlayer;
import phrase.towerClans.clan.impl.ClanImpl;
import phrase.towerClans.command.impl.base.Base;
import phrase.towerClans.config.Config;
import phrase.towerClans.database.Database;
import phrase.towerClans.database.DatabaseMananger;
import phrase.towerClans.glow.Glow;
import phrase.towerClans.serializable.InventorySerializable;
import phrase.towerClans.serializable.ListStringSerializable;
import phrase.towerClans.serializable.StatsSerializable;

import java.io.IOException;
import java.sql.*;
import java.util.*;

public class SQLite implements Database {

    private final DatabaseMananger databaseMananger;
    private final Plugin plugin;

    public SQLite(DatabaseMananger databaseMananger, Plugin plugin) {
        this.databaseMananger = databaseMananger;
        this.plugin = plugin;
    }

    @Override
    public void initTable() {
        int max = Config.getSettings().maxSizeClanName();
        String sqlClans = "CREATE TABLE IF NOT EXISTS clans (name VARCHAR(" + max + ") PRIMARY KEY, level INTEGER, xp INTEGER, balance INTEGER, x DOUBLE, y DOUBLE, z DOUBLE, world VARCHAR(255), storage TEXT, members TEXT, pvp INTEGER, r INTEGER DEFAULT 255, g INTEGER DEFAULT 0, b INTEGER DEFAULT 0);";
        String sqlPlayers = "CREATE TABLE IF NOT EXISTS players (name VARCHAR(16) PRIMARY KEY, stats TEXT);";
        try(Connection connection = databaseMananger.getConnection();
            Statement statement = connection.createStatement()) {
            statement.executeUpdate(sqlClans);
            statement.executeUpdate(sqlPlayers);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveClans() {
        String check = "SELECT * FROM clans";
        String save = "INSERT INTO clans (name, level, xp, balance, x, y, z, world, storage, members, pvp, r, g, b) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String update = "UPDATE clans SET level = ?, xp = ?, balance = ?, x = ?, y = ?, z = ?, world = ?, storage = ?, members = ?, pvp = ?, r = ?, g = ?, b = ? WHERE name = ?";
        String delete = "DELETE FROM clans WHERE name = ?";
        try (Connection connection = databaseMananger.getConnection();
             Statement statementCheck = connection.createStatement();
             PreparedStatement preparedStatementSave = connection.prepareStatement(save);
             PreparedStatement preparedStatementUpdate = connection.prepareStatement(update);
             PreparedStatement preparedStatementDelete = connection.prepareStatement(delete)
        ) {
            List<String> existingClanNames = new ArrayList<>();
            ResultSet resultSet = statementCheck.executeQuery(check);
            while (resultSet.next()) if (!ClanImpl.getClans().containsKey(resultSet.getString(1))) {
                preparedStatementDelete.setString(1, resultSet.getString(1));
                preparedStatementDelete.addBatch();
            } else existingClanNames.add(resultSet.getString(1));

            for (ClanImpl clan : ClanImpl.getClans().values()) {
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
                    preparedStatementSave.setString(9, InventorySerializable.inventoryToBase64(clan.getStorage().getInventory()));
                    preparedStatementSave.setString(10, ListStringSerializable.listToString(ClanDataConverter.mapToList(clan.getMembers())));
                    preparedStatementSave.setBoolean(11, clan.isPvp());
                    preparedStatementSave.setInt(12, clan.getColor().getR());
                    preparedStatementSave.setInt(12, clan.getColor().getG());
                    preparedStatementSave.setInt(12, clan.getColor().getB());
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
                    preparedStatementUpdate.setString(8, InventorySerializable.inventoryToBase64(clan.getStorage().getInventory()));
                    preparedStatementUpdate.setString(9, ListStringSerializable.listToString(ClanDataConverter.mapToList(clan.getMembers())));
                    preparedStatementUpdate.setBoolean(10, clan.isPvp());
                    preparedStatementUpdate.setInt(11, clan.getColor().getR());
                    preparedStatementUpdate.setInt(12, clan.getColor().getG());
                    preparedStatementSave.setInt(13, clan.getColor().getB());
                    preparedStatementUpdate.setString(14, clan.getName());
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
                int r = resultSet.getInt(12);
                int g = resultSet.getInt(13);
                int b = resultSet.getInt(14);
                clan.setColor(Glow.LeatherColor.getLeaherColor(r, g, b));
                Inventory storage = InventorySerializable.base64ToInventory(resultSet.getString(9));
                clan.getStorage().getInventory().setContents(storage.getContents());
                Map<ModifiedPlayer, String> members = ClanDataConverter.listToMap(ListStringSerializable.stringToList(resultSet.getString(10)), clan);
                clan.setMembers(members);
                ClanImpl.getClans().put(name, clan);
            }
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public void savePlayers() {

        String check = "SELECT * FROM players";
        String save = "INSERT INTO players (name, stats) VALUES (?, ?)";
        String update = "UPDATE players SET stats = ? WHERE name = ?";
        try (Connection connection = databaseMananger.getConnection();
             PreparedStatement preparedStatementSave = connection.prepareStatement(save);
             Statement statementCheck = connection.createStatement();
             PreparedStatement preparedStatementUpdate = connection.prepareStatement(update)) {
            ResultSet resultSet = statementCheck.executeQuery(check);
            List<String> existingPlayerNames = new ArrayList<>();
            while (resultSet.next()) existingPlayerNames.add(resultSet.getString(1));
            for (Map.Entry<UUID, Stats> entry : Stats.PLAYERS.entrySet()) {
                String name = (Bukkit.getPlayer(entry.getKey()) == null) ? Bukkit.getOfflinePlayer(entry.getKey()).getName() : Bukkit.getPlayer(entry.getKey()).getName();
                boolean exists = existingPlayerNames.contains(name);
                if (!exists) {
                    preparedStatementSave.setString(1, name);
                    preparedStatementSave.setString(2, StatsSerializable.statsToString(entry.getValue()));
                    preparedStatementSave.addBatch();
                } else {
                    preparedStatementUpdate.setString(1, StatsSerializable.statsToString(entry.getValue()));
                    preparedStatementUpdate.setString(2, name);
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
                UUID uuid = Bukkit.getOfflinePlayer(resultSet.getString(1)).getUniqueId();
                Stats stats = StatsSerializable.stringToStats(resultSet.getString(2));
                Stats.PLAYERS.put(uuid, stats);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }
}

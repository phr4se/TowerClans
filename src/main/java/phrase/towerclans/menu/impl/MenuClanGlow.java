package phrase.towerclans.menu.impl;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import phrase.towerclans.TowerClans;
import phrase.towerclans.action.ActionExecutor;
import phrase.towerclans.action.ActionTransformer;
import phrase.towerclans.clan.entity.ModifiedPlayer;
import phrase.towerclans.clan.impl.clan.ClanImpl;
import phrase.towerclans.clan.permission.PermissionType;
import phrase.towerclans.config.Config;
import phrase.towerclans.glow.Color;
import phrase.towerclans.glow.ColorManager;
import phrase.towerclans.glow.GlowManager;
import phrase.towerclans.menu.Handler;
import phrase.towerclans.menu.Menu;
import phrase.towerclans.util.Utils;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class MenuClanGlow extends Menu implements Handler {
    public MenuClanGlow(String fileName, TowerClans plugin, ModifiedPlayer modifiedPlayer) {
        super(fileName, plugin, modifiedPlayer);
        setupDefaultItems();
    }

    @Override
    public void handleClick(ClickType clickType, Player player, PersistentDataContainer persistentDataContainer, Class<? extends Cancellable> clazz, Object object, Object... args) {
        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);
        ColorManager colorManager = plugin.getColorManager();
        GlowManager glowManager = plugin.getGlowManager();
        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
        switch (clickType) {
            case RIGHT -> {
                if (!persistentDataContainer.has(NamespacedKey.fromString("right_click_actions"), PersistentDataType.STRING))
                    return;
                try {
                    clazz.getMethod("setCancelled", boolean.class).invoke(object, true);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
                String rightClickActions = persistentDataContainer.get(NamespacedKey.fromString("right_click_actions"), PersistentDataType.STRING);
                Color color = colorManager.getColor(rightClickActions);
                if (color == null) {
                    ActionExecutor.execute(player, ActionTransformer.transform(List.of(rightClickActions.split("\\|"))));
                    return;
                }
                if (!modifiedPlayer.hasPermission(PermissionType.GLOW)) {
                    Utils.sendMessage(modifiedPlayer.getPlayer(), Config.getCommandMessages().noPermission());
                    return;
                }
                clan.setColor(color);
                for (ModifiedPlayer o : clan.getMembers().keySet()) {
                    Player target = o.getPlayer();
                    if (glowManager.isEnableForPlayer(target)) {
                        glowManager.removePlayer(target);
                        glowManager.addPlayer(target);
                    }
                }
            }
            case LEFT -> {
                try {
                    clazz.getMethod("setCancelled", boolean.class).invoke(object, true);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
                String leftClickActions = persistentDataContainer.get(NamespacedKey.fromString("left_click_actions"), PersistentDataType.STRING);
                Color color = colorManager.getColor(leftClickActions);
                if (color == null) {
                    ActionExecutor.execute(player, ActionTransformer.transform(List.of(leftClickActions.split("\\|"))));
                    return;
                }
                if (!modifiedPlayer.hasPermission(PermissionType.GLOW)) {
                    Utils.sendMessage(modifiedPlayer.getPlayer(), Config.getCommandMessages().noPermission());
                    return;
                }
                clan.setColor(color);
                for (ModifiedPlayer o : clan.getMembers().keySet()) {
                    Player target = o.getPlayer();
                    if (glowManager.isEnableForPlayer(target)) {
                        glowManager.removePlayer(target);
                        glowManager.addPlayer(target);
                    }
                }
            }
        }
    }
}
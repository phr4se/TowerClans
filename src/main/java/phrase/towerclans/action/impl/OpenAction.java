package phrase.towerclans.action.impl;

import org.bukkit.entity.Player;
import phrase.towerclans.action.Action;
import phrase.towerclans.action.context.impl.StringContext;
import phrase.towerclans.clan.entity.ModifiedPlayer;
import phrase.towerclans.clan.impl.clan.ClanImpl;
import phrase.towerclans.gui.*;

public class OpenAction implements Action<StringContext> {
    @Override
    public void execute(Player player, StringContext context) {
        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);
        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
        MenuType menuType = MenuType.valueOf(context.message());
        switch (menuType) {
            case MENU_CLAN_PREVIOUS -> {
                MenuProvider menuProvider = MenuFactory.getProvider(menuType);
                MenuPages menuPages = ((Pages) menuProvider).getMenuPages(modifiedPlayer.getPlayerUUID());
                if (!menuPages.hasPreviousPage()) return;
                menuPages.setCurrentPage(menuPages.getCurrentPage() - 1);
                modifiedPlayer.getPlayer().openInventory(menuPages.getPage(menuPages.getCurrentPage()));
            }
            case MENU_CLAN_NEXT -> {
                MenuProvider menuProvider = MenuFactory.getProvider(menuType);
                MenuPages menuPages = ((Pages) menuProvider).getMenuPages(modifiedPlayer.getPlayerUUID());
                if (!menuPages.hasNextPage()) return;
                menuPages.setCurrentPage(menuPages.getCurrentPage() + 1);
                modifiedPlayer.getPlayer().openInventory(menuPages.getPage(menuPages.getCurrentPage()));
            }
            default -> clan.showMenu(modifiedPlayer, MenuType.valueOf(context.message()));
        }
    }
}

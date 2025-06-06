package phrase.towerClans.clan;

import phrase.towerClans.clan.entity.ModifiedPlayer;

public interface Clan {

    ClanResponse invite(ModifiedPlayer modifiedPlayer);
    ClanResponse kick(ModifiedPlayer modifiedPlayer);
    ClanResponse invest(ModifiedPlayer modifiedPlayer, int amount);
    ClanResponse withdraw(ModifiedPlayer modifiedPlayer, int amount);
    ClanResponse leave(ModifiedPlayer modifiedPlayer);
    ClanResponse rank(ModifiedPlayer modifiedPlayer, int id);
    ClanResponse disband(ModifiedPlayer modifiedPlayer);

}

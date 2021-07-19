package com.jumbo.trus.match;

import com.jumbo.trus.IFragment;
import com.jumbo.trus.player.Player;
import com.jumbo.trus.season.Season;

import java.util.List;

public interface IMatchFragment extends IFragment {

    boolean createNewMatch(String opponent, String date, boolean homeMatch, Season season, List<Player> playerList);
    boolean editMatch(String opponent, String date, boolean homeMatch, Season season, List<Player> playerList, Match match);
}

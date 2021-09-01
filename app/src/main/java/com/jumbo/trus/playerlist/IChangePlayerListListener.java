package com.jumbo.trus.playerlist;

import com.jumbo.trus.match.Match;
import com.jumbo.trus.player.Player;

import java.util.List;

public interface IChangePlayerListListener {

    boolean editMatch(List<Player> playerList, Match match);
}

package com.jumbo.trus.playerlist;

import com.jumbo.trus.fine.Fine;
import com.jumbo.trus.fine.ReceivedFine;
import com.jumbo.trus.match.Match;
import com.jumbo.trus.player.Player;

import java.util.List;

public interface IChangeFineListListener {

    boolean editPlayer(List<ReceivedFine> fineList, Player player, Match match);
    boolean editMatchFines(List<Player> playerList, List<Fine> fineList, List<Integer> finesPlus, Match match);
}

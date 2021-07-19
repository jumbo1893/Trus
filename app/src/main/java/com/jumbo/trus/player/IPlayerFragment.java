package com.jumbo.trus.player;

import com.jumbo.trus.IFragment;

public interface IPlayerFragment extends IFragment {

    boolean createNewPlayer(String jmeno, String birthday, boolean fan);
    boolean editPlayer(String jmeno, String birthday, boolean fan, Player player);
}

package com.jumbo.trus.season;

import com.jumbo.trus.IFragment;

public interface ISeasonFragment extends IFragment {

    boolean createNewSeason(String name, String seasonStart, String seasonEnd);
    boolean editSeason(String name, String seasonStart, String seasonEnd, Season season);
}

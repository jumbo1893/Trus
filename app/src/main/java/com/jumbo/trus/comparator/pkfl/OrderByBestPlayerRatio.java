package com.jumbo.trus.comparator.pkfl;

import com.jumbo.trus.pkfl.stats.PkflPlayerStats;

import java.util.Comparator;

public class OrderByBestPlayerRatio implements Comparator<PkflPlayerStats> {

    private boolean desc;

    public OrderByBestPlayerRatio(boolean desc) {
        this.desc = desc;
    }

    @Override
    public int compare(PkflPlayerStats o1, PkflPlayerStats o2) {
        if (desc) {
            return Float.compare(o2.getBestPlayerMatchesRatio(), o1.getBestPlayerMatchesRatio());
        }
        else {
            return Float.compare(o1.getBestPlayerMatchesRatio(), o2.getBestPlayerMatchesRatio());
        }
    }
}

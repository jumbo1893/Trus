package com.jumbo.trus.home;


import com.jumbo.trus.Date;
import com.jumbo.trus.fine.Fine;
import com.jumbo.trus.match.Match;
import com.jumbo.trus.player.Player;
import com.jumbo.trus.season.Season;
import java.util.ArrayList;
import java.util.List;

class RandomFact {

    private static final String TAG = "RandomFact";

    private List<Player> players;
    private List<Match> matches;
    private List<Season> seasons;
    private List<Fine> fines;
    private Match compareMatch;

    RandomFact(List<Player> players, List<Match> matches, List<Season> seasons, List<Fine> fines) {
        this.players = players;
        this.matches = matches;
        this.seasons = seasons;
        this.fines = fines;
        initCompareMatch();
    }

    private void initCompareMatch() {
        Date date = new Date();
        compareMatch = new Match("compare", date.getCurrentDateInMillis(), false, null, seasons);
    }

    /**
     * @return Vrátí oslavný text s hráčem si seznamem hráčů, kteří slaví narozeniny. Pokud nikdo narozeniny neslaví, tak vrátí hráče nebo seznam hráčů kteří mají nejdříve narozeniny
     */
    String getPlayerWithEarliestBirthDay() {
        List<Player> returnPlayers = new ArrayList<>();
        int minimumDays = 367;
        for (Player player : players) {
            if (player.calculateDaysToBirthday() < minimumDays) {
                returnPlayers.clear();
                returnPlayers.add(player);
                minimumDays = player.calculateDaysToBirthday();
            }
            else if (player.calculateDaysToBirthday() == minimumDays) {
                returnPlayers.add(player);
            }
        }
        if (minimumDays == 367) {
            return ("Nelze dny do narozenin hráčů, hrajou vůbec nějaký za Trus?");
        }
        else if (minimumDays > 0) {
            if (returnPlayers.size() == 1) {
                if (returnPlayers.get(0).isFan()) {
                    return ("Příští rundu platí věrný fanoušek " + returnPlayers.get(0).getName() + ", který bude mít za " + minimumDays +
                            " dní své " + (returnPlayers.get(0).getVek()+1) + ". narozeniny");
                }
                else {
                    return ("Příští rundu platí " + returnPlayers.get(0).getName() + ", který bude mít za " + minimumDays +
                            " dní své " + (returnPlayers.get(0).getVek()+1) + ". narozeniny");
                }
            }
            else {
                String text = "Příští rundu platí ";
                for (int i = 0; i < returnPlayers.size(); i++) {
                    if (i == returnPlayers.size()-1) {
                        text += returnPlayers.get(i).getName() + " ";
                    }
                    else if (i == returnPlayers.size()-2) {
                        text += returnPlayers.get(i).getName() + " a ";
                    }
                    else {
                        text += returnPlayers.get(i).getName() + ", ";
                    }

                }
                text += "kteří mají za " + minimumDays + " dní své ";
                for (int i = 0; i < returnPlayers.size(); i++) {
                    text += (returnPlayers.get(i).getVek()+1) + ".";
                    if (i == returnPlayers.size()-1) {
                        text += " narozeniny";
                    }
                    else if (i == returnPlayers.size()-2) {
                        text += " a ";
                    }
                    else {
                        text += ", ";
                    }

                }

                return text;
            }
        }
        else {
            if (returnPlayers.size() == 1) {
                if (returnPlayers.get(0).isFan()) {
                    return ("Dnes slaví narozeniny fanoušek " + returnPlayers.get(0).getName() + ", který má " + returnPlayers.get(0).getVek() +
                            " let. Už ten sud vyval a ať ti slouží splávek!");
                }
                else {
                    return ("Dnes slaví narozeniny " + returnPlayers.get(0).getName() + ", který má " + returnPlayers.get(0).getVek() +
                            " let. Už ten sud vyval a ať ti slouží splávek! Na Trus!!");
                }
            }
            else {
                String text = "Dnešní oslavenci jsou ";
                for (int i = 0; i < returnPlayers.size(); i++) {
                    if (i == returnPlayers.size() - 1) {
                        text += returnPlayers.get(i).getName() + " ";
                    } else if (i == returnPlayers.size() - 2) {
                        text += returnPlayers.get(i).getName() + " a ";
                    } else {
                        text += returnPlayers.get(i).getName() + ", ";
                    }

                }
                text += "kteří mají slaví své nádherné ";
                for (int i = 0; i < returnPlayers.size(); i++) {
                    text += returnPlayers.get(i).getVek() + ".";
                    if (i == returnPlayers.size() - 1) {
                        text += " narozeniny. Pánové, všichni doufáme že se pochlapíte. Na Trus!!!!";
                    } else if (i == returnPlayers.size() - 2) {
                        text += " a ";
                    } else {
                        text += ", ";
                    }
                }
                return text;
            }
        }
    }

    /**
     * @return Vrátí text s hráčem nebo seznam hráčů, kteří vypili za celou historii (všechny zápasy v db) nejvíce piv
     */
    String getPlayerWithMostBeers() {
        List<Player> returnPlayers = new ArrayList<>();
        int maximumBeers = 0;
        for (Player player : players) {
            player.calculateAllBeersNumber(matches);
            if (player.getNumberOfBeersInMatches() > maximumBeers) {
                returnPlayers.clear();
                returnPlayers.add(player);
                maximumBeers = player.getNumberOfBeersInMatches();
            }
            else if (player.getNumberOfBeersInMatches() == maximumBeers) {
                returnPlayers.add(player);
            }
        }
        if (maximumBeers == 0) {
            return "Nelze najít největšího pijana, protože si ještě nikdo nedal pivo???!!";
        }
        else if (returnPlayers.size() == 1) {
            return "Nejvíce velkých piv za historii si dal " + returnPlayers.get(0).getName() + " který vypil " + maximumBeers + " piv";

        }
        else {
            String result = "O pozici největšího pijana v historii se dělí ";
            for (int i = 0; i < returnPlayers.size(); i++) {
                result += returnPlayers.get(i).getName();
                if (i == returnPlayers.size()-1) {
                    result += " kteří všichni do jednoho vypili " + maximumBeers + " piv. Boj, boj, boj!";
                }
                else if (i == returnPlayers.size()-2) {
                    result += " a ";
                }
                else {
                    result += ", ";
                }
            }
            return result;
        }
    }

    /**
     * @return vrátí text se zápasem nebo seznamem zápasů, ve kterém se za historii (z db) vypilo nejvíce piv
     */
    String getMatchWithMostBeers() {
        List<Match> returnMatches = new ArrayList<>();
        int maximumBeers = 0;
        for (Match match : matches) {
            if (match.getNumberOfBeersInMatch() > maximumBeers) {
                returnMatches.clear();
                returnMatches.add(match);
                maximumBeers = match.getNumberOfBeersInMatch();
            }
            else if (match.getNumberOfBeersInMatch() == maximumBeers) {
                returnMatches.add(match);
            }
        }
        if (maximumBeers == 0) {
            return "Nelze najít zápas s nejvíce pivy, protože si zatím nikdo žádný nedal!";
        }
        else if (returnMatches.size() == 1) {
            return "Nejvíce piv za historii padlo v zápase se soupeřem " + returnMatches.get(0).getOpponent() + ", který se hrál " +
                    returnMatches.get(0).getDateOfMatchInStringFormat() + " a padlo v něm " + maximumBeers + " piv";

        }
        else {
            String result = "O pozici zápasu ve kterém padlo nejvíce piv se dělí zápas se soupeřem ";
            for (int i = 0; i < returnMatches.size(); i++) {
                result += returnMatches.get(i).getOpponent() + ", který se hrál " + returnMatches.get(i).getDateOfMatchInStringFormat();
                if (i == returnMatches.size()-1) {
                    result += " ve kterých padlo " + maximumBeers + " piv.";
                }
                else if (i == returnMatches.size()-2) {
                    result += " a ";
                }
                else {
                    result += ", ";
                }
            }
            return result;
        }
    }

    /**
     * @return vrátí počet piv v aktuální sezoně dle data
     */
    String getNumberOfBeersInCurrentSeason() {
        int beerNumber = 0;
        for (Match match : matches) {
            if (match.getSeason().equals(compareMatch.getSeason())) {
                beerNumber += match.getNumberOfBeersInMatch();
            }
        }
        return "V aktuální sezoně " + compareMatch.getSeason().getName() + " se vypilo " + beerNumber + " piv";
    }

    /**
     * @return vrátí text se zápasem nebo listem zápasů ve kterých se tuto sezonu vypilo nejvíce piv
     */
    public String getMatchWithMostBeersInCurrentSeason() {
        List<Match> returnMatches = new ArrayList<>();
        int maximumBeers = 0;
        for (Match match : matches) {
            if (match.getSeason().equals(compareMatch.getSeason())) {
                if (match.getNumberOfBeersInMatch() > maximumBeers) {
                    returnMatches.clear();
                    returnMatches.add(match);
                    maximumBeers = match.getNumberOfBeersInMatch();
                }
                else if (match.getNumberOfBeersInMatch() == maximumBeers) {
                    returnMatches.add(match);
                }
            }
        }
        if (maximumBeers == 0) {
            return "Nelze najít zápas s nejvíce pivy odehraný v této sezoně " + compareMatch.getSeason().getName() + ", protože si zatím nikdo žádný nedal!";
        }
        else if (returnMatches.size() == 1) {
            return "Nejvíce piv v aktuální sezoně " + compareMatch.getSeason().getName() + " padlo v zápase se soupeřem " +
                    returnMatches.get(0).getOpponent() + ", který se hrál " + returnMatches.get(0).getDateOfMatchInStringFormat() + " a padlo v něm " + maximumBeers + " piv";

        }
        else {
            String result = "O pozici zápasu ve kterém padlo nejvíce piv v aktuální sezoně " + compareMatch.getSeason().getName() + " se dělí zápas se soupeřem ";
            for (int i = 0; i < returnMatches.size(); i++) {
                result += returnMatches.get(i).getOpponent() + ", který se hrál " + returnMatches.get(i).getDateOfMatchInStringFormat();
                if (i == returnMatches.size()-1) {
                    result += " ve kterých padlo " + maximumBeers + " piv.";
                }
                else if (i == returnMatches.size()-2) {
                    result += " a ";
                }
                else {
                    result += ", ";
                }
            }
            result += "ve kterých padlo " + maximumBeers + " piv.";
            return result;
        }
    }

    /**
     * @return vrátí text sezonu/sezony ve kterých padlo v historii nejvíce piv, společně s počtem a počtem zápasů
     */
    String getSeasonWithMostBeers() {
        List<Season> returnSeasons = new ArrayList<>();
        List<Integer> numberOfMatches = new ArrayList<>();
        int maximumBeers = 0;
        for (Season season : seasons) {
            int seasonBeers = 0;
            int matchesNumber = 0;
            for (Match match : matches) {
                if (match.getSeason().equals(season)) {
                    matchesNumber++;
                    seasonBeers += match.getNumberOfBeersInMatch();
                }
            }
            if (seasonBeers > maximumBeers) {
                maximumBeers = seasonBeers;
                numberOfMatches.clear();
                numberOfMatches.add(matchesNumber);
                returnSeasons.clear();
                returnSeasons.add(season);
            }
            else if (seasonBeers == maximumBeers) {
                numberOfMatches.add(matchesNumber);
                returnSeasons.add(season);
            }
        }
        if (maximumBeers == 0) {
            return "Nelze najít sezonu s nejvíce pivy, protože si zatím nikdo pivo nedal!!!";
        }
        else if (returnSeasons.size() == 1) {
            return "Nejvíce piv se vypilo v sezoně " + returnSeasons.get(0).getName() + ",kdy se v " + numberOfMatches.get(0) + ". zápasech vypilo " + maximumBeers + " piv";

        }
        else {
            String result = "Nejvíce pivy se můžou chlubit  sezony ";
            for (int i = 0; i < returnSeasons.size(); i++) {
                result += returnSeasons.get(i).getName() + " s " + numberOfMatches.get(i) + ". zápasy";
                if (i == returnSeasons.size()-1) {
                    result += ", ve kterých padlo " + maximumBeers + " piv.";
                }
                else if (i == returnSeasons.size()-2) {
                    result += " a ";
                }
                else {
                    result += ", ";
                }

            }

            return result;
        }

    }

    /**
     * @return vrátí průměrný počet piv na všechny účastníky včetně fans
     */
    String getAverageNumberOfBeersInMatchForPlayersAndFans() {
        float beerNumber = 0;
        int playerNumber = 0;
        for (Match match : matches) {
            beerNumber += match.getNumberOfBeersInMatch();
            playerNumber += match.getNumberOfPlayersAndFansInMatch();
        }
        float average = beerNumber/playerNumber;

        return "Za celou historii průměrně každý hráč a fanoušek Trusu vypil " + average + " piv za zápas";
    }
    /**
     * @return vrátí průměrný počet piv na hráče
     */
    String getAverageNumberOfBeersInMatchForPlayers() {
        float beerNumber = 0;
        int playerNumber = 0;
        for (Match match : matches) {
            beerNumber += match.getNumberOfBeersInMatchForPlayers();
            playerNumber += match.getNumberOfPlayersInMatch();
        }
        float average = beerNumber/playerNumber;

        return "Za celou historii průměrně každý hráč Trusu vypil " + average + " piv za zápas";
    }
    /**
     * @return vrátí průměrný počet piv na fanouška
     */
    String getAverageNumberOfBeersInMatchForFans() {
        float beerNumber = 0;
        int fanNumber = 0;
        for (Match match : matches) {
            beerNumber += (match.getNumberOfBeersInMatch() - match.getNumberOfBeersInMatchForPlayers());
            fanNumber += (match.getNumberOfPlayersAndFansInMatch() - match.getNumberOfPlayersInMatch());
        }
        float average = beerNumber/fanNumber;

        return "Za celou historii průměrně každý fanoušek Trusu vypil " + average + " piv za zápas";
    }

    /**
     * @return vrátí průměrný počet piv na zápas
     */
    public String getAverageNumberOfBeersInMatch() {
        float beerNumber = 0;
        for (Match match : matches) {
            beerNumber += match.getNumberOfBeersInMatch();
        }
        float average = beerNumber/matches.size();
        return "V naprosto průměrném zápasu Trusu se vypije " + average + " piv";
    }

    /**
     * @return Vrací dosud nejvyšší průměrný počet vypitých piv v jednom zápase
     */
    String getMatchWithHighestAverageBeers() {
        List<Match> returnMatches = new ArrayList<>();
        float average = 0;
        for (Match match : matches) {
            if (match.getNumberOfBeersInMatch() / (float) match.getNumberOfPlayersAndFansInMatch() > average) {
                average = match.getNumberOfBeersInMatch() / (float) match.getNumberOfPlayersAndFansInMatch();
                returnMatches.clear();
                returnMatches.add(match);
            } else if (match.getNumberOfBeersInMatch() / (float) match.getNumberOfPlayersAndFansInMatch() == average) {
                returnMatches.add(match);
            }
        }
        if (average == 0) {
            return "Nelze najít zápas s nejvíce průměrnými pivy, protože si zatím nikdo pivo nedal!!!";
        } else if (returnMatches.size() == 1) {
            return "Nejvyšší průměr počtu vypitých piv v zápase proběhl na zápase se soupeřem " + returnMatches.get(0).getOpponent() +
                    " hraném v sezoně " + returnMatches.get(0).getSeason().getName() + " konkrétně " + returnMatches.get(0).getDateOfMatchInStringFormat() +
                    ". Vypilo se " + returnMatches.get(0).getNumberOfBeersInMatch() + " piv v " + returnMatches.get(0).getNumberOfPlayersAndFansInMatch() +
                    " lidech, což dělá průměr " + average + " na hráče. Tak ještě jedno, ať to překonáme!";

        } else {
            String result = "Nejvyšší průměr počtu vypitých piv v zápase proběhl na v zápasech se ";
            for (int i = 0; i < returnMatches.size(); i++) {
                result += "soupeřem " + returnMatches.get(i).getOpponent() + " hraném " + returnMatches.get(i).getDateOfMatchInStringFormat() +
                        " s celkovým počtem " + returnMatches.get(i).getNumberOfBeersInMatch() + ". piv v " + returnMatches.get(i).getNumberOfPlayersAndFansInMatch() +
                        " lidech";
                if (i == returnMatches.size() - 1) {
                    result += ".\n Celkový průměr pro tyto zápasy je " + average + " piv.";
                } else if (i == returnMatches.size() - 2) {
                    result += " a ";
                } else {
                    result += "; ";
                }
            }
            return result;
        }
    }

    /**
     * @return Vrací dosud nejnižší průměrný počet vypitých piv v jednom zápase
     */
    String getMatchWithLowestAverageBeers() {
        List<Match> returnMatches = new ArrayList<>();
        float average = 1000;
        for (Match match : matches) {
            if ((match.getNumberOfBeersInMatch() / (float) match.getNumberOfPlayersAndFansInMatch() < average) && match.getNumberOfBeersInMatch() != 0) {
                average = match.getNumberOfBeersInMatch() / (float) match.getNumberOfPlayersAndFansInMatch();
                returnMatches.clear();
                returnMatches.add(match);
            } else if ((match.getNumberOfBeersInMatch() / (float) match.getNumberOfPlayersAndFansInMatch()) == average && match.getNumberOfBeersInMatch() != 0) {
                returnMatches.add(match);
            }
        }
        if (average == 1000) {
            return "Nelze najít zápas s nejméně průměrnými pivy, protože si zatím nikdo pivo nedal!!!";
        } else if (returnMatches.size() == 1) {
            return "Ostudný den Liščího Trusu, kdy byl pokořen rekord v nejnižším průměru počtu vypitých piv v zápase proběhl na zápase se soupeřem " + returnMatches.get(0).getOpponent() +
                    " hraném v sezoně " + returnMatches.get(0).getSeason().getName() + " konkrétně " + returnMatches.get(0).getDateOfMatchInStringFormat() +
                    ". Vypilo se " + returnMatches.get(0).getNumberOfBeersInMatch() + " piv v " + returnMatches.get(0).getNumberOfPlayersAndFansInMatch() +
                    " lidech, což dělá průměr " + average + " na hráče. Vzpomeňte si na to, až si budete objednávat další rundu!";

        } else {
            String result = "Je až neuvěřitelné, že se taková potupa opakovala vícekrát, ovšem existuje více zápasů v zápasech s ostudně nejnižším průměrem " +
                    "vypitých piv v zápase. Stalo se tak se ";
            for (int i = 0; i < returnMatches.size(); i++) {
                result += "soupeřem " + returnMatches.get(i).getOpponent() + " hraném " + returnMatches.get(i).getDateOfMatchInStringFormat() +
                        " s celkovým počtem " + returnMatches.get(i).getNumberOfBeersInMatch() + ". piv v " + returnMatches.get(i).getNumberOfPlayersAndFansInMatch() +
                        " lidech";
                if (i == returnMatches.size() - 1) {
                    result += ".\n Celkový průměr pro tyto zápasy je " + average + " piv.";
                } else if (i == returnMatches.size() - 2) {
                    result += " a ";
                } else {
                    result += "; ";
                }
            }
            return result;
        }
    }

    /**
     * @return Vrací dosud nejvyšší účast jednom zápase spolu s podrobnými údaji
     */
    String getHighestAttendanceInMatch() {
        List<Match> returnMatches = new ArrayList<>();
        int matchAttendance = 0;
        for (Match match : matches) {
            if (match.getNumberOfPlayersAndFansInMatch() > matchAttendance) {
                returnMatches.clear();
                returnMatches.add(match);
                matchAttendance = match.getNumberOfPlayersAndFansInMatch();
            }
            else if (match.getNumberOfPlayersAndFansInMatch() == matchAttendance) {
                returnMatches.add(match);
            }
        }
        if (matchAttendance == 0) {
            return "Nelze najít zápas s nejvyšší účastí, protože se zatím žádný nehrál";
        }
        else if (returnMatches.size() == 1) {
            return "Největší účast na zápase Liščího Trusu proběhla " + returnMatches.get(0).getDateOfMatchInStringFormat() + " se soupeřem "
                    + returnMatches.get(0).getOpponent() + " kdy celkový počet účastníků byl " +
                    (matchAttendance-returnMatches.get(0).getNumberOfPlayersInMatch()) + " fanoušků a "  + returnMatches.get(0).getNumberOfPlayersInMatch() +
                    " hráčů, celkem tedy " + matchAttendance + " lidí. Celkově se v tomto zápase vypilo " +
                    returnMatches.get(0).getNumberOfBeersInMatch() + " piv";
        }
        else {
            String result = "Největší účast na zápase Liščího Trusu proběhla se soupeři ";
            for (int i = 0; i < returnMatches.size(); i++) {
                result += returnMatches.get(i).getOpponent() + ", hraný " + returnMatches.get(0).getDateOfMatchInStringFormat() +
                        ", kdy celkový počet účastníků byl " +
                        (matchAttendance-returnMatches.get(i).getNumberOfPlayersInMatch()) + " fanoušků a "  + returnMatches.get(i).getNumberOfPlayersInMatch() +
                        " hráčů s počtem " + returnMatches.get(i).getNumberOfBeersInMatch() + ". vypitých piv";
                if (i == returnMatches.size()-1) {
                    result += ".";
                }
                else if (i == returnMatches.size()-2) {
                    result += " a ";
                }
                else {
                    result += "; ";
                }
            }
            return result;
        }
    }

    /**
     * @return Vrací dosud nejnižší účast jednom zápase spolu s podrobnými údaji
     */
    String getLowestAttendanceInMatch() {
        List<Match> returnMatches = new ArrayList<>();
        int matchAttendance = 1000;
        for (Match match : matches) {
            if ((match.getNumberOfPlayersAndFansInMatch() < matchAttendance) && match.getNumberOfPlayersAndFansInMatch() != 0) {
                returnMatches.clear();
                returnMatches.add(match);
                matchAttendance = match.getNumberOfPlayersAndFansInMatch();
            }
            else if ((match.getNumberOfPlayersAndFansInMatch() == matchAttendance) && match.getNumberOfPlayersAndFansInMatch() != 0) {
                returnMatches.add(match);
            }
        }
        if (matchAttendance == 1000) {
            return "Nelze najít zápas s nejnižší účastí, protože se nejspíš zatím žádný nehrál";
        }
        else if (returnMatches.size() == 1) {
            return "Nejnižší účast na zápase Liščího Trusu proběhla " + returnMatches.get(0).getDateOfMatchInStringFormat() + " se soupeřem "
                    + returnMatches.get(0).getOpponent() + " kdy celkový počet účastníků byl " +
                    (matchAttendance-returnMatches.get(0).getNumberOfPlayersInMatch()) + " fanoušků a "  + returnMatches.get(0).getNumberOfPlayersInMatch() +
                    " hráčů, celkem tedy " + matchAttendance + " lidí. Celkově se v tomto zápase vypilo " +
                    returnMatches.get(0).getNumberOfBeersInMatch() + " piv";
        }
        else {
            String result = "Nejnižší účast na zápase Liščího Trusu proběhla se soupeři ";
            for (int i = 0; i < returnMatches.size(); i++) {
                result += returnMatches.get(i).getOpponent() + ", hraný " + returnMatches.get(0).getDateOfMatchInStringFormat() +
                        ", kdy celkový počet účastníků byl " +
                        (matchAttendance-returnMatches.get(i).getNumberOfPlayersInMatch()) + " fanoušků a "  + returnMatches.get(i).getNumberOfPlayersInMatch() +
                        " hráčů s počtem " + returnMatches.get(i).getNumberOfBeersInMatch() + ". vypitých piv";
                if (i == returnMatches.size()-1) {
                    result += ".";
                }
                else if (i == returnMatches.size()-2) {
                    result += " a ";
                }
                else {
                    result += "; ";
                }
            }
            return result;
        }
    }

    /**
     * @return Vrátí text s hráčem nebo seznam hráčů, kteří dostali za celou historii (všechny zápasy v db) nejvíce pokut
     */
    String getPlayerWithMostFines() {
        List<Player> returnPlayers = new ArrayList<>();
        int maximumFines = 0;
        for (Player player : players) {
            player.calculateAllFinesNumber(matches);
            if (player.getNumberOfFinesInMatches() > maximumFines) {
                returnPlayers.clear();
                returnPlayers.add(player);
                maximumFines = player.getNumberOfFinesInMatches();
            }
            else if (player.getNumberOfFinesInMatches() == maximumFines) {
                returnPlayers.add(player);
            }
        }
        if (maximumFines == 0) {
            return "Nelze nalézt hráče s nejvíce pokutami, protože ještě nikdo žádnou nedostal??!!";
        }
        else if (returnPlayers.size() == 1) {
            return "Nejvíce pokut za historii dostal hráč " + returnPlayers.get(0).getName() + ", který dostal " + maximumFines + " pokut. Za pokladníka děkujeme!";

        }
        else {
            String result = "O pozici hráče s největším počtem pokut v historii se dělí ";
            for (int i = 0; i < returnPlayers.size(); i++) {
                result += returnPlayers.get(i).getName();
                if (i == returnPlayers.size()-1) {
                    result += " kteří všichni do jednoho dostali " + maximumFines + " pokut. Nádherný souboj, prosíme, ještě přidejte!";
                }
                else if (i == returnPlayers.size()-2) {
                    result += " a ";
                }
                else {
                    result += ", ";
                }
            }
            return result;
        }
    }

    /**
     * @return vrátí text se zápasem nebo seznamem zápasů, ve kterém se za historii (z db) padlo nejvíce pokut
     */
    String getMatchWithMostFines() {
        List<Match> returnMatches = new ArrayList<>();
        int maximumFines = 0;
        for (Match match : matches) {
            if (match.getNumberOfFinesInMatch() > maximumFines) {
                returnMatches.clear();
                returnMatches.add(match);
                maximumFines = match.getNumberOfFinesInMatch();
            }
            else if (match.getNumberOfFinesInMatch() == maximumFines) {
                returnMatches.add(match);
            }
        }
        if (maximumFines == 0) {
            return "Nelze nalézt zápas s nejvíce pokutami, protože ještě nikdo žádnou nedostal??!!";
        }
        else if (returnMatches.size() == 1) {
            return "Nejvíce pokut v historii padlo v zápase se soupeřem " + returnMatches.get(0).getOpponent() + ", který se hrál " +
                    returnMatches.get(0).getDateOfMatchInStringFormat() + " a udělilo se v něm " + maximumFines + " pokut";

        }
        else {
            String result = "O pozici zápasu ve kterém padlo nejvíce pokut se dělí zápas se soupeřem ";
            for (int i = 0; i < returnMatches.size(); i++) {
                result += returnMatches.get(i).getOpponent() + ", který se hrál " + returnMatches.get(i).getDateOfMatchInStringFormat();
                if (i == returnMatches.size()-1) {
                    result += " ve kterých padlo " + maximumFines + " pokut.";
                }
                else if (i == returnMatches.size()-2) {
                    result += " a ";
                }
                else {
                    result += ", ";
                }
            }
            return result;
        }
    }

    /**
     * @return Vrátí text s hráčem nebo seznam hráčů, kteří zaplatili za celou historii (všechny zápasy v db) nejvíce na pokutách
     */
    String getPlayerWithMostFinesAmount() {
        List<Player> returnPlayers = new ArrayList<>();
        int maximumFinesAmount = 0;
        for (Player player : players) {
            player.calculateAllFinesNumber(matches);
            if (player.getAmountOfFinesInMatches() > maximumFinesAmount) {
                returnPlayers.clear();
                returnPlayers.add(player);
                maximumFinesAmount = player.getAmountOfFinesInMatches();
            }
            else if (player.getAmountOfFinesInMatches() == maximumFinesAmount) {
                returnPlayers.add(player);
            }
        }
        if (maximumFinesAmount == 0) {
            return "Nelze nalézt hráče, co nejvíc zaplatil na pokutách, protože ještě nikdo žádnou nedostal??!!";
        }
        else if (returnPlayers.size() == 1) {
            return "Nejvíce pokuty stály hráče " + returnPlayers.get(0).getName() + ", který celkem zacáloval " + maximumFinesAmount +
                    " Kč. Nezapomeň, je to nedílná součást tréningového procesu!";

        }
        else {
            String result = "O pozici hráče co nejvíc zacálovali na pokutách se dělí ";
            for (int i = 0; i < returnPlayers.size(); i++) {
                result += returnPlayers.get(i).getName();
                if (i == returnPlayers.size()-1) {
                    result += " kteří všichni do jednoho zaplatili " + maximumFinesAmount + " Kč. Díky kluci, zvyšuje to autoritu trenéra!";
                }
                else if (i == returnPlayers.size()-2) {
                    result += " a ";
                }
                else {
                    result += ", ";
                }
            }
            return result;
        }
    }

    /**
     * @return vrátí text se zápasem nebo seznamem zápasů, ve kterém se za historii (z db) nejvíce vydělalo na pokutách
     */
    String getMatchWithMostFinesAmount() {
        List<Match> returnMatches = new ArrayList<>();
        int maximumFinesAmount = 0;
        for (Match match : matches) {
            if (match.getAmountOfFinesInMatch() > maximumFinesAmount) {
                returnMatches.clear();
                returnMatches.add(match);
                maximumFinesAmount = match.getAmountOfFinesInMatch();
            }
            else if (match.getAmountOfFinesInMatch() == maximumFinesAmount) {
                returnMatches.add(match);
            }
        }
        if (maximumFinesAmount == 0) {
            return "Nelze nalézt zápas, kde se nejvíc cálovalo za pokuty, protože ještě nikdo žádnou nedostal??!!";
        }
        else if (returnMatches.size() == 1) {
            return "Největší objem peněz v pokutách přinesl zápas se soupeřem " + returnMatches.get(0).getOpponent() + ", který se hrál " +
                    returnMatches.get(0).getDateOfMatchInStringFormat() + " a vybralo se v něm " + maximumFinesAmount + " Kč.";

        }
        else {
            String result = "O pozici zápasu ve kterém se klubová pokladna nejvíce nafoukla se dělí zápas se soupeřem ";
            for (int i = 0; i < returnMatches.size(); i++) {
                result += returnMatches.get(i).getOpponent() + ", který se hrál " + returnMatches.get(i).getDateOfMatchInStringFormat();
                if (i == returnMatches.size()-1) {
                    result += " ve kterých se vybralo " + maximumFinesAmount + " Kč.";
                }
                else if (i == returnMatches.size()-2) {
                    result += " a ";
                }
                else {
                    result += ", ";
                }
            }
            return result;
        }
    }

    /**
     * @return vrátí počet pokut v aktuální sezoně dle data
     */
    String getNumberOfFinesInCurrentSeason() {
        int fineNumber = 0;
        for (Match match : matches) {
            if (match.getSeason().equals(compareMatch.getSeason())) {
                fineNumber += match.getNumberOfFinesInMatch();
            }
        }
        return "V aktuální sezoně " + compareMatch.getSeason().getName() + " se rozdalo již " + fineNumber + " pokut";
    }

    /**
     * @return vrátí vybranou částku na pokutách v aktuální sezoně dle data
     */
    public String getAmountOfFinesInCurrentSeason() {
        int fineNumber = 0;
        for (Match match : matches) {
            if (match.getSeason().equals(compareMatch.getSeason())) {
                fineNumber += match.getAmountOfFinesInMatch();
            }
        }
        return "V aktuální sezoně " + compareMatch.getSeason().getName() + " se na pokutách vybralo již " + fineNumber + " Kč.";
    }

    /**
     * @return vrátí text se zápasem nebo listem zápasů ve kterých se tuto sezonu rozdalo nejvíce pokut
     */
    String getMatchWithMostFinesInCurrentSeason() {
        List<Match> returnMatches = new ArrayList<>();
        int maximumFines = 0;
        for (Match match : matches) {
            if (match.getSeason().equals(compareMatch.getSeason())) {
                if (match.getNumberOfFinesInMatch() > maximumFines) {
                    returnMatches.clear();
                    returnMatches.add(match);
                    maximumFines = match.getNumberOfFinesInMatch();
                }
                else if (match.getNumberOfFinesInMatch() == maximumFines) {
                    returnMatches.add(match);
                }
            }
        }
        if (maximumFines == 0) {
            return "Nelze najít zápas s nejvíce pokutami odehraný v této sezoně " + compareMatch.getSeason().getName() + ", protože se zatím žádná pokuta neudělila!";
        }
        else if (returnMatches.size() == 1) {
            return "Nejvíce pokut v aktuální sezoně " + compareMatch.getSeason().getName() + " se rozdalo v zápase se soupeřem " +
                    returnMatches.get(0).getOpponent() + ", který se hrál " + returnMatches.get(0).getDateOfMatchInStringFormat() + " a padlo v něm " + maximumFines + " pokut";

        }
        else {
            String result = "O pozici zápasu ve kterém se rozdalo nejvíce pokut v aktuální sezoně " + compareMatch.getSeason().getName() + " se dělí zápas se soupeřem ";
            for (int i = 0; i < returnMatches.size(); i++) {
                result += returnMatches.get(i).getOpponent() + ", který se hrál " + returnMatches.get(i).getDateOfMatchInStringFormat();
                if (i == returnMatches.size()-1) {
                    result += " ve kterých padlo " + maximumFines + " pokut.";
                }
                else if (i == returnMatches.size()-2) {
                    result += " a ";
                }
                else {
                    result += ", ";
                }
            }
            return result;
        }
    }

    /**
     * @return vrátí text se zápasem nebo listem zápasů ve kterých se tuto sezonu nejvíce vydělalo na pokutách
     */
    String getMatchWithMostFinesAmountInCurrentSeason() {
        List<Match> returnMatches = new ArrayList<>();
        int maximumFinesAmount = 0;
        for (Match match : matches) {
            if (match.getSeason().equals(compareMatch.getSeason())) {
                if (match.getAmountOfFinesInMatch() > maximumFinesAmount) {
                    returnMatches.clear();
                    returnMatches.add(match);
                    maximumFinesAmount = match.getAmountOfFinesInMatch();
                }
                else if (match.getAmountOfFinesInMatch() == maximumFinesAmount) {
                    returnMatches.add(match);
                }
            }
        }
        if (maximumFinesAmount == 0) {
            return "Nelze najít zápas, kde se v této sezoně " + compareMatch.getSeason().getName() + " nejvíce cálovalo za pokuty, protože se zatím žádná pokuta neudělila!";
        }
        else if (returnMatches.size() == 1) {
            return "Největší objem peněz v aktuální sezoně " + compareMatch.getSeason().getName() + " se přinesl zápas se soupeřem " +
                    returnMatches.get(0).getOpponent() + ", který se hrál " + returnMatches.get(0).getDateOfMatchInStringFormat() +
                    " a vybralo se v něm " + maximumFinesAmount + " Kč.";

        }
        else {
            String result = "O pozici zápasu, který přinesl klubové pokladně nejvíce peněz z pokut v aktuální sezoně " + compareMatch.getSeason().getName() + " se dělí zápas se soupeřem ";
            for (int i = 0; i < returnMatches.size(); i++) {
                result += returnMatches.get(i).getOpponent() + ", který se hrál " + returnMatches.get(i).getDateOfMatchInStringFormat();
                if (i == returnMatches.size()-1) {
                    result += " ve kterých se vybralo " + maximumFinesAmount + " Kč.";
                }
                else if (i == returnMatches.size()-2) {
                    result += " a ";
                }
                else {
                    result += ", ";
                }
            }
            return result;
        }
    }

    /**
     * @return vrátí text sezonu/sezony ve kterých padlo v historii nejvíce pokut, společně s počtem a počtem zápasů
     */
    String getSeasonWithMostFines() {
        List<Season> returnSeasons = new ArrayList<>();
        List<Integer> numberOfMatches = new ArrayList<>();
        int maximumFines = 0;
        for (Season season : seasons) {
            int seasonFines = 0;
            int matchesNumber = 0;
            for (Match match : matches) {
                if (match.getSeason().equals(season)) {
                    matchesNumber++;
                    seasonFines += match.getNumberOfFinesInMatch();
                }
            }
            if (seasonFines > maximumFines) {
                maximumFines = seasonFines;
                numberOfMatches.clear();
                numberOfMatches.add(matchesNumber);
                returnSeasons.clear();
                returnSeasons.add(season);
            }
            else if (seasonFines == maximumFines) {
                numberOfMatches.add(matchesNumber);
                returnSeasons.add(season);
            }
        }
        if (maximumFines == 0) {
            return "Nelze najít sezonu s nejvíce pokutama, protože se zatím žádná pokuta nerozdala!";
        }
        else if (returnSeasons.size() == 1) {
            return "Nejvíce pokut se rozdalo v sezoně " + returnSeasons.get(0).getName() + ", kdy v " + numberOfMatches.get(0) + ". zápasech padlo " + maximumFines + " pokut";

        }
        else {
            String result = "Nejvíce pokutami se můžou chlubit sezony ";
            for (int i = 0; i < returnSeasons.size(); i++) {
                result += returnSeasons.get(i).getName() + " s " + numberOfMatches.get(i) + ". zápasy";
                if (i == returnSeasons.size()-1) {
                    result += ", ve kterých padlo " + maximumFines + " pokut.";
                }
                else if (i == returnSeasons.size()-2) {
                    result += " a ";
                }
                else {
                    result += ", ";
                }

            }

            return result;
        }

    }

    /**
     * @return vrátí text sezonu/sezony ve kterých se vybralo v historii nejvíce za pokuty, společně s počtem zápasů
     */
    String getSeasonWithMostFinesAmount() {
        List<Season> returnSeasons = new ArrayList<>();
        List<Integer> numberOfMatches = new ArrayList<>();
        int maximumFines = 0;
        for (Season season : seasons) {
            int seasonFines = 0;
            int matchesNumber = 0;
            for (Match match : matches) {
                if (match.getSeason().equals(season)) {
                    matchesNumber++;
                    seasonFines += match.getAmountOfFinesInMatch();
                }
            }
            if (seasonFines > maximumFines) {
                maximumFines = seasonFines;
                numberOfMatches.clear();
                numberOfMatches.add(matchesNumber);
                returnSeasons.clear();
                returnSeasons.add(season);
            }
            else if (seasonFines == maximumFines) {
                numberOfMatches.add(matchesNumber);
                returnSeasons.add(season);
            }
        }
        if (maximumFines == 0) {
            return "Nelze najít sezonu kdy se vybralo nejvíc za pokuty, protože se zatím žádná pokuta nerozdala!";
        }
        else if (returnSeasons.size() == 1) {
            return "Nejvíc peněz na pokutách se vybralo v sezoně " + returnSeasons.get(0).getName() + ", kdy v " + numberOfMatches.get(0) + ". zápasech pokladna shrábla " + maximumFines + " Kč.";

        }
        else {
            String result = "Největším objemem vybraných peněz se můžou chlubit sezony ";
            for (int i = 0; i < returnSeasons.size(); i++) {
                result += returnSeasons.get(i).getName() + " s " + numberOfMatches.get(i) + ". zápasy";
                if (i == returnSeasons.size()-1) {
                    result += ", ve kterých se zaplatilo " + maximumFines + " Kč.";
                }
                else if (i == returnSeasons.size()-2) {
                    result += " a ";
                }
                else {
                    result += ", ";
                }

            }

            return result;
        }

    }

    /**
     * @return vrátí průměrný počet pokut na hráče
     */
    String getAverageNumberOfFinesInMatchForPlayers() {
        float finesNumber = 0;
        int playerNumber = 0;
        for (Match match : matches) {
            finesNumber += match.getNumberOfFinesInMatch();
            playerNumber += match.getNumberOfPlayersInMatch();
        }
        float average = finesNumber/playerNumber;

        return "Za celou historii průměrně každý hráč Trusu dostal " + average + " pokut za zápas";
    }

    /**
     * @return vrátí průměrný cálování pokut na hráče
     */
    public String getAverageNumberOfFinesAmountInMatchForPlayers() {
        float finesNumber = 0;
        int playerNumber = 0;
        for (Match match : matches) {
            finesNumber += match.getAmountOfFinesInMatch();
            playerNumber += match.getNumberOfPlayersInMatch();
        }
        float average = finesNumber/playerNumber;

        return "Za celou historii průměrně každý hráč Trusu zacáloval " + average + " korun každý zápas za pokuty";
    }


    /**
     * @return vrátí průměrný počet pokut na zápas
     */
    String getAverageNumberOfFinesInMatch() {
        float finesNumber = 0;
        for (Match match : matches) {
            finesNumber += match.getNumberOfFinesInMatch();
        }
        float average = finesNumber/matches.size();
        return "V naprosto průměrném zápasu Trusu se udělí " + average + " pokut";
    }

    /**
     * @return vrátí průměrný počet vybranejch peněz v zápase
     */
    String getAverageNumberOfFinesAmountInMatch() {
        float finesNumber = 0;
        for (Match match : matches) {
            finesNumber += match.getAmountOfFinesInMatch();
        }
        float average = finesNumber/matches.size();
        return "V naprosto průměrném zápasu Trusu se vybere " + average + " Kč na pokutách";
    }

    /**
     * @return vrátí nejčastěji udělovanou pokutu v zápasech
     */
    String getTheMostCommonFineInAllMatches() {
        List<Fine> returnFines = new ArrayList<>();
        int maximumFines = 0;
        for (Fine fine : fines) {
            if (fine.returnNumberOfFineInMatches(matches) > maximumFines) {
                returnFines.clear();
                returnFines.add(fine);
                maximumFines = fine.returnNumberOfFineInMatches(matches);
            }
            else if (fine.returnNumberOfFineInMatches(matches) == maximumFines) {
                returnFines.add(fine);
            }
        }
        float average = (float)maximumFines/matches.size();
        if (maximumFines == 0) {
            return "Nelze zobrazit nejčastější pokutu, protože nikdo zatim žádnou nedostal?!";
        }
        else if (returnFines.size() == 1) {
            return "Zatím nejčastější pokuta byla " + returnFines.get(0).getName() + ", která padla celkem " +
                    maximumFines + "., tedy průměrně " + average + " v každém zápase";

        }
        else {
            String result = "O pozici pokuty, která padla doteď nejvíckrát se dělí ";
            for (int i = 0; i < returnFines.size(); i++) {
                result += returnFines.get(i).getName();
                if (i == returnFines.size()-1) {
                    result += " která padla celkem " + maximumFines + "., tedy průměrně " + average + " v každém zápase";
                }
                else if (i == returnFines.size()-2) {
                    result += " a ";
                }
                else {
                    result += ", ";
                }
            }
            return result;
        }
    }

    /**
     * @return vrátí nejvýdělečnější pokutu v zápasech
     */
    String getTheMostProfitableFineInAllMatches() {
        List<Fine> returnFines = new ArrayList<>();
        int maximumFines = 0;
        for (Fine fine : fines) {
            if (fine.returnAmountOfFineInMatches(matches) > maximumFines) {
                returnFines.clear();
                returnFines.add(fine);
                maximumFines = fine.returnAmountOfFineInMatches(matches);
            }
            else if (fine.returnAmountOfFineInMatches(matches) == maximumFines) {
                returnFines.add(fine);
            }
        }
        float average = (float)maximumFines/matches.size();
        if (maximumFines == 0) {
            return "Nelze zobrazit nejvýdělečnější pokutu, protože nikdo zatim žádnou nedostal?!";
        }
        else if (returnFines.size() == 1) {
            return "Zatím nejvýdělečnější pokuta byla " + returnFines.get(0).getName() + ", na které se vybralo již" +
                    maximumFines + " Kč, tedy průměrně " + average + " Kč v každém zápase";

        }
        else {
            String result = "O pozici nejvýdělečnější pokuty se dělí ";
            for (int i = 0; i < returnFines.size(); i++) {
                result += returnFines.get(i).getName();
                if (i == returnFines.size()-1) {
                    result += " na kterých se vybralo již " + maximumFines + " Kč, tedy průměrně " + average + " Kč v každém zápase";
                }
                else if (i == returnFines.size()-2) {
                    result += " a ";
                }
                else {
                    result += ", ";
                }
            }
            return result;
        }
    }

    String getMatchWithBirthday() {
        Date date = new Date();
        List<Match> matchesWithBirthday = new ArrayList<>();
        List<Player> playersWithBirthday = new ArrayList<>();
        for (Match match : matches) {
            for (Player player : players) {
                if (date.wasBirthdayInMatchday(player.getDateOfBirth(), match.getDateOfMatch())) {
                    matchesWithBirthday.add(match);
                    playersWithBirthday.add(player);
                }
            }
        }
        //pokud se nikdo nenašel v první iteraci, tedy žádné narozky se nekrejou s datem zápasu
        if (matchesWithBirthday.size() == 0) {
            return "Zatím se nenašel zápas kde by nějaký hráč zapíjel narozky. Už se všichni těšíme";
        }
        else {
            //nejprve zjišťujeme, zda hráč, který měl narozky v den zápasu, skutečně byl na zápasu přítomen
            List<Match> returnMatches = new ArrayList<>();
            List<Player> returnPlayers = new ArrayList<>();
            for (int i = 0; i < matchesWithBirthday.size(); i++) {
                for (Player player : matchesWithBirthday.get(i).getPlayerListOnlyWithParticipants()) {
                    if (player.equals(playersWithBirthday.get(i))) {
                        returnPlayers.add(player);
                        returnMatches.add(matchesWithBirthday.get(i));
                    }
                }
            }
            //Pokud nikdo s narozkama nebyl na zápase trusu, tak uděláme stěnu hamby
            if (returnMatches.size() == 0) {
                String result = "Zatím se nenašel zápas kde by nějaký hráč zapíjel narozky. Už se všichni těšíme. Do té doby zde máme zeď hamby pro hráče, " +
                        "kteří raději své narozeniny zapíjeli jinde než na Trusu. Hamba! : ";
                for (int i = 0; i < playersWithBirthday.size(); i++) {
                    result += playersWithBirthday.get(i).getName();
                    if (i == playersWithBirthday.size() - 1) {
                        result += ".";
                    } else if (i == playersWithBirthday.size() - 2) {
                        result += " a ";
                    } else {
                        result += ", ";
                    }
                }
                return result;
            }
            //Pokud byl jenom jeden takový šťastlivec
            else if (returnMatches.size() == 1) {
                return  "Zatím jediný zápas, kdy někdo z Trusu zapíjel narozky byl " + returnMatches.get(0).getDateOfMatchInStringFormat() +
                        " se soupeřem " + returnMatches.get(0).getOpponent() + " kdy slavil " + returnPlayers.get(0).getName() + ", který vypil " +
                        returnPlayers.get(0).getNumberOfBeers() + " piv. Celkově se tento zápas vypilo " + returnMatches.get(0).getNumberOfBeersInMatch() + " piv";
            }
            //pokud jich bylo víc
            else {
                String result = "Velká společenská událost v podobě oslavy narozek se konala na zápasech proti soupeřům: ";
                for (int i = 0; i < returnMatches.size(); i++) {
                    result += returnMatches.get(i).getOpponent() + ", hraném " + returnMatches.get(i).getDateOfMatchInStringFormat() + ", kdy slavil narozky "
                            + returnPlayers.get(i).getName() + ", který vypil " + returnPlayers.get(i).getNumberOfBeers() +
                            " piv. Celkově se tento zápas vypilo " + returnMatches.get(i).getNumberOfBeersInMatch() + " piv";
                    if (i == returnMatches.size() - 1) {
                        result += ".";
                    } else if (i == returnMatches.size() - 2) {
                        result += " a proti ";
                    } else {
                        result += ". Proti ";
                    }
                }
                return result;
            }
        }
    }
}

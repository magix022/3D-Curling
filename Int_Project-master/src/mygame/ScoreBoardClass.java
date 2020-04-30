package mygame;

import com.jme3.scene.Spatial;

public class ScoreBoardClass {

    int numberOfRounds;
    int round;
    int team1TotalScore = 0;
    int team2TotalScore = 0;
    int totalShots;
    Spatial rockModel;
    String rockModelPath;
    int[] team1RoundScore;
    int[] team2RoundScore;
    int hammer;

    public ScoreBoardClass() {
    }

    public int getNumberOfRounds() {
        return numberOfRounds;
    }

    public void setNumberOfRounds(int numberOfRounds) {
        this.numberOfRounds = numberOfRounds;
        team1RoundScore = new int[numberOfRounds];
        team2RoundScore = new int[numberOfRounds];
    }

    public void setRound(int round) {
        this.round = round;
    }

    public int getRound() {
        return round;
    }

    public int getTotalShots() {
        return totalShots;
    }

    public void setTotalShots(int totalShots) {
        this.totalShots = totalShots;
    }

    public int getTeam1RoundScore(int round) {
        return team1RoundScore[round];
    }

    public void setTeam1RoundScore(int round, int roundScoreTeam1) {
        this.team1RoundScore[round] = roundScoreTeam1;
    }

    public int getTeam2RoundScore(int round) {
        return team2RoundScore[round];
    }

    public void setTeam2RoundScore(int round, int roundScoreTeam2) {
        this.team2RoundScore[round] = roundScoreTeam2;
    }

    public int getTeam1TotalScore() {
        return team1TotalScore;
    }

    public int getTeam2TotalScore() {
        return team2TotalScore;
    }

    public String getRoundWinner() {
        if (team1TotalScore > this.getTeam2TotalScore()) {
            return "Team 1 wins this round";
        } else {
            return "Team 2 wins this round";
        }
    }

    public String getGameWinner() {
        if (this.getRound() == this.getNumberOfRounds()) {
            if (this.getTeam1TotalScore() > this.getTeam2TotalScore()) {
                return "Team 1 wins!";
            } else if (this.getTeam1TotalScore() == this.getTeam2TotalScore()) {
                return "Tie game!";
            } else {
                return "Team 2 wins!";
            }
        } else {
            return "Game is not finished... :(";
        }
    }

    public void calculateTotalScoreTeam1() {
        team1TotalScore = 0;
        for (int i = 0; i < team1RoundScore.length; i++) {
            team1TotalScore += team1RoundScore[i];
        }
    }

    public void calculateTotalScoreTeam2() {
        team2TotalScore = 0;
        for (int i = 0; i < team2RoundScore.length; i++) {
            team2TotalScore += team2RoundScore[i];
        }
    }

    public void setHammer(int hammer) {
        this.hammer = hammer;
    }

    public int getHammer() {
        if (getRound() == 0) {
            return this.hammer;
        } else if (getTeam2RoundScore(getRound() - 1) > getTeam1RoundScore(getRound() - 1)) {
            return 2;
        } else if (getTeam2RoundScore(getRound() - 1) < getTeam1RoundScore(getRound() - 1)) {
            return 1;
        } else {
            if (getRound() == 1) {
                return this.hammer;
            } else if (getTeam2RoundScore(getRound() - 2) > getTeam1RoundScore(getRound() - 2)) {
                return 2;
            } else if (getTeam2RoundScore(getRound() - 2) < getTeam1RoundScore(getRound() - 2)) {
                return 1;
            } else {
                if (getRound() == 2) {
                    return this.hammer;
                } else if (getTeam2RoundScore(getRound() - 3) > getTeam1RoundScore(getRound() - 3)) {
                    return 2;
                } else if (getTeam2RoundScore(getRound() - 3) < getTeam1RoundScore(getRound() - 3)) {
                    return 1;
                } else {
                    if (getRound() == 3) {
                        return this.hammer;
                    } else if (getTeam2RoundScore(getRound() - 4) > getTeam1RoundScore(getRound() - 4)) {
                        return 2;
                    } else if (getTeam2RoundScore(getRound() - 4) < getTeam1RoundScore(getRound() - 4)) {
                        return 1;
                    } else {
                        if (getRound() == 4) {
                            return this.hammer;
                        } else if (getTeam2RoundScore(getRound() - 5) > getTeam1RoundScore(getRound() - 5)) {
                            return 2;
                        } else if (getTeam2RoundScore(getRound() - 5) < getTeam1RoundScore(getRound() - 5)) {
                            return 1;
                        } else {
                            if (getRound() == 5) {
                                return this.hammer;
                            } else if (getTeam2RoundScore(getRound() - 6) > getTeam1RoundScore(getRound() - 6)) {
                                return 2;
                            } else if (getTeam2RoundScore(getRound() - 6) < getTeam1RoundScore(getRound() - 6)) {
                                return 1;
                            } else {
                                if (getRound() == 6) {
                                    return this.hammer;
                                } else if (getTeam2RoundScore(getRound() - 7) > getTeam1RoundScore(getRound() - 7)) {
                                    return 2;
                                } else if (getTeam2RoundScore(getRound() - 7) < getTeam1RoundScore(getRound() - 7)) {
                                    return 1;
                                } else {
                                    if (getRound() == 7) {
                                        return this.hammer;
                                    } else if (getTeam2RoundScore(getRound() - 8) > getTeam1RoundScore(getRound() - 8)) {
                                        return 2;
                                    } else if (getTeam2RoundScore(getRound() - 8) < getTeam1RoundScore(getRound() - 8)) {
                                        return 1;
                                    } else {
                                        if (getRound() == 8) {
                                            return this.hammer;
                                        } else if (getTeam2RoundScore(getRound() - 9) > getTeam1RoundScore(getRound() - 9)) {
                                            return 2;
                                        } else if (getTeam2RoundScore(getRound() - 9) < getTeam1RoundScore(getRound() - 9)) {
                                            return 1;
                                        } else {
                                            return this.hammer;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

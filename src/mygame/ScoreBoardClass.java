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
        for (int i = 0; i < team1RoundScore.length; i++) {
            team1TotalScore += this.getTeam1RoundScore(i);
            System.out.println("round " + i + ": " + this.getTeam1RoundScore(i));
        }
        return team1TotalScore;
    }

    public int getTeam2TotalScore() {
        for (int i = 0; i < team2RoundScore.length; i++) {
            team2TotalScore += this.getTeam2RoundScore(i);
            System.out.println("round " + i + ": " + this.getTeam2RoundScore(i));
        }
        return team2TotalScore;
    }

    public String getRoundWinner() {
        if (this.getTeam1TotalScore() > this.getTeam2TotalScore()) {
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
}

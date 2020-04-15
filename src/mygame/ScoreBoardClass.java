package mygame;

import com.jme3.scene.Spatial;
import java.util.Scanner;

public class ScoreBoardClass {

    Scanner input = new Scanner(System.in);
    int numberOfRounds = 10; //get number of rounds wanted by players in the main menu (GUI)
    int currentRound = 1;
    int team1RoundScore;
    int team2RoundScore;
    int team1TotalScore;
    int team2TotalScore;
    int totalShots;
    Spatial rockModel;
    String rockModelPath;

    public ScoreBoardClass() {
    }

    public ScoreBoardClass(int team1TotalScore, int team2TotalScore) {
        this.team1TotalScore = team1TotalScore;
        this.team2TotalScore = team2TotalScore;
//        this.rockModel = rockModel;
//        this.rockModelPath = rockModelPath;
    }
    
    public int getTotalShots() {
        return totalShots;
    }

    public void setTotalShots(int totalShots) {
        this.totalShots = totalShots;
    }
    public int currentRound() {
        return currentRound;
    }

    public void currentRound(int currentRound) {
        this.currentRound = currentRound;
    }

    public int getTeam1RoundScore() {
        return team1RoundScore;
    }

    public void setTeam1RoundScore(int team1RoundScore) {
        this.team1RoundScore = team1RoundScore;
    }

    public int getTeam1TotalScore() {
        return team1TotalScore += team1RoundScore;
    }

    public void setTeam1TotalScore(int team1TotalScore) {
        this.team1TotalScore = team1TotalScore;
    }

    public int getTeam2RoundScore() {
        return team2RoundScore;
    }

    public void setTeam2RoundScore(int team2RoundScore) {
        this.team2RoundScore = team2RoundScore;
    }

    public int getTeam2TotalScore() {
        return team2TotalScore += team2RoundScore;
    }

    public void setTeam2TotalScore(int team2TotalScore) {
        this.team2TotalScore = team2TotalScore;
    }

//    User input for number of rounds in Main Menu
//    public int getNumberOfRounds() {
//        return numberOfRounds;
//    }
//
//    public void setNumberOfRounds(int numberOfRounds) {
//        this.numberOfRounds = numberOfRounds;
//    }
    public int getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
    }
}

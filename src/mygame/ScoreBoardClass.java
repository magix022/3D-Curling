package mygame;

import com.jme3.scene.Spatial;
import java.util.Scanner;

public class ScoreBoardClass {

    Scanner input = new Scanner(System.in);
    int numberOfRounds = input.nextInt();
    int currentRound = 1;
    int[] team1RoundScore = new int[currentRound];
    int[] team2RoundScore = new int[currentRound];
    int team1TotalScore;
    int team2TotalScore;
    Spatial rockModel;
    String rockModelPath;

    public ScoreBoardClass() {
    }

    public ScoreBoardClass(int team1TotalScore, int team2TotalScore, Spatial rockModel, String rockModelPath) {
        this.team1TotalScore = team1TotalScore;
        this.team2TotalScore = team2TotalScore;
        this.rockModel = rockModel;
        this.rockModelPath = rockModelPath;
    }

    public int currentRound() {
        return currentRound;
    }

    public void currentRound(int currentRound) {
        this.currentRound = currentRound;
    }

    public int getTeam1RoundScore() {
        return team1RoundScore[currentRound];
    }

    public void setTeam1RoundScore(int[] team1RoundScore) {
        this.team1RoundScore[currentRound] = team1RoundScore[currentRound];
    }

    public int getTeam1TotalScore() {
        for (int i = 1; i <= currentRound; i++) {
            team1TotalScore += team1RoundScore[i];
        }
        return team1TotalScore;
    }

    public void setTeam1TotalScore(int team1TotalScore) {
        this.team1TotalScore = team1TotalScore;
    }

    public int getTeam2RoundScore() {
        return team2RoundScore[currentRound];
    }

    public void setTeam2RoundScore(int[] team2RoundScore) {
        this.team2RoundScore[currentRound] = team2RoundScore[currentRound];
    }

    public int getTeam2TotalScore() {
        for (int i = 1; i <= currentRound; i++) {
            team2TotalScore += team2RoundScore[i];
        }
        return team2TotalScore;
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

//    public int getNumberOfShotsTeam1****
//    public int getRoundResult() {
////    Distance of rock from center of the house
////    Must determine which rock is which each team to divide score accordingly (Roch(1,1) means first rock for team 1, ...)
//        if (numberOfShotsTeam1 ==4 && numberofShotsTeam2 ==4){
//            
//        }
//        if (Rock(1, 1).getRockModel().getDistanceFromCenter() < Rock(2, 1).getRockModel().getDistanceFromCenter()){
//            if (Rock(1, 1).getRockModel().getDistanceFromCenter() < Rock(2, 1).getRockModel().getDistanceFromCenter()){
//                if (Rock(1, 1).getRockModel().getDistanceFromCenter() < Rock(2, 1).getRockModel().getDistanceFromCenter()){
//            }
//            return 1;
//        } else {
//            return 2;
//            }
//    }
//
//}
//
////    public void getDistance(){
//      rock.getRockModel().getTranslation();
//}

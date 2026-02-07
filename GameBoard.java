
import java.util.ArrayList;
public class GameBoard {
    public int day;
    public String[] Players;
    public String[] Scenes;

    public static void main(String[] args) {
        int day = 1;
        ArrayList<Scene> scenes = new ArrayList<>();
        // something











        
    }
    //code for setting up the game
    void setupGame() {

    }
    // code for starting the day
    void startDay() {

    }   



    /*  END DAY
    we need a function to end the day */
    public void endDay(){
        // reset board and move to next day
        day++;
        // reset board
    }


    // we need to signal to next turn
    void nextTurn() {

    }

    /* game Status, is used to check if the game is over or not.  */
    // we just called end day, we need to check if the game is over
    public boolean checkGameEnd(int day, int players){
        if (day > 4 && players > 3 || day > 3 && players < 4){
            return true;
        } 
        return false;
    }
}
 

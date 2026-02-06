import java.util.ArrayList;
public class GameBoard {
    public int day;

    public static void main(String[] args) {
        int day = 1;
        ArrayList<Scene> scenes = new ArrayList<>();
        // something











        
    }
    



    /*  END DAY
    we need a function to end the day */
    public void endDay(){
        // reset board and move to next day
        day++;
        // reset board
    }
    /* game Status, is used to check if the game is over or not.  */
    // we just called end day, we need to check if the game is over
    public boolean gameStatus(int day, int players){
        if ( day > 4 || players == 0){
            return true;
        } else {
            return false;
        }
    }
}
 

import java.util.Random;
public class Dice {

    int diceNum;
    Random r = new Random();

    // We want roll dice to return a list sorted in descending order 
    //so it can be passes amount amongst the players
    public int[] rollDice(int diceNum) {
        int [] rolledDice = new int[diceNum];
        for(int i = 0; i < diceNum; i++) {
            int die = r.nextInt(6);
            // to convert form 0-5 to 1-6
            rolledDice[i] = die +1;
        }
        return rolledDice;
    }

    
}

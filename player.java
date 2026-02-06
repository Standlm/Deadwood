import java.util.Random;
public class Player {
    private String name;
    private int rank;
    private int dollars;
    private int credits;
    private int rehearsalChips;

    public Player(String name, int rank, int dollars, int credits, int rehearsalChips){
        this.name = name;
        this.rank = rank;
        this.dollars = dollars;
        this.credits = credits;
        this.rehearsalChips = rehearsalChips;
    }
    move(boardSpace space){
        // move to a new space
    }

    takeRole(Scene scene,  Role r){
        // take a role
    }
    act(Scene scene, Role r){
        // act on a scene
    }
    rehearse(Scene scene, Role r){
        // rehearse a scene
    }   
    upgradeRank(Boolean useDollars, Boolean useCredits, int rank){
        
        // upgrade rank
    }




}
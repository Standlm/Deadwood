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
      public static void main(String[] args) {
        
    }
    void move(BoardSpace space){
        // move to a new space
    }

    void takeRole(Scene scene,  Role r){
        // take a role
    }
    void act(Scene scene, Role r){
        // act on a scene
    }
     void rehearse(Scene scene, Role r){
        // rehearse a scene
    }   
    public Boolean upgradeRank(Boolean useDollars, Boolean useCredits, int rank){
        return true;
        
        // upgrade rank
    }




}

 
public class Scene {
    private String locationID;
    private String sceneName;
    private int budget;
    private Role[] roles;
    private int shotsLeft;

    public static void main(String[] args) {
        
    }

    // Scene Constructor just to heintlp understand Role more
    public Scene(String locationID, String sceneName, int budget, Role[] roles, int shots){
        this.locationID = locationID;
        this.sceneName = sceneName;
        this.budget = budget;
        this.roles = roles;
        this.shotsLeft = shots;
    }

    // Getters
    public String getSceneName(){
        return sceneName;
    }
    public int getBudget(){
        return budget;
    }
    public int getShotsLeft(){
        return shotsLeft;
    }
    
    // act
    // Requires Dice class to give total
    public boolean act(Role role, int diceTotal){
        if (shotsLeft > 0 && (diceTotal + role.getPracticeShots()) >= budget){
            shotsLeft--;
            return true;
        } 
        return false;
    }

    // rehearse
    public void rehearse(Role role){
        role.rehearse();
    }
    
    // wrap
    public boolean isWrapped() {
        return shotsLeft >= 0;
    }

}

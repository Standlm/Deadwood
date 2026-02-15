

public class BoardSpace {

    private final String id;
    private final String type;

    // May need value for scene later, once scene type is decided on

    // May need to change element type for XML parsing cohesion
    private String[] neighbors;
    

    public static void main(String[] args) {
        String id = "Jail";
        String type = "sceneHolder";
        String[] neighbors = {"General Store", "Train Station", "Main Street"};
        // board space concept
        BoardSpace bSpace = new BoardSpace(id, type, neighbors);
        printBoardSpace(bSpace);
    }

    // getters
    public String getId(){
        return id;
    }
    public String getType(){
        return type;
    }

    // Checker
    public boolean isSet(){
        return type.equals("set");
    }

    public BoardSpace(String spaceId, String spaceType, String[] spaceNeighbors) {
        this.id = spaceId; // use 'this' to refer to the class variable
        this.type = spaceType;
        this.neighbors = spaceNeighbors;
    }

    public static void printBoardSpace(BoardSpace boardSpace){
        System.out.println("The id of this space is: " + boardSpace.id 
            + ".\n" + "The type of space: " + boardSpace.type);
        
        System.out.println("Here are the neighbors of this space:");

        // Loop for all spaces to go next
        for (int i = 0; i < boardSpace.neighbors.length; i++){
            System.out.println((i + 1)  + ". " + boardSpace.neighbors[i]);
        }
    }
    // Assign a scene to this BoardSpace and set shots
    private Scene scene;
    private int shots;
    private int maxShots;

    public void setScene(Scene s) {
        this.scene = s;
        if (s != null) {
            this.maxShots = s.getNumShots();
            this.shots = this.maxShots;
        } else {
            this.maxShots = 0;
            this.shots = 0;
        }
    }

    // Remove the scene from this BoardSpace and reset shots
    public void removeScene() {
        this.scene = null;
        this.shots = 0;
        this.maxShots = 0;
    }

    // Reset shots to maxShots (if a scene is present)
    public void resetShots() {
        if (this.scene != null) {
            this.shots = this.maxShots;
        } else {
            this.shots = 0;
        }
    }
}

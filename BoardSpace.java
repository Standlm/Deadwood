


public class BoardSpace {
    private final String id;
    private final String type;
    private String[] neighbors;
    private Role[] roles; // extras for this set (or empty for trailer/office)

    public static void main(String[] args) {}

    // getters
    public String getId(){ return id; }
    public String getType(){ return type; }
    public boolean isSet(){ return type.equals("set"); }

    // Old constructor for compatibility
    public BoardSpace(String spaceId, String spaceType, String[] spaceNeighbors) {
        this(spaceId, spaceType, spaceNeighbors, new Role[0]);
    }

    // New constructor for sets with roles/extras
    public BoardSpace(String spaceId, String spaceType, String[] spaceNeighbors, Role[] roles) {
        this.id = spaceId;
        this.type = spaceType;
        this.neighbors = spaceNeighbors;
        this.roles = roles;
    }

    public static void printBoardSpace(BoardSpace boardSpace){
        System.out.println("The id of this space is: " + boardSpace.id 
            + ".\n" + "The type of space: " + boardSpace.type);
        System.out.println("Here are the neighbors of this space:");
        for (int i = 0; i < boardSpace.neighbors.length; i++){
            System.out.println((i + 1)  + ". " + boardSpace.neighbors[i]);
        }
        if (boardSpace.roles != null && boardSpace.roles.length > 0) {
            System.out.println("Extras/roles on this set:");
            for (int i = 0; i < boardSpace.roles.length; i++) {
                System.out.println((i+1) + ". " + boardSpace.roles[i].getName() + " (Rank: " + boardSpace.roles[i].getRank() + ")");
            }
        }
    }
        // Return extras/roles for this set (or empty array)
        public Role[] getRoles() {
            return roles != null ? roles : new Role[0];
        }
    //make a trailer space for reset purposes
    public static BoardSpace createTrailerSpace() {
        String id = "Trailer";
        String type = "trailer";
        String[] neighbors = {}; // Trailer has no neighbors
        return new BoardSpace(id, type, neighbors);
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

    // we need to return neighbors for player movement
    public String[] getNeighbors() {
        return neighbors;
    }

    // we need to return the scene for acting
    public Scene getScene() {
        return scene;
    }
    public boolean isCastingOffice() {
        return type.equals("office");
    }   
}

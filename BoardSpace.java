

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
}

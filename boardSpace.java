

public class boardSpace {

    private String id;
    private String type;

    public static void main(String[] args) {
        boardSpace bSpace = new boardSpace(args[0], args[1]);
        System.out.println("The id of this role is: " + bSpace.id + ".\n" + "The type of role: " + bSpace.type);
    }

    public boardSpace(String spaceId, String spaceType) {
        this.id = spaceId; // use 'this' to refer to the class variable
        this.type = spaceType;
    }
}
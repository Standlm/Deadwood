import java.util.Scanner;

public class Deadwood {

    public static void main(String[] args) {

        // only instantiate once
        Scanner myObj = new Scanner(System.in);


        // Testing BoardSpace
        String id = "Jail";
        String type = "sceneHolder";
        String[] neighbors = {"General Store", "Train Station", "Main Street"};
        BoardSpace jail = new BoardSpace(id, type, neighbors);
        BoardSpace.printBoardSpace(jail);

        // we want main to set up the GameBoard
        // testing a role
        String roletype = "On-Card";
        String rolename = "Marshal Canfield";
        int rolerank = 3;
        //Role example for test cases
        Role roleExample = new Role(rolename, rolerank, roletype);
        Role.printRole(roleExample);

        // Eventually, run GameBoard
    }


    private static void print(String toPrint) {
        char[] chars = toPrint.toCharArray();
        for (int i=0; i < chars.length; i++) {
            System.out.print(chars[i]);
            try { Thread.sleep(25);} 
            catch (InterruptedException e) {Thread.currentThread().interrupt();}
        }
        System.out.println("");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


}

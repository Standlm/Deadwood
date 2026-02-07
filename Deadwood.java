import java.util.Scanner;

public class Deadwood {

    public static void main(String[] args) {

        // only instantiate once
        Scanner myObj = new Scanner(System.in);

     
    
     


        // Run Board Space Example

        // Run Roll Example

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

import java.util.List;
import java.util.Scanner;

public class ConsoleView implements GameView {

    private Scanner scanner = new Scanner(System.in);

    @Override
    public void printMessage(String message) {
        System.out.println(message);
    }

    @Override
    public void printCurrentDayAndTurn(int day, Player currentPlayer) {
        System.out.println("\n=================================");
        System.out.println("Day " + day + " | " + currentPlayer.getName() + "'s Turn");
        System.out.println("=================================");
    }

    @Override
    public void printPlayerTurnStart(Player player) {
        System.out.println("Player: " + player.getName());
    }

    @Override
    public void printPlayerStats(Player player) {
        System.out.println("Rank: " + player.getRank());
        System.out.println("Dollars: " + player.getDollars());
        System.out.println("Credits: " + player.getCredits());
        System.out.println("Location: " + player.getCurrentSpace().getId());
    }

    @Override
    public void printMoveOptions(String[] neighbors) {
        System.out.println("Choose where to move:");

        for (int i = 0; i < neighbors.length; i++) {
            System.out.println((i + 1) + ". " + neighbors[i]);
        }
    }

    @Override
    public int getMoveChoice() {
        System.out.print("Enter number: ");
        return scanner.nextInt() - 1;
    }

    @Override
    public String getActionChoice() {
        return scanner.next();
    }

    @Override
    public void printWinner(List<Player> players) {

        Player winner = null;
        int highestScore = -1;

        for (Player p : players) {
            int score = p.getDollars() + p.getCredits() + (5 * p.getRank());
            System.out.println(p.getName() + " final score: " + score);

            if (score > highestScore) {
                highestScore = score;
                winner = p;
            }
        }

        if (winner != null) {
            System.out.println("\n Winner: " + winner.getName());
        }
    }
    @Override
    public void printUpgradeOptions(Player player) {
        int currentRank = player.getRank();
        System.out.println("Current rank: " + currentRank);
        System.out.println("Available upgrades:");
        // Upgrade costs: [rank] -> {dollars, credits}
        int[][] costs = {
            {0, 0}, {0, 0}, {4, 5}, {10, 10}, {18, 15}, {28, 20}, {40, 25}
        };
        for (int rank = currentRank + 1; rank <= 6; rank++) {
            System.out.println("Rank " + rank + ": $" + costs[rank][0] + " or " + costs[rank][1] + " credits");
        }
        System.out.print("Enter rank to upgrade to (or 0 to cancel): ");
    }

    @Override
    public int getUpgradeChoice() {
        return scanner.nextInt();
    }
    public void printScenesLeft(List<BoardSpace> boardSpaces) {
        System.out.println("\nScenes left on the board:");
        for (BoardSpace space : boardSpaces) {
            if (space.isSet() && space.hasScene()) {
                System.out.println("- " + space.getScene().getName() + " at " + space.getId() + " (Space: " + space.getId() + ")");
            }
        }
    }
} 

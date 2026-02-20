import java.util.List;

public interface GameView {

    // General output
    void printMessage(String message);

    // Start of turn
    void printCurrentDayAndTurn(int day, Player currentPlayer);
    void printPlayerTurnStart(Player player);
    void printPlayerStats(Player player);

    // Movement
    void printMoveOptions(String[] neighbors);
    int getMoveChoice();

    //upgrades 
    void printUpgradeOptions(Player player);
    int getUpgradeChoice();

    // Actions
    String getActionChoice();

    // End of game
    void printWinner(List<Player> players);


}
import java.util.Scanner;

public class Deadwood {
    public static void main(String[] args) {
        GameBoard game = new GameBoard();
        LoadXml loader = new LoadXml();
        GameView view = new ConsoleView();

        // Load XML data into GameBoard
        loader.loadGameData(game);

        view.printMessage("Enter number of players (2–8): ");
        int numPlayers = -1;
        while (numPlayers < 2 || numPlayers > 8) {
            try {
                numPlayers = Integer.parseInt(view.getActionChoice());
                if (numPlayers < 2 || numPlayers > 8) {
                    view.printMessage("Please enter a number between 2 and 8.");
                }
            } catch (Exception e) {
                view.printMessage("Invalid input. Please enter a number between 2 and 8.");
            }
        }

        game.setupGame(numPlayers);

        runGame(game, view);

        view.printMessage("\nGame Over!");
        game.calculateScores();
        view.printWinner(game.players);
    }

    private static void runGame(GameBoard game, GameView view) {
        while (!game.isGameOver()) {
            Player current = game.getCurrentPlayer();
            view.printCurrentDayAndTurn(game.getCurrentDay(), current);
            view.printPlayerTurnStart(current);
            view.printPlayerStats(current);

            boolean turnOver = false;
            while (!turnOver) {
                view.printMessage("\nChoose action:");
                view.printMessage("1. Move");
                view.printMessage("2. Take Role");
                view.printMessage("3. Act");
                view.printMessage("4. Rehearse");
                view.printMessage("5. Upgrade Rank");
                view.printMessage("6. End Turn");

                int choice = -1;
                try {
                    choice = Integer.parseInt(view.getActionChoice());
                } catch (Exception e) {
                    view.printMessage("Invalid input. Please enter a number.");
                    continue;
                }

                switch (choice) {
                    case 1:
                        handleMove(game, current, view);
                        break;
                    case 2:
                        handleTakeRole(current, view);
                        view.printCurrentDayAndTurn(game.getCurrentDay(), current);
                        view.printPlayerTurnStart(current);
                        view.printPlayerStats(current);
                        break;
                    case 3:
                        if (current.getRole() == null) {
                            view.printMessage("You must take a role before acting.");
                        } else {
                            handleAct(current, view);
                        }
                        break;
                    case 4:
                        if (current.getRole() == null) {
                            view.printMessage("You must take a role before rehearsing.");
                        } else {
                            handleRehearse(current, view);
                        }
                        break;
                    case 5:
                        handleUpgradeRank(current, view);
                        view.printCurrentDayAndTurn(game.getCurrentDay(), current);
                        view.printPlayerTurnStart(current);
                        view.printPlayerStats(current);
                        break;
                    case 6:
                        turnOver = true;
                        break;
                    default:
                        view.printMessage("Invalid choice.");
                }
            }
            game.nextTurn();
        }
    
    }

    private static void handleMove(GameBoard game, Player player, GameView view) {
        BoardSpace current = player.getCurrentSpace();
        String[] neighbors = current.getNeighbors();
        view.printMoveOptions(neighbors);
        int choice = view.getMoveChoice();
        if (choice >= 0 && choice < neighbors.length) {
            BoardSpace destination = game.getBoardSpaceByName(neighbors[choice]);
            game.movePlayer(player, destination);
            view.printMessage("Moved to " + neighbors[choice]);
            // Print scene and roles if on a set
            if (destination.isSet()) {
                Scene scene = destination.getScene();
                if (scene != null) {
                    view.printMessage("Scene: " + scene.getSceneName() + " (Budget: " + scene.getBudget() + ")");
                    Role[] roles = scene.roles;
                    view.printMessage("Available roles on this scene:");
                    for (int i = 0; i < roles.length; i++) {
                        view.printMessage((i+1) + ". " + roles[i].getName() + " (Rank: " + roles[i].getRank() + ")");
                    }
                }
            }
        } else {
            view.printMessage("Invalid move.");
        }
    }
        // Handle taking a role
        private static void handleTakeRole(Player player, GameView view) {
            BoardSpace current = player.getCurrentSpace();
            if (!current.isSet()) {
                view.printMessage("You must be on a set to take a role.");
                return;
            }
            Scene scene = current.getScene();
            if (scene == null) {
                view.printMessage("No scene available on this set.");
                return;
            }
            Role[] roles = scene.roles;
            view.printMessage("Available roles:");
            for (int i = 0; i < roles.length; i++) {
                view.printMessage((i+1) + ". " + roles[i].getName() + " (Rank: " + roles[i].getRank() + ")");
            }
            view.printMessage("Enter the number of the role you want to take:");
            int choice = -1;
            try {
                choice = Integer.parseInt(view.getActionChoice()) - 1;
            } catch (Exception e) {
                view.printMessage("Invalid input. Please enter a number.");
                return;
            }
            if (choice >= 0 && choice < roles.length) {
                boolean taken = player.takeRole(scene, roles[choice]);
                if (taken) {
                    view.printMessage("You took the role: " + roles[choice].getName());
                } else {
                    view.printMessage("Could not take the role. Check your rank or if you already have a role.");
                }
            } else {
                view.printMessage("Invalid role choice.");
            }

    }
    // Upgrade costs: [rank] -> {dollars, credits}
    private static final int[][] UPGRADE_COSTS = {
        {0, 0},   // rank 0 (unused)
        {0, 0},   // rank 1 (unused)
        {4, 5},   // rank 2: 4 dollars or 5 credits
        {10, 10}, // rank 3: 10 dollars or 10 credits
        {18, 15}, // rank 4: 18 dollars or 15 credits
        {28, 20}, // rank 5: 28 dollars or 20 credits
        {40, 25}  // rank 6: 40 dollars or 25 credits
    };

    private static void handleUpgradeRank(Player player, GameView view) {
        BoardSpace castingOffice = player.getCurrentSpace();
        if (!castingOffice.isCastingOffice()) {
            view.printMessage("You must be in the Casting Office to upgrade your rank.");
            return;
        }
        view.printUpgradeOptions(player);
        int choice = view.getUpgradeChoice();
        if (choice > player.getRank() && choice <= 6) {
            int dollarCost = UPGRADE_COSTS[choice][0];
            int creditCost = UPGRADE_COSTS[choice][1];
            
            // Ask if they want to pay with dollars or credits
            view.printMessage("Pay with: 1. Dollars ($" + dollarCost + ")\n or 2. Credits (" + creditCost + ")");
            int payChoice = -1;
            try {
                payChoice = Integer.parseInt(view.getActionChoice());
            } catch (Exception e) {
                view.printMessage("Invalid input.");
                return;
            }
            
            boolean useDollars = (payChoice == 1);
            boolean useCredits = (payChoice == 2);
            int cost = useDollars ? dollarCost : creditCost;
            
            boolean upgraded = player.upgradeRank(useDollars, useCredits, choice, cost);
            if (upgraded) {
                view.printMessage("Upgraded to rank " + choice + "!");
            } else {
                view.printMessage("Not enough dollars or credits to upgrade.");
            }
        } else {
            view.printMessage("Invalid rank choice.");
        }
    }

    private static void handleAct(Player player, GameView view) {
        if (player.getRole() == null) {
            view.printMessage("You do not have a role.");
            return;
        }
        Scene scene = player.getCurrentSpace().getScene();
        int dice = (int)(Math.random() * 6) + 1;
        boolean success = scene.act(player.getRole(), dice);
        view.printMessage("Rolled: " + dice);
        if (success) {
            view.printMessage("Acting successful!");
        } else {
            view.printMessage("Acting failed.");
        }
        if (scene.isWrapped()) {
            view.printMessage("Scene wrapped!");
            player.getCurrentSpace().removeScene();
        }
    }

    private static void handleRehearse(Player player, GameView view) {
        if (player.getRole() == null) {
            view.printMessage("You do not have a role.");
            return;
        }
        player.getRole().rehearse();
        view.printMessage("Rehearsed. Practice shots: " + player.getRole().getPracticeShots());
    }
}
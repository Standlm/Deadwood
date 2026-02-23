
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
                view.printMessage("7. Print Scenes Left");

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
                            checkAndEndDay(game, view);
                            turnOver = true;
                        }
                        break;
                    case 4:
                        if (current.getRole() == null) {
                            view.printMessage("You must take a role before rehearsing.");
                        } else {
                            handleRehearse(current, view);
                            turnOver = true;
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
                    case 7: // Debug option to print scenes left
                        view.printScenesLeft(game.boardSpaces);
                        break;
                    case 8: // print player stats
                        view.printPlayerStats(current);
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
                    // Sort and display on-card roles
                    Role[] onCardRoles = scene.roles.clone();
                    java.util.Arrays.sort(onCardRoles, (a, b) -> a.getRank() - b.getRank());
                    view.printMessage("Available on-card roles:");
                    for (int i = 0; i < onCardRoles.length; i++) {
                        view.printMessage((i+1) + ". " + onCardRoles[i].getName() + " (Rank: " + onCardRoles[i].getRank() + ")");
                    }
                    // Sort and display off-card roles
                    Role[] offcardRoles = destination.getRoles().clone();
                    java.util.Arrays.sort(offcardRoles, (a, b) -> a.getRank() - b.getRank());
                    view.printMessage("Available off-card roles:");
                    for (int i = 0; i < offcardRoles.length; i++) {
                        view.printMessage((i+1 + onCardRoles.length) + ". " + offcardRoles[i].getName() + " (Rank: " + offcardRoles[i].getRank() + ")");
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
            // Sort on-card roles by rank (ascending)
            Role[] roles = scene.roles.clone();
            java.util.Arrays.sort(roles, (a, b) -> a.getRank() - b.getRank());
            view.printMessage("Available on-card roles:");
            for (int i = 0; i < roles.length; i++) {
                view.printMessage((i+1) + ". " + roles[i].getName() + " (Rank: " + roles[i].getRank() + ")");
            }
            // Sort off-card roles by rank (ascending)
            Role[] offcardRoles = current.getRoles().clone();
            java.util.Arrays.sort(offcardRoles, (a, b) -> a.getRank() - b.getRank());
            view.printMessage("Available off-card roles:");
            for (int i = 0; i < offcardRoles.length; i++) {
                view.printMessage((roles.length + i + 1) + ". " + offcardRoles[i].getName() + " (Rank: " + offcardRoles[i].getRank() + ")");
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
                // different paths if we take an off card role
            } else if (choice >= roles.length && choice < offcardRoles.length+roles.length) {
                boolean taken = player.takeRole(current, offcardRoles[choice-roles.length]);
                if (taken) {
                    view.printMessage("You took the role: " + offcardRoles[choice-roles.length].getName());
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
        if (scene == null) {
            view.printMessage("The scene has already wrapped. Your role has ended.");
            player.resetRole();
            return;
        }
        Role role = player.getRole();
        boolean isOnCard = role.isOnCard();
        int dice = (int)(Math.random() * 6) + 1;
        boolean success = scene.act(role, dice);
        view.printMessage("Rolled: " + dice);
        if (success) {
            view.printMessage("Acting successful!");
            if (isOnCard) {
                // On-card role: 2 credits on success
                player.addCredits(2);
                view.printMessage("You earned 2 credits!");
            } else {
                // Off-card role: 1 credit + 1 dollar on success
                player.addCredits(1);
                player.addDollars(1);
                view.printMessage("You earned 1 credit and 1 dollar!");
            }
        } else {
            view.printMessage("Acting failed.");
            if (!isOnCard) {
                // Off-card role: 1 dollar on failure
                player.addDollars(1);
                view.printMessage("You earned 1 dollar.");
            }
        }
        if (scene.isWrapped()) {
            view.printMessage("Scene wrapped!");
            // Only on-card players get wrap bonuses (handled elsewhere if needed)
            if (isOnCard) {
                // On-card players could get bonus payout here
            }
            // Off-card roles do NOT get rewards for ending the scene
            player.resetRole();
            player.getCurrentSpace().removeScene();
        }
    }

    // Check if only 1 scene left and end day if so (call from main game loop)
    private static boolean checkAndEndDay(GameBoard game, GameView view) {
        if (game.shouldEndDay()) {
            view.printMessage("\nOnly 1 scene left! Day " + game.getCurrentDay() + " is ending...");
            game.startDay();
            view.printMessage("Day " + game.getCurrentDay() + " begins!");
            return true;
        }
        return false;
    }

    private static void handleRehearse(Player player, GameView view) {
        if (player.getRole() == null) {
            view.printMessage("You do not have a role.");
            return;
        }
        Scene scene = player.getCurrentSpace().getScene();
        if (scene == null) {
            view.printMessage("The scene has already wrapped. Your role has ended.");
            player.resetRole();
            return;
        }
        player.getRole().rehearse();
        view.printMessage("Rehearsed. Practice shots: " + player.getRole().getPracticeShots());
    }
}
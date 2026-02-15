
import java.util.ArrayList;
    // code for starting the day
import java.util.Collections;
import java.util.Random;

    public class GameBoard {
        public int day = 1;
        public ArrayList<Player> players = new ArrayList<>();
        public ArrayList<Scene> scenes = new ArrayList<>();
        public ArrayList<BoardSpace> boardSpaces = new ArrayList<>();
        public BoardSpace trailerSpace;
        public ArrayList<Scene> sceneDeck = new ArrayList<>();
        public int currentPlayerIndex = 0;

        // Setup the game board, players, and scenes
        void setupGame(int numPlayers) {
            // Setup board spaces (including trailer)
            // ...existing code...
            // Find and set trailerSpace
            for (BoardSpace space : boardSpaces) {
                if (space.getId().equalsIgnoreCase("trailer")) {
                    trailerSpace = space;
                    break;
                }
            }
            // Setup players
            for (int i = 0; i < numPlayers; i++) {
                int rank = 1;
                int credits = 0;
                if (numPlayers == 5) credits = 2;
                if (numPlayers == 6) credits = 4;
                if (numPlayers == 7 || numPlayers == 8) rank = 2;
                Player p = new Player("Player" + (i+1), rank, 0, credits, 0);
                p.move(trailerSpace);
                players.add(p);
            }
            // Shuffle and deal scenes
            Collections.shuffle(sceneDeck, new Random());
            for (BoardSpace space : boardSpaces) {
                if (space.isSet()) {
                    Scene scene = sceneDeck.remove(0);
                    space.setScene(scene);
                }
            }
            day = 1;
        }

        // Start a new day: reset players to trailer, redeal scenes, reset shots
        void startDay() {
            for (Player p : players) {
                p.move(trailerSpace);
                p.resetRole();
            }
            // Remove last scene, redeal 10 new scenes
            for (BoardSpace space : boardSpaces) {
                if (space.isSet()) {
                    space.removeScene();
                    if (!sceneDeck.isEmpty()) {
                        Scene scene = sceneDeck.remove(0);
                        space.setScene(scene);
                    }
                    space.resetShots();
                }
            }
            day++;
        }

        // End the day: move all players to trailer, clear scenes, reset shots
        public void endDay() {
            for (Player p : players) {
                p.move(trailerSpace);
                // Define Reset role in Player
                p.resetRole();
            }
            for (BoardSpace space : boardSpaces) {
                if (space.isSet()) {
                    space.removeScene();
                    space.resetShots();
                }
            }
            day++;
        }

        // Advance to next player's turn
        void nextTurn() {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        }

        // Check if the game is over
        public boolean checkGameEnd() {
            if ((day > 4 && players.size() > 3) || (day > 3 && players.size() < 4)) {
                return true;
            }
            return false;
        }

        // Calculate final scores
        public void calculateScores() {
            for (Player p : players) {
                int score = p.getDollars() + p.getCredits() + 5 * p.getRank();
                System.out.println(p.getName() + " score: " + score);
            }
        }
    }


 

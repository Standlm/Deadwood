import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class GuiView extends JFrame implements GameView {

    // Main board panel with background image
    private BoardPanel boardPanel;
    
    // Side panel for player info and controls
    private JPanel sidePanel;
    private JTextArea messageArea;
    private JScrollPane messageScroll;
    
    // Input handling - use blocking queue for synchronous input
    private BlockingQueue<String> inputQueue = new LinkedBlockingQueue<>();
    
    // Player colors for dice
    private static final String[] PLAYER_COLORS = {"b", "c", "g", "o", "p", "r", "v", "w", "y"};
    
    public GuiView() {
        setTitle("Deadwood");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Create the board panel with background
        boardPanel = new BoardPanel();
        add(boardPanel, BorderLayout.CENTER);
        
        // Create side panel for messages and controls
        sidePanel = new JPanel();
        sidePanel.setLayout(new BorderLayout());
        sidePanel.setPreferredSize(new Dimension(300, 600));
        
        // Message area
        messageArea = new JTextArea();
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageScroll = new JScrollPane(messageArea);
        messageScroll.setPreferredSize(new Dimension(300, 400));
        sidePanel.add(messageScroll, BorderLayout.CENTER);
        
        // TODO: Add buttons panel at bottom of sidePanel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(0, 2, 5, 5));
        // Add action buttons here
        sidePanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(sidePanel, BorderLayout.EAST);
        
        pack();
        setSize(1500, 900);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    

    // GameView Interface Methods
    // =============================
    
    @Override
    public void printMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            messageArea.append(message + "\n");
            messageArea.setCaretPosition(messageArea.getDocument().getLength());
        });
    }

    @Override
    public void printCurrentDayAndTurn(int day, Player currentPlayer) {
        printMessage("\n=================================");
        printMessage("Day " + day + " | " + currentPlayer.getName() + "'s Turn");
        printMessage("=================================");
        // TODO: Update GUI to highlight current player
    }

    @Override
    public void printPlayerTurnStart(Player player) {
        printMessage("Player: " + player.getName());
        // TODO: Update player display
    }

    @Override
    public void printPlayerStats(Player player) {
        printMessage("Rank: " + player.getRank());
        printMessage("Dollars: " + player.getDollars());
        printMessage("Credits: " + player.getCredits());
        printMessage("Location: " + player.getCurrentSpace().getId());
        // TODO: Update stats panel
    }

    @Override
    public void printMoveOptions(String[] neighbors) {
        // TODO: Show move options dialog or highlight clickable locations
        printMessage("Choose where to move:");
        for (int i = 0; i < neighbors.length; i++) {
            printMessage((i + 1) + ". " + neighbors[i]);
        }
    }

    @Override
    public int getMoveChoice() {
        // TODO: Replace with GUI selection (clicking on board or dropdown)
        String input = getInput("Enter move choice:");
        try {
            return Integer.parseInt(input) - 1;
        } catch (Exception e) {
            return -1;
        }
    }

    @Override
    public void printUpgradeOptions(Player player) {
        int currentRank = player.getRank();
        printMessage("Current rank: " + currentRank);
        printMessage("Available upgrades:");
        int[][] costs = {
            {0, 0}, {0, 0}, {4, 5}, {10, 10}, {18, 15}, {28, 20}, {40, 25}
        };
        for (int rank = currentRank + 1; rank <= 6; rank++) {
            printMessage("Rank " + rank + ": $" + costs[rank][0] + " or " + costs[rank][1] + " credits");
        }
        // TODO: Show upgrade dialog
    }

    @Override
    public int getUpgradeChoice() {
        // TODO: Replace with GUI dialog
        String input = getInput("Enter rank to upgrade to (or 0 to cancel):");
        try {
            return Integer.parseInt(input);
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public String getActionChoice() {
        // TODO: Replace with button clicks
        return getInput("");
    }

    @Override
    public void printScenesLeft(List<BoardSpace> boardSpaces) {
        printMessage("\nScenes left on the board:");
        for (BoardSpace space : boardSpaces) {
            if (space.isSet() && space.hasScene()) {
                printMessage("- " + space.getScene().getName() + " at " + space.getId());
            }
        }
    }

    @Override
    public void printWinner(List<Player> players) {
        Player winner = null;
        int highestScore = -1;

        StringBuilder sb = new StringBuilder();
        for (Player p : players) {
            int score = p.getDollars() + p.getCredits() + (5 * p.getRank());
            sb.append(p.getName() + " final score: " + score + "\n");
            if (score > highestScore) {
                highestScore = score;
                winner = p;
            }
        }
        
        if (winner != null) {
            sb.append("\nWinner: " + winner.getName());
        }
        
        // Show winner dialog
        JOptionPane.showMessageDialog(this, sb.toString(), "Game Over!", JOptionPane.INFORMATION_MESSAGE);
        printMessage(sb.toString());
    }
    
    
    // Helper Methods
   
    
    private String getInput(String prompt) {
        if (!prompt.isEmpty()) {
            printMessage(prompt);
        }
        try {
            // TODO: Replace with proper GUI input (buttons/dialogs)
            String result = JOptionPane.showInputDialog(this, prompt);
            return result != null ? result : "";
        } catch (Exception e) {
            return "";
        }
    }
    

    // Inner Board Panel
    
    private class BoardPanel extends JPanel {
        private Image boardImage;
        
        public BoardPanel() {
            // Load the board background image
            boardImage = new ImageIcon("gui/board.jpg").getImage();
            setPreferredSize(new Dimension(1200, 900));
            
            // TODO: Add mouse listener for clicking on locations
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int x = e.getX();
                    int y = e.getY();
                    // Determine which location was clicked based on coordinates
                    System.out.println("Clicked at: " + x + ", " + y);
                }
            });
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            // Draw board background
            if (boardImage != null) {
                g.drawImage(boardImage, 0, 0, getWidth(), getHeight(), this);
            }
            
            //  Draw players as dice on their locations
            //  Draw scene cards on sets
            // T Draw shot counters
        }
        
        // TODO: Add methods to draw players, cards, shots
        public void drawPlayer(Graphics g, Player player, int x, int y) {
            // Load dice image based on player color and rank
            // String diceFile = "gui/Dice/" + PLAYER_COLORS[playerIndex] + player.getRank() + ".png";
            // Draw dice at x, y
        }
        
        public void drawCard(Graphics g, Scene scene, int x, int y) {
            // Load card image: "gui/Card/XX.png" where XX is card number
            // Draw at set location
        }
        
        public void drawShot(Graphics g, int x, int y, boolean taken) {
            // Draw shot.png if not taken
        }
    }
    
    
    // Update Methods for GUI
    
    public void updateBoard(GameBoard game) {
        // TODO: Refresh all player positions, cards, shots
        boardPanel.repaint();
    }
    
    public void updatePlayerPosition(Player player, int x, int y) {
        // TODO: Move player dice to new location
        boardPanel.repaint();
    }
}

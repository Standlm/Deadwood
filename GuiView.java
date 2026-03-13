import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import java.util.List;
import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class GuiView extends JFrame implements GameView {

    // Main board panel with background image
    private BoardPanel boardPanel;
    
    // Side panel for player info and controls
    private JComboBox<String> playerCountCombo;
    private JTextArea messageArea;
    private JPanel choicePanel;
    private JLabel choicePromptLabel;
    private JComboBox<String> choiceCombo;
    private JPanel buttonPanel;
    private JPanel playerInfoPanel;
    private JLabel dayLabel;
    private JLabel currentPlayerLabel;
    private JLabel rankLabel;
    private JLabel dollarsLabel;
    private JLabel creditsLabel;
    private JLabel locationLabel;
    
    // Input handling locks/state so the background game loop can wait for GUI choices.
    private String lastAction = "";
    private final Object actionLock = new Object();
    private final Object setupLock = new Object();
    private boolean setupReady = false;
    private int selectedPlayerCount = 4;
    private final Object choiceLock = new Object();
    private boolean choiceReady = false;
    private int selectedChoiceIndex = -1;
    private boolean awaitingBoardMove = false;
    private final Map<String, Integer> moveOptionIndexByLocation = new HashMap<>();
    
    // Player colors for dice (matching the file naming convention)
    private static final String[] PLAYER_COLORS = {"b", "c", "g", "o", "p", "r", "v", "w", "y"};
    
    // Location coordinates on the board (from board.xml)
    private static final Map<String, Rectangle> LOCATION_COORDS = new HashMap<>();
    private static final Map<String, List<Rectangle>> SHOT_COORDS = new HashMap<>();
    
    // Game state references
    private GameBoard gameBoard;
    private Map<String, Image> cardImages = new HashMap<>();
    private Map<String, Image> diceImages = new HashMap<>();
    private Image shotImage;
    private Image cardBackImage;
    private final Map<String, Rectangle> offCardRoleCoords = new HashMap<>();
    private final Map<String, Rectangle> onCardRoleCoords = new HashMap<>();
    private final SceneCardCatalog sceneCardCatalog = new SceneCardCatalog("xml/cards.xml");
    private final Set<String> revealedSets = new HashSet<>();
    private int lastRenderedDay = -1;
    
    static {
        // Set locations from board.xml (x, y, width, height for card placement)
        LOCATION_COORDS.put("Train Station", new Rectangle(21, 69, 205, 115));
        LOCATION_COORDS.put("Secret Hideout", new Rectangle(27, 732, 205, 115));
        LOCATION_COORDS.put("Church", new Rectangle(623, 734, 205, 115));
        LOCATION_COORDS.put("Hotel", new Rectangle(969, 740, 205, 115));
        LOCATION_COORDS.put("Main Street", new Rectangle(969, 28, 205, 115));
        LOCATION_COORDS.put("Jail", new Rectangle(281, 27, 205, 115));
        LOCATION_COORDS.put("General Store", new Rectangle(370, 282, 205, 115));
        LOCATION_COORDS.put("Ranch", new Rectangle(252, 478, 205, 115));
        LOCATION_COORDS.put("Bank", new Rectangle(623, 475, 205, 115));
        LOCATION_COORDS.put("Saloon", new Rectangle(632, 280, 205, 115));
        LOCATION_COORDS.put("trailer", new Rectangle(991, 248, 201, 194));
        LOCATION_COORDS.put("Casting Office", new Rectangle(9, 459, 209, 208));
        
        // Shot counter positions for each set
        SHOT_COORDS.put("Train Station", Arrays.asList(
            new Rectangle(36, 11, 47, 47),
            new Rectangle(89, 11, 47, 47),
            new Rectangle(141, 11, 47, 47)
        ));
        SHOT_COORDS.put("Secret Hideout", Arrays.asList(
            new Rectangle(244, 764, 47, 47),
            new Rectangle(299, 764, 47, 47),
            new Rectangle(354, 764, 47, 47)
        ));
        SHOT_COORDS.put("Church", Arrays.asList(
            new Rectangle(623, 675, 47, 47),
            new Rectangle(682, 675, 47, 47)
        ));
        SHOT_COORDS.put("Hotel", Arrays.asList(
            new Rectangle(1005, 683, 47, 47),
            new Rectangle(1058, 683, 47, 47),
            new Rectangle(1111, 683, 47, 47)
        ));
        SHOT_COORDS.put("Main Street", Arrays.asList(
            new Rectangle(912, 23, 47, 47),
            new Rectangle(858, 23, 47, 47),
            new Rectangle(804, 23, 47, 47)
        ));
        SHOT_COORDS.put("Jail", Arrays.asList(
            new Rectangle(442, 156, 47, 47)
        ));
        SHOT_COORDS.put("General Store", Arrays.asList(
            new Rectangle(313, 277, 47, 47),
            new Rectangle(313, 330, 47, 47)
        ));
        SHOT_COORDS.put("Ranch", Arrays.asList(
            new Rectangle(472, 473, 47, 47),
            new Rectangle(525, 473, 47, 47)
        ));
        SHOT_COORDS.put("Bank", Arrays.asList(
            new Rectangle(840, 549, 47, 47)
        ));
        SHOT_COORDS.put("Saloon", Arrays.asList(
            new Rectangle(626, 216, 47, 47),
            new Rectangle(679, 216, 47, 47)
        ));
    }
    
    public GuiView() {
        setTitle("Deadwood");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        loadImages();
        
        // Create the board panel with background
        boardPanel = new BoardPanel();
        JScrollPane boardScroll = new JScrollPane(boardPanel);
        add(boardScroll, BorderLayout.CENTER);
        
        // Create side panel for messages and controls
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BorderLayout());
        sidePanel.setPreferredSize(new Dimension(320, 900));
        sidePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        // Setup panel shown at launch: choose player count and start the game.
        JPanel setupPanel = new JPanel();
        setupPanel.setLayout(new BoxLayout(setupPanel, BoxLayout.Y_AXIS));
        setupPanel.setBorder(BorderFactory.createTitledBorder("Setup"));
        setupPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        // Row for player count selection.
        JPanel setupRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        setupRow.add(new JLabel("Players:"));
        playerCountCombo = new JComboBox<>(new String[]{"2", "3", "4", "5", "6", "7", "8"});
        playerCountCombo.setSelectedItem("4");
        setupRow.add(playerCountCombo);
        setupRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        setupPanel.add(setupRow);
        setupPanel.add(Box.createVerticalStrut(8));

        // Starts game initialization after player count is chosen.
        JButton startGameButton = new JButton("Start Game");
        startGameButton.setVisible(true);
        startGameButton.setFont(new Font("Arial", Font.BOLD, 14));
        startGameButton.setPreferredSize(new Dimension(170, 36));
        startGameButton.setMaximumSize(new Dimension(220, 36));
        startGameButton.setBackground(Color.BLACK);
        startGameButton.setForeground(Color.WHITE);
        startGameButton.setOpaque(true);
        startGameButton.setBorderPainted(false);
        startGameButton.setFocusPainted(false);
        startGameButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        startGameButton.addActionListener(e -> {
            synchronized (setupLock) {
                selectedPlayerCount = Integer.parseInt((String) playerCountCombo.getSelectedItem());
                setupReady = true;
                setupLock.notifyAll();
            }
            // Hide setup once the game starts; reveal active game controls.
            setupPanel.setVisible(false);
            playerInfoPanel.setVisible(true);
            buttonPanel.setVisible(true);
            sidePanel.revalidate();
            sidePanel.repaint();
        });
        setupPanel.add(startGameButton);
        topPanel.add(setupPanel);
        
        // Player info panel: hidden until Start Game is clicked.
        playerInfoPanel = new JPanel();
        playerInfoPanel.setLayout(new GridLayout(6, 1, 5, 5));
        playerInfoPanel.setBorder(BorderFactory.createTitledBorder("Current Player"));
        
        dayLabel = new JLabel("Day: 1");
        dayLabel.setFont(new Font("Arial", Font.BOLD, 16));
        currentPlayerLabel = new JLabel("Player: -");
        currentPlayerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        rankLabel = new JLabel("Rank: -");
        dollarsLabel = new JLabel("Dollars: $0");
        creditsLabel = new JLabel("Credits: 0");
        locationLabel = new JLabel("Location: -");
        
        playerInfoPanel.add(dayLabel);
        playerInfoPanel.add(currentPlayerLabel);
        playerInfoPanel.add(rankLabel);
        playerInfoPanel.add(dollarsLabel);
        playerInfoPanel.add(creditsLabel);
        playerInfoPanel.add(locationLabel);
        playerInfoPanel.setVisible(false);

        topPanel.add(playerInfoPanel);
        
        sidePanel.add(topPanel, BorderLayout.NORTH);
        
        // Message area in center
        messageArea = new JTextArea();
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane messageScroll = new JScrollPane(messageArea);
        messageScroll.setPreferredSize(new Dimension(300, 400));
        messageScroll.setBorder(BorderFactory.createTitledBorder("Game Log"));
        sidePanel.add(messageScroll, BorderLayout.CENTER);

        // Contextual selection panel used for move/upgrade/confirm prompts.
        choicePanel = new JPanel(new BorderLayout(5, 5));
        choicePanel.setBorder(BorderFactory.createTitledBorder("Selection"));
        choicePromptLabel = new JLabel("Choose an option");
        choiceCombo = new JComboBox<>();
        JButton confirmChoiceButton = new JButton("Confirm");
        confirmChoiceButton.addActionListener(e -> {
            synchronized (choiceLock) {
                selectedChoiceIndex = choiceCombo.getSelectedIndex();
                choiceReady = true;
                choiceLock.notifyAll();
            }
            choicePanel.setVisible(false);
        });
        choicePanel.add(choicePromptLabel, BorderLayout.NORTH);
        choicePanel.add(choiceCombo, BorderLayout.CENTER);
        choicePanel.add(confirmChoiceButton, BorderLayout.SOUTH);
        choicePanel.setVisible(false);
        
        // Main turn-action controls; hidden until setup is complete.
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(5, 2, 5, 5));
        buttonPanel.setBorder(BorderFactory.createTitledBorder("Actions"));
        
        String[] actions = {"1. Move", "2. Take Role", "3. Act", "4. Rehearse", 
                           "5. Upgrade", "6. End Turn", "7. Scenes", "8. Players", "9. Quit"};
        for (String action : actions) {
            JButton btn = new JButton(action);
            btn.setFont(new Font("Arial", Font.PLAIN, 11));
            final String actionNum = action.split("\\.")[0].trim();
            btn.addActionListener(e -> {
                synchronized(actionLock) {
                    lastAction = actionNum;
                    actionLock.notifyAll();
                }
            });
            buttonPanel.add(btn);
        }
        buttonPanel.setVisible(false);
        
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.add(choicePanel, BorderLayout.NORTH);
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);
        sidePanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(sidePanel, BorderLayout.EAST);
        
        setSize(1520, 950);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void loadImages() {
        try {
            // Load shot counter image
            File shotFile = new File("gui/shot.png");
            if (shotFile.exists()) {
                shotImage = ImageIO.read(shotFile);
            }
            
            // Load the card back image uploaded by the user
            File cardBackFile = new File("gui/Cardback.png");
            if (cardBackFile.exists()) {
                cardBackImage = ImageIO.read(cardBackFile);
            }
            
            // Pre-load dice images
            for (String color : PLAYER_COLORS) {
                for (int rank = 1; rank <= 6; rank++) {
                    String key = color + rank;
                    File diceFile = new File("gui/Dice/" + key + ".png");
                    if (diceFile.exists()) {
                        diceImages.put(key, ImageIO.read(diceFile));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading images: " + e.getMessage());
        }

        loadRoleCoordinates();
    }

    private void loadRoleCoordinates() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document boardDoc = builder.parse(new File("xml/board.xml"));
            boardDoc.getDocumentElement().normalize();
            NodeList setNodes = boardDoc.getElementsByTagName("set");
            for (int i = 0; i < setNodes.getLength(); i++) {
                Element setElem = (Element) setNodes.item(i);
                String setName = setElem.getAttribute("name");
                NodeList partNodes = setElem.getElementsByTagName("part");
                for (int j = 0; j < partNodes.getLength(); j++) {
                    Element partElem = (Element) partNodes.item(j);
                    String roleName = partElem.getAttribute("name");
                    NodeList areaNodes = partElem.getElementsByTagName("area");
                    if (areaNodes.getLength() > 0) {
                        Element areaElem = (Element) areaNodes.item(0);
                        int x = Integer.parseInt(areaElem.getAttribute("x"));
                        int y = Integer.parseInt(areaElem.getAttribute("y"));
                        int w = Integer.parseInt(areaElem.getAttribute("w"));
                        int h = Integer.parseInt(areaElem.getAttribute("h"));
                        offCardRoleCoords.put(setName + "|" + roleName, new Rectangle(x, y, w, h));
                    }
                }
            }

            Document cardsDoc = builder.parse(new File("xml/cards.xml"));
            cardsDoc.getDocumentElement().normalize();
            NodeList cardNodes = cardsDoc.getElementsByTagName("card");
            for (int i = 0; i < cardNodes.getLength(); i++) {
                Element cardElem = (Element) cardNodes.item(i);
                String sceneName = cardElem.getAttribute("name");
                NodeList partNodes = cardElem.getElementsByTagName("part");
                for (int j = 0; j < partNodes.getLength(); j++) {
                    Element partElem = (Element) partNodes.item(j);
                    String roleName = partElem.getAttribute("name");
                    NodeList areaNodes = partElem.getElementsByTagName("area");
                    if (areaNodes.getLength() > 0) {
                        Element areaElem = (Element) areaNodes.item(0);
                        int x = Integer.parseInt(areaElem.getAttribute("x"));
                        int y = Integer.parseInt(areaElem.getAttribute("y"));
                        int w = Integer.parseInt(areaElem.getAttribute("w"));
                        int h = Integer.parseInt(areaElem.getAttribute("h"));
                        onCardRoleCoords.put(sceneName + "|" + roleName, new Rectangle(x, y, w, h));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading role coordinates: " + e.getMessage());
        }
    }

    private Point getPlayerDrawPoint(Player player, int fallbackX, int fallbackY) {
        Role role = player.getRole();
        BoardSpace space = player.getCurrentSpace();

        if (role == null || space == null || !space.isSet()) {
            return new Point(fallbackX, fallbackY);
        }

        Rectangle roleRect = null;
        if (role.isOnCard()) {
            Scene scene = space.getScene();
            if (scene != null) {
                roleRect = onCardRoleCoords.get(scene.getSceneName() + "|" + role.getName());
                Rectangle setRect = LOCATION_COORDS.get(space.getId());
                if (roleRect != null && setRect != null) {
                    return new Point(setRect.x + roleRect.x + 3, setRect.y + roleRect.y + 3);
                }
            }
        } else {
            roleRect = offCardRoleCoords.get(space.getId() + "|" + role.getName());
            if (roleRect != null) {
                return new Point(roleRect.x + 3, roleRect.y + 3);
            }
        }

        return new Point(fallbackX, fallbackY);
    }
    
    private Image getCardImage(String cardNumber) {
        if (!cardImages.containsKey(cardNumber)) {
            try {
                File cardFile = new File("gui/Card/" + cardNumber + ".png");
                if (cardFile.exists()) {
                    cardImages.put(cardNumber, ImageIO.read(cardFile));
                }
            } catch (Exception e) {
                System.err.println("Error loading card " + cardNumber + ": " + e.getMessage());
            }
        }
        return cardImages.get(cardNumber);
    }
    
    // Set the game board reference for drawing
    public void setGameBoard(GameBoard game) {
        this.gameBoard = game;
        resetSceneReveals();
        boardPanel.repaint();
    }

    public void revealSet(String setName) {
        if (setName == null || setName.isEmpty()) return;
        revealedSets.add(setName);
        boardPanel.repaint();
    }

    public void resetSceneReveals() {
        revealedSets.clear();
    }

    public int waitForPlayerCount() {
        synchronized (setupLock) {
            while (!setupReady) {
                try {
                    setupLock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return -1;
                }
            }
            return selectedPlayerCount;
        }
    }

    public int getOptionChoice(String prompt, String[] options) {
        if (options == null || options.length == 0) return -1;

        synchronized (choiceLock) {
            choiceReady = false;
            selectedChoiceIndex = -1;
        }

        SwingUtilities.invokeLater(() -> {
            choicePromptLabel.setText(prompt);
            choiceCombo.removeAllItems();
            for (String option : options) {
                choiceCombo.addItem(option);
            }
            choiceCombo.setSelectedIndex(0);
            choicePanel.setVisible(true);
            choicePanel.revalidate();
            choicePanel.repaint();
        });

        synchronized (choiceLock) {
            while (!choiceReady) {
                try {
                    choiceLock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return -1;
                }
            }
            return selectedChoiceIndex;
        }
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
        if (lastRenderedDay != -1 && day != lastRenderedDay) {
            resetSceneReveals();
        }
        lastRenderedDay = day;

        SwingUtilities.invokeLater(() -> {
            dayLabel.setText("Day: " + day);
            currentPlayerLabel.setText("Player: " + currentPlayer.getName());
        });
        printMessage("\n=================================");
        printMessage("Day " + day + " | " + currentPlayer.getName() + "'s Turn");
        printMessage("=================================");
        boardPanel.repaint();
    }

    @Override
    public void printPlayerTurnStart(Player player) {
        SwingUtilities.invokeLater(() -> {
            currentPlayerLabel.setText("Player: " + player.getName());
        });
    }

    @Override
    public void printPlayerStats(Player player) {
        SwingUtilities.invokeLater(() -> {
            rankLabel.setText("Rank: " + player.getRank());
            dollarsLabel.setText("Dollars: $" + player.getDollars());
            creditsLabel.setText("Credits: " + player.getCredits());
            locationLabel.setText("Location: " + player.getCurrentSpace().getId());
        });
        boardPanel.repaint();
    }

    @Override
    public void printMoveOptions(String[] neighbors) {
        printMessage("Choose where to move:");
        for (int i = 0; i < neighbors.length; i++) {
            printMessage((i + 1) + ". " + neighbors[i]);
        }
    }

    @Override
    public int getMoveChoice() {
        String[] options = null;
        if (gameBoard != null) {
            Player current = gameBoard.getCurrentPlayer();
            if (current != null && current.getCurrentSpace() != null) {
                options = current.getCurrentSpace().getNeighbors();
            }
        }

        if (options == null || options.length == 0) return -1;

        synchronized (choiceLock) {
            moveOptionIndexByLocation.clear();
            for (int i = 0; i < options.length; i++) {
                moveOptionIndexByLocation.put(options[i].toLowerCase(Locale.ROOT), i);
            }
            awaitingBoardMove = true;
        }

        printMessage("Click a neighboring location on the board, or use the Selection panel.");
        int choice = getOptionChoice("Select destination or click board:", options);

        synchronized (choiceLock) {
            awaitingBoardMove = false;
            moveOptionIndexByLocation.clear();
        }

        return choice;
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
    }

    @Override
    public int getUpgradeChoice() {
        // Determine current player's rank to filter valid upgrade options
        Player current = gameBoard != null ? gameBoard.getCurrentPlayer() : null;
        int currentRank = current != null ? current.getRank() : 1;
        
        List<String> options = new ArrayList<>();
        List<Integer> rankTargets = new ArrayList<>();
        options.add("Cancel");
        rankTargets.add(0);
        int[][] costs = {{0, 0}, {0, 0}, {4, 5}, {10, 10}, {18, 15}, {28, 20}, {40, 25}};
        for (int rank = currentRank + 1; rank <= 6; rank++) {
            options.add("Rank " + rank + " ($" + costs[rank][0] + " / " + costs[rank][1] + " cr)");
            rankTargets.add(rank);
        }

        int idx = getOptionChoice("Select rank to upgrade to:", options.toArray(new String[0]));
        if (idx >= 0 && idx < rankTargets.size()) {
            return rankTargets.get(idx);
        }
        return 0;
    }

    @Override
    public String getActionChoice() {
        synchronized(actionLock) {
            lastAction = "";
            try {
                actionLock.wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return "";
            }
            return lastAction;
        }
    }

    @Override
    public void printScenesLeft(List<BoardSpace> boardSpaces) {
        printMessage("\nScenes left on the board:");
        int count = 0;
        for (BoardSpace space : boardSpaces) {
            if (space.isSet() && space.hasScene()) {
                printMessage("- " + space.getScene().getName() + " at " + space.getId());
                count++;
            }
        }
        printMessage("Total: " + count + " scenes remaining");
    }

    @Override
    public void printWinner(List<Player> players) {
        Player winner = null;
        int highestScore = -1;

        StringBuilder sb = new StringBuilder();
        sb.append("=== FINAL SCORES ===\n\n");
        
        for (Player p : players) {
            int score = p.getDollars() + p.getCredits() + (5 * p.getRank());
            sb.append(p.getName() + ": " + score + " points\n");
            sb.append("  ($" + p.getDollars() + " + " + p.getCredits() + " cr + " + (5 * p.getRank()) + " rank)\n");
            if (score > highestScore) {
                highestScore = score;
                winner = p;
            }
        }
        
        if (winner != null) {
            sb.append("\nWINNER: " + winner.getName() + " 🏆");
        }
        
        printMessage(sb.toString());
    }
    
    // Update Methods for GUI
    
    public void updateBoard() {
        boardPanel.repaint();
    }

    // Inner Board Panel
    private class BoardPanel extends JPanel {
        private Image boardImage;
        
        public BoardPanel() {
            // Load the board background image
            try {
                File boardFile = new File("gui/board.jpg");
                if (boardFile.exists()) {
                    boardImage = ImageIO.read(boardFile);
                }
            } catch (Exception e) {
                System.err.println("Error loading board image: " + e.getMessage());
            }
            setPreferredSize(new Dimension(1200, 900));
            setBackground(new Color(139, 90, 43)); // Brown fallback
            
            // Add mouse listener for clicking on locations 
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int x = e.getX();
                    int y = e.getY();
                    
                    // Check if clicked on a location
                    for (Map.Entry<String, Rectangle> entry : LOCATION_COORDS.entrySet()) {
                        Rectangle r = entry.getValue();
                        if (r.contains(x, y)) {
                            String clickedLocation = entry.getKey();
                            boolean consumedAsMove = false;
                            // If we're waiting for a move choice, check if this location is a valid move option
                            synchronized (choiceLock) {
                                if (awaitingBoardMove) {
                                    Integer moveIndex = moveOptionIndexByLocation.get(clickedLocation.toLowerCase(Locale.ROOT));
                                    if (moveIndex != null) {
                                        selectedChoiceIndex = moveIndex;
                                        choiceReady = true;
                                        awaitingBoardMove = false;
                                        moveOptionIndexByLocation.clear();
                                        choiceLock.notifyAll();
                                        consumedAsMove = true;
                                    }
                                }
                            }

                            if (consumedAsMove) {
                                printMessage("Selected move: " + clickedLocation);
                                choicePanel.setVisible(false);
                                choicePanel.revalidate();
                                choicePanel.repaint();
                            } else {
                                printMessage("Clicked on: " + clickedLocation);
                            }
                            break;
                        }
                    }
                }
            });
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Draw board background
            if (boardImage != null) {
                g2d.drawImage(boardImage, 0, 0, getWidth(), getHeight(), this);
            }
            
            if (gameBoard == null) return;
            
            // Draw scene cards on sets
            for (BoardSpace space : gameBoard.boardSpaces) {
                if (space.isSet() && space.hasScene()) {
                    Rectangle loc = LOCATION_COORDS.get(space.getId());
                    if (loc != null) {
                        Scene scene = space.getScene();
                        boolean revealed = revealedSets.contains(space.getId());
                        if (revealed) {
                            String cardNum = sceneCardCatalog.getCardNumberForScene(scene.getSceneName());
                            Image cardImg = getCardImage(cardNum);
                            if (cardImg != null) {
                                g2d.drawImage(cardImg, loc.x, loc.y, 205, 115, this);
                            } else {
                                g2d.setColor(new Color(200, 180, 140));
                                g2d.fillRect(loc.x, loc.y, 205, 115);
                                g2d.setColor(Color.BLACK);
                                g2d.drawRect(loc.x, loc.y, 205, 115);
                                g2d.setFont(new Font("Arial", Font.BOLD, 10));
                                g2d.drawString(scene.getSceneName(), loc.x + 5, loc.y + 60);
                                g2d.drawString("Budget: " + scene.getBudget(), loc.x + 5, loc.y + 75);
                            }
                        } else {
                            // Draw uploaded card back image; fall back to a plain rectangle if missing
                            if (cardBackImage != null) {
                                g2d.drawImage(cardBackImage, loc.x, loc.y, 205, 115, this);
                            } else {
                                g2d.setColor(new Color(120, 70, 30));
                                g2d.fillRect(loc.x, loc.y, 205, 115);
                                g2d.setColor(Color.BLACK);
                                g2d.drawRect(loc.x, loc.y, 205, 115);
                            }
                        }
                    }
                }
            }
            
            // Draw shot counters
            for (BoardSpace space : gameBoard.boardSpaces) {
                if (space.isSet()) {
                    List<Rectangle> shots = SHOT_COORDS.get(space.getId());
                    if (shots != null && shotImage != null) {
                        Scene scene = space.getScene();
                        int shotsRemaining = scene != null ? scene.getShotsLeft() : 0;
                        for (int i = 0; i < shots.size(); i++) {
                            Rectangle shotRect = shots.get(i);
                            if (i < shotsRemaining) {
                                g2d.drawImage(shotImage, shotRect.x, shotRect.y, 
                                    shotRect.width, shotRect.height, this);
                            }
                        }
                    }
                }
            }
            
            // Draw players as dice at their locations
            Map<String, List<Player>> playersByLocation = new HashMap<>();
            for (Player player : gameBoard.players) {
                BoardSpace space = player.getCurrentSpace();
                if (space != null) {
                    String locId = space.getId();
                    playersByLocation.computeIfAbsent(locId, k -> new ArrayList<>()).add(player);
                }
            }
            
            for (Map.Entry<String, List<Player>> entry : playersByLocation.entrySet()) {
                Rectangle loc = LOCATION_COORDS.get(entry.getKey());
                if (loc == null) continue;
                
                List<Player> playersHere = entry.getValue();
                int offsetX = 0;
                int offsetY = 0;
                
                for (int i = 0; i < playersHere.size(); i++) {
                    Player player = playersHere.get(i);
                    int playerIndex = gameBoard.players.indexOf(player);
                    String color = PLAYER_COLORS[playerIndex % PLAYER_COLORS.length];
                    String diceKey = color + player.getRank();
                    
                    Image diceImg = diceImages.get(diceKey);
                    int defaultX = loc.x + loc.width + 5 + offsetX;
                    int defaultY = loc.y + offsetY;
                    Point drawPoint = getPlayerDrawPoint(player, defaultX, defaultY);
                    int drawX = drawPoint.x;
                    int drawY = drawPoint.y;
                    
                    if (diceImg != null) {
                        g2d.drawImage(diceImg, drawX, drawY, 40, 40, this);
                    } else {
                        // Draw placeholder dice
                        g2d.setColor(getPlayerColor(playerIndex));
                        g2d.fillRect(drawX, drawY, 40, 40);
                        g2d.setColor(Color.WHITE);
                        g2d.setFont(new Font("Arial", Font.BOLD, 20));
                        g2d.drawString(String.valueOf(player.getRank()), drawX + 14, drawY + 28);
                    }
                    
                    // Draw player name below dice
                    g2d.setColor(Color.WHITE);
                    g2d.setFont(new Font("Arial", Font.PLAIN, 9));
                    g2d.drawString(player.getName(), drawX, drawY + 52);
                    
                    if (!player.hasRole()) {
                        offsetX += 45;
                        if (offsetX > 90) {
                            offsetX = 0;
                            offsetY += 55;
                        }
                    }
                }
            }
            
            // Draw current player indicator
            Player current = gameBoard.getCurrentPlayer();
            if (current != null) {
                int playerIndex = gameBoard.players.indexOf(current);
                g2d.setColor(getPlayerColor(playerIndex));
                g2d.setStroke(new BasicStroke(3));
                Rectangle loc = LOCATION_COORDS.get(current.getCurrentSpace().getId());
                if (loc != null) {
                    g2d.drawRect(loc.x - 3, loc.y - 3, loc.width + 6, loc.height + 6);
                }
            }
        }
        
        private Color getPlayerColor(int index) {
            Color[] colors = {
                Color.BLUE, Color.CYAN, Color.GREEN, Color.ORANGE,
                Color.PINK, Color.RED, new Color(148, 0, 211), // Violet
                Color.WHITE, Color.YELLOW
            };
            return colors[index % colors.length];
        }
    }
}

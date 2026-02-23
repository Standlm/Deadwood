public class Player {

    private String name;
    private int rank;
    private int dollars;
    private int credits;

    private BoardSpace currentSpace;
    private Role currentRole;

    public Player(String name, int rank, int dollars, int credits, int rehearsalChips) {
        this.name = name;
        this.rank = rank;
        this.dollars = dollars;
        this.credits = credits;
        this.currentRole = null;
        this.currentSpace = null;
    }

    // -----------------------
    // Getters
    // -----------------------

    public String getName() {
        return name;
    }

    public int getRank() {
        return rank;
    }

    public int getDollars() {
        return dollars;
    }

    public int getCredits() {
        return credits;
    }

    public BoardSpace getCurrentSpace() {
        return currentSpace;
    }

    public Role getRole() {
        return currentRole;
    }

    public boolean hasRole() {
        return currentRole != null;
    }

    // -----------------------
    // Movement
    // -----------------------

    public void move(BoardSpace space) {

        // Cannot move if working on a role
        if (currentRole != null) {
            System.out.println("You cannot move while working a role.");
            return;
        }

        this.currentSpace = space;
    }

    // -----------------------
    // Taking a Role
    // -----------------------

    public boolean takeRole(Scene scene, Role role) {

        if (currentRole != null) {
            System.out.println("You already have a role.");
            return false;
        }

        if (role.isOccupied()) {
            System.out.println("This role is already taken.");
            return false;
        }

        if (rank < role.getRank()) {
            System.out.println("Your rank is too low for this role. Role:" + role.getName());
            return false;
        }

        this.currentRole = role;
        role.setOccupied(true);
        return true;
    }
    //two different take roles one for the role on a space one for the role on a scene
    public boolean takeRole(BoardSpace space, Role role) {

        if (currentRole != null) {
            System.out.println("You already have a role.");
            return false;
        }

        if (role.isOccupied()) {
            System.out.println("This role is already taken.");
            return false;
        }

        if (rank < role.getRank()) {
            System.out.println("Your rank is too low for this role. Role:" + role.getName());
            return false;
        }

        this.currentRole = role;
        role.setOccupied(true);
        return true;
    }

    // -----------------------
    // Reset Role (End of Scene)
    // -----------------------

    public void resetRole() {
        if (currentRole != null) {
            currentRole.setOccupied(false);
            currentRole.reset();
        }
        currentRole = null;
    }

    // -----------------------
    // Money Handling
    // -----------------------

    public void addDollars(int amount) {
        dollars += amount;
    }

    public void addCredits(int amount) {
        credits += amount;
    }

    public void removeDollars(int amount) {
        dollars -= amount;
    }

    public void removeCredits(int amount) {
        credits -= amount;
    }

    // -----------------------
    // Upgrade Rank
    // -----------------------

    public boolean upgradeRank(boolean useDollars, boolean useCredits, int newRank, int cost) {

        if (newRank <= rank) {
            System.out.println("You already have this rank or higher.");
            return false;
        }

        if (useDollars) {
            if (dollars >= cost) {
                dollars -= cost;
                rank = newRank;
                return true;
            }
        }

        if (useCredits) {
            if (credits >= cost) {
                credits -= cost;
                rank = newRank;
                return true;
            }
        }

        System.out.println("Not enough currency to upgrade.");
        return false;
    }
}
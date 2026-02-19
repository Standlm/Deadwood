 
public class Role {
    private String type;
    private int rank;
    private String name;
    private int practiceShots;

    // roles get sent in Name, rank, type, praticeShots
    public Role( String name, int rank, String type){
        this.name = name;
        this.rank = rank;
        this.type = type;
        this.practiceShots = 0;
    }

    public static void main(String[] args) {
        String type = "On-Card";
        String name = "Marshal Canfield";
        int rank = 3;
        //Role example for test cases
        Role roleExample = new Role(name, rank, type);
        printRole(roleExample);   
    }

    // Getters
    public String getName(){
        return name;
    }
    public int getRank(){
        return rank;
    }
    public String getType(){
        return type;
    }
    public int getPracticeShots(){
        return practiceShots;
    }

    // local value changer for practiceShots
    public void rehearse(){
        practiceShots++;
    }


    public static void printRole(Role role ) {
        System.out.println("This is a " + role.type + " role");
        System.out.println("This role is playing as " + role.name);
        System.out.println("This role is level " + role.rank);
        System.out.println("Current practice shots: " + role.practiceShots);

    }

    public boolean isOnCard() {
    if (type.equals("Featured")) {
        return true;
    }
    if (type.equals("Extra")) {
        return false;
    }
    throw new IllegalArgumentException("Invalid role type");
}
}

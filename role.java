public class role {
    private String type;
    private int rank;
    private String name;
    // roles get sent in Name, rank, type
    public role( String name, int rank, String type){
        this.name = name;
        this.rank = rank;
        this.type = type;

    }





    public boolean isOnCard(){
    if ( this.type.equals("Featured") == true ){
            return true;
        } else if (this.type.equals("Extra") == true ){
            return false;
        }else{
            
        }
        System.err.println("Role is Neither On Card or Off Card\n");
         throw new  IllegalArgumentException("card Issue");

    }
}
public class PlayerPosition{

    private int i;
    private int j;

    /**
     * The Constructor for PlayerPosition
     *
     * @param i A position in Array of Player (Player[i][]), known as Row
     * @param j A position in Array of Player (Player[][j]), known as Player
     */
    public PlayerPosition(int i, int j){
        this.i = i;
        this.j = j;
    }

    /**
     * @return Get the Row that player is in
     */
    public int getI(){
        return i;
    }

    /**
     * Set the Row that player is in
     *
     * @param i New value for Row
     */
    public void setI(int i){
        this.i = i;
    }

    /**
     * @return Get the Position that player is in
     */
    public int getJ(){
        return j;
    }

    /**
     * Set the Position in a Row that player is in
     * @param j New value for Position in a Row
     */
    public void setJ(int j){
        this.j = j;
    }

    /**
     * Compare 2 PlayerPosition if one is lower than another by
     * Comparing Row
     *      - If row (i) is lower, then return true
     *      - If row is the same, then compare position (j)
     * @param otherPlayerPosition the comparision target
     * @return true If this position is smaller
     */
    public boolean isLowerThan(PlayerPosition otherPlayerPosition){
        if(this.i < otherPlayerPosition.i){
            return true;
        }else if(this.i == otherPlayerPosition.i){
            if(this.j < otherPlayerPosition.j){
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }

    @Override
    public String toString(){
        return "(" + getI() + ", " + getJ() + ")";
    }
}

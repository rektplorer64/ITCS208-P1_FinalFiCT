public class PlayerPosition{

    private int i;
    private int j;

    public PlayerPosition(int i, int j){
        this.i = i;
        this.j = j;
    }

    public int getI(){
        return i;
    }

    public void setI(int i){
        this.i = i;
    }

    public int getJ(){
        return j;
    }

    public void setJ(int j){
        this.j = j;
    }

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

}

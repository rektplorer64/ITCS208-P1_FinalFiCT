public class Player{

    public enum TargetingMode{attack_LowestHP, heal_teamLowestHP, revive_teamLowestHP}


    private static Arena arena;
    private Arena.Team team;

    private PlayerType type;    //Type of this player. Can be one of either Healer, Tank, Samurai, BlackMage, or Phoenix

    private double maxHP;        //Max HP of this player
    private double currentHP;    //Current HP of this player
    private double atk;            //Attack power of this player
    private int numSpecialTurns;            //Number of Special Turns of this player

    private int current_Turn_In_A_Row;            //Number of Special Turns of this player
    private int internalturn;

    private boolean isTaunting;     //Priority of this player to being attacked

    private boolean isCursed;       //Cursed Status of this player
    private Player curser;          //The player who given the Cursed Status to this player

    /**
     * Constructor of class Player, which initializes this player's type, maxHP, atk, numSpecialTurns,
     * as specified in the given table. It also reset the internal turn count of this player.
     *
     * @param _type
     */
    public Player(PlayerType _type, Arena.Team team, Arena arena){
        //INSERT YOUR CODE HERE
        this.arena = arena;
        this.type = _type;
        this.team = team;
        this.isTaunting = false;
        this.curser = null;
        this.isCursed = false;

        current_Turn_In_A_Row = 0;
        switch(_type){
            case Healer:{
                maxHP = 4790;
                currentHP = 4790;
                atk = 238;
                numSpecialTurns = 4;
                break;
            }
            case Tank:{
                maxHP = 5390;
                currentHP = 5390;
                atk = 255;
                numSpecialTurns = 4;
                break;
            }
            case Samurai:{
                maxHP = 4005;
                currentHP = 4005;
                atk = 368;
                numSpecialTurns = 3;
                break;
            }

            case BlackMage:{
                maxHP = 4175;
                currentHP = 4175;
                atk = 303;
                numSpecialTurns = 4;
                break;
            }

            case Phoenix:{
                maxHP = 4175;
                currentHP = 4175;
                atk = 209;
                numSpecialTurns = 8;
                break;
            }

            case Cherry:{
                maxHP = 3560;
                currentHP = 3560;
                atk = 198;
                numSpecialTurns = 4;
                break;
            }

        }
    }

    /**
     * Returns the current HP of this player
     *
     * @return
     */
    public double getCurrentHP(){
        return currentHP;
    }

    /**
     * Returns type of this player
     *
     * @return
     */
    public Player.PlayerType getType(){
        //INSERT YOUR CODE HERE
        return null;
    }

    /**
     * Returns max HP of this player.
     *
     * @return
     */
    public double getMaxHP(){
        //INSERT YOUR CODE HERE
        return maxHP;
    }

    /**
     * Returns whether this player is sleeping.
     *
     * @return
     */
    public boolean isSleeping(){
        //INSERT YOUR CODE HERE

        return false;
    }

    /**
     * Returns whether this player is being cursed.
     *
     * @return
     */
    public boolean isCursed(){
        //INSERT YOUR CODE HERE

        return isCursed;
    }

    /**
     * Returns whether this player is alive (i.e. current HP > 0).
     *
     * @return
     */
    public boolean isAlive(){
        return currentHP > 0;
    }

    /**
     * Returns whether this player is taunting the other team.
     *
     * @return
     */
    public boolean isTaunting(){
        //INSERT YOUR CODE HERE
        return isTaunting;
    }



    private static PlayerPosition findTargetablePlayers(TargetingMode targetingMode){
        //TODO: write a fucking code to select target properly!

        int i, j;
        int countFullHP = 0;
        Player[][] targetTeamPlayers;

        int MinPositionI = Arena.numRowPlayers, MinPositionJ = Arena.numRowPlayers;

        switch(targetingMode){
            case attack_LowestHP:{
                targetTeamPlayers = Arena.getOpponentTeamPlayers(this);
                double minValue;
                int countTaunt = 0;
                if(targetTeamPlayers != null){
                    minValue = targetTeamPlayers[0][0].currentHP / targetTeamPlayers[0][0].maxHP;
                }else{
                    return null;
                }

                PlayerPosition playerPosition[] = new PlayerPosition[Arena.MAX_EACH_TYPE];
                for(i = 0; i < Arena.NUMBER_OF_ROWS - 1; i++){
                    for(j = 0; j < Arena.numRowPlayers; j++){
                        if(targetTeamPlayers[i][j].isTaunting){
                            playerPosition[countTaunt] = new PlayerPosition(i, j);
                            countTaunt++;
                        }
                    }
                }

                int rowRange;
                if(countTaunt > 0){
                    rowRange = Arena.NUMBER_OF_ROWS - 1;
                }else{
                    rowRange = Arena.getFrontRow(targetTeamPlayers);
                }

                int countTauntingArray = 0;
                for(i = 0; i < rowRange; i++){
                    for(j = 0; j < Arena.numRowPlayers; j++){
                        if(countTaunt == 0){
                            if(targetTeamPlayers[i][j].currentHP / targetTeamPlayers[i][j].maxHP < minValue
                                    && targetTeamPlayers[i][j].isAlive()){
                                minValue = targetTeamPlayers[i][j].currentHP / targetTeamPlayers[i][j].maxHP;
                                if(targetTeamPlayers[i][j].currentHP / targetTeamPlayers[i][j].maxHP == minValue){
                                    if(i < MinPositionI && j < MinPositionJ){
                                        MinPositionI = i;
                                        MinPositionJ = j;
                                    }
                                }
                            }
                        }else{
                            if(targetTeamPlayers[i][j].currentHP / targetTeamPlayers[i][j].maxHP < minValue
                                    && targetTeamPlayers[i][j].isAlive()
                                    && (i == playerPosition[countTauntingArray].getI() && j == playerPosition[countTauntingArray].getJ())){
                                countTauntingArray++;
                                minValue = targetTeamPlayers[i][j].currentHP / targetTeamPlayers[i][j].maxHP;
                                if(targetTeamPlayers[i][j].currentHP / targetTeamPlayers[i][j].maxHP == minValue){
                                    if(i < MinPositionI && j < MinPositionJ){
                                        MinPositionI = i;
                                        MinPositionJ = j;
                                    }
                                }
                            }
                            if(countTauntingArray == countTaunt - 1){
                                break;
                            }
                        }
                    }
                }


                break;
            }
            case heal_teamLowestHP:{
                targetTeamPlayers = Arena.getFriendlyTeamPlayers(this);
                double minValue;
                if(targetTeamPlayers != null){
                    minValue = targetTeamPlayers[0][0].currentHP / targetTeamPlayers[0][0].maxHP;
                }else{
                    return null;
                }
                for(i = 0; i < 2; i++){
                    for(j = 0; j < Arena.numRowPlayers; j++){
                        if(targetTeamPlayers[i][j].maxHP == targetTeamPlayers[i][j].currentHP){
                            countFullHP++;
                            continue;
                        }
                        if(targetTeamPlayers[i][j].currentHP / targetTeamPlayers[i][j].maxHP < minValue
                                && targetTeamPlayers[i][j].isAlive() && !targetTeamPlayers[i][j].isCursed){
                            minValue = targetTeamPlayers[i][j].currentHP / targetTeamPlayers[i][j].maxHP;
                            if(targetTeamPlayers[i][j].currentHP / targetTeamPlayers[i][j].maxHP == minValue){
                                if(i < MinPositionI && j < MinPositionJ){
                                    MinPositionI = i;
                                    MinPositionJ = j;
                                }
                            }
                        }
                    }
                }

                if(countFullHP == Arena.numRowPlayers * 2){
                    return null;
                }
                break;
            }
            case revive_teamLowestHP:{
                break;
            }
        }
        return new PlayerPosition(MinPositionI, MinPositionJ);
    }

    /**
     * This method is called by Arena when it is this player's turn to take an action.
     * By default, the player simply just "attack(target)". However, once this player has
     * fought for "numSpecialTurns" rounds, this player must perform "useSpecialAbility(myTeam, theirTeam)"
     * where each player type performs his own special move.
     *
     * @param arena the current arena
     */
    public void takeAction(Arena arena){
        //INSERT YOUR CODE HERE
        if(current_Turn_In_A_Row == numSpecialTurns){
            useSpecialAbility();
        }else{
            attack(Player.arena.getOpponentTeamPlayers(Player.this));
        }
    }

    void useSpecialAbility(Player[][] myTeam, Player[][] theirTeam){
        //INSERT YOUR CODE HERE
        switch(type){
            case Healer:{
                heal(myTeam);
                break;
            }
            case Tank:{
                taunt();
                break;
            }
            case Phoenix:{
                break;
            }
            case BlackMage:{
                curse(theirTeam);
                break;
            }
            case Samurai:{
                doubleSlash(theirTeam);
                break;
            }
            case Cherry:{
            }
        }
    }

    private void taunt(){
        isTaunting = true;
    }

    void attack(Player[][] theirTeam){
        //INSERT YOUR CODE HERE
        PlayerPosition targetPosition = findTargetablePlayers(TargetingMode.attack_LowestHP);
        Player target = theirTeam[targetPosition.getI()][targetPosition.getJ()];

        target.currentHP -= atk;
        if(target.maxHP < 0){
            target.maxHP = 0;
        }
    }

    private void doubleSlash(Player[][] theirTeam){
        PlayerPosition targetPosition = findTargetablePlayers(TargetingMode.attack_LowestHP);
        Player target = theirTeam[targetPosition.getI()][targetPosition.getJ()];

        for(int i = 1; i <= 2; i++){
            target.currentHP -= atk;
            if(target.maxHP < 0){
                target.maxHP = 0;
            }
        }
    }

    private void heal(Player[][] myTeam){
        PlayerPosition targetPosition = findTargetablePlayers(TargetingMode.heal_teamLowestHP);
        int i = targetPosition.getI(), j = targetPosition.getI();

        myTeam[i][j].currentHP += (0.3 * myTeam[i][j].maxHP);
        if(myTeam[i][j].currentHP > myTeam[i][j].maxHP){
            myTeam[i][j].currentHP = myTeam[i][j].maxHP;
        }
    }

    void curse(Player[][] theirTeam){
        PlayerPosition targetPosition = findTargetablePlayers(TargetingMode.attack_LowestHP);
        assert targetPosition != null;
        int i = targetPosition.getI(), j = targetPosition.getI();

        theirTeam[i][j].isCursed = true;
        theirTeam[i][j].curser = Player.this;
    }

    private void fortuneCookies(){

    }

    /**
     * This method returns the faction of this player.
     *
     * @return team
     */
    public Arena.Team getPlayerTeam(){
        return team;
    }

    public Arena.Team getOpponentTeam(){
        if(this.getPlayerTeam() == Arena.Team.A){
            return Arena.Team.B;
        }else if(this.getPlayerTeam() == Arena.Team.B){
            return Arena.Team.A;
        }
    }

    /**
     * This method overrides the default Object's toString() and is already implemented for you.
     */
    @Override
    public String toString(){
        return "[" + this.type.toString() + " HP:" + this.currentHP + "/" + this.maxHP + " ATK:" + this.atk + "]["
                + ((this.isCursed()) ? "C" : "")
                + ((this.isTaunting()) ? "T" : "")
                + ((this.isSleeping()) ? "S" : "")
                + "]";
    }

    public enum PlayerType{Healer, Tank, Samurai, BlackMage, Phoenix, Cherry}


}

public class Player{

    public enum TargetingMode{attack_LowestHP, heal_teamLowestHP, revive_teamLowestHP}

    ;

    public enum PlayerType{Healer, Tank, Samurai, BlackMage, Phoenix, Cherry}

    ;

    private static Arena arena;
    private Arena.Team team;    //

    private PlayerType type;    //Type of this player. Can be one of either Healer, Tank, Samurai, BlackMage, or Phoenix

    private double maxHP;        //Max HP of this player
    private double currentHP;    //Current HP of this player
    private double atk;            //Attack power of this player
    private int numSpecialTurns;            //Number of Special Turns of this player

    private int current_Turn_In_A_Row;            //Number of Special Turns of this player
    private int internalTurn;

    private boolean isTaunting;     //Priority of this player to being attacked
    private boolean isSleeping;
    private boolean isCursed;       //Cursed Status of this player
    private Player iAmCursing;          //The player who given the Cursed Status by this player
    private Player cursedBy;          //The player who given the Cursed Status to this player

    /**
     * Constructor of class Player, which initializes this player's type, maxHP, atk, numSpecialTurns,
     * as specified in the given table. It also reset the internal turn count of this player.
     *
     * @param _type The type of Player Character
     */
    Player(PlayerType _type, Arena.Team team, Arena _arena){
        //INSERT YOUR CODE HERE
        arena = _arena;
        this.type = _type;
        this.team = team;

        this.isSleeping = false;
        this.isTaunting = false;
        this.cursedBy = null;
        this.isCursed = false;

        internalTurn = 0;
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
     * @return currentHP of this player
     */
    private double getCurrentHP(){
        return currentHP;
    }

    /**
     * Returns type of this player
     *
     * @return type or class of this player
     */
    Player.PlayerType getType(){
        //INSERT YOUR CODE HERE
        return null;
    }

    /**
     * Returns max HP of this player.
     *
     * @return maxHP of this player
     */
    public double getMaxHP(){
        //INSERT YOUR CODE HERE
        return maxHP;
    }

    public int getInternalTurn(){
        return internalTurn;
    }

    /**
     * Set internalTurn of this player to 0, it has to be used before This player takeAction()
     */
    private void emptyInternalTurn(){
        this.internalTurn = 0;
    }

    /**
     * Set internalTurn of this player to 1, it has to be used after This player takeAction()
     */
    void fillInternalTurn(){
        this.internalTurn = 1;
    }

    /**
     * Returns whether this player is sleeping.
     *
     * @return whether this player is sleeping.
     */
    private boolean isSleeping(){
        return isSleeping;
    }

    /**
     * Returns whether this player is being cursed.
     *
     * @return whether this player get cursed debuff or not
     */
    private boolean isCursed(){
        return isCursed;
    }

    /**
     * Returns whether this player is alive (i.e. current HP > 0).
     *
     * @return whether this player is alive
     */
    boolean isAlive(){
        return currentHP > 0;
    }

    /**
     * Returns whether this player is taunting the other team.
     *
     * @return whether this player is taunting or not
     */
    private boolean isTaunting(){
        return isTaunting;
    }

    /**
     * This method will select an Algorithm for each action that is the most suitable
     * 1. Attacking and Cursing - Find A player with the LOWEST HP PERCENTAGE on the FRONT ROW
     * ** Taunting - If there are multiple taunting player, the first taunting player according to
     * the position order gets attacked first.**
     * 2. Healing - Find an ALLY with the LOWEST HP PERCENTAGE
     * 3. Find a dead Ally in the first according to the position
     *
     * @param targetingMode The mode to get Target based on the player action
     *
     * @return Target player position
     */
    private PlayerPosition findTargetablePlayers(TargetingMode targetingMode){

        int i, j;
        int countFullHP = 0;
        int countDead = 0;
        Player[][] targetTeamPlayers;

        int minPositionI = Arena.numRowPlayers, minPositionJ = Arena.numRowPlayers;

        switch(targetingMode){
            case attack_LowestHP:{
                targetTeamPlayers = arena.getOpponentTeamPlayers(this);
                double minValue;
                int countTaunt = 0;
                if(targetTeamPlayers != null){
                    minValue = targetTeamPlayers[0][0].currentHP / targetTeamPlayers[0][0].maxHP;
                }else{
                    return null;
                }

                PlayerPosition playerPosition[] = new PlayerPosition[Arena.MAX_EACH_TYPE];
                for(i = 0; i < Arena.NUMBER_OF_ROWS; i++){
                    for(j = 0; j < Arena.numRowPlayers; j++){
                        if(targetTeamPlayers[i][j].isTaunting){
                            playerPosition[countTaunt] = new PlayerPosition(i, j);
                            countTaunt++;
                        }
                    }
                }

                int rowRange;
                if(countTaunt > 0){
                    rowRange = Arena.NUMBER_OF_ROWS;
                }else{
                    rowRange = arena.getFrontRow(targetTeamPlayers);
                }

                int countTauntingArray = 0;
                for(i = 0; i < rowRange; i++){
                    for(j = 0; j < Arena.numRowPlayers; j++){
                        if(countTaunt == 0){
                            if(targetTeamPlayers[i][j].currentHP / targetTeamPlayers[i][j].maxHP < minValue
                                    && targetTeamPlayers[i][j].isAlive()){
                                minValue = targetTeamPlayers[i][j].currentHP / targetTeamPlayers[i][j].maxHP;
                                if(targetTeamPlayers[i][j].currentHP / targetTeamPlayers[i][j].maxHP == minValue){
                                    if(i < minPositionI && j < minPositionJ){
                                        minPositionI = i;
                                        minPositionJ = j;
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
                                    if(i < minPositionI && j < minPositionJ){
                                        minPositionI = i;
                                        minPositionJ = j;
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
                targetTeamPlayers = arena.getFriendlyTeamPlayers(this);
                double minValue;
                if(targetTeamPlayers != null){
                    minValue = targetTeamPlayers[0][0].currentHP / targetTeamPlayers[0][0].maxHP;
                }else{
                    return null;
                }
                for(i = 0; i < Arena.NUMBER_OF_ROWS; i++){
                    for(j = 0; j < Arena.numRowPlayers; j++){
                        if(targetTeamPlayers[i][j].maxHP == targetTeamPlayers[i][j].currentHP){
                            countFullHP++;
                            continue;
                        }
                        if(targetTeamPlayers[i][j].currentHP / targetTeamPlayers[i][j].maxHP < minValue
                                && targetTeamPlayers[i][j].isAlive() && !targetTeamPlayers[i][j].isCursed){
                            minValue = targetTeamPlayers[i][j].currentHP / targetTeamPlayers[i][j].maxHP;
                            if(targetTeamPlayers[i][j].currentHP / targetTeamPlayers[i][j].maxHP == minValue){
                                if(i < minPositionI && j < minPositionJ){
                                    minPositionI = i;
                                    minPositionJ = j;
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
                targetTeamPlayers = arena.getFriendlyTeamPlayers(this);
                double minValue;

                for(i = 0; i < Arena.NUMBER_OF_ROWS; i++){
                    for(j = 0; j < Arena.numRowPlayers; j++){
                        if(targetTeamPlayers[i][j].currentHP == 0){
                            countDead++;
                        }
                    }
                }
                if(countDead == 0){
                    return new PlayerPosition(-1, -1);
                }

                PlayerPosition deadPlayerPositions[] = new PlayerPosition[countDead];
                int countDeadPlayerArray = 0;
                for(i = 0; i < Arena.NUMBER_OF_ROWS; i++){
                    for(j = 0; j < Arena.numRowPlayers; j++){
                        if(targetTeamPlayers[i][j].currentHP == 0){
                            deadPlayerPositions[countDeadPlayerArray] = new PlayerPosition(i, j);
                            countDeadPlayerArray++;
                        }
                    }
                }

                PlayerPosition minPosition = null;
                for(i = 0; i < countDead; i++){
                    if(deadPlayerPositions[i].isLowerThan(deadPlayerPositions[i + 1]) && i < countDead - 1){
                        minPosition = deadPlayerPositions[i];
                    }
                }
                return minPosition;
            }
        }
        return new PlayerPosition(minPositionI, minPositionJ);
    }

    private void statusHandler(){
        //If the Player is taunting, remove the buff.
        if(this.isTaunting){
            isTaunting = false;
        }

        //If THE CURSER has a CURSE TARGET and THE CURSER has internalTurn = 0, The CURSED TARGET will no longer CURSED.
        if(this.iAmCursing != null){
            //The CURSED TARGET will no longer cursed.
            iAmCursing.isCursed = false;

            //The CURSED TARGET will no remember the curser.
            iAmCursing.cursedBy = null;

            //The CURSER will no longer remember the cursed target.
            this.iAmCursing = null;
        }

        if(this.isSleeping){
            isSleeping = false;
        }
    }

    private void cleanBuffWhenDead(Player player){
        player.isSleeping = false;
        player.isTaunting = false;

        player.isCursed = false;
        player.cursedBy = null;
        player.iAmCursing = null;
    }

    /**
     * This method is called by Arena when it is this player's turn to take an action.
     * By default, the player simply just "attack(target)". However, once this player has
     * fought for "numSpecialTurns" rounds, this player must perform "useSpecialAbility(myTeam, theirTeam)"
     * where each player type performs his own special move.
     *
     * @param arena the current arena
     */
    void takeAction(Arena arena){
        this.emptyInternalTurn();
        // Check if player has Status (Buff) and enforce the Turn rules.
        statusHandler();
        // INSERT YOUR CODE HERE
        if(current_Turn_In_A_Row == numSpecialTurns){
            useSpecialAbility(arena.getFriendlyTeamPlayers(this), arena.getOpponentTeamPlayers(this));
        }else{
            attack(Player.arena.getOpponentTeamPlayers(Player.this));
        }
        this.fillInternalTurn();
        current_Turn_In_A_Row++;
    }

    /**
     * This method will choose an ability based on Player Type
     *
     * @param myTeam    the array of friendly team
     * @param theirTeam the array of opponent team
     */
    private void useSpecialAbility(Player[][] myTeam, Player[][] theirTeam){
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
        current_Turn_In_A_Row = 0;
    }

    /**
     * Make the players of the opposite team attack (including double-slashing and cursing) himself
     * for one internal turn.
     */
    private void taunt(){
        isTaunting = true;
    }

    private void attack(Player[][] theirTeam){
        //INSERT YOUR CODE HERE
        PlayerPosition targetPosition = findTargetablePlayers(TargetingMode.attack_LowestHP);
        assert targetPosition != null;
        Player target = theirTeam[targetPosition.getI()][targetPosition.getJ()];

        target.currentHP -= atk;
        if(target.maxHP < 0){
            target.maxHP = 0;
            cleanBuffWhenDead(target);
        }
    }

    private void doubleSlash(Player[][] theirTeam){
        PlayerPosition targetPosition = findTargetablePlayers(TargetingMode.attack_LowestHP);
        assert targetPosition != null;
        Player target = theirTeam[targetPosition.getI()][targetPosition.getJ()];

        for(int i = 1; i <= 2; i++){
            target.currentHP -= atk;
            if(target.maxHP < 0){
                target.maxHP = 0;
                cleanBuffWhenDead(target);
            }
        }
    }

    private void heal(Player[][] myTeam){
        PlayerPosition targetPosition = findTargetablePlayers(TargetingMode.heal_teamLowestHP);
        assert targetPosition != null;
        int i = targetPosition.getI(), j = targetPosition.getI();

        myTeam[i][j].currentHP += (0.3 * myTeam[i][j].maxHP);
        if(myTeam[i][j].currentHP > myTeam[i][j].maxHP){
            myTeam[i][j].currentHP = myTeam[i][j].maxHP;
        }
    }

    private void revive(Player[][] myTeam){
        PlayerPosition targetPlayerPosition = findTargetablePlayers(TargetingMode.revive_teamLowestHP);
        int i = targetPlayerPosition.getI(), j = targetPlayerPosition.getJ();
        myTeam[i][j].currentHP += 0.3 * myTeam[i][j].maxHP;
    }

    private void curse(Player[][] theirTeam){
        PlayerPosition targetPosition = findTargetablePlayers(TargetingMode.attack_LowestHP);
        assert targetPosition != null;
        int i = targetPosition.getI(), j = targetPosition.getI();

        // The Curser will remember their target.
        this.iAmCursing = theirTeam[i][j];

        // The Target is now cursed.
        theirTeam[i][j].isCursed = true;

        // The Curse Target will remember their curser.
        theirTeam[i][j].cursedBy = Player.this;
    }

    private void fortuneCookies(){

    }

    /**
     * This method returns the faction of this player.
     *
     * @return current team of this player
     */
    Arena.Team getPlayerTeam(){
        return team;
    }

    /**
     * This method returns the opponent team of this player.
     *
     * @return opponent team of this player
     */
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

}

import java.util.ArrayList;

public class Player{

    public enum TargetingMode{attack_LowestHP, heal_teamLowestHP, selectAllTarget, revive_teamLowestHP}

    public enum PlayerType{Healer, Tank, Samurai, BlackMage, Phoenix, Cherry}

    private final int MIN_HP_VALUE = 6000;

    private static Arena arena;                   //Specifies the arena which this player joined in.
    private Arena.Team team;                      //Specifies the team which this player joined in.

    private PlayerType type;                      //Type of this player. Can be one of either Healer,
    //Tank, Samurai, BlackMage, or Phoenix

    private double maxHP;                         //Max HP of this player
    private double currentHP;                     //Current HP of this player
    private double atk;                           //Attack power of this player
    private int numSpecialTurns;                  //Number of Special Turns of this player

    private int current_Turn_In_A_Row;            //Number of Special Turns of this player
    private int internalTurn;                     //A turn meter which will reset to ONE AFTER THE PLAYER takeAction()
    //And will set to ZERO BEFORE THE PLAYER takeAction()

    private boolean isTaunting;                   //Priority of this player to being attacked

    private boolean isSleeping;

    private int turnsSinceStartSleeping = 0;

    private boolean isCursed;                     //Cursed Status of this player
    private Player iAmCursing;                    //The player who given the Cursed Status by this player
    private Player cursedBy;                      //The player who given the Cursed Status to this player

    private PlayerPosition playerPosition;

    /**
     * Constructor of class Player, which initializes this player's type, maxHP, atk, numSpecialTurns,
     * as specified in the given table. It also reset the internal turn count of this player.
     *
     * @param _type The type of Player Character
     */
    Player(PlayerType _type, Arena.Team team, Arena _arena, PlayerPosition playerPosition){
        //INSERT YOUR CODE HERE
        this.playerPosition = playerPosition;
        arena = _arena;
        this.type = _type;
        this.team = team;

        this.isSleeping = false;
        this.isTaunting = false;
        this.cursedBy = null;
        this.isCursed = false;

        internalTurn = 0;
        current_Turn_In_A_Row = 1;

        switch(_type){
            case Healer:{
                maxHP = 4790;
                currentHP = 4790;
                atk = 238;
                numSpecialTurns = 4;
                break;
            }
            case Tank:{
                maxHP = 5340;
                currentHP = 5340;
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
    public double getCurrentHP(){
        return currentHP;
    }

    /**
     * Returns type of this player
     *
     * @return type or class of this player
     */
    Player.PlayerType getType(){
        //INSERT YOUR CODE HERE
        return type;
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
        }else{
            return Arena.Team.A;
        }
    }

    public int getTurnsSinceStartSleeping(){
        return turnsSinceStartSleeping;
    }

    public void setTurnsSinceStartSleeping(int turnsSinceStartSleeping){
        this.turnsSinceStartSleeping = turnsSinceStartSleeping;
    }

    public void setSleeping(boolean sleeping){
        isSleeping = sleeping;
    }

    /**
     * Returns whether this player is sleeping.
     *
     * @return whether this player is sleeping.
     */
    public boolean isSleeping(){
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
     * Set internalTurn of this player to 1, it has to be used after This player takeAction()
     */
    void fillInternalTurn(){
        this.internalTurn = 1;
    }

    /**
     * Set internalTurn of this player to 0, it has to be used before This player takeAction()
     */
    private void emptyInternalTurn(){
        this.internalTurn = 0;
    }

    private ArrayList<PlayerPosition> findMultipleTargetablePlayers(TargetingMode targetingMode){
        int i, j;
        Player[][] targetTeamPlayers = arena.getOpponentTeamPlayers(this);
        ArrayList<PlayerPosition> playerPositionArrayList = new ArrayList<>();

        if(targetingMode == TargetingMode.selectAllTarget){
            for(i = 0; i < Arena.NUMBER_OF_ROWS; i++){
                for(j = 0; j < arena.getNumRowPlayers(); j++){
                    if(targetTeamPlayers[i][j].isAlive()){
                        playerPositionArrayList.add(new PlayerPosition(i, j));
                    }
                }
            }
        }
        return playerPositionArrayList;
    }

    /**
     * Similar to statusHandler(), this function will clean all negative buff.
     * This will be called when a player dies.
     *
     * @param player target player
     */
    private void cleanBuffWhenDie(Player player){

        player.current_Turn_In_A_Row = 0;               /* This needs to be reset. */

        player.isSleeping = false;                      /* When someone dies, they will no longer sleep. */
        player.isTaunting = false;                      /* When someone dies, they cannot taunt anyone. */

        player.isCursed = false;                        /* When someone dies, they cannot taunt anyone. */
        player.cursedBy = null;
        player.iAmCursing = null;
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
        int countDead = 0;
        Player[][] targetTeamPlayers;

        switch(targetingMode){
            case attack_LowestHP:{
                ///TODO: FIX THIS
                targetTeamPlayers = arena.getOpponentTeamPlayers(this);
                double minHPValue;

                ArrayList<PlayerPosition> tauntingPlayerPosition = new ArrayList<>();
                for(i = 0; i < Arena.NUMBER_OF_ROWS; i++){
                    for(j = 0; j < arena.getNumRowPlayers(); j++){
                        if(targetTeamPlayers[i][j].isAlive() && targetTeamPlayers[i][j].isTaunting){
                            tauntingPlayerPosition.add(new PlayerPosition(i, j));
                        }
                    }
                }

                if(tauntingPlayerPosition.size() > 1){
                    /// TODO: Fix it please
                    // If there are more than one player who is taunting, find the lowest PlayerPosition of those
                    int x, y;
                    //System.out.println("Statement 1");
                    PlayerPosition minPosition = tauntingPlayerPosition.get(0);
                    for(i = 0; i < tauntingPlayerPosition.size(); i++){
                        x = tauntingPlayerPosition.get(i).getI();
                        y = tauntingPlayerPosition.get(i).getJ();
                        if(!targetTeamPlayers[x][y].isAlive()){
                            continue;
                        }
                        if(targetTeamPlayers[x][y].playerPosition.isLowerThan(minPosition)){
                            minPosition = targetTeamPlayers[x][y].playerPosition;
                        }
                    }
                    return minPosition;
                }else if(tauntingPlayerPosition.size() == 1){
                    //System.out.println("Statement 2");
                    return tauntingPlayerPosition.get(0);
                }else{
                    //Search for THE LOWEST HP
                    //System.out.println("Statement 3");
                    minHPValue = MIN_HP_VALUE;
                    ArrayList<PlayerPosition> playerPositionArrayList = new ArrayList<>();
                    for(j = 0; j < arena.getNumRowPlayers(); j++){
                        if(targetTeamPlayers[arena.getFrontRow(targetTeamPlayers)][j].isAlive()
                                && (targetTeamPlayers[arena.getFrontRow(targetTeamPlayers)][j].currentHP <= minHPValue)){
                            minHPValue = targetTeamPlayers[arena.getFrontRow(targetTeamPlayers)][j].currentHP;
                        }
                    }

                    //If there are any player whose HP is equals to the lowest, stores their position.
                    for(j = 0; j < arena.getNumRowPlayers(); j++){
                        if(targetTeamPlayers[arena.getFrontRow(targetTeamPlayers)][j].isAlive()
                                && targetTeamPlayers[arena.getFrontRow(targetTeamPlayers)][j].currentHP == minHPValue){
                            playerPositionArrayList.add(new PlayerPosition(arena.getFrontRow(targetTeamPlayers), j));

                            //Debugging Message
                            if(StudentTester.debug_TargetSearching){
                                System.out.println(playerPositionArrayList.get(playerPositionArrayList.size() - 1).toString());
                            }
                        }
                    }

                    //For debugging purposes only
                    if(StudentTester.debug_TargetSearching){
                        System.out.println("Total HP: " + Arena.getSumHP(targetTeamPlayers));
                    }

                    if(playerPositionArrayList.size() == 1){
                        return playerPositionArrayList.get(0);
                    }else if(playerPositionArrayList.size() == 0){
                        for(i = 0; i < arena.getNumRowPlayers(); i++){
                            if(targetTeamPlayers[arena.getFrontRow(targetTeamPlayers)][i].currentHP > 0){
                                playerPositionArrayList.add(new PlayerPosition(arena.getFrontRow(targetTeamPlayers), i));
                            }
                        }
                        if(Arena.getSumHP(targetTeamPlayers) == 0.0){
                            // If all player of the opponent team = 0
                            return null;
                        }else{
                            return playerPositionArrayList.get(0);
                        }
                    }

                    //Search for lowest position
                    int countArrayList = 0;
                    PlayerPosition minPosition = playerPositionArrayList.get(0);
                    for(i = 0; i < playerPositionArrayList.size(); i++){
                        if(!targetTeamPlayers[playerPositionArrayList.get(countArrayList).getI()][playerPositionArrayList.get(countArrayList).getJ()].isAlive()){
                            continue;
                        }
                        if(targetTeamPlayers[playerPositionArrayList.get(countArrayList).getI()]
                                [playerPositionArrayList.get(countArrayList).getJ()].playerPosition.isLowerThan(minPosition)){
                            minPosition = targetTeamPlayers[playerPositionArrayList.get(countArrayList).getI()]
                                    [playerPositionArrayList.get(countArrayList).getJ()].playerPosition;
                        }
                    }
                    return minPosition;
                }
            }
            case heal_teamLowestHP:{
                targetTeamPlayers = arena.getFriendlyTeamPlayers(this);
                double minHPValue = MIN_HP_VALUE;
                ArrayList<PlayerPosition> playerPositionArrayList = new ArrayList<>();

                // Search for lowest HP
                for(i = 0; i < Arena.NUMBER_OF_ROWS; i++){
                    for(j = 0; j < arena.getNumRowPlayers(); j++){
                        if(targetTeamPlayers[i][j].isAlive() && !targetTeamPlayers[i][j].isCursed()
                                && targetTeamPlayers[i][j].currentHP < targetTeamPlayers[i][j].maxHP
                                && targetTeamPlayers[i][j].currentHP <= minHPValue){
                            minHPValue = targetTeamPlayers[i][j].currentHP;
                        }
                    }
                }

                // Search player who has HP equals to that Lowest HP
                for(i = 0; i < Arena.NUMBER_OF_ROWS; i++){
                    for(j = 0; j < arena.getNumRowPlayers(); j++){
                        if(targetTeamPlayers[i][j].currentHP == minHPValue){
                            playerPositionArrayList.add(new PlayerPosition(i, j));
                        }
                    }
                }

                if(playerPositionArrayList.size() == 1){
                    return playerPositionArrayList.get(0);
                }else if(playerPositionArrayList.size() > 1){
                    int countArrayList = 0, x, y;
                    PlayerPosition minPosition = playerPositionArrayList.get(0);
                    for(i = 0; i < playerPositionArrayList.size(); i++){
                        x = playerPositionArrayList.get(countArrayList).getI();
                        y = playerPositionArrayList.get(countArrayList).getJ();
                        if(!targetTeamPlayers[x][y].isAlive()){
                            continue;
                        }

                        // Search for lowest Position
                        if(targetTeamPlayers[x][y].playerPosition.isLowerThan(minPosition)){
                            minPosition = targetTeamPlayers[x][y].playerPosition;
                        }
                    }
                    return minPosition;
                }

            }
            case selectAllTarget:{
                break;
            }
            case revive_teamLowestHP:{

                targetTeamPlayers = arena.getFriendlyTeamPlayers(this);

                // Find number of dead players
                for(i = 0; i < Arena.NUMBER_OF_ROWS; i++){
                    for(j = 0; j < arena.getNumRowPlayers(); j++){
                        if(!targetTeamPlayers[i][j].isAlive()){
                            countDead++;
                        }
                    }
                }

                if(countDead == 0){
                    return null;
                }

                int countArrayList = 0;
                ArrayList<PlayerPosition> deadPositionsArrayList = new ArrayList<>();
                for(i = 0; i < Arena.NUMBER_OF_ROWS; i++){
                    for(j = 0; j < arena.getNumRowPlayers(); j++){
                        if(!targetTeamPlayers[i][j].isAlive()){
                            deadPositionsArrayList.add(new PlayerPosition(i, j));
                            System.out.println("deadPositionsArrayList: " + deadPositionsArrayList.get(countArrayList).toString() + ", hp: " + targetTeamPlayers[i][j].getCurrentHP());
                            countArrayList++;
                        }
                    }
                }

                PlayerPosition minPosition;
                if(countDead > 1){
                    countArrayList = 0;
                    int x, y;
                    minPosition = new PlayerPosition(Arena.NUMBER_OF_ROWS, arena.getNumRowPlayers());
                    for(i = 0; i < deadPositionsArrayList.size(); i++){
                        x = deadPositionsArrayList.get(countArrayList).getI();
                        y = deadPositionsArrayList.get(countArrayList).getJ();
                        if(targetTeamPlayers[x][y].isAlive()){
                            continue;
                        }

                        // Search for lowest Position
                        if(targetTeamPlayers[x][y].playerPosition.isLowerThan(minPosition)){
                            minPosition = targetTeamPlayers[x][y].playerPosition;
                        }
                    }
                }else{
                    minPosition = deadPositionsArrayList.get(0);
                }
                return minPosition;
            }
        }
        return null;
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
        statusHandler(this);

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
                revive(myTeam);
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
                fortuneCookies(theirTeam);
            }
        }
        current_Turn_In_A_Row = 0;
    }

    private void attack(Player[][] theirTeam){
        PlayerPosition targetPosition = findTargetablePlayers(TargetingMode.attack_LowestHP);
        //System.out.println("targetPosition.getI() = " + targetPosition.getI());

        if(targetPosition == null){
            return;
        }
        Player target = theirTeam[targetPosition.getI()][targetPosition.getJ()];


        if(StudentTester.debug_ActionMessages){
            System.out.println(toStringDebug("attacks", target));
        }
        target.currentHP -= atk;
        if(target.currentHP < 0){

            target.currentHP = 0;
            cleanBuffWhenDie(target);

            // For debugging purpose only!
            //if(StudentTester.debug_ActionMessages){
            //    System.out.println("# " + getOpponentTeam().name() + target.playerPosition.toReadableString() + " {" + target.type.name() + "} " + " dies");
            //}
        }

    }

    private void doubleSlash(Player[][] theirTeam){
        PlayerPosition targetPosition = findTargetablePlayers(TargetingMode.attack_LowestHP);
        if(targetPosition == null){
            return;
        }
        Player target = theirTeam[targetPosition.getI()][targetPosition.getJ()];

        if(StudentTester.debug_ActionMessages){
            System.out.println(toStringDebug("double-slashes", target));
        }

        for(int i = 1; i <= 2; i++){
            target.currentHP -= atk;
            if(target.currentHP < 0){
                //if(StudentTester.debug_ActionMessages){
                //   System.out.println("# " + getPlayerTeam().name() + playerPosition.toReadableString() + " {" + type.name() + "} " + " dies");
                //}
                target.currentHP = 0;
                cleanBuffWhenDie(target);
            }
        }

    }

    /**
     * Make the players of the opposite team attack (including double-slashing and cursing) himself
     * for one internal turn.
     */
    private void taunt(){
        if(StudentTester.debug_ActionMessages){
            System.out.println("# " + getPlayerTeam().name() + playerPosition.toReadableString() + " {" + type.name() + "} " + "is taunting ");
        }
        isTaunting = true;
    }

    private void heal(Player[][] myTeam){
        PlayerPosition targetPosition = findTargetablePlayers(TargetingMode.heal_teamLowestHP);
        if(targetPosition == null){
            System.out.println("No target for healing");
            return;
        }
        int i = targetPosition.getI(), j = targetPosition.getJ();

        myTeam[i][j].currentHP += (0.25 * myTeam[i][j].maxHP);
        if(myTeam[i][j].currentHP > myTeam[i][j].maxHP){
            myTeam[i][j].currentHP = myTeam[i][j].maxHP;
        }
        if(StudentTester.debug_ActionMessages){
            System.out.println(toStringDebug("heals", myTeam[i][j]));
        }

    }

    private void revive(Player[][] myTeam){
        PlayerPosition targetPlayerPosition = findTargetablePlayers(TargetingMode.revive_teamLowestHP);
        if(targetPlayerPosition == null){
            return;
        }
        int i = targetPlayerPosition.getI(), j = targetPlayerPosition.getJ();

        // Revives and heals target by 30% of target's max hp
        myTeam[i][j].currentHP += 0.30 * myTeam[i][j].maxHP;

        //For debugging purposes only
        if(StudentTester.debug_ActionMessages){
            System.out.println(toStringDebug("revives", myTeam[i][j]));
        }
    }

    /**
     * Give curse status effect to an opponent
     *
     * @param theirTeam opponent team
     */
    private void curse(Player[][] theirTeam){
        PlayerPosition targetPosition = findTargetablePlayers(TargetingMode.attack_LowestHP);

        if(targetPosition == null){
            return;
        }

        int i = targetPosition.getI(), j = targetPosition.getI();

        // The Curser will remember their target.
        this.iAmCursing = theirTeam[i][j];

        // The Target is now cursed.
        theirTeam[i][j].isCursed = true;

        // The Curse Target will remember their curser.
        theirTeam[i][j].cursedBy = Player.this;
        //For debugging purposes only
        if(StudentTester.debug_ActionMessages){
            System.out.println(toStringDebug("curses", theirTeam[i][j]));
        }
    }

    /**
     * Make the players of the opposite team sleep for one internal turn
     *
     * @param theirTeam opponent team
     */
    private void fortuneCookies(Player[][] theirTeam){
        int i, j, countPlayerPosition = 0;

        if(StudentTester.debug_ActionMessages){
            System.out.println("# " + getPlayerTeam().name() + playerPosition.toReadableString()
                                       + " {" + type.name() + "} " + " uses fortune cookies to " + Arena.team_toString(this.getOpponentTeam()));
        }

        ArrayList<PlayerPosition> playerPositionArrayList = findMultipleTargetablePlayers(TargetingMode.selectAllTarget);

        if(StudentTester.debug_TargetSearching){
            for(i = 0; i < playerPositionArrayList.size(); i++){
                System.out.println("#" + i + ": " + playerPositionArrayList.get(i).toString());
            }
            System.out.println("playerPositionArrayList.size() = " + playerPositionArrayList.size());
        }

        for(i = 0; i < Arena.NUMBER_OF_ROWS; i++){
            for(j = 0; j < arena.getNumRowPlayers(); j++){
                if(playerPositionArrayList.get(countPlayerPosition).getI() == i
                        && playerPositionArrayList.get(countPlayerPosition).getJ() == j && !theirTeam[i][j].isSleeping){
                    theirTeam[i][j].turnsSinceStartSleeping = 0;
                    theirTeam[i][j].isSleeping = true;
                    countPlayerPosition++;
                    if(countPlayerPosition == playerPositionArrayList.size()){
                        break;
                    }
                }
            }
        }
    }

    /**
     * This method handles expiration of several debuff.
     * This will be called only when one internal turn has passed.
     */
    private void statusHandler(Player target){
        //If the Player is taunting, remove the buff.
        if(target.isTaunting){
            isTaunting = false;
        }

        //If THE CURSER has a CURSE TARGET and THE CURSER has internalTurn = 0, The CURSED TARGET will no longer CURSED.
        if(target.iAmCursing != null){
            //The CURSED TARGET will no longer cursed.
            target.iAmCursing.isCursed = false;

            //The CURSED TARGET will no remember the curser.
            target.iAmCursing.cursedBy = null;

            //The CURSER will no longer remember the cursed target.
            target.iAmCursing = null;
        }

        if(target.isSleeping){
            target.isSleeping = false;
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

    /**
     * This method is used for debugging the program.
     *
     * @return A string of battle log
     */
    public String toStringDebug(String move, Player target){
        return "# " + getPlayerTeam().name() + playerPosition.toReadableString() + " {" + type.name() + "} "
                + move + " " + target.getPlayerTeam().name() + target.playerPosition.toReadableString() + " {" + target.type.name() + "}";
    }

    private boolean isHpAreAllEqualInRow(Player[][] players, int row){
        int j, count = 0;

        if(arena.getNumRowPlayers() == 1){
            return true;
        }else if(arena.getNumRowPlayers() == 2){
            return players[row][0].currentHP == players[row][1].currentHP;
        }

        for(j = 0; j < arena.getNumRowPlayers(); j++){
            if(j + 1 < arena.getNumRowPlayers() - 1 && players[row][j].currentHP == players[row][j + 1].currentHP){
                count++;
            }
        }

        return count == arena.getNumRowPlayers() - 1;
    }

}

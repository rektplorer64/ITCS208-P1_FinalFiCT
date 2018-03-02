
/*
 * This java file is part of Project 1 of subject ITCS208 - Object Oriented Programming of Mahidol University, Faculty of ICT
 * Modified by Tanawin Wichit, ID 6088221, a section 1 student.
 */

import java.util.ArrayList;

public class Player{

    private static final boolean debug_TargetSearching = false;        /* For debugging purposes only, this variable
                                                                         controls the visibility of target searching log */

    public static final boolean debug_ActionMessages = true;         /* For debugging purposes only, this variable
                                                                         controls the visibility of battle log */

    //Enum for Player Types which are based on given Rules
    public enum PlayerType{Healer, Tank, Samurai, BlackMage, Phoenix, Cherry}

    //Enum for Targeting Modes which are categorized based on given Rules
    public enum TargetingMode{attack_LowestHP, heal_teamLowestHP, selectAllTarget, revive_teamLowestHP}

    private final int MIN_HP_VALUE = 6000;        //This variable is used in finding lowest number

    private static Arena arena;                   //Specifies the arena which this player joined in.
    private Arena.Team team;                      //Specifies the team which this player joined in.

    /* Must have fields for every players */
    private PlayerPosition playerPosition;        //Position of the player
    private PlayerType type;                      //Type of this player. Can be one of either Healer, Tank, Samurai, BlackMage, or Phoenix
    private int internalTurn;                     //A turn meter which will reset to ONE AFTER THE PLAYER takeAction(), and will be set to ZERO BEFORE THE PLAYER takeAction()

    private double maxHP;                         //Max HP of this player
    private double currentHP;                     //Current HP of this player
    private double atk;                           //Attack power of this player
    private int numSpecialTurns;                  //Number of Special Turns of this player

    private int current_Turn_In_A_Row;            //Number of Current Turns-in-a-row  of this player

    /* Status */
    private boolean isTaunting;                   //Priority of this player to being attacked
    private boolean isSleeping;                   //Sleep Status of this player

    private int turnsSinceStartSleeping = 0;

    private boolean isCursed;                     //Cursed Status of this player
    private Player iAmCursing;                    //The player who given the Cursed Status by this player
    private Player cursedBy;                      //The player who given the Cursed Status to this player

    /**
     * Constructor of class Player, which initializes this player's type, maxHP, atk, numSpecialTurns,
     * as specified in the given table. It also reset the internal turn count of this player.
     *
     * @param _type The type of Player Character
     */
    Player(PlayerType _type, Arena.Team team, Arena _arena, PlayerPosition playerPosition){
        this.playerPosition = playerPosition;
        arena = _arena;
        this.type = _type;
        this.team = team;

        /* Status */
        this.isSleeping = false;
        this.isTaunting = false;
        this.cursedBy = null;
        this.isCursed = false;

        internalTurn = 0;
        current_Turn_In_A_Row = 1;

        //These configurations are based on the given set of Rules
        switch(_type){
            case Healer:{
                maxHP = 4790;
                atk = 238;
                numSpecialTurns = 4;
                break;
            }
            case Tank:{
                maxHP = 5340;
                atk = 255;
                numSpecialTurns = 4;
                break;
            }
            case Samurai:{
                maxHP = 4005;
                atk = 368;
                numSpecialTurns = 3;
                break;
            }
            case BlackMage:{
                maxHP = 4175;
                atk = 303;
                numSpecialTurns = 4;
                break;
            }
            case Phoenix:{
                maxHP = 4175;
                atk = 209;
                numSpecialTurns = 8;
                break;
            }
            case Cherry:{
                maxHP = 3560;
                atk = 198;
                numSpecialTurns = 4;
                break;
            }
        }
        currentHP = maxHP;
    }

    /*
        Fields Getters and Setters
     */

    /**
     * Returns type of this player
     *
     * @return type or class of this player
     */
    Player.PlayerType getType(){
        return type;
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
     * Returns max HP of this player.
     *
     * @return maxHP of this player
     */
    public double getMaxHP(){
        return maxHP;
    }

    /**
     * This method returns getTurnsSinceStartSleeping of this player.
     *
     * @return TurnsSinceStartSleeping
     */
    public int getTurnsSinceStartSleeping(){
        return turnsSinceStartSleeping;
    }

    /**
     * Returns Internal Turn of this player.
     *
     * @return internalTurn
     */
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

    /**
     * Set TurnsSinceStartSleeping
     *
     * @param turnsSinceStartSleeping a target integer for TurnsSinceStartSleeping
     */
    public void setTurnsSinceStartSleeping(int turnsSinceStartSleeping){
        this.turnsSinceStartSleeping = turnsSinceStartSleeping;
    }

    /**
     * Set sleeping status
     *
     * @param sleeping a boolean of target status
     */
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

    /*
        Turn Utilities Methods
     */

    /**
     * Set internalTurn of this player to 1, it has to be used after This player takeAction()
     */
    private void fillInternalTurn(){
        this.internalTurn = 1;
    }

    /**
     * Set internalTurn of this player to 0, it has to be used before This player takeAction()
     */
    private void emptyInternalTurn(){
        this.internalTurn = 0;
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

    /*
        Methods for Player to TAKE ACTIONS
     */

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
            //Use player's special ability.
            useSpecialAbility(arena.getFriendlyTeamPlayers(this), arena.getOpponentTeamPlayers(this));
        }else{
            //Attack other player.
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
     * @return A target player position
     */
    private PlayerPosition findTargetablePlayers(TargetingMode targetingMode){
        int i, j;
        Player[][] targetTeamPlayers;

        switch(targetingMode){      /* Targeting Mode Classification */
            case attack_LowestHP:{      /* Called by attack() and curse() */

                //Assigns target team which is opponent team.
                targetTeamPlayers = arena.getOpponentTeamPlayers(this);

                ArrayList<PlayerPosition> tauntingPlayerPosition = new ArrayList<>();
                //Search for all player who is taunting (They have higher priority)
                for(i = 0; i < Arena.NUMBER_OF_ROWS; i++){
                    for(j = 0; j < arena.getNumRowPlayers(); j++){
                        if(targetTeamPlayers[i][j].isAlive() && targetTeamPlayers[i][j].isTaunting){
                            tauntingPlayerPosition.add(new PlayerPosition(i, j));
                        }
                    }
                }

                if(tauntingPlayerPosition.size() > 1){      /* If there are more than 1 player taunting. */
                    //Returns the lowest position, see searchLowestPosition(), if there are multiple taunt target with same HP.
                    return searchLowestPosition(tauntingPlayerPosition, targetTeamPlayers);
                }else if(tauntingPlayerPosition.size() == 1){       /* If there is only 1 player taunting. */
                    //Returns the only one who is taunting
                    return tauntingPlayerPosition.get(0);
                }

                /* If there is no player taunting. */
                //Search for THE LOWEST HP
                double minHPValue = MIN_HP_VALUE;
                i = arena.getFrontRow(targetTeamPlayers);       /* A player can only attack the front row */
                for(j = 0; j < arena.getNumRowPlayers(); j++){
                    if(targetTeamPlayers[i][j].isAlive() && (targetTeamPlayers[i][j].currentHP <= minHPValue)){
                        minHPValue = targetTeamPlayers[i][j].currentHP;
                    }
                }

                //If there are any player whose HP is equals to the lowest, stores their position.
                ArrayList<PlayerPosition> playerPositionArrayList = new ArrayList<>();
                searchLowestHPinArray(TargetingMode.attack_LowestHP, playerPositionArrayList, targetTeamPlayers, minHPValue);

                if(debug_TargetSearching){    /* For Debugging Purposes only */
                    System.out.println("Total HP: " + Arena.getSumHP(targetTeamPlayers));
                }

                if(playerPositionArrayList.size() == 1){        /* If there are only 1 player who has the lowest HP. */
                    return playerPositionArrayList.get(0);
                }else if(playerPositionArrayList.size() == 0){      /* On the front row, if all player is dead */
                    if(Arena.getSumHP(targetTeamPlayers) == 0.0){
                        //If all player of the opponent team has sum of current HP = 0
                        return null;
                    }
                }

                //Returns the lowest position, see searchLowestPosition()
                return searchLowestPosition(playerPositionArrayList, targetTeamPlayers);

            }
            case heal_teamLowestHP:{    /* Called by heal() */

                //Assigns target team which is ally team.
                targetTeamPlayers = arena.getFriendlyTeamPlayers(this);
                double minHPValue = MIN_HP_VALUE;

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

                //Search player who has HP equals to that Lowest HP
                ArrayList<PlayerPosition> playerPositionArrayList = new ArrayList<>();
                searchLowestHPinArray(TargetingMode.heal_teamLowestHP, playerPositionArrayList, targetTeamPlayers, minHPValue);

                if(playerPositionArrayList.size() == 1){    /* If there are only 1 player who has the lowest HP. */
                    //Returns the only element in ArrayList
                    return playerPositionArrayList.get(0);
                }else if(playerPositionArrayList.size() > 1){
                    //Returns the lowest position, see searchLowestPosition()
                    return searchLowestPosition(playerPositionArrayList, targetTeamPlayers);
                }

            }
            case selectAllTarget:{
                //It is illegal to call this function in this mode. If you call it, nothing will happen.
                break;
            }
            case revive_teamLowestHP:{      /* Called by revive() */

                //Assigns target team which is ally team.
                targetTeamPlayers = arena.getFriendlyTeamPlayers(this);
                int countDead = 0;      /* Count Number of Dead Player */

                // Find number of dead players
                for(i = 0; i < Arena.NUMBER_OF_ROWS; i++){
                    for(j = 0; j < arena.getNumRowPlayers(); j++){
                        if(!targetTeamPlayers[i][j].isAlive()){
                            countDead++;
                        }
                    }
                }

                if(countDead == 0){     /* If there is no one to revive */
                    return null;
                }

                //Search player who has HP equals to 0 HP
                ArrayList<PlayerPosition> deadPositionsArrayList = new ArrayList<>();
                searchLowestHPinArray(TargetingMode.revive_teamLowestHP, deadPositionsArrayList, targetTeamPlayers, 0.0);

                PlayerPosition minPosition;
                if(countDead > 1){
                    //Returns the lowest position, see searchLowestPosition()
                    return searchLowestPosition(deadPositionsArrayList, targetTeamPlayers);
                }else{
                    minPosition = deadPositionsArrayList.get(0);
                }
                //Returns null if there is no target.
                return minPosition;
            }
        }
        return null;
    }

    /**
     * This method will select targets for any special ability which require or has Area of Effects
     *
     * @param targetingMode The mode to get Target based on the player action
     *
     * @return An Array for player positions
     */
    private ArrayList<PlayerPosition> findMultipleTargetablePlayers(TargetingMode targetingMode){
        int i, j;
        Player[][] targetTeamPlayers = arena.getOpponentTeamPlayers(this);
        ArrayList<PlayerPosition> playerPositionArrayList = new ArrayList<>();

        if(targetingMode == TargetingMode.selectAllTarget){
            //Cycle through all player
            for(i = 0; i < Arena.NUMBER_OF_ROWS; i++){
                for(j = 0; j < arena.getNumRowPlayers(); j++){
                    if(targetTeamPlayers[i][j].isAlive()){
                        playerPositionArrayList.add(new PlayerPosition(i, j));      /* Only alive players are eligible */
                    }
                }
            }
        }
        return playerPositionArrayList;
    }

    /**
     * Deal a certain damage based on player's Attack Taunting will attract this move.
     *
     * @param theirTeam the array for victim team
     */
    private void attack(Player[][] theirTeam){
        //Find all possible targets first
        PlayerPosition targetPosition = findTargetablePlayers(TargetingMode.attack_LowestHP);

        //If there is no target stop and return
        if(targetPosition == null){
            return;
        }
        Player target = theirTeam[targetPosition.getI()][targetPosition.getJ()];

        if(debug_ActionMessages){     /* For Debugging Purposes only */
            System.out.println(toStringDebug("attacks", target));
        }

        target.currentHP -= atk;            /* Reduces target's current HP by the number of this player's attack */
        if(target.currentHP < 0){           /* If the target's HP reduce to less than 0, set it back to 0 */
            // For debugging purpose only!
            //if(debug_ActionMessages){
            //    System.out.println("# " + getOpponentTeam().name() + target.playerPosition.toReadableString() + " {" + target.type.name() + "} " + " dies");
            //}
            target.currentHP = 0;
            cleanBuffWhenDie(target);       /* Clean any buff on the target player since the target is killed */
        }
    }

    /**
     * Attack the target twice. Taunting will attract this move.
     *
     * @param theirTeam the array for victim team
     */
    private void doubleSlash(Player[][] theirTeam){
        //Find all possible targets first
        PlayerPosition targetPosition = findTargetablePlayers(TargetingMode.attack_LowestHP);

        //If there is no target stop and return
        if(targetPosition == null){
            return;
        }
        Player target = theirTeam[targetPosition.getI()][targetPosition.getJ()];

        if(debug_ActionMessages){     /* For Debugging Purposes only */
            System.out.println(toStringDebug("double-slashes", target));
        }

        for(int i = 1; i <= 2; i++){            /* Attacking twice */
            target.currentHP -= atk;            /* Reduces target's current HP by the number of this player's attack */
            if(target.currentHP < 0){           /* If the target's HP reduce to less than 0, set it back to 0 */
                //if(debug_ActionMessages){
                //   System.out.println("# " + getPlayerTeam().name() + playerPosition.toReadableString() + " {" + type.name() + "} " + " dies");
                //}
                target.currentHP = 0;
                cleanBuffWhenDie(target);       /* Clean any buff on the target player since the target is killed */
            }
        }

    }

    /**
     * Make the players of the opposite team attack (including double-slashing and cursing) himself
     * for one internal turn.
     */
    private void taunt(){
        if(debug_ActionMessages){     /* For Debugging Purposes only */
            System.out.println("# " + getPlayerTeam().name() + playerPosition.toReadableString() + " {" + type.name() + "} " + "is taunting ");
        }
        isTaunting = true;
    }

    /**
     * Heal the target by 25% of max HP.
     *
     * @param myTeam the array for allied team
     */
    private void heal(Player[][] myTeam){
        //Find all possible targets first
        PlayerPosition targetPosition = findTargetablePlayers(TargetingMode.heal_teamLowestHP);

        //If there is no target stop and return
        if(targetPosition == null){
            if(debug_TargetSearching){     /* For Debugging Purposes only */
                System.out.println("No target for healing");
            }
            return;
        }
        Player target = myTeam[targetPosition.getI()][targetPosition.getJ()];

        target.currentHP += (0.25 * target.maxHP);      /* Heals target by 25% of target's max HP */
        if(target.currentHP > target.maxHP){        /* If current HP exceeds max HP, set it to equal value of max HP */
            target.currentHP = target.maxHP;
        }

        if(debug_ActionMessages){     /* For Debugging Purposes only */
            System.out.println(toStringDebug("heals", target));
        }

    }

    /**
     * Heal dead player the target by 30% of max HP.
     *
     * @param myTeam the array for allied team
     */
    private void revive(Player[][] myTeam){
        PlayerPosition targetPosition = findTargetablePlayers(TargetingMode.revive_teamLowestHP);

        //If there is no target stop and return
        if(targetPosition == null){
            return;
        }
        Player target = myTeam[targetPosition.getI()][targetPosition.getJ()];

        target.currentHP += 0.30 * target.maxHP;        /* Heals target by 30% of target's max HP */

        if(target.currentHP > target.maxHP){        /* THIS IS REDUNDANT. If current HP exceeds max HP, set it to equal value of max HP */
            target.currentHP = target.maxHP;
        }

        if(debug_ActionMessages){     /* For Debugging Purposes only */
            System.out.println(toStringDebug("revives", target));
        }
    }

    /**
     * Give curse status effect to an opponent
     *
     * @param theirTeam opponent team
     */
    private void curse(Player[][] theirTeam){
        PlayerPosition targetPosition = findTargetablePlayers(TargetingMode.attack_LowestHP);

        //If there is no target stop and return
        if(targetPosition == null){
            return;
        }
        Player target = theirTeam[targetPosition.getI()][targetPosition.getJ()];

        this.iAmCursing = target;       /* The Curser will remember their target. */
        target.isCursed = true;     /* The Target is now cursed. */
        target.cursedBy = Player.this;      /* The Curse Target will remember their curser. */

        if(debug_ActionMessages){     /* For Debugging Purposes only */
            System.out.println(toStringDebug("curses", target));
        }
    }

    /**
     * Make the players of the opposite team sleep for one internal turn
     *
     * @param theirTeam opponent team
     */
    private void fortuneCookies(Player[][] theirTeam){
        int i, j, countPlayerPosition = 0;

        if(debug_ActionMessages){     /* For Debugging Purposes only */
            System.out.println("# " + getPlayerTeam().name() + playerPosition.toReadableString()
                                       + " {" + type.name() + "} " + " uses fortune cookies to " + Arena.team_toString(this.getOpponentTeam()));
        }
        ArrayList<PlayerPosition> playerPositionArrayList = findMultipleTargetablePlayers(TargetingMode.selectAllTarget);

        if(debug_TargetSearching){    /* For Debugging Purposes only */
            for(i = 0; i < playerPositionArrayList.size(); i++){
                System.out.println("#" + i + ": " + playerPositionArrayList.get(i).toString());
            }
            System.out.println("playerPositionArrayList.size() = " + playerPositionArrayList.size());
        }

        //Cycles through every member
        for(i = 0; i < Arena.NUMBER_OF_ROWS; i++){
            for(j = 0; j < arena.getNumRowPlayers(); j++){
                if(playerPositionArrayList.get(countPlayerPosition).getI() == i
                        && playerPositionArrayList.get(countPlayerPosition).getJ() == j && !theirTeam[i][j].isSleeping){
                    theirTeam[i][j].turnsSinceStartSleeping = 0;
                    theirTeam[i][j].isSleeping = true;
                    countPlayerPosition++;
                    if(countPlayerPosition == playerPositionArrayList.size()){      /* If every target is sleep, stop the loop */
                        break;
                    }
                }
            }
        }
    }

    /**
     * This method's Algorithm may look a little bit overthought. I know there is a more simple way but for the flexibility
     * of the codebase, I decided leave it like this.
     *
     * By giving an ArrayList of PlayerPosition and Team Player Array, the method can search for lowest Position.
     *
     * @param playerPositionArrayList ArrayList of Player Position
     * @param targetTeamPlayers       2-Dimensional Array of Target players
     *
     * @return Minimum position
     */
    private PlayerPosition searchLowestPosition(ArrayList<PlayerPosition> playerPositionArrayList, Player[][] targetTeamPlayers){
        int a, x, y;
        PlayerPosition minPosition = playerPositionArrayList.get(0);        /* Get the first element of the ArrayList */
        for(a = 0; a < playerPositionArrayList.size(); a++){        /* Go through every element of ArrayList */
            x = playerPositionArrayList.get(a).getI();      /* Assign x to i value of element number #a */
            y = playerPositionArrayList.get(a).getJ();      /* Assign y to j value of element number #a */

            if(targetTeamPlayers[x][y].playerPosition.isLowerThan(minPosition)){
                minPosition = targetTeamPlayers[x][y].playerPosition;
            }
        }
        return minPosition;
    }

    /**
     * This method will search for the Lowest HP in an Array
     *
     * @param targetingMode           Depending on which action calls this function
     * @param playerPositionArrayList target ArrayList for modifying
     * @param targetTeamPlayers       2-Dimensional Array of Target players for searching
     * @param minValue                Minimum Value for searching
     */
    private void searchLowestHPinArray(TargetingMode targetingMode, ArrayList<PlayerPosition> playerPositionArrayList, Player[][] targetTeamPlayers, double minValue){
        int i, j;
        if(targetingMode == TargetingMode.attack_LowestHP){
            i = arena.getFrontRow(targetTeamPlayers);
            for(j = 0; j < arena.getNumRowPlayers(); j++){      /* Find and get the position of player who has current HP equal to the lowest HP in THE FRONT ROW */
                if(targetTeamPlayers[i][j].isAlive() && targetTeamPlayers[i][j].currentHP == minValue){
                    playerPositionArrayList.add(new PlayerPosition(i, j));

                    if(debug_TargetSearching){     /* For Debugging Purposes only */
                        System.out.println(playerPositionArrayList.get(playerPositionArrayList.size() - 1).toString());
                    }
                }
            }
        }else if(targetingMode == TargetingMode.heal_teamLowestHP){     /* Find and get the position of player who has current HP equal to the lowest HP */
            for(i = 0; i < Arena.NUMBER_OF_ROWS; i++){
                for(j = 0; j < arena.getNumRowPlayers(); j++){
                    if(targetTeamPlayers[i][j].currentHP == minValue){
                        playerPositionArrayList.add(new PlayerPosition(i, j));
                    }
                }
            }
        }else if(targetingMode == TargetingMode.revive_teamLowestHP){
            for(i = 0; i < Arena.NUMBER_OF_ROWS; i++){      /* Find and get the position of player who has current HP equals to ZERO */
                for(j = 0; j < arena.getNumRowPlayers(); j++){
                    if(!targetTeamPlayers[i][j].isAlive()){
                        playerPositionArrayList.add(new PlayerPosition(i, j));
                    }
                }
            }
        }
        /* No need to return anything, because This method receives ArrayList parameter. Therefore we only need to call add() */
    }

    /**
     * This method handles expiration of several debuff.
     * This will be called only when one internal turn has passed.
     */
    private void statusHandler(Player target){
        if(target.isTaunting){
            isTaunting = false;     /* If the Player is taunting, remove the buff. */
        }

        //If THE CURSER has a CURSE TARGET and THE CURSER has internalTurn = 0, The CURSED TARGET will no longer CURSED.
        if(target.iAmCursing != null){
            target.iAmCursing.isCursed = false;     /* The CURSED TARGET will no longer cursed. */
            target.iAmCursing.cursedBy = null;      /* The CURSED TARGET will no remember the curser. */
            target.iAmCursing = null;       /* The CURSER will no longer remember the cursed target. */
        }

        //If the target is sleeping
        if(target.isSleeping){
            target.isSleeping = false;         /* The target wakes up */
        }
    }

    /*
        Debugging Methods
    */

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
}


/*
 * This java file is part of Project 1 of subject ITCS208 - Object Oriented Programming of Mahidol University, Faculty of ICT
 * Modified by Tanawin Wichit, ID 6088221, a section 1 student.
 */

class PlayerPosition{

    private int i;      /* A position in Array of Player (Player[i][]), known as Row */
    private int j;      /* A position in Array of Player (Player[][j]), known as Player */

    /**
     * The Default Constructor for PlayerPosition
     */
    public PlayerPosition(){
        this.i = 0;
        this.j = 0;
    }

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
     * @return Get the Position that player is in
     */
    public int getJ(){
        return j;
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
     * Set the Position in a Row that player is in
     *
     * @param j New value for Position in a Row
     */
    public void setJ(int j){
        this.j = j;
    }

    /**
     * Compare 2 PlayerPosition if one is lower than another by
     * Comparing Row
     * - If row (i) is lower, then return true
     * - If row is the same, then compare position (j)
     *
     * @param otherPlayerPosition the comparision target
     *
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

    /**
     * Process current i and j to a string
     *
     * @return order pair of i and j
     */
    @Override
    public String toString(){
        return "(" + getI() + ", " + getJ() + ")";
    }

    public String toReadableString(){
        String row;
        if(getI() == 0){
            row = "Front";
        }else{
            row = "Back";
        }
        return "[" + row + "][" + (getJ() + 1) + "]";
    }
}

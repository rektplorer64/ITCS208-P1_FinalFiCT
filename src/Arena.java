import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

public class Arena{

    public enum Row{Front, Back};    //enum for specifying the front or back row

    public enum Team{A, B};        //enum for specifying team A or B

    public static final int NUMBER_OF_ROWS = 2;
    public static int numRowPlayers;

    private static Player[][] teamA = null;    //two dimensional array representing the players of Team A
    private static Player[][] teamB = null;    //two dimensional array representing the players of Team B

    public static final int MAX_ROUNDS = 100;    //Max number of turn
    public static final int MAX_EACH_TYPE = 3;    //Max number of players of each type, in each team.
    private final Path logFile = Paths.get("battle_log.txt");

    private int numRounds = 0;    //keep track of the number of rounds so far

    /**
     * Constructor.
     *
     * @param _numRowPlayers is the number of player in each row.
     */
    public Arena(int _numRowPlayers){
        //INSERT YOUR CODE HERE
        numRowPlayers = _numRowPlayers;


        ////Keep this block of code. You need it for initialize the log file.
        ////(You will learn how to deal with files later)
        try{
            Files.deleteIfExists(logFile);
        }catch(IOException e){
            e.printStackTrace();
        }
        /////////////////////////////////////////

    }

    /**
     * Returns true if "player" is a member of "team", false otherwise.
     * Assumption: team can be either Team.A or Team.B
     *
     * @param player
     * @param team
     * @return
     */
    public boolean isMemberOf(Player player, Team team){
        //INSERT YOUR CODE HERE
        return player.getPlayerTeam() == team;
    }


    /**
     * This methods receives a player configuration (i.e., team, type, row, and position),
     * creates a new player instance, and places him at the specified position.
     *
     * @param team     is either Team.A or Team.B
     * @param pType    is one of the Player.Type  {Healer, Tank, Samurai, BlackMage, Phoenix}
     * @param row      either Row.Front or Row.Back
     * @param position is the position of the player in the row. Note that position starts from 1, 2, 3....
     */
    public void addPlayer(Team team, Player.PlayerType pType, Row row, int position){



        //INSERT YOUR CODE HERE
        if(team == Team.A){
            if(row == Row.Front){
                teamA[1][position - 1] = new Player(pType, team, this);
            }else if(row == Row.Back){
                teamA[2][numRowPlayers + position - 1] = new Player(pType, team, this);
            }
        }else if(team == Team.B){
            if(row == Row.Front){
                teamB[1][position - 1] = new Player(pType, team, this);
            }else if(row == Row.Back){
                teamB[2][numRowPlayers + position - 1] = new Player(pType, team, this);
            }
        }
    }


    /**
     * Validate the players in both Team A and B. Returns true if all of the following conditions hold:
     * <p>
     * 1. All the positions are filled. That is, there each team must have exactly numRow*numRowPlayers players.
     * 2. There can be at most MAX_EACH_TYPE players of each type in each team. For example, if MAX_EACH_TYPE = 3
     * then each team can have at most 3 Healers, 3 Tanks, 3 Samurais, 3 BlackMages, and 3 Phoenixes.
     * <p>
     * Returns true if all the conditions above are satisfied, false otherwise.
     *
     * @return
     */
    public boolean validatePlayers(){
        //INSERT YOUR CODE HERE
        int i, j;

        int A_countMembers;
        int A_countHealer = 0, A_countTanks = 0, A_countSamurais = 0, A_countBlackMages = 0, A_countPhoenixes = 0, A_countCherry = 0;

        int B_countMembers;
        int B_countHealer = 0, B_countTanks = 0, B_countSamurais = 0, B_countBlackMages = 0, B_countPhoenixes = 0, B_countCherry = 0;
        for(i = 0; i < 2; i++){
            for(j = 0; j < numRowPlayers; j++){
                switch(teamA[i][j].getType()){
                    case Healer:
                        A_countHealer++;
                        break;
                    case Tank:
                        A_countTanks++;
                        break;
                    case Samurai:
                        A_countSamurais++;
                        break;
                    case BlackMage:
                        A_countBlackMages++;
                        break;
                    case Phoenix:
                        A_countPhoenixes++;
                        break;
                    case Cherry:
                        A_countCherry++;
                        break;
                }

                switch(teamB[i][j].getType()){
                    case Healer:
                        B_countHealer++;
                        break;
                    case Tank:
                        B_countTanks++;
                        break;
                    case Samurai:
                        B_countSamurais++;
                        break;
                    case BlackMage:
                        B_countBlackMages++;
                        break;
                    case Phoenix:
                        B_countPhoenixes++;
                        break;
                    case Cherry:
                        B_countCherry++;
                        break;
                }
            }
        }

        if((A_countHealer > MAX_EACH_TYPE || A_countTanks > MAX_EACH_TYPE || A_countSamurais > MAX_EACH_TYPE || A_countBlackMages > MAX_EACH_TYPE || A_countPhoenixes > MAX_EACH_TYPE || A_countCherry > MAX_EACH_TYPE) ||
                (B_countHealer > MAX_EACH_TYPE || B_countTanks > MAX_EACH_TYPE || B_countSamurais > MAX_EACH_TYPE || B_countBlackMages > MAX_EACH_TYPE || B_countPhoenixes > MAX_EACH_TYPE || B_countCherry > MAX_EACH_TYPE)){
            return false;
        }

        A_countMembers = A_countHealer + A_countTanks + A_countSamurais + A_countBlackMages + A_countPhoenixes + A_countCherry;
        B_countMembers = B_countHealer + B_countTanks + B_countSamurais + B_countBlackMages + B_countPhoenixes + B_countCherry;

        if(A_countMembers != NUMBER_OF_ROWS * numRowPlayers || B_countMembers != NUMBER_OF_ROWS * numRowPlayers){
            return false;
        }

        return true;
    }


    /**
     * Returns the sum of HP of all the players in the given "team"
     *
     * @param team
     * @return
     */
    public static double getSumHP(Player[][] team){
        int i, j;
        double sumHP = 0.0;
        for(i = 0; i < 2; i++){
            for(j = 0; j < numRowPlayers; j++){
                sumHP += team[i][j].getCurrentHP();
            }
        }
        return sumHP;
    }

    /**
     * Return the team (either teamA or teamB) whose number of alive players is higher than the other.
     * <p>
     * If the two teams have an equal number of alive players, then the team whose sum of HP of all the
     * players is higher is returned.
     * <p>
     * If the sums of HP of all the players of both teams are equal, return teamA.
     *
     * @return teamB
     */
    public Player[][] getWinningTeam(){
        //INSERT YOUR CODE HERE
        int row, player;
        int A_countDead = 0;
        int B_countDead = 0;
        for(row = 0; row < NUMBER_OF_ROWS; row++){
            for(player = 0; player < numRowPlayers; player++){
                if(!teamA[row][player].isAlive()){
                    A_countDead++;
                }
                if(!teamA[row][player].isAlive()){
                    B_countDead++;
                }
            }
        }

        // If every players in Team A died, Team B wins.
        if(A_countDead == NUMBER_OF_ROWS * numRowPlayers){
            return teamB;
        }

        // If every players in Team B died, Team A wins.
        if(B_countDead == NUMBER_OF_ROWS * numRowPlayers){
            return teamA;
        }


        // If every players in TeamA and Team B died, the game is draw.
        if(A_countDead == NUMBER_OF_ROWS * numRowPlayers && B_countDead == NUMBER_OF_ROWS * numRowPlayers){
            return null;
        }

        return null;
    }

    /**
     * This method simulates the battle between teamA and teamB. The method should have a loop that signifies
     * a round of the battle. In each round, each player in teamA invokes the method takeAction(). The players'
     * turns are ordered by its position in the team. Once all the players in teamA have invoked takeAction(),
     * not it is teamB's turn to do the same.
     * <p>
     * The battle terminates if one of the following two conditions is met:
     * <p>
     * 1. All the players in a team has been eliminated.
     * 2. The number of rounds exceeds MAX_ROUNDS
     * <p>
     * After the battle terminates, report the winning team, which is determined by getWinningTeam().
     */
    public void startBattle(){
        //INSERT YOUR CODE HERE
        int row, player;
        for(int turn = 1; turn <= MAX_ROUNDS; turn++){

            for(row = 0; row < NUMBER_OF_ROWS; row++){
                for(player = 0; player < numRowPlayers; player++){
                    teamA[row][player].PlayerActions.takeAction(this);
                }
            }

            for(row = 0; row < NUMBER_OF_ROWS; row++){
                for(player = 0; player < numRowPlayers; player++){
                    teamA[row][player].PlayerActions.takeAction(this);
                }
            }

        }
    }


    /**
     * This method displays the current area state, and is already implemented for you.
     * In startBattle(), you should call this method once before the battle starts, and
     * after each round ends.
     *
     * @param arena
     * @param verbose
     */
    public static void displayArea(Arena arena, boolean verbose){
        StringBuilder str = new StringBuilder();
        if(verbose){
            str.append(String.format("%43s   %40s", "Team A", "") + "\t\t" + String.format("%-38s%-40s", "", "Team B") + "\n");
            str.append(String.format("%43s", "BACK ROW") + String.format("%43s", "FRONT ROW") + "  |  " + String.format("%-43s", "FRONT ROW") + "\t" + String.format("%-43s", "BACK ROW") + "\n");
            for(int i = 0; i < numRowPlayers; i++){
                str.append(String.format("%43s", arena.teamA[1][i]) + String.format("%43s", arena.teamA[0][i]) + "  |  " + String.format("%-43s", arena.teamB[0][i]) + String.format("%-43s", arena.teamB[1][i]) + "\n");
            }
        }

        str.append("@ Total HP of Team A = " + getSumHP(arena.teamA) + ". @ Total HP of Team B = " + getSumHP(arena.teamB) + "\n\n");
        System.out.print(str.toString());


    }

    /**
     * This method writes a log (as round number, sum of HP of teamA, and sum of HP of teamB) into the log file.
     * You are not to modify this method, however, this method must be call by startBattle() after each round.
     * <p>
     * The output file will be tested against the auto-grader, so make sure the output look something like:
     * <p>
     * 1	47415.0	49923.0
     * 2	44977.0	46990.0
     * 3	42092.0	43525.0
     * 4	44408.0	43210.0
     * <p>
     * Where the numbers of the first, second, and third columns specify round numbers, sum of HP of teamA, and sum of HP of teamB respectively.
     */
    private void logAfterEachRound(){
        try{
            Files.write(logFile, Arrays.asList(new String[]{numRounds + "\t" + getSumHP(teamA) + "\t" + getSumHP(teamB)}), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
        }catch(IOException e){
            e.printStackTrace();
        }

    }

    public static Player[][] getOpponentTeamPlayers(Player player){
        if(player.getPlayerTeam() == Team.A){
            return teamB;
        }else if(player.getPlayerTeam() == Team.B){
            return teamA;
        }
        return null;
    }

    public static Player[][] getFriendlyTeamPlayers(Player player){
        if(player.getPlayerTeam() == Team.A){
            return teamA;
        }else if(player.getPlayerTeam() == Team.B){
            return teamB;
        }
        return null;
    }

    public static int getFrontRow(Player[][] teamPlayers){
        int i, j;
        int firstRowCountAlive = 0;
        for(i = 0; i < 1; i++){
            for(j = 0; j < Arena.numRowPlayers; j++){
                if(teamPlayers[0][j].isAlive()){
                    firstRowCountAlive++;
                }
            }
        }

        if(firstRowCountAlive > 0){
            return 0;
        }else{
            return 1;
        }
    }
}

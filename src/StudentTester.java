
/*
 * This java file is part of Project 1 of subject ITCS208 - Object Oriented Programming of Mahidol University, Faculty of ICT
 * Modified by Tanawin Wichit, ID 6088221, a section 1 student.
 */

public class StudentTester {

    public static final boolean debug_TargetSearching = false;        /* For debugging purposes only, this variable
                                                                         controls the visibility of target searching */

    public static final boolean debug_ActionMessages = true;         /* For debugging purposes only, this variable
                                                                         controls the visibility of battle log */

    public static void simpleCase(){
        //Instantiate an arena
        Arena battleField = new Arena(2);

        //Add players to the front row of Team A
        battleField.addPlayer(Arena.Team.A, Player.PlayerType.Samurai, Arena.Row.Front, 1);
        battleField.addPlayer(Arena.Team.A, Player.PlayerType.Samurai, Arena.Row.Front, 2);

        //Add players to the Back row of Team A
        battleField.addPlayer(Arena.Team.A, Player.PlayerType.Healer, Arena.Row.Back, 1);
        battleField.addPlayer(Arena.Team.A, Player.PlayerType.BlackMage, Arena.Row.Back, 2);


        //Add players to the front row of Team B
        battleField.addPlayer(Arena.Team.B, Player.PlayerType.Tank, Arena.Row.Front, 1);
        battleField.addPlayer(Arena.Team.B, Player.PlayerType.Tank, Arena.Row.Front, 2);

        //Add players to the back row of Team B
        battleField.addPlayer(Arena.Team.B, Player.PlayerType.Tank, Arena.Row.Back, 1);
        battleField.addPlayer(Arena.Team.B, Player.PlayerType.Phoenix, Arena.Row.Back, 2);

        //check if player selection does not violate any rules
        boolean okToStart = battleField.validatePlayers();

        //start battle
        if(okToStart){
            System.out.println("@ Welcome to Final FiCT. An automated fantasy battle simulation.");
            System.out.println("@ Please join us to welcome both Arena.Team A and Arena.Team B.");
            Arena.displayArea(battleField, true);

            battleField.startBattle();

        }else{
            System.out.println("@ Error: Something is wrong with your player configuration. Please check.");
        }
    }

    public static void advancedCase(){
        Arena battleField = new Arena(5);

        //Add players to the front row of Team A
        battleField.addPlayer(Arena.Team.A, Player.PlayerType.Samurai, Arena.Row.Front, 1);
        battleField.addPlayer(Arena.Team.A, Player.PlayerType.Tank, Arena.Row.Front, 2);
        battleField.addPlayer(Arena.Team.A, Player.PlayerType.Samurai, Arena.Row.Front, 3);
        battleField.addPlayer(Arena.Team.A, Player.PlayerType.Tank, Arena.Row.Front, 4);
        battleField.addPlayer(Arena.Team.A, Player.PlayerType.Healer, Arena.Row.Front, 5);

        //Add players to the Back row of Team A
        battleField.addPlayer(Arena.Team.A, Player.PlayerType.BlackMage, Arena.Row.Back, 1);
        battleField.addPlayer(Arena.Team.A, Player.PlayerType.Tank, Arena.Row.Back, 2);
        battleField.addPlayer(Arena.Team.A, Player.PlayerType.BlackMage, Arena.Row.Back, 3);
        battleField.addPlayer(Arena.Team.A, Player.PlayerType.Cherry, Arena.Row.Back, 4);
        battleField.addPlayer(Arena.Team.A, Player.PlayerType.Samurai, Arena.Row.Back, 5);


        //Add players to the front row of Team B
        battleField.addPlayer(Arena.Team.B, Player.PlayerType.Tank, Arena.Row.Front, 1);
        battleField.addPlayer(Arena.Team.B, Player.PlayerType.Samurai, Arena.Row.Front, 2);
        battleField.addPlayer(Arena.Team.B, Player.PlayerType.Tank, Arena.Row.Front, 3);
        battleField.addPlayer(Arena.Team.B, Player.PlayerType.Samurai, Arena.Row.Front, 4);
        battleField.addPlayer(Arena.Team.B, Player.PlayerType.Tank, Arena.Row.Front, 5);

        //Add players to the back row of Team B
        battleField.addPlayer(Arena.Team.B, Player.PlayerType.Healer, Arena.Row.Back, 1);
        battleField.addPlayer(Arena.Team.B, Player.PlayerType.Healer, Arena.Row.Back, 2);
        battleField.addPlayer(Arena.Team.B, Player.PlayerType.Phoenix, Arena.Row.Back, 3);
        battleField.addPlayer(Arena.Team.B, Player.PlayerType.Phoenix, Arena.Row.Back, 4);
        battleField.addPlayer(Arena.Team.B, Player.PlayerType.Phoenix, Arena.Row.Back, 5);


        //check for sanity
        boolean okToStart = battleField.validatePlayers();


        //start battle
        if(okToStart){
            System.out.println("@ Welcome to Final FiCT. An automated fantasy battle simulation.");
            System.out.println("@ Please join us to welcome both Arena.Team A and Arena.Team B.");
            Arena.displayArea(battleField, true);

            battleField.startBattle();

        }else{
            System.out.println("@ Error: Something is wrong with your player configuration. Please check.");
        }
    }

    public static void bonusCredit(){

        Arena battleField = new Arena(5);

		/*
		 * In order to earn bonus credits, you need to configure the players of your team (Team A)
		 * so that your team wins the battle against the instructors' team (Team B).
		 */


        //////////////////// CONFIGURE YOUR TEAM HERE ////////////////////////////////////
        //Add players to the front row of Team A
        battleField.addPlayer(Arena.Team.A, Player.PlayerType.Samurai, Arena.Row.Front, 1);
        battleField.addPlayer(Arena.Team.A, Player.PlayerType.Tank, Arena.Row.Front, 2);
        battleField.addPlayer(Arena.Team.A, Player.PlayerType.Samurai, Arena.Row.Front, 3);
        battleField.addPlayer(Arena.Team.A, Player.PlayerType.Tank, Arena.Row.Front, 4);
        battleField.addPlayer(Arena.Team.A, Player.PlayerType.Samurai, Arena.Row.Front, 5);

        //Add players to the Back row of Team A
        battleField.addPlayer(Arena.Team.A, Player.PlayerType.Healer, Arena.Row.Back, 1);
        battleField.addPlayer(Arena.Team.A, Player.PlayerType.Phoenix, Arena.Row.Back, 2);
        battleField.addPlayer(Arena.Team.A, Player.PlayerType.Cherry, Arena.Row.Back, 3);
        battleField.addPlayer(Arena.Team.A, Player.PlayerType.Phoenix, Arena.Row.Back, 4);
        battleField.addPlayer(Arena.Team.A, Player.PlayerType.Healer, Arena.Row.Back, 5);
        //////////////////////////////////////////////////////////////////////////////////


        ///////////////////// INSTRUCTORS' TEAM //////////////////////////////////////
        battleField.addPlayer(Arena.Team.B, Player.PlayerType.Samurai, Arena.Row.Front, 1);
        battleField.addPlayer(Arena.Team.B, Player.PlayerType.Tank, Arena.Row.Front, 2);
        battleField.addPlayer(Arena.Team.B, Player.PlayerType.BlackMage, Arena.Row.Front, 3);
        battleField.addPlayer(Arena.Team.B, Player.PlayerType.Samurai, Arena.Row.Front, 4);
        battleField.addPlayer(Arena.Team.B, Player.PlayerType.Samurai, Arena.Row.Front, 5);

        battleField.addPlayer(Arena.Team.B, Player.PlayerType.Healer, Arena.Row.Back, 1);
        battleField.addPlayer(Arena.Team.B, Player.PlayerType.Healer, Arena.Row.Back, 2);
        battleField.addPlayer(Arena.Team.B, Player.PlayerType.Tank, Arena.Row.Back, 3);
        battleField.addPlayer(Arena.Team.B, Player.PlayerType.Phoenix, Arena.Row.Back, 4);
        battleField.addPlayer(Arena.Team.B, Player.PlayerType.Cherry, Arena.Row.Back, 5);
        //////////////////////////////////////////////////////////////////////////////

        //check for sanity
        boolean okToStart = battleField.validatePlayers();


        //start battle
        if(okToStart){
            System.out.println("@ Welcome to Final FiCT. An automated fantasy battle simulation.");
            System.out.println("@ This is a special battle to determine your worthiness to obtain a heap of mana (aka. Bonus Scores)");
            System.out.println("@ Please join us to welcome both Your team (Team A) and Instructors' Team (Team B).");
            Arena.displayArea(battleField, true);

            battleField.startBattle();
            if(battleField.isMemberOf(battleField.getWinningTeam()[0][0], Arena.Team.A)){
                System.out.println("@ !!!!!!!!!!!!!!!!!!! VICTORY !!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println("Congratulations. Don't forget to report your team configuration to obtain a heap of mana.");
            }else{
                System.out.println("@ -------- Awww. Your team was defeated. Don't Give Up :) --------");
            }

        }else{
            System.out.println("@ Error: Something is wrong with your player configuration. Please check.");
        }
    }

    public static void main(String[] args){
        /// Your test cases
        //simpleCase();
        advancedCase();


        ////// BONUS CREDIT: Beat Our Team /////
        //bonusCredit();


    }
}

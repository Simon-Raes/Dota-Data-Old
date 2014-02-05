package util;

/**
 * Created by Simon on 2/02/14.
 */
public class GameModes {

    public static String getLobbyType(String number){

        String lobbyType = "Unknown lobby type";

        if(number.equals("-1")){
            lobbyType = "Invalid";
        }
        if(number.equals("0")){
            lobbyType = "Public matchmaking";
        }
        if(number.equals("1")){
            lobbyType = "Practice";
        }
        if(number.equals("2")){
            lobbyType = "Tournament";
        }
        if(number.equals("3")){
            lobbyType = "Tutorial";
        }
        if(number.equals("4")){
            lobbyType = "Co-op with bots";
        }
        if(number.equals("5")){
            lobbyType = "Team matchmaking";
        }
        if(number.equals("6")){
            lobbyType = "Solo queue matchmaking";
        }
        if(number.equals("7")){
            lobbyType = "Ranked match";
        }
        return lobbyType;
    }

    public static String getGameMode(String number){


        String gameMode = "Unknown gamemode";

        if(number.equals("0")){
            gameMode = "Unknown gamemode"; //DOTA_GAMEMODE_NONE
        }
        if(number.equals("1")){
            gameMode = "All Pick";
        }
        if(number.equals("2")){
            gameMode = "Captain's Mode";
        }
        if(number.equals("3")){
            gameMode = "Random Draft";
        }
        if(number.equals("4")){
            gameMode = "Single Draft";
        }
        if(number.equals("5")){
            gameMode = "All Random";
        }
        if(number.equals("6")){
            gameMode = "Intro";
        }
        if(number.equals("7")){
            gameMode = "Diretide";
        }
        if(number.equals("8")){
            gameMode = "Reverse Captain's Mode";
        }
        if(number.equals("9")){
            gameMode = "Greeviling";
        }
        if(number.equals("10")){
            gameMode = "Tutorial";
        }
        if(number.equals("11")){
            gameMode = "Mid Only";
        }
        if(number.equals("12")){
            gameMode = "Least Played";
        }
        if(number.equals("13")){
            gameMode = "Limited Hero Pool";
        }
        if(number.equals("14")){
            gameMode = "FH";
        }
        if(number.equals("15")){
            gameMode = "Custom gamemode";
        }
        if(number.equals("16")){
            gameMode = "Captain's Draft";
        }
        if(number.equals("17")){
            gameMode = "Ability Draft";
        }

        return gameMode;
    }
}

package be.simonraes.dotadata.util;

import java.util.HashMap;

/**
 * Created by Simon on 2/02/14.
 */
public class GameModes {

    private static HashMap<String, String> lobbyTypes = new HashMap<String, String>();
    private static HashMap<String, String> gameModes = new HashMap<String, String>();


    private static void initLobbyTypesHashMap() {
        lobbyTypes.put("-1", "Invalid");
        lobbyTypes.put("0", "Public matchmaking");
        lobbyTypes.put("1", "Practice");
        lobbyTypes.put("2", "Tournament");
        lobbyTypes.put("3", "Tutorial");
        lobbyTypes.put("4", "Co-op with bots");
        lobbyTypes.put("5", "Team matchmaking");
        lobbyTypes.put("6", "Solo queue matchmaking");
        lobbyTypes.put("7", "Ranked Match");
    }

    public static String getLobbyType(String number) {

        String lobbyType = "Unknown lobby type";

        if (number.equals("-1")) {
            lobbyType = "Invalid";
        }
        if (number.equals("0")) {
            lobbyType = "Public matchmaking";
        }
        if (number.equals("1")) {
            lobbyType = "Practice";
        }
        if (number.equals("2")) {
            lobbyType = "Tournament";
        }
        if (number.equals("3")) {
            lobbyType = "Tutorial";
        }
        if (number.equals("4")) {
            lobbyType = "Co-op with bots";
        }
        if (number.equals("5")) {
            lobbyType = "Team matchmaking";
        }
        if (number.equals("6")) {
            lobbyType = "Solo queue matchmaking";
        }
        if (number.equals("7")) {
            lobbyType = "Ranked match";
        }
        return lobbyType;
    }

    private static void initGameModesHashMap() {
        gameModes.put("0", "Unknown gamemode");
        gameModes.put("1", "All Pick");
        gameModes.put("2", "Captain's Mode");
        gameModes.put("3", "Random Draft");
        gameModes.put("4", "Single Draft");
        gameModes.put("5", "All Random");
        gameModes.put("6", "Intro");
        gameModes.put("7", "Diretide");
        gameModes.put("8", "Reverse Captain's Mode");
        gameModes.put("9", "Greeviling");
        gameModes.put("10", "Tutorial");
        gameModes.put("11", "Mid only");
        gameModes.put("12", "Least Played");
        gameModes.put("13", "Limited Hero Pool");
        gameModes.put("14", "Compendium");
        gameModes.put("15", "Custom Gamemode");
        gameModes.put("16", "Captain's Draft");
        gameModes.put("17", "Balanced Draft");
        gameModes.put("18", "Ability Draft");

    }

    public static String getGameMode(String number) {

        String gameMode = "Unknown gamemode";

        if (number.equals("0")) {
            gameMode = "Unknown gamemode"; //DOTA_GAMEMODE_NONE
        }
        if (number.equals("1")) {
            gameMode = "All Pick";
        }
        if (number.equals("2")) {
            gameMode = "Captain's Mode";
        }
        if (number.equals("3")) {
            gameMode = "Random Draft";
        }
        if (number.equals("4")) {
            gameMode = "Single Draft";
        }
        if (number.equals("5")) {
            gameMode = "All Random";
        }
        if (number.equals("6")) {
            gameMode = "Intro";
        }
        if (number.equals("7")) {
            gameMode = "Diretide";
        }
        if (number.equals("8")) {
            gameMode = "Reverse Captain's Mode";
        }
        if (number.equals("9")) {
            gameMode = "Greeviling";
        }
        if (number.equals("10")) {
            gameMode = "Tutorial";
        }
        if (number.equals("11")) {
            gameMode = "Mid Only";
        }
        if (number.equals("12")) {
            gameMode = "Least Played";
        }
        if (number.equals("13")) {
            gameMode = "Limited Hero Pool";
        }
        if (number.equals("14")) {
            gameMode = "Compendium";
        }
        if (number.equals("15")) {
            gameMode = "Custom gamemode";
        }
        if (number.equals("16")) {
            gameMode = "Captain's Draft";
        }
        if (number.equals("17")) {
            gameMode = "Balanced Draft";
        }
        if (number.equals("18")) {
            gameMode = "Ability Draft";
        }

        return gameMode;
    }

    public static HashMap<String, String> getLobbyTypes() {
        if (lobbyTypes.size() < 1) {
            initLobbyTypesHashMap();
        }
        return lobbyTypes;
    }

    public static HashMap<String, String> getGameModes() {
        if (gameModes.size() < 1) {
            initGameModesHashMap();
        }
        return gameModes;
    }
}

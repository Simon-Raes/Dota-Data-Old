package be.simonraes.dotadata.util;

import java.util.HashMap;

public class HeroList {
    private static HashMap<String, String> heroes = new HashMap<String, String>();
    private static HashMap<String, String> heroimages = new HashMap<String, String>();

    //todo: get hero info from GetHeroes API and store it (in db) - needs solution for hero images
    //heroes, id and name can be downloaded from
    //https://api.steampowered.com/IEconDOTA2_570/GetHeroes/v0001/?key=EB5773FAAF039592D9383FA104EEA55D&language=en_us
    //hero images can be obtained from
    //http://cdn.dota2.com/apps/dota2/images/heroes/<name>_<suffix>

    private static void initHeroesHashMap() {
        heroes.put("1", "Anti-Mage");
        heroes.put("2", "Axe");
        heroes.put("3", "Bane");
        heroes.put("4", "Bloodseeker");
        heroes.put("5", "Crystal Maiden");
        heroes.put("6", "Drow Ranger");
        heroes.put("7", "Earthshaker");
        heroes.put("8", "Juggernaut");
        heroes.put("9", "Mirana");
        heroes.put("10", "Morphling");
        heroes.put("11", "Shadow Fiend");
        heroes.put("12", "Phantom Lancer");
        heroes.put("13", "Puck");
        heroes.put("14", "Pudge");
        heroes.put("15", "Razor");
        heroes.put("16", "Sand King");
        heroes.put("17", "Storm Spirit");
        heroes.put("18", "Sven");
        heroes.put("19", "Tiny");
        heroes.put("20", "Vengeful Spirit");
        heroes.put("21", "Windrunner");
        heroes.put("22", "Zeus");
        heroes.put("23", "Kunkka");
        heroes.put("24", "Unknown"); //Hero 24 doesn't exist
        heroes.put("25", "Lina");
        heroes.put("26", "Lion");
        heroes.put("27", "Shadow Shaman");
        heroes.put("28", "Slardar");
        heroes.put("29", "Tidehunter");
        heroes.put("30", "Witch Doctor");
        heroes.put("31", "Lich");
        heroes.put("32", "Riki");
        heroes.put("33", "Enigma");
        heroes.put("34", "Tinker");
        heroes.put("35", "Sniper");
        heroes.put("36", "Necrolyte");
        heroes.put("37", "Warlock");
        heroes.put("38", "Beastmaster");
        heroes.put("39", "Queen of Pain");
        heroes.put("40", "Venomancer");
        heroes.put("41", "Faceless Void");
        heroes.put("42", "Wraith King");
        heroes.put("43", "Death Prophet");
        heroes.put("44", "Phantom Assassin");
        heroes.put("45", "Pugna");
        heroes.put("46", "Templar Assassin");
        heroes.put("47", "Viper");
        heroes.put("48", "Luna");
        heroes.put("49", "Dragon Knight");
        heroes.put("50", "Dazzle");
        heroes.put("51", "Clockwerk");
        heroes.put("52", "Leshrac");
        heroes.put("53", "Nature's Prophet");
        heroes.put("54", "Lifestealer");
        heroes.put("55", "Dark Seer");
        heroes.put("56", "Clinkz");
        heroes.put("57", "Omniknight");
        heroes.put("58", "Enchantress");
        heroes.put("59", "Huskar");
        heroes.put("60", "Night Stalker");
        heroes.put("61", "Broodmother");
        heroes.put("62", "Bounty Hunter");
        heroes.put("63", "Weaver");
        heroes.put("64", "Jakiro");
        heroes.put("65", "Batrider");
        heroes.put("66", "Chen");
        heroes.put("67", "Spectre");
        heroes.put("68", "Ancient Apparition");
        heroes.put("69", "Doom");
        heroes.put("70", "Ursa");
        heroes.put("71", "Spirit Breaker");
        heroes.put("72", "Gyrocopter");
        heroes.put("73", "Alchemist");
        heroes.put("74", "Invoker");
        heroes.put("75", "Silencer");
        heroes.put("76", "Outworld Devourer");
        heroes.put("77", "Lycanthrope");
        heroes.put("78", "Brewmaster");
        heroes.put("79", "Shadow Demon");
        heroes.put("80", "Lone Druid");
        heroes.put("81", "Chaos Knight");
        heroes.put("82", "Meepo");
        heroes.put("83", "Treant Protector");
        heroes.put("84", "Ogre Magi");
        heroes.put("85", "Undying");
        heroes.put("86", "Rubick");
        heroes.put("87", "Disruptor");
        heroes.put("88", "Nyx Assassin");
        heroes.put("89", "Naga Siren");
        heroes.put("90", "Keeper of the Light");
        heroes.put("91", "Wisp");
        heroes.put("92", "Visage");
        heroes.put("93", "Slark");
        heroes.put("94", "Medusa");
        heroes.put("95", "Troll Warlord");
        heroes.put("96", "Centaur Warrunner");
        heroes.put("97", "Magnus");
        heroes.put("98", "Timbersaw");
        heroes.put("99", "Bristleback");
        heroes.put("100", "Tusk");
        heroes.put("101", "Skywrath Mage");
        heroes.put("102", "Abaddon");
        heroes.put("103", "Elder Titan");
        heroes.put("104", "Legion Commander");
        heroes.put("106", "Ember Spirit");
        heroes.put("107", "Earth Spirit");
        heroes.put("108", "Abyssal Underlord");
        heroes.put("109", "Terrorblade");
        heroes.put("110", "Phoenix");
    }

    private static void initHeroImagesHashMap() {
        heroimages.put("1", "antimage");
        heroimages.put("2", "axe");
        heroimages.put("3", "bane");
        heroimages.put("4", "bloodseeker");
        heroimages.put("5", "crystal_maiden");
        heroimages.put("6", "drow_ranger");
        heroimages.put("7", "earthshaker");
        heroimages.put("8", "juggernaut");
        heroimages.put("9", "mirana");
        heroimages.put("10", "morphling");
        heroimages.put("11", "nevermore");
        heroimages.put("12", "phantom_lancer");
        heroimages.put("13", "puck");
        heroimages.put("14", "pudge");
        heroimages.put("15", "razor");
        heroimages.put("16", "sand_king");
        heroimages.put("17", "storm_spirit");
        heroimages.put("18", "sven");
        heroimages.put("19", "tiny");
        heroimages.put("20", "vengefulspirit");
        heroimages.put("21", "windrunner");
        heroimages.put("22", "zuus");
        heroimages.put("23", "kunkka");
        heroimages.put("24", "unknown");
        heroimages.put("25", "lina");
        heroimages.put("26", "lion");
        heroimages.put("27", "shadow_shaman");
        heroimages.put("28", "slardar");
        heroimages.put("29", "tidehunter");
        heroimages.put("30", "witch_doctor");
        heroimages.put("31", "lich");
        heroimages.put("32", "riki");
        heroimages.put("33", "enigma");
        heroimages.put("34", "tinker");
        heroimages.put("35", "sniper");
        heroimages.put("36", "necrolyte");
        heroimages.put("37", "warlock");
        heroimages.put("38", "beastmaster");
        heroimages.put("39", "queenofpain");
        heroimages.put("40", "venomancer");
        heroimages.put("41", "faceless_void");
        heroimages.put("42", "skeleton_king");
        heroimages.put("43", "death_prophet");
        heroimages.put("44", "phantom_assassin");
        heroimages.put("45", "pugna");
        heroimages.put("46", "templar_assassin");
        heroimages.put("47", "viper");
        heroimages.put("48", "luna");
        heroimages.put("49", "dragon_knight");
        heroimages.put("50", "dazzle");
        heroimages.put("51", "rattletrap");
        heroimages.put("52", "leshrac");
        heroimages.put("53", "furion");
        heroimages.put("54", "life_stealer");
        heroimages.put("55", "dark_seer");
        heroimages.put("56", "clinkz");
        heroimages.put("57", "omniknight");
        heroimages.put("58", "enchantress");
        heroimages.put("59", "huskar");
        heroimages.put("60", "night_stalker");
        heroimages.put("61", "broodmother");
        heroimages.put("62", "bounty_hunter");
        heroimages.put("63", "weaver");
        heroimages.put("64", "jakiro");
        heroimages.put("65", "batrider");
        heroimages.put("66", "chen");
        heroimages.put("67", "spectre");
        heroimages.put("68", "ancient_apparition");
        heroimages.put("69", "doom_bringer");
        heroimages.put("70", "ursa");
        heroimages.put("71", "spirit_breaker");
        heroimages.put("72", "gyrocopter");
        heroimages.put("73", "alchemist");
        heroimages.put("74", "invoker");
        heroimages.put("75", "silencer");
        heroimages.put("76", "obsidian_destroyer");
        heroimages.put("77", "lycan");
        heroimages.put("78", "brewmaster");
        heroimages.put("79", "shadow_demon");
        heroimages.put("80", "lone_druid");
        heroimages.put("81", "chaos_knight");
        heroimages.put("82", "meepo");
        heroimages.put("83", "treant");
        heroimages.put("84", "ogre_magi");
        heroimages.put("85", "undying");
        heroimages.put("86", "rubick");
        heroimages.put("87", "disruptor");
        heroimages.put("88", "nyx_assassin");
        heroimages.put("89", "naga_siren");
        heroimages.put("90", "keeper_of_the_light");
        heroimages.put("91", "wisp");
        heroimages.put("92", "visage");
        heroimages.put("93", "slark");
        heroimages.put("94", "medusa");
        heroimages.put("95", "troll_warlord");
        heroimages.put("96", "centaur");
        heroimages.put("97", "magnataur");
        heroimages.put("98", "shredder");
        heroimages.put("99", "bristleback");
        heroimages.put("100", "tusk");
        heroimages.put("101", "skywrath_mage");
        heroimages.put("102", "abaddon");
        heroimages.put("103", "elder_titan");
        heroimages.put("104", "legion_commander");
        heroimages.put("106", "ember_spirit");
        heroimages.put("107", "earth_spirit");
        heroimages.put("108", "abyssal_underlord");
        heroimages.put("109", "terrorblade");
        heroimages.put("110", "phoenix");
    }

    public static String getHeroName(String nummer) {
        if (heroes.size() == 0) {
            initHeroesHashMap();
        }

        if (heroes.containsKey(nummer)) {
            return heroes.get(nummer);
        } else {
            return "Unknown hero";
        }
    }

    public static String getHeroImageName(String nummer) {
        if (heroimages.size() == 0) {
            initHeroImagesHashMap();
        }

        if (heroimages.containsKey(nummer)) {
            return heroimages.get(nummer);
        } else {
            return "unknown_hero";
        }
    }

    public static HashMap<String, String> getHeroes() {
        if (heroes.size() < 1) {
            initHeroesHashMap();
        }
        return heroes;
    }


}

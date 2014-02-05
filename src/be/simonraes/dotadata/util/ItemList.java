package be.simonraes.dotadata.util;

import java.util.HashMap;

public class ItemList 
{
//	private static String itemnaam;
	private static HashMap<String,String> items = new HashMap<String, String>();
	
	private static void initItemsHashMap()
	{
		items.put("0", "emptyitembg");
		items.put("1", "blink");
		items.put("2", "blades_of_attack");
		items.put("3", "broadsword");
		items.put("4", "chainmail");
		items.put("5", "claymore");
		items.put("6", "helm_of_iron_will");
		items.put("7", "javelin");
		items.put("8", "mithril_hammer");
		items.put("9", "platemail");
		items.put("10", "quarterstaff");
		items.put("11", "quelling_blade");
		items.put("12", "ring_of_protection");
		items.put("13", "gauntlets");
		items.put("14", "slippers");
		items.put("15", "mantle");
		items.put("16", "branches");
		items.put("17", "belt_of_strength");
		items.put("18", "boots_of_elves");
		items.put("19", "robe");
		items.put("20", "circlet");
		items.put("21", "ogre_axe");
		items.put("22", "blade_of_alacrity");
		items.put("23", "staff_of_wizardry");
		items.put("24", "ultimate_orb");
		items.put("25", "gloves");
		items.put("26", "lifesteal");
		items.put("27", "ring_of_regen");
		items.put("28", "sobi_mask");
		items.put("29", "boots");
		items.put("30", "gem");
		items.put("31", "cloak");
		items.put("32", "talisman_of_evasion");
		items.put("33", "cheese");
		items.put("34", "magic_stick");
		items.put("35", "recipe_magic_wand");
		items.put("36", "magic_wand");
		items.put("37", "ghost");
		items.put("38", "clarity");
		items.put("39", "flask");
		items.put("40", "dust");
		items.put("41", "bottle");
		items.put("42", "ward_observer");
		items.put("43", "ward_sentry");
		items.put("44", "tango");
		items.put("45", "courier");
		items.put("46", "tpscroll");
		items.put("47", "recipe_travel_boots");
		items.put("48", "travel_boots");
		items.put("49", "recipe_phase_boots");
		items.put("50", "phase_boots");
		items.put("51", "demon_edge");
		items.put("52", "eagle");
		items.put("53", "reaver");
		items.put("54", "relic");
		items.put("55", "hyperstone");
		items.put("56", "ring_of_health");
		items.put("57", "void_stone");
		items.put("58", "mystic_staff");
		items.put("59", "energy_booster");
		items.put("60", "point_booster");
		items.put("61", "vitality_booster");
		items.put("62", "recipe_power_treads");
		items.put("63", "power_treads");
		items.put("64", "recipe_hand_of_midas");
		items.put("65", "hand_of_midas");
		items.put("66", "recipe_oblivion_staff");
		items.put("67", "oblivion_staff");
		items.put("68", "recipe_pers");
		items.put("69", "pers");
		items.put("70", "recipe_poor_mans_shield");
		items.put("71", "poor_mans_shield");
		items.put("72", "recipe_bracer");
		items.put("73", "bracer");
		items.put("74", "recipe_wraith_band");
		items.put("75", "wraith_band");
		items.put("76", "recipe_null_talisman");
		items.put("77", "null_talisman");
		items.put("78", "recipe_mekansm");
		items.put("79", "mekansm");
		items.put("81", "recipe_vladmir");
		items.put("82", "recipe_magic_wand");
		items.put("83", "recipe_magic_wand");
		items.put("84", "flying_courier");
		items.put("85", "recipe_buckler");
		items.put("86", "buckler");
		items.put("87", "recipe_ring_of_basilius");
		items.put("88", "ring_of_basilius");
		items.put("89", "recipe_pipe");
		items.put("90", "pipe");
		items.put("91", "recipe_urn_of_shadows");
		items.put("92", "urn_of_shadows");
		items.put("93", "recipe_headdress");
		items.put("94", "headdress");
		items.put("95", "recipe_sheepstick");
		items.put("96", "sheepstick");
		items.put("97", "recipe_orchid");
		items.put("98", "orchid");
		items.put("99", "recipe_cyclone");
		items.put("100", "cyclone");
		items.put("101", "recipe_force_staff");
		items.put("102", "force_staff");
		items.put("103", "recipe_dagon");
		items.put("104", "dagon");
		items.put("105", "recipe_necronomicon");
		items.put("106", "necronomicon");
		items.put("107", "recipe_ultimate_scepter");
		items.put("108", "ultimate_scepter");
		items.put("109", "recipe_refresher");
		items.put("110", "refresher");
		items.put("111", "recipe_assault");
		items.put("112", "assault");
		items.put("113", "recipe_heart");
		items.put("114", "heart");
		items.put("115", "recipe_black_king_bar");
		items.put("116", "black_king_bar");
		items.put("117", "aegis");
		items.put("118", "recipe_shivas_guard");
		items.put("119", "shivas_guard");
		items.put("120", "recipe_bloodstone");
		items.put("121", "bloodstone");
		items.put("122", "recipe_sphere");
		items.put("123", "sphere");
		items.put("124", "recipe_vanguard");
		items.put("125", "vanguard");
		items.put("126", "recipe_blade_mail");
		items.put("127", "blade_mail");
		items.put("128", "recipe_soul_booster");
		items.put("129", "soul_booster");
		items.put("130", "recipe_hood_of_defiance");
		items.put("131", "hood_of_defiance");
		items.put("132", "recipe_rapier");
		items.put("133", "rapier");
		items.put("134", "recipe_monkey_king_bar");
		items.put("135", "monkey_king_bar");
		items.put("136", "recipe_radiance");
		items.put("137", "radiance");
		items.put("138", "recipe_butterfly");
		items.put("139", "butterfly");
		items.put("140", "recipe_greater_crit");
		items.put("141", "greater_crit");
		items.put("142", "recipe_basher");
		items.put("143", "basher");
		items.put("144", "recipe_bfury");
		items.put("145", "bfury");
		items.put("146", "recipe_manta");
		items.put("147", "manta");
		items.put("148", "recipe_lesser_crit");
		items.put("149", "lesser_crit");
		items.put("150", "recipe_armlet");
		items.put("151", "armlet");
		items.put("152", "recipe_invis_sword");
		items.put("153", "invis_sword");
		items.put("154", "sange_and_yasha");
		items.put("155", "recipe_satanic");
		items.put("156", "satanic");
		items.put("157", "recipe_mjollnir");
		items.put("158", "mjollnir");
		items.put("159", "recipe_skadi");
		items.put("160", "skadi");
		items.put("161", "recipe_sange");
		items.put("162", "sange");
		items.put("163", "recipe_helm_of_the_dominator");
		items.put("164", "helm_of_the_dominator");
		items.put("165", "recipe_maelstrom");
		items.put("166", "maelstrom");
		items.put("167", "recipe_desolator");
		items.put("168", "desolator");
		items.put("169", "recipe_yasha");
		items.put("170", "yasha");
		items.put("171", "recipe_mask_of_madness");
		items.put("172", "mask_of_madness");
		items.put("173", "recipe_diffusal_blade");
		items.put("174", "diffusal_blade");
		items.put("175", "recipe_ethereal_blade");
		items.put("176", "ethereal_blade");
		items.put("177", "recipe_soul_ring");
		items.put("178", "soul_ring");
		items.put("179", "recipe_arcane_boots");
		items.put("180", "arcane_boots");
		items.put("181", "orb_of_venom");
		items.put("182", "stout_shield");
		items.put("183", "recipe_invis_sword");
		items.put("184", "recipe_ancient_janggo");
		items.put("185", "ancient_janggo");
		items.put("186", "recipe_medallion_of_courage");
		items.put("187", "medallion_of_courage");
		items.put("188", "smoke_of_deceit");
		items.put("189", "recipe_veil_of_discord");
		items.put("190", "veil_of_discord");
		items.put("191", "recipe_necronomicon_2");
		items.put("192", "recipe_necronomicon_3");
		items.put("193", "necronomicon_2");
		items.put("194", "necronomicon_3");
		items.put("195", "recipe_diffusal_blade_2");
		items.put("196", "diffusal_blade_2");
		items.put("197", "recipe_dagon_2");
		items.put("198", "recipe_dagon_3");
		items.put("199", "recipe_dagon_4");
		items.put("200", "recipe_dagon_5");
		items.put("201", "dagon_2");
		items.put("202", "dagon_3");
		items.put("203", "dagon_4");
		items.put("204", "dagon_5");
		items.put("205", "recipe_rod_of_atos");
		items.put("206", "rod_of_atos");
		items.put("207", "recipe_abyssal_blade");
		items.put("208", "abyssal_blade");
		items.put("209", "recipe_heavens_halberd");
		items.put("210", "heavens_halberd");
		items.put("211", "recipe_ring_of_aquila");
		items.put("212", "ring_of_aquila");
		items.put("213", "recipe_tranquil_boots");
		items.put("214", "tranquil_boots");
	}
	
	public static String getItem(String nummer)
	{
		if(items.size()==0)
		{
			initItemsHashMap();
		}
		if(items.containsKey(nummer))
		{
            if(items.get(nummer).contains("recipe")){
                return "recipe";
            }else if(Integer.parseInt(nummer)==0){
                //todo: needs to be an empty item slot image
                return "blink";
            } else {
                return items.get(nummer);
            }
		}
		else
		{
			return "Unknown item";
		}
	}

}

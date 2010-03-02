import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.util.LinkedList;
import java.util.Queue;

import javax.swing.JApplet;


/**
 * All collected data is from the Bulbapedia 
 * and I in no way claim it as my own
 * 
 * 
 * This scrapes bulbapedia for Pokenet information that will 
 * be in Pokenet.ini formatted as 
 * 
 * [1]
	Name=BULBASAUR
	InternalName=BULBASAUR
	Kind=SEED
	Pokedex=BULBASAUR can be seen napping in bright sunlight. There is a seed on its back. By soaking up the sun's rays, the seed grows progressively larger.
	Type1=GRASS
	Type2=POISON
	BaseStats=45,49,49,45,65,65
	Rareness=45
	BaseEXP=64
	Happiness=70
	GrowthRate=Parabolic
	StepsToHatch=5120
	Color=Green
	Habitat=Grassland
	EffortPoints=0,0,0,0,1,0
	Abilities=OVERGROW
	Compatibility=1,7
	Height=0.7
	Weight=6.9
	GenderRate=FemaleOneEighth
	Moves=1,TACKLE,4,GROWL,7,LEECH SEED,10,VINE WHIP,15,POISONPOWDER,15,SLEEP POWDER,20,RAZOR LEAF,25,SWEET SCENT,32,GROWTH,39,SYNTHESIS,46,SOLARBEAM
	EggMoves=LIGHT SCREEN,SKULL BASH,SAFEGUARD,CHARM,PETAL DANCE,MAGICAL LEAF,GRASSWHISTLE,CURSE
	Evolutions=IVYSAUR,Level,16
	BattlerPlayerY=16
	BattlerEnemyY=14
	BattlerAltitude=0
	
	
	TODO:
		Stuff that absolutely needs to be working for long term use
			- Evolution
			- Maybe change to use state machine objects
 *
 * @author lprestonsegoiii.
 *         Created Feb 28, 2010.
 */
public class BulbapediaScraper {
	private static String URL_TO_POKEMON_LIST = "http://bulbapedia.bulbagarden.net/wiki/List_of_Pok%C3%A9mon_by_National_Pok%C3%A9dex_number";
	private static String OUT_FILE_NAME = "pokemon.ini";
	private static String OLD_POKEMON_INI_NAME = "old-pokemon.ini";
	
	private static Queue<String> PokemonLinks = new LinkedList<String>();
	
	private static String TITLE_START = "<big><big><b>";
	private static String TITLE_END = "</b></big></big>";
	
	private static String TYPES_START = " (type)\"><span style=\"color:#FFFFFF;\">&nbsp;";
	private static String TYPES_END = "&nbsp;</span></a></span>";
	
	private static String KIND_START = "<td align=\"center\" style=\"background: #FFFFFF; width: 50%; -moz-border-radius-bottomright: 10px;\"> ";
	private static String KIND_END = " Pok";
	
	private static String ABILITY_START = "(ability)\"><span style=\"color:#000;\">";
	private static String ABILITY_END = "</span></a>";
	
	private static String NUMBER_START = "National</span></a></b></small><br />#";
	private static String NUMBER_END = "";
	
	private static String HEIGHT_START = "<span class=\"explain\" title=\"";
	private static String HEIGHT_END = "m\">";
	
	private static String WEIGHT_START = "<span class=\"explain\" title=\"";
	private static String WEIGHT_END = "kg\">";
	
	private static String COLOR_START = "width: 25%; border-right: 1px solid";
	private static String COLOR_START2 = "-moz-border-radius-bottomleft: 10px;\"> ";
	
	private static String CATCH_RATE_START = "</td><td align=\"center\" style=\"background: #FFFFFF; width: 25%; border-left: ";
	private static String CATCH_RATE_START2 = " -moz-border-radius-bottomright: 10px;\"> ";
	
	private static String GENDER_RATIO_START = "<td style=\"background:#FFFFFF; -moz-border-radius-bottomright: 10px; width:90%;\"> ";
	private static String GENDER_RATIO_END = "&nbsp;female";
	private static String GENDER_ONE_START = "<td colspan=\"2\" align=\"center\" style=\"background: #FFFFFF; width: 100%; -moz-border-radius-bottomright: 10px; -moz-border-radius-bottomleft: 10px;\"> ";
	
	private static String EGG_STEPS_START = "&nbsp;<small>(";
	private static String EGG_STEPS_END = " steps";
	
	private static String BASE_EXP = " <small>Exp.</small><br />";
	private static String EV_ATK = "<small>Atk</small><br />";
	private static String EV_DEF = "<small>Def</small><br />";
	private static String EV_SPATK = "<small>Sp.Atk</small><br />";
	private static String EV_SPDEF = "<small>Sp.Def</small><br />";
	private static String EV_SPEED = "<small>Speed</small><br />";
	
	private static String HABITAT_START = "mon by habitat\">";
	private static String HABITAT_END = "Pok";
	
	private static String POKEDEX_SOUL_SILVER_TRIGGER = "SoulSilver</b></span></a>";
	private static String POKEDEX_PLATINUM_TRIGGER = "Platinum</b></span></a>"; // 252
	private static String POKEDEX_PLATINUM_START = "style=\"background:#FFFFFF\"> ";
	
	private static String BASE_STATS_TRIGGER = "<span class=\"mw-headline\">Base stats</span>";
	private static String BASE_HP_TRIGGER = "width: 60px;\"> HP:";
	private static String BASE_ATK_TRIGGER = "width: 60px;\"> Attack:";
	private static String BASE_DEF_TRIGGER = "width: 60px;\"> Defense:";
	private static String BASE_SPATK_TRIGGER = "width: 60px;\"> Sp.Atk:";
	private static String BASE_SPDEF_TRIGGER = "width: 60px;\"> Sp.Def:";
	private static String BASE_SPEED_TRIGGER = "width: 60px;\"> Speed:";
	private static String BASE_STAT_START = "width: 30px;\"> ";
	
	private static String CURRENT_EVOLUTION_TRIGGER = "<strong class=\"selflink\"><span style=\"color:#000;\">";
	private static String NEXT_EVOLUTION_LEVEL_START = "title=\"Level\"><span style=\"color:#000;\">";
	private static String NEXT_EVOLUTION_LEVEL_END =  "</span></a>";
	private static String NEXT_EVOLUTION_NAME_START = "mon)\"><span style=\"color:#000;\">";
	private static String NEXT_EVOLUTION_NAME_END = "</span></a><br />";
	
	
	// gender appearances
	private static String FEMALE_ONE_EIGHTH = "FemaleOneEighth";
	private static String FEMALE_25_PERCENT = "Female25Percent";
	private static String FEMALE_50_PERCENT = "Female50Percent";
	private static String FEMALE_75_PERCENT = "Female75Percent";
	private static String ALWAYS_FEMALE = "AlwaysFemale";
	private static String ALWAYS_MALE = "AlwaysMale";
	private static String GENDERLESS = "Genderless";
	
	private static String MOVES_BY_LEVEL_TRIGGER = "<a href=\"/wiki/Level\" title=\"Level\">leveling up</a>";
	private static String MOVES_BY_LEVEL_NAME_START = "_(move)\" title=\"";
	private static String MOVES_BY_LEVEL_NAME_END = " (move)\">";
	private static String MOVES_BY_LEVEL_LEVEL_START = "<td> ";
	private static String MOVES_BY_LEVEL_TERMINATE = "<small><b>Bold</b> indicates a move that gets";
	private static String MOVES_BY_BREEDING_TRIGGER = "<a name=\"By_breeding\" id=\"By_breeding\">";
	
	static String number = "", 
					name = "", 
					internalName = "", 
					kind = "", 
					pokedex = "",
					type1 = "", 
					type2 = "", 
					baseStats = "", 
					rareness = "", 
					baseEXP = "",
					happiness = "", 
					growthRate = "", 
					stepsToHatch = "", 
					color = "",
					habitat = "", 
					effortPoints = "", 
					abilities = "", 
					compatibility = "",
					height = "", 
					weight = "", 
					genderRate = "", 
					moves = "", 
					eggMoves = "",
					evolutions = "",
					battlerPlayerY = "", 
					battlerEnemyY = "", 
					battlerAltitude = "";
	static String ev_atk, ev_def, ev_spatk, ev_spdef, ev_speed;
	static String base_hp, base_atk, base_def, base_spatk, base_spdef, base_speed;
	static String evolution_Level, evolution_Type, evolution_Name;
	static String curMoveLevel, curMoveName;

	/**
	 * TODO Put here a description of what this method does.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		int start, end;
		boolean dontWriteToFile = true;
		boolean doneWithGeneralStats = false;
		boolean doneWithEvolution = false;
		boolean doneWithHappiness = false; 
		boolean readyToBreak = false;
	
		
		// these triggers make sure we don't read any similar data elsewhere in teh file
		boolean readPokedex = false;
		boolean readBaseStats = false;
		boolean readBaseHP = false;
		boolean readBaseATK = false;
		boolean readBaseDEF = false;
		boolean readBaseSpATK = false;
		boolean readBaseSpDEF = false;
		boolean readBaseSpeed = false;
		boolean foundCurrentEvolution = false;
		boolean readMovesByLevel = false;
		boolean readMovesByBreeding = false;
		
		// get start time
		long startTime = System.currentTimeMillis();
		long curStartTime;
		
		/*
		 * for every pokemon, we need to download their HTML, and than scrape it
		 */
		
		downloadPokemonList();
		
		try {
			String currentLinkToPokemon = null;
		
			// open file for writing
			File outFile = new File(OUT_FILE_NAME);
			FileWriter out = null;
			try {
				out = new FileWriter(outFile);
			}catch(IOException e){}
			
			
			String oldPokemonINILine;
			File f = new File(OLD_POKEMON_INI_NAME);
			BufferedReader inOld = new BufferedReader(new FileReader(f));
			
			
			
			while(true){
				try{
					currentLinkToPokemon = PokemonLinks.remove();
				}catch (Exception e){break;}
				
				
				// read link from file, and download that linked html page
				URL yahoo = new URL(currentLinkToPokemon);
		        URLConnection yc = yahoo.openConnection();
		        BufferedReader in = new BufferedReader(
		                                new InputStreamReader(
		                                yc.getInputStream()));
		        String inputLine;
		        
		        System.out.println("Beginning scrape of ---- " + currentLinkToPokemon);
		        curStartTime = System.currentTimeMillis();
		        // the while loop kinda has to start from the top, 
		        // and work it's way down...
		        while ((inputLine = in.readLine()) != null) 
		        {
		   
		        	// all this stuff is from the table at the right of the page
		        	if(!doneWithGeneralStats){
		        		// name
			        	if(inputLine.contains(TITLE_START )){
			            	start = inputLine.indexOf(TITLE_START) + TITLE_START.length();
			            	end = inputLine.indexOf(TITLE_END);
			            	name = inputLine.substring(start, end);
			            	
			            	// as far as I know, these are the same
			            	internalName = name;
			            }
			        	// type(s)
			        	else if(inputLine.contains(TYPES_START)){
			        		start = inputLine.indexOf(TYPES_START) + TYPES_START.length();
			        		end = inputLine.indexOf(TYPES_END);
			        		type1 = inputLine.substring(start, end);
			        		
			        		//check if there is a second type
			        		start = inputLine.indexOf(TYPES_START, end) + TYPES_START.length();
			        		end = inputLine.indexOf(TYPES_END, start);
			        		type2 = inputLine.substring(start, end);
			        		type2 = type2.length() > 20 ? "" : type2;
			        	}
			        	// kind
			        	else if(inputLine.contains(KIND_START) && inputLine.contains(KIND_END)){
			        		start = inputLine.indexOf(KIND_START) + KIND_START.length();
			        		end = inputLine.indexOf(KIND_END, start);

			        		kind = inputLine.substring(start, end);
			        	}
			        	// ability
			        	else if(inputLine.contains(ABILITY_START)){
			        		start = inputLine.indexOf(ABILITY_START) + ABILITY_START.length();
			        		end = inputLine.indexOf(ABILITY_END, start);
			        		abilities = inputLine.substring(start, end);
			        	}
			        	// number
			        	else if(inputLine.contains(NUMBER_START)){
			        		start = inputLine.indexOf(NUMBER_START) + NUMBER_START.length();
			        		number = inputLine.substring(start);
			        		
			        		// the last two don't have numbers
			        		if (number.equals("???")) {
			        			resetDataVars();
			        			break; 
			        		}
			        	}
			        	// height (metric)
			        	else if(inputLine.contains(HEIGHT_START) && inputLine.contains(HEIGHT_END)){
			        		start = inputLine.indexOf(HEIGHT_START) + HEIGHT_START.length();
			        		end = inputLine.indexOf(HEIGHT_END, start);
			        		height = inputLine.substring(start, end);
			        	}
			        	// weight (metric)
			        	else if(inputLine.contains(WEIGHT_START) && inputLine.contains(WEIGHT_END)){
			        		start = inputLine.indexOf(WEIGHT_START) + WEIGHT_START.length();
			        		end = inputLine.indexOf(WEIGHT_END, start);
			        		weight = inputLine.substring(start, end);
			        	}
			        	// color
			        	else if(inputLine.contains(COLOR_START) && inputLine.contains(COLOR_START2)){
			        		start = inputLine.indexOf(COLOR_START2) + COLOR_START2.length();
			        		color = inputLine.substring(start, inputLine.length());
			        	}
			        	//rareness (catch rate)
			        	else if(inputLine.contains(CATCH_RATE_START) && inputLine.contains(CATCH_RATE_START2)){
			        		start = inputLine.indexOf(CATCH_RATE_START2) + CATCH_RATE_START2.length();
			        		rareness = inputLine.substring(start);
			        	}
			        	// gender ratio
			        	else if(inputLine.contains(GENDER_RATIO_START) && inputLine.contains(GENDER_RATIO_END)){
			        		start = inputLine.indexOf(GENDER_RATIO_START) + GENDER_RATIO_START.length();
			        		end = inputLine.indexOf(GENDER_RATIO_END, start);
			        		genderRate = inputLine.substring(start, end);
			        		
			        		if(genderRate.equals("12.5%")) genderRate = FEMALE_ONE_EIGHTH;
			        		else if(genderRate.equals("50%")) genderRate = FEMALE_50_PERCENT;
			        		else if (genderRate.equals("25%")) genderRate = FEMALE_25_PERCENT;
			        		else if (genderRate.equals("75%")) genderRate = FEMALE_75_PERCENT;
			        	}
			        	// what if there isn't a gender or there is only one gender for this pokemon?
			        	else if(inputLine.contains(GENDER_ONE_START)){
			        		start = inputLine.indexOf(GENDER_ONE_START) + GENDER_ONE_START.length();
			        		genderRate = inputLine.substring(start);
			        		
			        		if (genderRate.contains("Female")) genderRate = ALWAYS_FEMALE;
			        		else if (genderRate.contains("Male")) genderRate = ALWAYS_MALE;
			        		else if(genderRate.equals("Genderless")) genderRate = GENDERLESS;
			        	}
			        	// egg steps
			        	else if(inputLine.contains(EGG_STEPS_START) && inputLine.contains(EGG_STEPS_END)){
			        		start = inputLine.indexOf(EGG_STEPS_START) + EGG_STEPS_START.length();
			        		end = inputLine.indexOf(EGG_STEPS_END, start);
			        		stepsToHatch = inputLine.substring(start, end);
			        	}
			        	// ev stuff
			        	// consists of Atk, Def, Sp.Atk, Sp.Def, and Speed
			        	else if(inputLine.contains(EV_ATK)){
			        		start = inputLine.indexOf(EV_ATK) + EV_ATK.length();
			        		ev_atk = inputLine.substring(start);
			        	} else if(inputLine.contains(EV_DEF)){
			        		start = inputLine.indexOf(EV_DEF) + EV_DEF.length();
			        		ev_def = inputLine.substring(start);
			        	} else if(inputLine.contains(EV_SPATK)){
			        		start = inputLine.indexOf(EV_SPATK) + EV_SPATK.length();
			        		ev_spatk = inputLine.substring(start);
			        	} else if(inputLine.contains(EV_SPDEF)){
			        		start = inputLine.indexOf(EV_SPDEF) + EV_SPDEF.length();
			        		ev_spdef = inputLine.substring(start);
			        	} else if(inputLine.contains(EV_SPEED)){
			        		start = inputLine.indexOf(EV_SPEED) + EV_SPEED.length();
			        		ev_speed = inputLine.substring(start);
			        	}
			        	
			        	// base exp
			        	else if(inputLine.contains(BASE_EXP)){
			        		start = inputLine.indexOf(BASE_EXP) + BASE_EXP.length();
			        		baseEXP = inputLine.substring(start);
				        	doneWithGeneralStats = true;

			        	}
			        	
			        	
		        	}
		        	
		        	/* move on to other stuff... at this point, we have the following left:
		        	 * 	pokedex
		        	 * 	baseStats ex... 45,49,49,45,65,65
		        	 * 	happiness
		        	 * 	growthRate
		        	 * 	habitat
		        	 * 	compatibility
		        	 * 	moves
		        	 * 	eggMoves
		        	 * 	evolutions
		        	 */
		        	
		        	// habitat
		        	else if(inputLine.contains(HABITAT_START)){
		        		start = inputLine.indexOf(HABITAT_START) + HABITAT_START.length();
		        		end = inputLine.indexOf(HABITAT_END, start);
		        		habitat = inputLine.substring(start, end);
		        	}
		        	
		        	//pokdex entry
//		        	else if(Integer.parseInt(number) < 252 && inputLine.contains(POKEDEX_SOUL_SILVER_TRIGGER)) readPokedex = true;
//		        	else if(Integer.parseInt(number) > 251 && inputLine.contains(POKEDEX_PLATINUM_TRIGGER)) readPokedex = true;
		        	else if(inputLine.contains(POKEDEX_PLATINUM_TRIGGER)) readPokedex = true;
		        	else if(readPokedex && inputLine.contains(POKEDEX_PLATINUM_START)){
		        		start = inputLine.indexOf(POKEDEX_PLATINUM_START) + POKEDEX_PLATINUM_START.length();
		        		pokedex = inputLine.substring(start);
		        		readPokedex = false;
		        	}
		        	
		        	// base stats
		        	else if(inputLine.contains(BASE_STATS_TRIGGER)) readBaseStats = true;
		        	else if(readBaseStats && inputLine.contains(BASE_HP_TRIGGER)) readBaseHP = true;
		        	else if(readBaseStats && inputLine.contains(BASE_ATK_TRIGGER)) readBaseATK = true;
		        	else if(readBaseStats && inputLine.contains(BASE_DEF_TRIGGER)) readBaseDEF = true;
		        	else if(readBaseStats && inputLine.contains(BASE_SPATK_TRIGGER)) readBaseSpATK = true;
		        	else if(readBaseStats && inputLine.contains(BASE_SPDEF_TRIGGER)) readBaseSpDEF = true;
		        	else if(readBaseStats && inputLine.contains(BASE_SPEED_TRIGGER)) readBaseSpeed = true;
		        	else if(readBaseStats && inputLine.contains(BASE_STAT_START)){
		        		start = inputLine.indexOf(BASE_STAT_START) + BASE_STAT_START.length();
		        		
		        		if(readBaseHP){
		        			base_hp = inputLine.substring(start);
			        		readBaseHP = false;
		        		}else if(readBaseATK){
		        			base_atk = inputLine.substring(start);
			        		readBaseATK = false;
		        		}else if(readBaseDEF){
		        			base_def = inputLine.substring(start);
			        		readBaseDEF = false;
		        		}else if(readBaseSpATK){
		        			base_spatk = inputLine.substring(start);
			        		readBaseSpATK = false;
		        		}else if(readBaseSpDEF){
		        			base_spdef = inputLine.substring(start);
			        		readBaseSpDEF = false;
		        		}else if(readBaseSpeed){
		        			base_speed = inputLine.substring(start);
			        		readBaseSpeed = false;
			        		readBaseStats = false;

		        		}
		        		
		        		
		        	}
		        	
		        	
		        	// move that can be learned by levelling up
		        	else if (inputLine.contains(MOVES_BY_LEVEL_TRIGGER)) readMovesByLevel = true;
		        	else if (readMovesByLevel){
		        		if (inputLine.startsWith(MOVES_BY_LEVEL_LEVEL_START)){
		        			start = inputLine.indexOf(MOVES_BY_LEVEL_LEVEL_START) + MOVES_BY_LEVEL_LEVEL_START.length();
		        			curMoveLevel = inputLine.substring(start);
		        			if (curMoveLevel.equals("Start")) curMoveLevel = "1";
		        			
		        			moves += (moves.equals("") ? "" : ",") + curMoveLevel;
		        		}else if(inputLine.contains(MOVES_BY_LEVEL_NAME_START)){
		        			start = inputLine.indexOf(MOVES_BY_LEVEL_NAME_START) + MOVES_BY_LEVEL_NAME_START.length();
		        			end = inputLine.indexOf(MOVES_BY_LEVEL_NAME_END, start);
		        			curMoveName = inputLine.substring(start, end);
		        			
		        			// add to list of moves
		        			moves += "," + curMoveName;
		        		}else if(inputLine.contains(MOVES_BY_LEVEL_TERMINATE)){
		        			curMoveLevel = "";
		        			curMoveName = "";
		        			readMovesByLevel = false;
		        		}
		        	}
		        	
		        	// moves that can be learned by brreding
		        	else if(inputLine.contains(MOVES_BY_BREEDING_TRIGGER)) readMovesByBreeding = true;
		        	else if (readMovesByBreeding){
		        		if(inputLine.contains(MOVES_BY_LEVEL_NAME_START)){
		        			start = inputLine.indexOf(MOVES_BY_LEVEL_NAME_START) + MOVES_BY_LEVEL_NAME_START.length();
		        			end = inputLine.indexOf(MOVES_BY_LEVEL_NAME_END, start);
		        			curMoveName = inputLine.substring(start, end);
		        			
		        			// add to list of moves
		        			eggMoves += (eggMoves.equals("") ? "" : ",") + curMoveName;
		        		}else if(inputLine.contains(MOVES_BY_LEVEL_TERMINATE)){
		        			curMoveName = "";
		        			readMovesByBreeding = false;
		        		}
		        	}
		        	
		        	/*
		        	 * This needs to be more generic...
		        	 * as the way it is now, it has no chance of working for pokemon
		        	 * with multiple evolutions, like Eevee
		        	 */
//		        	//evolution -- only works if a pokemon can only evolve into one thing....
//		        	// luckily, evolutionsn don't change much...
//		        	else if(inputLine.contains(CURRENT_EVOLUTION_TRIGGER)) foundCurrentEvolution = true;
//		        	else if(foundCurrentEvolution && inputLine.contains(NEXT_EVOLUTION_LEVEL_START)){
//		        		start = inputLine.indexOf(NEXT_EVOLUTION_LEVEL_START) + NEXT_EVOLUTION_LEVEL_START.length();
//		        		end = inputLine.indexOf(NEXT_EVOLUTION_LEVEL_END, start);
//		        		System.out.println(currentLinkToPokemon);
//		        		System.out.println(start);
//		        		System.out.println(end);
//		        		System.out.println(inputLine);
//		        		String temp = inputLine.substring(start, end);
//		        		evolution_Level = temp.substring(temp.indexOf(" "));
//		        		evolution_Type = "Level";
//		        		
//		        	}// what are we evolving into?
//		        	else if(foundCurrentEvolution && inputLine.contains(NEXT_EVOLUTION_NAME_START)){
//		        		start = inputLine.indexOf(NEXT_EVOLUTION_NAME_START) + NEXT_EVOLUTION_NAME_START.length();
//		        		end = inputLine.indexOf(NEXT_EVOLUTION_NAME_END, start);
//
//		        		try{evolution_Name = inputLine.substring(start, end);}
//		        		catch (StringIndexOutOfBoundsException e){
//		        			evolution_Name = null;
//		        		}
//		        	}
  

		        }
	        	effortPoints = ev_atk + "," + ev_def + "," + ev_speed + "," + ev_spatk + "," + ev_spdef;

		        baseStats = base_hp + "," + base_atk + "," + 
		        			base_def + "," + base_speed + "," + 
							base_spatk + "," + base_spdef; 
	
//		        evolutions = evolution_Name == null ? "" : evolution_Name + "," + evolution_Type + "," + evolution_Level;
//	
		        /* 
		         * before we write to file, there are some things that bulbapedia doesn't have
		         * so.. for now, I'll just take them from the old pokemon.ini
		         * 
		         * this is, of course, dangerous.. cause it assumes that the following
		         * are correct in the old pokemon.ini.....
		         * 
		         * Things I'm taking from the old ini file
		         * 	happiness
		         * 	growthRate
		         * 	compatibility
		         * 	evolutions
		         * 
		         * happiness, growthRate, and compatibility are not on the
		         * bulbapedia pages.
		         * 
		         * evolution is a bit complex, so for now, we just copy the old.
		         */
		        readyToBreak = false;
		        String tempNum;
		        StringBuilder current = new StringBuilder("");
		        while ((oldPokemonINILine = inOld.readLine()) != null){
		        	current.append(oldPokemonINILine);
		        	current.append("\n");
		        	
		        	if(oldPokemonINILine.startsWith("[")){
		        		tempNum = oldPokemonINILine.substring(1, oldPokemonINILine.length() - 1);
		        		if(Integer.parseInt(tempNum) != Integer.parseInt(number)){
		        			System.err.println("Old ini is out of sync");
		        			System.err.println("Old\tNew\tName\tHappiness\t" +
		        					"GrowthRate\tCompatibility\tEvolutions");
		        			System.err.println(tempNum + "\t" + number + "\t" + name +
		        					"\t" + happiness + "\t" + growthRate + "\t" + 
		        					compatibility + "\t" + evolutions);
		        			System.err.println(current);
		        			System.exit(1);
		        		}
		        	}else if(oldPokemonINILine.startsWith("Happiness")) {
		        		happiness = oldPokemonINILine.split("=")[1];
		        	}else if(oldPokemonINILine.startsWith("GrowthRate")){
		        		growthRate = oldPokemonINILine.split("=")[1];
		        	}else if(oldPokemonINILine.startsWith("Compatibility")){
		        		compatibility = oldPokemonINILine.split("=")[1];
		        	}else if(oldPokemonINILine.startsWith("Evolutions")){
		        		try{
		        			evolutions = oldPokemonINILine.split("=")[1];
		        		} catch (ArrayIndexOutOfBoundsException e){
		        			evolutions = "";
		        		}
		        		
		        		// Evolution is the last of these 4 fields
		        		break;
		        	}
		        	// we only want one pokemon's worth of this info
//		        	if(readyToBreak) break;
		        }
		        
		        // take care of the special cases
		        // Nidoran male / female
		        if(name.contains("Nidoran")){
		        	if(genderRate.equals(ALWAYS_MALE)){
		        		name = "Nidoran?";
		        		internalName = "NidoranMa";
		        	}
		        	else if(genderRate.equals(ALWAYS_FEMALE)){
		        		name = "Nidoran?";
		        		internalName = "NidoranFe";
		        	}
		        }
		        
		        
				writePokemonToFile(out);          
		        System.out.println("Writing Pokemon " + (number.equals("") ? "failed" : number) + 
		        		" -- " + name + " :::: " + ((System.currentTimeMillis() - curStartTime) / 1000.0) + " seconds");
		        
		        // reset all the flags
		        dontWriteToFile = true;
				doneWithGeneralStats = false;
				doneWithEvolution = false;
				doneWithHappiness = false; 
				
				// these triggers make sure we don't read any similar data elsewhere in teh file
				readPokedex = false;
				readBaseStats = false;
				readBaseHP = false;
				readBaseATK = false;
				readBaseDEF = false;
				readBaseSpATK = false;
				readBaseSpDEF = false;
				readBaseSpeed = false;
				foundCurrentEvolution = false;
				
				//reset our vars so we don't get garbage in the next pokemon
				resetDataVars();
		        
		        in.close();
				
				
			}
			
	        out.close();

		}
		catch (IOException exception) {
			// TODO Auto-generated catch-block stub.
			exception.printStackTrace();
		}
		
		System.out.println("Total Time: " + ((System.currentTimeMillis() - startTime) / 1000 / 60)+ " minutes");
	
	}
	
	  	
	
	/**
	 * We don't want garbage in the next pokemon
	 *
	 */
	private static void resetDataVars() {
		number = "";
		name = "";
		internalName = ""; 
		kind = ""; 
		pokedex = "";
		type1 = ""; 
		type2 = ""; 
		baseStats = ""; 
		rareness = ""; 
		baseEXP = "";
		happiness = ""; 
		growthRate = ""; 
		stepsToHatch = ""; 
		color = "";
		habitat = ""; 
		effortPoints = ""; 
		abilities = ""; 
		compatibility = "";
		height = ""; 
		weight = ""; 
		genderRate = ""; 
		moves = ""; 
		eggMoves = "";
		evolutions = "";
		battlerPlayerY = ""; 
		battlerEnemyY = ""; 
		battlerAltitude = "";
	}



	/**
	 * TODO Put here a description of what this method does.
	 *
	 */
	private static void writePokemonToFile(FileWriter out) {
		try {
			if(!number.equals("")){
				out.write("[" + number + "]\n");
				out.write("Name=" + name + "\n");
				out.write("InternalName=" + internalName + "\n");
				out.write("Kind=" + kind + "\n");
				out.write("Pokedex=" + pokedex + "\n");
				out.write("Type1=" + type1 + "\n");
				if(!type2.equals("")) out.write("Type2=" + type2 + "\n");
				out.write("BaseStats=" + baseStats + "\n");
				out.write("Rareness=" + rareness + "\n");
				out.write("BaseEXP=" + baseEXP + "\n");
				out.write("Happiness=" + happiness + "\n");
				out.write("GrowthRate=" + growthRate + "\n");
				out.write("StepsToHatch=" + stepsToHatch + "\n");
				out.write("Color=" + color + "\n");
				out.write("Habitat=" + habitat + "\n");
				out.write("EfforPoints=" + effortPoints + "\n");
				out.write("Abilities=" + abilities + "\n");
				out.write("Compatibility=" + compatibility + "\n");
				out.write("Height=" + height + "\n");
				out.write("Weight=" + weight + "\n");
				out.write("GenderRate=" + genderRate + "\n");
				out.write("Moves=" + moves + "\n");
				out.write("EggMoves=" + eggMoves + "\n");
				out.write("Evolution=" + evolutions + "\n");
			}
			


			
		}
		catch (IOException exception) {
			System.out.println("Writing Pokemon failed");
			exception.printStackTrace();
		}
	}


/**
	 * adds all the pokemon's urls to a queue used up in main
	 *
	 */
	private static void downloadPokemonList() {
		StringBuilder contents = new StringBuilder();
		String curLine = "";
	    String curLink, prevLink = "";
	    int start, end;
	    int number = 1;
		
		System.out.println("Downloading and setting up list of pokemon to scan through...");
	    try {

	    	URL yahoo = new URL(URL_TO_POKEMON_LIST);
	        URLConnection yc = yahoo.openConnection();
	        BufferedReader in = new BufferedReader(
	                                new InputStreamReader(
	                                yc.getInputStream()));
	        String line = null;


	        while (( line = in.readLine()) != null){

	        	if(line.contains("<span class=\"plainlinks\">")){
	        		start = line.indexOf("<a");
	        		end = line.indexOf("\"", start + 9);
	        		curLink = line.substring(start + 9, end);
	        		
	        		// add the absolute URL
        			curLink = "http://bulbapedia.bulbagarden.net" + curLink.substring(0, curLink.length());

        			// dont save duplicates
	        		if(!curLink.equalsIgnoreCase(prevLink)){
	        			PokemonLinks.add(curLink);
		        		number++;
	        		}
	        		prevLink = curLink;
	        		// write to file
	        	}
	        }

	        in.close();
	      
	    }
	    catch (IOException ex){
	      ex.printStackTrace();
	    }
	    
	    System.out.println("There are " + number + " Pokemon");
		
	}


}

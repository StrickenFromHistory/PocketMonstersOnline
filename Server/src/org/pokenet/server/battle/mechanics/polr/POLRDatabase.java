package org.pokenet.server.battle.mechanics.polr;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Set;

import org.ini4j.Ini;
import org.pokenet.server.battle.PokemonSpecies;
import org.pokenet.server.battle.PokemonSpeciesData;
import org.pokenet.server.battle.Pokemon.ExpTypes;
import org.pokenet.server.battle.mechanics.moves.MoveList;
import org.pokenet.server.battle.mechanics.polr.POLREvolution.EvoTypes;
import org.pokenet.server.battle.mechanics.statuses.abilities.IntrinsicAbility;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
@Root
public class POLRDatabase {
	@ElementArray
	POLRDataEntry[] m_pokemonData;

	private static String toProperCase(String target) {
		return target.toUpperCase().substring(0, 1) +
		target.toLowerCase().substring(1);
	}
	public static void main(String[] args) {
		POLRDatabase db = new POLRDatabase();
		db.m_pokemonData = new POLRDataEntry[498];
		PokemonSpeciesData sp = null;
		try {
			sp = new Persister().read(PokemonSpeciesData.class, new File(
			"pokeglobal/server/res/species.xml"));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		PokemonSpecies.setDefaultData(sp);

		Ini ini = null;
		try {
			ini = new Ini(new FileInputStream("pokeglobal/server/res/db.ini"));
		} catch (Exception e) { e.printStackTrace(); }

		Ini.Section[] sections = new Ini.Section[498];
		HashMap<String, Integer> pokemonIndices = new HashMap<String, Integer>();
		for (int i = 0; i < 498; i++) {
			sections[i] = ini.get(String.valueOf(i+1));
			pokemonIndices.put(sections[i].get("InternalName"), i);
		}
		for (int i = 0; i < 498; i++) {
			Ini.Section s = sections[i];

			db.m_pokemonData[i] = new POLRDataEntry();

			db.m_pokemonData[i].setName(convertPokemonName(i));
			db.m_pokemonData[i].setInternalName(convertPokemonName(i));
			db.m_pokemonData[i].setKind(toProperCase(s.get("Kind")));
			db.m_pokemonData[i].setPokedex(s.get("Pokedex"));
			db.m_pokemonData[i].setType1(toProperCase(s.get("Type1")));
			db.m_pokemonData[i].setType2(s.get("Type2"));
			if (db.m_pokemonData[i].getType2() == null)
				db.m_pokemonData[i].setType2("");
			else db.m_pokemonData[i].setType2(toProperCase(db.m_pokemonData[i].getType2()));
			String[] stringBaseStats = s.get("BaseStats").split(",");
			for (int j = 0; j < stringBaseStats.length; j++)
				db.m_pokemonData[i].getBaseStats()[j] = 
					Integer.parseInt(stringBaseStats[j]);
			db.m_pokemonData[i].setRareness(Integer.parseInt(s.get("Rareness")));
			db.m_pokemonData[i].setBaseEXP(Integer.parseInt(s.get("BaseEXP")));
			db.m_pokemonData[i].setHappiness(Integer.parseInt(s.get("Happiness")));
			db.m_pokemonData[i].setGrowthRate(ExpTypes.valueOf(s.get("GrowthRate").toUpperCase()));
			db.m_pokemonData[i].setStepsToHatch(Integer.parseInt(s.get("StepsToHatch")));
			db.m_pokemonData[i].setColor(s.get("Color"));
			db.m_pokemonData[i].setHabitat(s.get("Habitat"));
			if (db.m_pokemonData[i].getHabitat() == null)
				db.m_pokemonData[i].setHabitat("");
			String[] stringEffortPoints = s.get("EffortPoints").split(",");
			for (int j = 0; j < stringEffortPoints.length; j++)
				db.m_pokemonData[i].getEffortPoints()[j] = 
					Integer.parseInt(stringEffortPoints[j]);
			String[] stringAbilities = s.get("Abilities").split(",");
			if (!stringAbilities[0].equals(""))
				for (int j = 0; j < stringAbilities.length; j++)
					db.m_pokemonData[i].getAbilities().add( 
							convertAbilityName(stringAbilities[j].trim()));
			String[] stringCompatibility = s.get("Compatibility").split(",");
			for (int j = 0; j < stringCompatibility.length; j++)
				db.m_pokemonData[i].getCompatibility()[j] = 
					Integer.parseInt(stringCompatibility[j]);
			db.m_pokemonData[i].setHeight(Float.parseFloat(s.get("Height")));
			db.m_pokemonData[i].setWeight(Float.parseFloat(s.get("Weight")));
			db.m_pokemonData[i].setGenderRate(s.get("GenderRate"));
			String[] stringMoves = s.get("Moves").split(",");
			for (int j = 0; j < stringMoves.length; j++) {
				if (j % 2 == 0) {
					int level = Integer.parseInt(stringMoves[j]);
					if (level == 1)
						db.m_pokemonData[i].getStarterMoves().add(convertMoveName(stringMoves[j+1]));
					else db.m_pokemonData[i].getMoves().put(level, 
							convertMoveName(stringMoves[j+1]));
				}
			}
			String[] stringEggMoves = s.get("EggMoves").split(",");
			if (!stringEggMoves[0].equals(""))
				for (int j = 0; j < stringEggMoves.length; j++)
					db.m_pokemonData[i].getEggMoves().add( 
							convertMoveName(stringEggMoves[j]));
			String[] stringEvolutions = s.get("Evolutions").split(",");
			for (int j = 2; j < stringEvolutions.length; j += 3) {
				POLREvolution evolution = new POLREvolution();
				evolution.setType(EvoTypes.valueOf(stringEvolutions[j - 1]));
				if (evolution.getType() == EvoTypes.Level)
					evolution.setLevel(Integer.parseInt(stringEvolutions[j]));
				else
					evolution.setAttribute(stringEvolutions[j]);
				evolution.setEvolveTo(convertPokemonName(pokemonIndices.get(stringEvolutions[j - 2])));
				db.m_pokemonData[i].getEvolutions().add(evolution);
			}

			/*db.m_temp[i].setBattlerPlayerY(Integer.parseInt(s.get("BattlerPlayerY")));
			db.m_temp[i].setBattlerEnemyY(Integer.parseInt(s.get("BattlerEnemyY")));
			db.m_temp[i].setBattlerAltitude(Integer.parseInt(s.get("BattlerAltitude")));*/
		}
		Serializer serializer = new Persister();
		File userfile = new File("pokeglobal/server/res/polrdb.xml");
		userfile.delete();
		try {
			serializer.write(db, userfile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static String convertAbilityName(String originalName) {
		Set<String> abilityNames = IntrinsicAbility.getAbilityNames();
		for (String abilityName : abilityNames) {
			if (abilityName.toUpperCase().replace(" ", "").
					replace("-", "").equals(originalName)) {
				return abilityName;
			}
		}
		throw new RuntimeException("Invalid ability name " + originalName);
	}
	private static String convertMoveName(String originalName) {
		MoveList moveList = MoveList.getDefaultData();
		for (String moveName : moveList.getMoveNames()) {
			if (moveName.toUpperCase().replace(" ", "").
					replace("-", "").equals(originalName)) {
				return moveName;
			}
		}
		throw new RuntimeException("Invalid move name " + originalName);
	}
	private static String convertPokemonName(int idx) {
		return PokemonSpecies.getDefaultData().getSpecies(idx).getName();
	}
	public POLRDataEntry getPokemonData(int i) {
		return m_pokemonData[i];
	}
}

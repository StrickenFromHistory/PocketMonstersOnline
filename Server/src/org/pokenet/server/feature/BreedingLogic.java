package org.pokenet.server.feature;

import java.util.ArrayList;
import java.util.Random;

import org.pokenet.server.battle.DataService;
import org.pokenet.server.battle.Pokemon;
import org.pokenet.server.battle.PokemonEgg;
import org.pokenet.server.battle.PokemonSpecies;
import org.pokenet.server.battle.mechanics.PokemonNature;
import org.pokenet.server.battle.mechanics.moves.MoveList;
import org.pokenet.server.battle.mechanics.moves.MoveListEntry;

/**
 * 
 * @author ZombieBear
 * 
 */
public class BreedingLogic {
	private Pokemon malePoke;
	private Pokemon femalePoke;

	/**
	 * Constructor
	 * @param poke1
	 * @param poke2
	 * @return
	 * @throws Exception
	 */
	public PokemonEgg generateEgg(Pokemon poke1, Pokemon poke2) throws Exception{
		Pokemon poke = null;
		if (canBreed(poke1, poke2)) {
			try{
				poke = generateHatchling(generateEggSpecies());
				return new PokemonEgg(poke, 200);
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception("BreedingException: Egg generation issue");
			}
		} else {
			throw new Exception("BreedingException: The given pokemon can't breed");
		}
	}

	/**
	 * Returns true if the given pokemon are able to breed.
	 * @param poke1
	 * @param poke2
	 * @return
	 */
	public boolean canBreed(Pokemon poke1, Pokemon poke2) {
		for (int i : PokemonSpecies.getDefaultData().getPokemonByName(
				poke1.getName()).getCompatibility()) {
			for (int x : PokemonSpecies.getDefaultData().getPokemonByName(
					poke2.getName()).getCompatibility()) {
				if (i == x) {
					if (poke1.getGender() == Pokemon.GENDER_MALE
							&& (poke2.getGender() == Pokemon.GENDER_FEMALE)
							|| poke2.getSpeciesName() == "Ditto") {
						malePoke = poke1;
						femalePoke = poke2;
						return true;
					} else if (poke2.getGender() == Pokemon.GENDER_MALE
							&& (poke1.getGender() == Pokemon.GENDER_FEMALE || poke1
									.getSpeciesName() == "Ditto")) {
						malePoke = poke2;
						femalePoke = poke1;
						return true;
					} else if (poke1.getGender() == Pokemon.GENDER_NONE
							&& poke2.getSpeciesName() == "Ditto") {
						malePoke = poke1;
						femalePoke = poke2;
						return true;
					}
				}
			}
		}
		return false;
	}

	
	/**
	 * Generates the new egg's species based on the parents
	 * @return the species number
	 */
	private int generateEggSpecies() {
		// TODO: Add code for incenses!
		// If the female pokemon is a ditto, species is set by the male parent
		if (femalePoke.getSpeciesName() != "Ditto") {
			// Nidoran species
			if (femalePoke.getSpeciesName() == "NidoranF"
					|| femalePoke.getSpeciesName() == "Nidorina"
					|| femalePoke.getSpeciesName() == "Nidoqueen") {
				if (DataService.getBattleMechanics().getRandom().nextInt(2) == 0)
					return PokemonSpecies.getDefaultData().getPokemonByName("NidoranM").getSpeciesNumber();
				else
					return PokemonSpecies.getDefaultData().getPokemonByName("NidoranF").getSpeciesNumber();
			}
			// Volbeat and Illumise
			else if (femalePoke.getSpeciesName() == "Illumise") {
				if (DataService.getBattleMechanics().getRandom().nextInt(2) == 0)
					return PokemonSpecies.getDefaultData().getPokemonByName("Illumise").getSpeciesNumber();
				else
					return PokemonSpecies.getDefaultData().getPokemonByName("Volbeat").getSpeciesNumber();
			}
			// Normal case
			else
				return femalePoke.getSpeciesNumber();
		}
		return malePoke.getSpeciesNumber();
	}

	/**
	 * Generates the baby pokemon's moves
	 * @param species
	 * @return
	 */
	public MoveListEntry[] getBabyMoves(int species) {
		MoveListEntry[] moves = new MoveListEntry[4];
		MoveList moveList = MoveList.getDefaultData();
		ArrayList<MoveListEntry> possibleMoves = new ArrayList<MoveListEntry>();
		PokemonSpecies s = PokemonSpecies.getDefaultData().getSpecies(species);
		// List of moves by level 5
		for (int i = 1; i <= 5; i++) {
			if (s.getLevelMoves().containsKey(i)) {
				possibleMoves.add(moveList.getMove(s.getLevelMoves().get(i)));
			}
		}

		int moveNum = possibleMoves.size();
		if (possibleMoves.size() <= 4) {
			for (int i = 0; i < possibleMoves.size(); i++) {
				moves[i] = possibleMoves.get(i);
			}
		} else {
			for (int i = 0; i < moves.length; i++) {
				if (possibleMoves.size() == 0)
					moves[i] = null;
				moves[i] = possibleMoves.get(moveNum);
				moveNum--;
				if (moveNum == 0)
					break;
			}
		}

		// Moves that both parents know

		// List of egg moves
		possibleMoves.clear();
		for (int i = 0; i < s.getEggMoves().length; i++) {
			for (int x = 0; i < 4; i++) {
				if (malePoke.getMove(x) == moveList.getMove(s.getEggMoves()[i])) {
					possibleMoves.add(moveList.getMove(s.getEggMoves()[i]));
				}
			}
		}
		for (int i = 0; i < 4; i++) {
			if (moves[i] == null && possibleMoves.size() < i) {
				moves[i] = possibleMoves.get(i);
			}
		}

		return moves;
	}

	/**
	 * Generates the baby pokemon to hatch from the egg.
	 * @param species
	 * @return
	 * @throws Exception
	 */
	private Pokemon generateHatchling(int species) throws Exception{
		Pokemon hatchling;
		try{
		PokemonSpecies speciesData = PokemonSpecies.getDefaultData()
				.getSpecies(species);
		Random random = DataService.getBattleMechanics().getRandom();

		// get Nature if female or ditto is holding an everstone, 50% chance
		String nature = "";
		if (femalePoke.getItemName() == "Everstone") {
			if (random.nextInt(2) == 0) {
				nature = femalePoke.getNature().getName();
			}
		} else
			nature = PokemonNature.getNature(
					random.nextInt(PokemonNature.getNatureNames().length))
					.getName();

		int natureIndex = 0;
		for (String name : PokemonNature.getNatureNames()) {
			if (name == nature) {
				break;
			}
			natureIndex++;
		}

		// Get 3 random IVS from parents
		int[] ivs = new int[6];
		for (int iv : ivs) {
			ivs[iv] = speciesData.getBaseStats()[iv];
		}

		int[] attempt = new int[3];
		for (int i = 0; i < 3; i++) {
			int randomNum = DataService.getBattleMechanics().getRandom()
					.nextInt(2);
			attempt[i] = randomNum;
			if (i == 2) {
				if (attempt[0] == 0 && attempt[1] == 0) {
					randomNum = 1;
				} else if (attempt[0] == 1 && attempt[1] == 1) {
					randomNum = 0;
				}
			}
			int iv = DataService.getBattleMechanics().getRandom().nextInt(6);
			if (randomNum == 0) {
				ivs[iv] = malePoke.getBaseStats()[iv];
			} else {
				ivs[iv] = femalePoke.getBaseStats()[iv];
			}
		}

		hatchling = new Pokemon(DataService.getBattleMechanics(), 
				PokemonSpecies.getDefaultData().getSpecies(species),
				PokemonNature.getNature(natureIndex),
				speciesData.getPossibleAbilities(PokemonSpecies.getDefaultData())[random
						.nextInt(speciesData.getPossibleAbilities(
								PokemonSpecies.getDefaultData()).length)], "", Pokemon
						.generateGender(speciesData.getPossibleGenders()), 5,
				ivs, new int[6], getBabyMoves(species), new int[4]);
		} catch (Exception e) {
			throw new Exception("BreedingException: Hatchling generation issue");
		}
		return hatchling;
	}
}
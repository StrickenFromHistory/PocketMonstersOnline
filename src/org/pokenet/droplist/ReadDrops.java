package org.pokenet.droplist;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.pokenet.items.ItemBean;
import org.pokenet.items.ReadItems;
import org.pokenet.utils.StringUtils;

public class ReadDrops {
	HashMap<String,ArrayList<PokemonDrops>> pokeDropsList = new HashMap<String,ArrayList<PokemonDrops>>();
	HashMap<String,ArrayList<ItemDrops>> itemDropsList = new HashMap<String,ArrayList<ItemDrops>>();
	HashMap<Integer,ItemBean> itemsList;
	
	public ReadDrops(HashMap<Integer, ItemBean> itemsList){
		this.itemsList = itemsList;

	}
	public static void main(String[] args){
		HashMap<Integer, ItemBean> itemsList = new ReadItems().getItemsList();
		new ReadDrops(itemsList).generateDropWiki(new ReadDrops(itemsList).getPokemonDropsList());

	}
	public HashMap<String, ArrayList<PokemonDrops>> getPokemonDropsList() {
		BufferedReader br = null;
		try
		{
			br = new BufferedReader(new FileReader("itemdrops.txt"));
			String line = null;

			while ( (line = br.readLine()) != null )
			{
				String[] parse = line.split(" ");
				ArrayList<PokemonDrops> items = new ArrayList<PokemonDrops>();

				for(int i=1;i<parse.length;i++){
					PokemonDrops id = new PokemonDrops();
					ItemBean item = new ItemBean();
					item = new ReadItems(itemsList).getItem(Integer.parseInt(parse[i]));
					id.setDropChance(Integer.parseInt(parse[i+1]));
					id.setItem(item);
					i++;
					items.add(id);
				}

				pokeDropsList.put(parse[0],items);
			}

		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return pokeDropsList;
	}
	public HashMap<String, ArrayList<ItemDrops>> getItemDropsList(HashMap<String,ArrayList<PokemonDrops>> pokeDrops) {
		itemDropsList = new HashMap<String, ArrayList<ItemDrops>>();
		for(int w = 1; w < 900;w++){
			try{
				ItemBean item = itemsList.get(w);
				ArrayList<ItemDrops> ids = new ArrayList<ItemDrops>();
				Iterator<String> pokemon = pokeDrops.keySet().iterator();
				while(pokemon.hasNext()){
					String pokename = pokemon.next().toString();
					ArrayList<PokemonDrops> pDrops = pokeDrops.get(pokename);
					for(int i = 0;i<pDrops.size();i++){
						if(item.getName().equals(pDrops.get(i).getItem().getName())){
							ItemDrops id = new ItemDrops();
							id.setPokemon(pokename);
							id.setDropChance(pDrops.get(i).getDropChance());
							ids.add(id);
						}
					}	
				}
				itemDropsList.put(item.getName(), ids);
			}catch(Exception e){} //Not an item. 
		}

		return itemDropsList;
	}
	
	public ArrayList<ItemDrops> findPokemonWithItem(HashMap<String,ArrayList<ItemDrops>> itemDropsList,String itemname){
		ArrayList<ItemDrops> items = new ArrayList<ItemDrops>();
		try{
			items = itemDropsList.get(itemname.replace(" ","").toLowerCase());			
		}catch(Exception e){
		} //Not a bug. 
		return items;
	}
	
	public ArrayList<PokemonDrops> findItemWithPokemon(HashMap<String,ArrayList<PokemonDrops>> pokeDropsList,String pokemon){
		ArrayList<PokemonDrops> items = new ArrayList<PokemonDrops>();
		try{
			items = pokeDropsList.get(pokemon.replaceAll(" ","").toLowerCase());
			
		}catch(Exception e){
		} //Not a bug. 
		return items;
	}
	public void setPokeDropsList(HashMap<String, ArrayList<PokemonDrops>> drops) {
		this.pokeDropsList = drops;
	}
	
	public void setItemDropsList(HashMap<String, ArrayList<ItemDrops>> drops) {
		this.itemDropsList = drops;
	}
	
	public void generateDropWiki(HashMap<String,ArrayList<PokemonDrops>> pokeDrops){
		System.out.println("This is a list of the different kinds of items drops [[Pokemon]] have in Fearless Feebass. Each Pokemon has a 33% chance of dropping an item. ");
		System.out.println("Not all Pokemon in this list are curently catchable.");

		System.out.println("<i>We suggest you use Ctrl+F to navigate the page easier.</i>");
		
		Iterator<String> pokemon = pokeDrops.keySet().iterator();
		List<String> pokemen = new ArrayList<String>();
		while(pokemon.hasNext()){
			pokemen.add(pokemon.next());
		}
		Collections.sort(pokemen);
		for(int i=0;i<pokemen.size();i++){
			ArrayList<PokemonDrops> items =  pokeDrops.get(pokemen.get(i));
			System.out.println("");
			System.out.println("[["+StringUtils.capitalizeFirstLettersTokenizer(pokemen.get(i))+"]]");
			for(int x = 0;x<items.size();x++){
				System.out.println(" * [["+items.get(x).getItem().getName()+"]] "+items.get(x).getDropChance()+"%");	
			}
		}
	}
}

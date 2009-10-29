/*
 * This is a list of the different kinds of items drops [[Pokemon]] have in Fearless Feebass. Each Pokemon has a 33% chance of dropping an item. All Pokemon in this list are curently catchable.

[[Caterpie]] 
 * [[Oran Berry]] 100%
[[Metapod]] 
 * [[Oran Berry]] 100%
 Ball]] 25%
[[Pidgeotto]] 
 * [[Oran Berry]] 95% [[Poke Ball]] 5%
[[Rattata]] 


 */
package org.pokenet.droplist;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.pokenet.items.ItemBean;
import org.pokenet.items.ItemContents;
import org.pokenet.items.ReadItems;
import org.pokenet.utils.Snippet;

public class ReadDrops {
	HashMap<String,ArrayList<PokemonDrops>> pokeDrops = new HashMap<String,ArrayList<PokemonDrops>>();
	HashMap<String,ArrayList<ItemDrops>> itemDrops = new HashMap<String,ArrayList<ItemDrops>>();
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

				pokeDrops.put(parse[0].toLowerCase(),items);
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
		return pokeDrops;
	}
	public HashMap<String, ArrayList<ItemDrops>> getItemDropsList(HashMap<String,ArrayList<PokemonDrops>> pokeDrops) {
		itemDrops = new HashMap<String, ArrayList<ItemDrops>>();
		for(int w = 1; w < 900;w++){
			try{
				ItemBean ibean = itemsList.get(w);
				ItemContents item = ibean.getItem();
				ArrayList<ItemDrops> ids = new ArrayList<ItemDrops>();
				Iterator<String> pokemon = pokeDrops.keySet().iterator();
				while(pokemon.hasNext()){
					String pokename = pokemon.next().toString();
					ArrayList<PokemonDrops> pDrops = pokeDrops.get(pokename);
					for(int i = 0;i<pDrops.size();i++){
						if(item.getName().equals(pDrops.get(i).getItem().getItem().getName())){
							ItemDrops id = new ItemDrops();
							id.setPokemon(pokename);
							id.setDropChance(pDrops.get(i).getDropChance());
							ids.add(id);
						}
					}	
				}
				itemDrops.put(ibean.getItem().getName().replace(" ","").toLowerCase(), ids);
			}catch(Exception e){} //Not an item. 
		}

		return itemDrops;
	}
	public void setPokeDropsList(HashMap<String, ArrayList<PokemonDrops>> drops) {
		this.pokeDrops = drops;
	}
	public void setItemDropsList(HashMap<String, ArrayList<ItemDrops>> drops) {
		this.itemDrops = drops;
	}
	public void generateDropWiki(HashMap<String,ArrayList<PokemonDrops>> pokeDrops){
		System.out.println("This is a list of the different kinds of items drops [[Pokemon]] have in Fearless Feebass. Each Pokemon has a 33% chance of dropping an item. All Pokemon in this list are curently catchable.");
		Iterator<String> pokemon = pokeDrops.keySet().iterator();
		while(pokemon.hasNext()){
			String pokeName =pokemon.next();
			ArrayList<PokemonDrops> items =  pokeDrops.get(pokeName);
			System.out.println("");
			System.out.println("[["+Snippet.capitalizeFirstLettersTokenizer(pokeName)+"]]");
			for(int i = 0;i<items.size();i++){
				System.out.println(" * [["+items.get(i).getItem().getItem().getName()+"]] "+items.get(i).getDropChance()+"%");	
			}
		}
	}
}

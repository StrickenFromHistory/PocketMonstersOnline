package org.pokenet.client.ui.frames;

import java.util.List;

import mdes.slick.sui.Frame;
import mdes.slick.sui.Label;

import org.newdawn.slick.Color;
import org.newdawn.slick.loading.LoadingList;
import org.pokenet.client.backend.Translator;
import org.pokenet.client.backend.entity.OurPokemon;

public class PokemonInfoDialog extends Frame{
        private Label icon = new Label();
        private Label data[] = new Label[14];
        private Label labels[] = new Label[14];
       
        public PokemonInfoDialog(OurPokemon poke){
                initGUI(poke);
        }
       
        public void loadImage(OurPokemon poke){
                LoadingList.setDeferredLoading(true);
                icon.setImage(poke.getSprite());
                icon.setSize(60,60);
                icon.setLocation(5, 5);
                this.add(icon);
                LoadingList.setDeferredLoading(false);
        }
       
        public void initGUI(OurPokemon poke){
        	
        	try{getContentPane().setX(getContentPane().getX() - 1);
    		getContentPane().setY(getContentPane().getY() + 1);
        	List<String> translated = Translator.translate("_GUI");
                this.setBackground(new Color(255,255,255,200));
                int x = 70;
                int y = 5;
                for (int i = 0; i < 14; i++){
                        data[i] = new Label();
                        labels[i] = new Label();
                        data[i].setX(x + 80);
                        data[i].setY(y);
                        labels[i].setX(x);
                        labels[i].setY(y);
                        y += 20;
                        getContentPane().add(labels[i]);
                        getContentPane().add(data[i]);
                }
                labels[0].setText(translated.get(1));
                labels[1].setText(translated.get(2));
                labels[2].setText(translated.get(3));
                labels[3].setText(translated.get(4));
                labels[4].setText(translated.get(5));
                labels[5].setText(translated.get(6));
                labels[6].setText(translated.get(7));
                labels[7].setText(translated.get(8));
                labels[8].setText(translated.get(9));
                labels[9].setText(translated.get(10));
                labels[10].setText(translated.get(11));
                labels[11].setText(translated.get(12));
                labels[12].setText(translated.get(13));
                labels[13].setText(translated.get(14));
                //labels[13].setText("Exp to next level:");
                data[0].setText(String.valueOf(poke.getLevel()));
                data[1].setText(poke.getName());
                data[2].setText(String.valueOf(poke.getCurHP()) + "/"
                                + String.valueOf(poke.getMaxHP()));
                data[3].setText(String.valueOf(poke.getAtk()));
                data[4].setText(String.valueOf(poke.getDef()));
                data[5].setText(String.valueOf(poke.getSpatk()));
                data[6].setText(String.valueOf(poke.getSpdef()));
                data[7].setText(String.valueOf(poke.getSpeed()));
                data[8].setText(poke.getAbility());
                data[9].setText(String.valueOf(poke.getExp()));
                data[10].setText(poke.getNature());
               
                data[11].setText(String.valueOf(poke.getType1()));
                if(poke.getType2() == null){
                        data[12].setText("");
                }else{
                        data[12].setText(String.valueOf(poke.getType2()));
                }
                if(poke.getGender() == 1){
                        data[13].setText(translated.get(29));
                }else if(poke.getGender() == 2){
                        data[13].setText(translated.get(30));
                }else
                        data[13].setText(translated.get(31));
               
                for (int i = 0; i < data.length; i++) {
                        data[i].pack();
                }
                for (int i = 0; i < labels.length; i++) {
                        labels[i].pack();
                }
                loadImage(poke);
                setVisible(true);
                setSize(270, 310);
                setResizable(false);
                setTitle(poke.getName());
        	} catch (Exception e) {e.printStackTrace();}
        }
       
        public int setSpriteNumber(int x) {
                int i = 0;
                if (x <= 385) {
                        i = x + 1;
                } else if (x <= 388) {
                        i = 386;
                } else if (x <= 414) {
                        i = x - 2;
                } else if (x <= 416) {
                        i = 413;
                } else if(x==431){
                	i = 431;
                } else{
                        i = x - 4;
                }
                return i;
        }
}


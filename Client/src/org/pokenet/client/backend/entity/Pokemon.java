package org.pokenet.client.backend.entity;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.loading.LoadingList;

public class Pokemon {
        static final long serialVersionUID = 1;
       
        //load sprite and icon
        private Image m_sprite;
        private Image m_icon;
        private int m_spriteNum;
       
        //load trainer data
        private int m_trainerID;
       
        // 0 or 1
        private int m_gender;
        //name and species data
        private String m_name;
        private String m_nick;
        private Enums.Pokenum m_species;
        private boolean m_shiny;
       
        //level and types
        private int m_level;
        private Enums.Poketype m_type1, m_type2;
       
        //moves and pp
        private String[] m_moves = new String[4];
        private int[] m_movemaxPP = new int[4];
        private int[] m_movecurPP = new int[4];
       
        //stats
        private int m_maxHP, m_curHP;
       
        /**
         * Returns the sprite
         * @return
         */
        public Image getSprite() {
                return m_sprite;
        }
        
        /**
         * Loads the sprite
         */
        public void setSprite() {
                try{
                        LoadingList.setDeferredLoading(true);
                        String path = new String();
                        String index, isShiny = new String();
                       
                        if (!isShiny()){
                                isShiny = "normal/";
                        }else{
                                isShiny = "shiny/";
                        }
                       
                        if (m_spriteNum < 10) {
                                index = "00" + String.valueOf(m_spriteNum);
                        }
                        else if (m_spriteNum < 100){
                                index = "0" + String.valueOf(m_spriteNum);
                        }
                        else{
                                index = String.valueOf(m_spriteNum);
                        }
                        int pathGender;
                        if (getGender() == 1)
                                pathGender = 2;
                        else
                                pathGender = 3;
                       
                        try {
                                path = "/res/pokemon/front/" + isShiny + index + "-"
                                + pathGender + ".png";
                                System.out.println(path);
                                m_sprite = new Image(path.toString());
                        }
                        catch (Exception e) {
                                if(pathGender == 3)
                                        pathGender = 2;
                                else
                                        pathGender = 3;
                                path = "/res/pokemon/front/" + isShiny + index + "-"
                                + pathGender + ".png";
                                System.out.println(path);
                                m_sprite = new Image(path.toString());
                                e.printStackTrace();
                        }
                        LoadingList.setDeferredLoading(false);
                }catch (SlickException e){e.printStackTrace();}
        }
        
        /**
         * Returns the icon
         * @return
         */
        public Image getIcon() {
                return m_icon;
        }
        
        /**
         * Loads the icon
         */
        public void setIcon() {
                try{
                        LoadingList.setDeferredLoading(true);
                        String path = new String();
                        String index = new String();
                       
                        if (m_spriteNum < 10) {
                                index = "00" + String.valueOf(m_spriteNum);
                        }
                        else if (m_spriteNum < 100){
                                index = "0" + String.valueOf(m_spriteNum);
                        }
                        else{
                                index = String.valueOf(m_spriteNum);
                        }
                       
                        path = "/res/pokemon/icons/" + index + ".gif";
                               
                        m_icon = new Image(path.toString());
                        LoadingList.setDeferredLoading(false);
                }catch (SlickException e){e.printStackTrace();}
        }      
        
        /**
         * Returns trainer ID
         * @return
         */
        public int getTrainerID() {
                return m_trainerID;
        }
        
        /**
         * Sets Trainer ID
         * @param trainerID
         */
        public void setTrainerID(int trainerID) {
                this.m_trainerID = trainerID;
        }
        
        /**
         * Returns gender
         * @return
         */
        public int getGender() {
                return m_gender;
        }
        
        /**
         * Sets gender
         * @param gender
         */
        public void setGender(int gender) {
                this.m_gender = gender;
        }
        
        /**
         * Returns name
         * @return
         */
        public String getName() {
                return m_name;
        }
        
        /**
         * Sets name
         * @param name
         */
        public void setName(String name) {
        		this.m_name = name;
        }
        
        /**
         * Returns nickname
         * @return
         */
        public String getNick() {
                return m_nick;
        }
        
        /**
         * Sets nickname
         */
        public void setNick(String nick) {
                this.m_nick = nick;
        }
        
        /**
         * Returns species
         * @return
         */
        public Enums.Pokenum getSpecies() {
                return m_species;
        }
        
        /**
         * Sets species
         * @param species
         */
        public void setSpecies(Enums.Pokenum species) {
                this.m_species = species;
        }
        
        /**
         * Returns whether or not a pokemon is shiny
         * @return
         */
        public boolean isShiny() {
                return m_shiny;
        }
        
        /**
         * Sets whether a pokemon is shiny
         * @param shiny
         */
        public void setShiny(boolean shiny) {
                this.m_shiny = shiny;
        }
        
        /**
         * Returns level
         * @return
         */
        public int getLevel() {
                return m_level;
        }
        
        /**
         * Sets level
         * @param level
         */
        public void setLevel(int level) {
                this.m_level = level;
        }
        
        /**
         * Returns type 1
         * @return
         */
        public Enums.Poketype getType1() {
                return m_type1;
        }
        
        /**
         * Sets type 1
         * @param type1
         */
        public void setType1(Enums.Poketype type1) {
                this.m_type1 = type1;
        }
        
        /**
         * Returns type 2
         * @return
         */
        public Enums.Poketype getType2() {
                return m_type2;
        }
        
        /**
         * Sets type 2
         * @param type2
         */
        public void setType2(Enums.Poketype type2) {
                this.m_type2 = type2;
        }
        
        /**
         * Returns moves
         * @return
         */
        public String[] getMoves() {
                return m_moves;
        }
        
        /**
         * Sets moves
         * @param moves
         */
        public void setMoves(String[] moves) {
        	this.m_moves = moves;
        }
        
        /**
         * Returns maximum PP for moves
         * @return
         */
        public int[] getMovemaxPP() {
                return m_movemaxPP;
        }
        
        /**
         * Sets maximum PP for moves
         * @param movemaxPP
         */
        public void setMovemaxPP(int[] movemaxPP) {
                this.m_movemaxPP = movemaxPP;
        }
        
        /**
         * Returns current PP for moves
         * @return
         */
        public int[] getMovecurPP() {
                return m_movecurPP;
        }
        
        /**
         * Sets current PP for moves
         * @param movecurPP
         */
        public void setMovecurPP(int[] movecurPP) {
                this.m_movecurPP = movecurPP;
        }
        
        /**
         * Sets current PP for a specified move
         * @param move
         * @param pp
         */
        public void setMovecurPP(int move, int pp) {
                this.m_movecurPP[move] = pp;
        }
        
        /**
         * Returns max HP
         * @return
         */
        public int getMaxHP() {
                return m_maxHP;
        }
        
        /**
         * Sets max HP
         * @param maxHP
         */
        public void setMaxHP(int maxHP) {
                this.m_maxHP = maxHP;
        }
        
        /**
         * Returns current HP
         * @return
         */
        public int getCurHP() {
                return m_curHP;
        }
        
        /**
         * Set current HP
         * @param curHP
         */
        public void setCurHP(int curHP) {
                this.m_curHP = curHP;
        }
        
        /**
         * Sets sprite number based on pokemon index
         * @param x
         * @return
         */
        public void setSpriteNumber(int x) {
                int i = 0;
                if (x <= 385) {
                        i = x + 1;
                } else if (x <= 388) {
                        i = 386;
                } else if (x <= 414) {
                        i = x - 2;
                } else if (x <= 416) {
                        i = 413;
                } else {
                        i = x - 4;
                }
                m_spriteNum = i;
        }
        
        /**
         * Returns the sprite number
         * @return
         */
        public int getSpriteNumber(){
        	return m_spriteNum;
        }
        
        /**
         * Ststic method to get the file path for a pokemon's icon by it's index number
         * @param i
         * @return
         */
        public static String getIconPathByIndex(int i){
        	String path = new String();
        	String index = new String();

        	if (i < 10) {
        		index = "00" + String.valueOf(i);
        	} else if (i < 100){
        		index = "0" + String.valueOf(i);
        	} else {
        		index = String.valueOf(i);
        	}
               
                path = "/res/pokemon/icons/" + index + ".gif";
                return path;
        }
}


package org.pokenet.client.backend.entity;

import org.newdawn.slick.Image;

/**
 * Represents one of our pokemon
 * @author shadowkanji
 * @author ZombieBear
 * 
 */
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.loading.LoadingList;

public class OurPokemon extends Pokemon {
        private Image m_backSprite;
        private int m_exp;
        private int m_atk;
        private int m_def;
        private int m_speed;
        private int m_spatk;
        private int m_spdef;
        private String m_ability;
        private String m_nature;
        
        /**
         * Returns Exp
         * @return
         */
        public int getExp() {
                return m_exp;
        }
        
        /**
         * Sets Exp
         * @param exp
         */
        public void setExp(int exp) {
                this.m_exp = exp;
        }
        
        /**
         * Returns back sprite
         * @return
         */
        public Image getBackSprite() {
                return m_backSprite;
        }
        
        /**
         * Sets back sprite
         */
        public void setBackSprite() {
                try{
                        LoadingList.setDeferredLoading(true);
                        String path = new String();
                        String index, isShiny = new String();
                        
                        if (!isShiny()){
                                isShiny = "normal/";
                        }else{
                                isShiny = "shiny/";
                        }
                        
                        int pokeNum = setSpriteNumber(getSpecies().ordinal());
                        
                        if (pokeNum < 9) {
                                index = "00" + String.valueOf(pokeNum + 1);
                        }
                        else if (pokeNum < 99){
                                index = "0" + String.valueOf(pokeNum + 1);
                        }
                        else{
                                index = String.valueOf(pokeNum + 1);
                        }
                        
                        int gender;
                        if (getGender() == 1)
                                gender = 2;
                        else
                                gender = 3;

                        try {
                                path = "/res/pokemon/back/" + isShiny + index + "-"
                                + String.valueOf(gender) + ".gif";
                                System.out.println(path);
                                m_backSprite = new Image(path.toString()).getSubImage(0, 0, 80, 80);
                        }
                        catch (Exception e) {
                                if (gender == 3) {
                                        path = "/res/pokemon/back/" + isShiny + index + "-2"
                                        + ".gif";
                                }
                                else {
                                        path = "/res/pokemon/back/" + isShiny + index + "-3"
                                        + ".gif";
                                }
                                m_backSprite = new Image(path.toString()).getSubImage(0, 0, 80, 80);
                                e.printStackTrace();
                        }
                        LoadingList.setDeferredLoading(false);
                }catch (SlickException e){e.printStackTrace();}
        }
        
        /**
         * Returns ATK
         */
        public int getAtk() {
                return m_atk;
        }

        /**
         * Sets ATK
         */
        public void setAtk(int atk) {
                this.m_atk = atk;
        }
        
        /**
         * Returns DEF
         */
        public int getDef() {
                return m_def;
        }
        
        /**
         * Sets DEF
         * @param def
         */
        public void setDef(int def) {
                this.m_def = def;
        }
        
        /**
         * Returns SPD
         */
        public int getSpeed() {
                return m_speed;
        }
        
        /**
         * Sets SPD
         * @param speed
         */
        public void setSpeed(int speed) {
                this.m_speed = speed;
        }
        
        /**
         * Returns SP.ATK
         */
        public int getSpatk() {
                return m_spatk;
        }
        
        /**
         * Sets SP.ATK
         */
        public void setSpatk(int spatk) {
                this.m_spatk = spatk;
        }
        
        /**
         * Returns SP.DEF
         * @return
         */
        public int getSpdef() {
                return m_spdef;
        }
        
        /**
         * Sets SP.DEF
         * @param spdef
         */
        public void setSpdef(int spdef) {
                this.m_spdef = spdef;
        }
        
        /**
         * Returns ability
         * @return
         */
        public String getAbility() {
                return m_ability;
        }
        
        /**
         * Sets ability
         * @param ability
         */
        public void setAbility(String ability) {
                this.m_ability = ability;
        }
        
        /**
         * Returns nature
         * @return
         */
        public String getNature() {
                return m_nature;
        }
        
        /**
         * Sets nature
         * @param nature
         */
        public void setNature(String nature) {
                this.m_nature = nature;
        }
}


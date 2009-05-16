package org.pokenet.server.battle.mechanics.intelligence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;
import java.util.TreeSet;

import org.pokenet.server.battle.mechanics.PokemonType;

/**
 *
 * @author Colin
 */
public class Situation {
    
    private ArrayList<Memory> m_memory = new ArrayList<Memory>();
    private static final Random m_random = new Random();
    private static final Situation m_inst = new Situation();
    private static final String m_file = "knowledge";
    private static int m_saves = 0;
    
    /**
     * Create a single Situation class.
     */
    private Situation() {
        File f = new File(m_file);
        if (f.exists()) {
            loadFromFile();
        } else {
            m_memory.add(new Memory(PokemonType.T_BUG,
                    PokemonType.T_DARK,
                    true,
                    "Tackle")
                );
        }
    }
    
    /**
     * Load the Situations database from a file.
     */
    @SuppressWarnings("unchecked")
	private void loadFromFile() {
        try {
            File f = new File(m_file);
            FileInputStream file = new FileInputStream(f);
            ObjectInputStream obj = new ObjectInputStream(file);
            m_memory = (ArrayList)obj.readObject();
            obj.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
    
    /**
     * Save the Situations database to a file.
     */
    public synchronized void saveToFile() {
        try {
            File f = new File(m_file + m_saves++);
            FileOutputStream file = new FileOutputStream(f);
            ObjectOutputStream obj = new ObjectOutputStream(file);
            obj.writeObject(m_memory);
            obj.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    
    /**
     * Get an instance of this class.
     */
    public static Situation getInstance() {
        return m_inst;
    }
    
    /**
     * Get the score of a memory, based on past experience and its closeness
     * to a target.
     */
    private static int getMemoryScore(Memory target, Memory mem) {
        // Offer a multiplicative factor based on closeness to the target.
        int factor = 0;
        if (target.m_low == mem.m_low)
            ++factor;
        if (target.m_me.equals(target.m_me))
            ++factor;
        if (target.m_opponent.equals(target.m_opponent))
            ++factor;
        
        // If it doesn't match at all, it will get a score of zero!
        return (target.m_score * factor);
    }
    
    /**
     * Find an exact match for a memory.
     */
    private synchronized Memory findMemory(Memory target) {
        Iterator<Memory> i = m_memory.iterator();
        while (i.hasNext()) {
            Memory mem = (Memory)i.next();
            if (target.equals(mem)) {
                return mem;
            }
        }
        return null;
    }
    
    /**
     * Find whether a String is in a move list.
     */
    private boolean isInMoveList(String entry, String[] moves) {
        /**if (entry == null) {
            return true;
        }**/
        for (int i = 0; i < moves.length; ++i) {
            if (moves[i].equals(entry)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Find the best move to use for the situation.
     */
    @SuppressWarnings("unchecked")
	public synchronized String getBestMemory(final Memory target, String[] moves) {
        Comparator comp = new Comparator() {
                public int compare(Object o1, Object o2) {
                    Memory a = (Memory)o1, b = (Memory)o2;
                    int scoreA = getMemoryScore(target, a);
                    int scoreB = getMemoryScore(target, b);
                    if (scoreA < scoreB) {
                        return -1;
                    } else if (scoreA > scoreB) {
                        return 1;
                    }
                    return 0;
                }
            };
            
        TreeSet items = new TreeSet(comp);
        Iterator i = m_memory.iterator();
        while (i.hasNext()) {
            Memory mem = (Memory)i.next();
            if (!isInMoveList(mem.m_move, moves)) {
                continue;
            }
            if (getMemoryScore(target, mem) != 0) {
                items.add(mem);
            }
        }
        
        if ((items.size() == 0) || (m_random.nextDouble() <= 0.2)) {
            Memory best = (Memory)m_memory.get(m_random.nextInt(m_memory.size()));
            if (!isInMoveList(best.m_move, moves)) {
                return moves[m_random.nextInt(moves.length)];
            }
        }
        
        if (m_random.nextDouble() <= 0.4) {
            Memory mem = (Memory)items.last();
            return mem.m_move;
        }
        
        int half = items.size() / 2;
        if (half == 0) {
            Memory mem = (Memory)items.last();
            return mem.m_move;
        }
        int start = m_random.nextInt(2) * half;
        int idx = start + m_random.nextInt(half);
        
        Iterator j = items.iterator();
        int k = 0;
        while (j.hasNext()) {
            if (k++ == idx) {
                break;
            }
            j.next();
        }
        Memory mem = (Memory)j.next();
        return mem.m_move;
    }
    
    /**
     * Update a set of memories -- were they positive experiences?
     */
    public synchronized void updateMemories(ArrayList<?> memories, boolean positive, int factor) {
        Iterator<?> i = memories.iterator();
        while (i.hasNext()) {
            Memory target = (Memory)i.next();
            Memory mem = findMemory(target);
            int delta = (positive ? 1 : -1) * factor;
            if (mem == null) {
                target.m_score = delta;
                m_memory.add(target);
            } else {
                mem.m_score += delta;
            }
        }
    }
    
}

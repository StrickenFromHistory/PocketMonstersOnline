package org.pokenet.client.backend;

import java.util.LinkedList;
import java.util.Queue;

import org.pokenet.client.GameClient;
import org.pokenet.client.ui.MoveLearning;

/**
 * Handles move learning, and allowis for queing items.
 * @author ZombieBear
 *
 */
public class MoveLearningManager extends Thread{
	private static MoveLearningManager m_instance;
	private MoveLearning m_moveLearning;
	private Queue<MoveLearnQueueObject> m_moveLearningQueue;
	private boolean m_canLearn = false;
	private boolean m_isRunning = true;
	
	/**
	 * Default constructor
	 */
	public MoveLearningManager() {
		m_instance = this;
		m_moveLearningQueue = new LinkedList<MoveLearnQueueObject>();
		m_moveLearning = new MoveLearning();
		System.out.println("Move Learning Manager started.");
	}
	
	/**
	 * Actions to be performed while the thread runs
	 */
	public void run(){
		while (true){
			try {
				Thread.sleep(250);
			} catch (Exception e) {}
			while (m_isRunning){
				if (m_canLearn && !m_moveLearningQueue.isEmpty()) {
					MoveLearnQueueObject temp = m_moveLearningQueue.poll();
					learnMove(temp.getPokeIndex(), temp.getMoveName());
					m_canLearn = false;
				}
				try {
					Thread.sleep(500);
				} catch (Exception e) {}
			}
		}
	}
	
	/**
	 * Returns the instance
	 * @return ths instance
	 */
	public static MoveLearningManager getInstance() {
		return m_instance;
	}

	/**
	 * Returns the Move Learning window
	 * @return the Move Learning window
	 */
	public MoveLearning getMoveLearning() {
		return m_moveLearning;
	}
	
	/**
	 * A pokemon wants to learn a move
	 * @param pokeIndex
	 * @param move
	 */
	public void learnMove(int pokeIndex, String move){
		BattleManager.getInstance().getBattleWindow().setAlwaysOnTop(false);
		m_moveLearning.learnMove(pokeIndex, move);
		GameClient.getInstance().getDisplay().add(m_moveLearning);
	}
	
	/**
	 * Removes the Move Learning window
	 */
	public void removeMoveLearning() {
		BattleManager.getInstance().getBattleWindow().setAlwaysOnTop(true);
		if (!m_moveLearningQueue.isEmpty())
			m_canLearn = true;
		GameClient.getInstance().getUi().nullSpeechFrame();
		GameClient.getInstance().getDisplay().remove(m_moveLearning);
	}
	
	/**
	 * Queues a move to be learned
	 * @param index
	 * @param move
	 */
	public void queueMoveLearning(int index, String move) {
		m_isRunning = true;
		if (m_moveLearningQueue.isEmpty())
			m_canLearn = true;
		m_moveLearningQueue.add(new MoveLearnQueueObject(index, move));
	}
}

/**.
 * Queue object for move learning
 * @author ZombieBear
 *
 */
class MoveLearnQueueObject {
	private int m_pokeIndex;
	private String m_move;
	
	/**
	 * Default constructor
	 * @param index
	 * @param move
	 */
	public MoveLearnQueueObject(int index, String move) {
		m_pokeIndex = index;
		m_move = move;
	}
	
	/**
	 * Returns the pokemon's index
	 * @return the pokemon's index
	 */
	public int getPokeIndex() {
		return m_pokeIndex;
	}
	
	/**
	 * Returns the name of the move to be learned
	 * @return the name of the move to be learned
	 */
	public String getMoveName() {
		return m_move;
	}
}
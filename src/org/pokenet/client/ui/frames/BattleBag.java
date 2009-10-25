package org.pokenet.client.ui.frames;

import org.pokenet.client.backend.BattleManager;

/**
 * Bag used during battles
 * @author ZombieBear
 *
 */
public class BattleBag extends BigBagDialog {
	/**
	 * Default Constructor
	 */
	public BattleBag() {
		super();
		m_categoryButtons[0].setEnabled(false);
		m_categoryButtons[4].setEnabled(false);
		m_curCategory = 1;
		m_update = true;
	}
	
	@Override
	public void useItem(int i){
		destroyPopup();
		if (m_curCategory == 0 || m_curCategory == 3){
			m_popup = new ItemPopup(m_itemBtns.get(i).getToolTipText().split("\n")[0], Integer.parseInt(
					m_itemBtns.get(i).getName()), false, true);
			setAlwaysOnTop(false);
			m_popup.setLocation(m_itemBtns.get(i).getAbsoluteX(), m_itemBtns.get(i).getAbsoluteY() 
					+ m_itemBtns.get(i).getHeight() - getTitleBar().getHeight());
			getDisplay().add(m_popup);
		} else {
			m_popup = new ItemPopup(m_itemBtns.get(i).getToolTipText().split("\n")[0], Integer.parseInt(
					m_itemBtns.get(i).getName()), true, true);
			setAlwaysOnTop(false);
			m_popup.setLocation(m_itemBtns.get(i).getAbsoluteX(), m_itemBtns.get(i).getAbsoluteY() 
					+ m_itemBtns.get(i).getHeight() - getTitleBar().getHeight());
			getDisplay().add(m_popup);
		}
		closeBag();
	}

	@Override
	public void closeBag() {
		setVisible(false);
		BattleManager.getInstance().getBattleWindow().getDisplay().remove(this);
		BattleManager.getInstance().getBattleWindow().showAttack();
		BattleManager.getInstance().getBattleWindow().m_bag = null;
	}
}

package org.pokenet.client.ui.frames;

import mdes.slick.sui.Button;
import mdes.slick.sui.Frame;
import mdes.slick.sui.Label;
import mdes.slick.sui.event.ActionListener;

import org.pokenet.client.GameClient;

/**
 * Confirmation Dialog
 * @author ZombieBear
 *
 */
public class ConfirmationDialog extends Frame{
	private Label m_text;
	private Button m_yesBtn, m_noBtn;

	/**
	 * Default Constructor
	 * @param text
	 */

	public ConfirmationDialog(String text){
		m_text = new Label(text);
		m_yesBtn = new Button();
		m_noBtn = new Button();
		
		getContentPane().add(m_text);
		
		m_yesBtn.setText("Yes");
		m_yesBtn.setSize(30, 20);
		getContentPane().add(m_yesBtn);
		
		m_noBtn.setText("No");
		m_noBtn.setSize(30, 20);
		getContentPane().add(m_noBtn);
		
		this.setSize(m_text.getWidth(), m_text.getHeight() + m_yesBtn.getHeight() + 20);
		this.setAlwaysOnTop(true);
		this.setVisible(true);
		GameClient.getInstance().getDisplay().add(this);
	}
	
	/**
	 * Constructor
	 * @param text
	 * @param yes
	 * @param no
	 */
	public ConfirmationDialog(String text, ActionListener yes, ActionListener no){
		super(text);
		addYesListener(yes);
		addNoListener(no);
	}
	
	/**
	 * Sets the Yes action
	 */
	public void addYesListener(ActionListener yes){
		m_yesBtn.addActionListener(yes);
	}
	
	/**
	 * Sets the No action
	 */
	public void addNoListener(ActionListener no){
		m_noBtn.addActionListener(no);
	}
}

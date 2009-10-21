package org.pokenet.client.ui.base;

import mdes.slick.sui.Button;
import mdes.slick.sui.Container;
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
	private Button m_yesBtn, m_noBtn;

	/**
	 * Default Constructor
	 * @param text
	 */

	public ConfirmationDialog(String text){
		super("Awaiting confirmation");
		getContentPane().setX(getContentPane().getX() - 1);
		getContentPane().setY(getContentPane().getY() + 1);
		Container m_label = new Container();
		String[] m_lines = text.split("\n");
		
		int maxWidth = 0;
		int maxHeight = 0;
		
		for (String s : m_lines) {
			Label line = new Label(s);
			line.pack();
			
			int lineWidth = (int)line.getWidth();
			int lineHeight = (int)line.getHeight();
			
			if (lineWidth > maxWidth)
				maxWidth = lineWidth;
			
			line.setY(maxHeight);
			maxHeight += lineHeight;
			
			m_label.add(line);
		}
		m_label.setSize(maxWidth, maxHeight);
		
		m_yesBtn = new Button();
		m_noBtn = new Button();
		
		m_yesBtn.setText("Yes");
		m_yesBtn.setSize(50, 25);
		m_yesBtn.setY(m_label.getY() + m_label.getHeight() + 20);
		
		m_noBtn.setText("No");
		m_noBtn.setSize(50, 25);
		m_noBtn.setY(m_yesBtn.getY());
		
		getContentPane().add(m_label);
		getContentPane().add(m_yesBtn);
		getContentPane().add(m_noBtn);
		
		m_label.setLocation(5, 15);
		
		this.setResizable(false);
		this.setSize(m_label.getWidth() + 10, m_label.getHeight() + 80);
		m_yesBtn.setX((getWidth() / 2) - (105 / 2));
		m_noBtn.setX(m_yesBtn.getX() + 55);
		
		setCenter();
		this.setVisible(true);
		GameClient.getInstance().getDisplay().add(this);
		this.setAlwaysOnTop(true);
	}
	
	/**
	 * Constructor
	 * @param text
	 * @param yes
	 * @param no
	 */
	public ConfirmationDialog(String text, ActionListener yes, ActionListener no){
		this(text);
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
	
	/**
	 * Centers the frame
	 */
	public void setCenter() {
		int height = (int) GameClient.getInstance().getDisplay().getHeight();
		int width = (int) GameClient.getInstance().getDisplay().getWidth();
		int x = (width / 2) - ((int)this.getWidth() / 2);
		int y = (height / 2) - ((int)this.getHeight() / 2);
		this.setLocation(x, y);
	}
}

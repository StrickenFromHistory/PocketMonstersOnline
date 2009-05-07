package org.pokenet.client.ui.frames;

import mdes.slick.sui.Button;
import mdes.slick.sui.Container;
import mdes.slick.sui.Frame;
import mdes.slick.sui.Label;
import mdes.slick.sui.event.ActionEvent;
import mdes.slick.sui.event.ActionListener;
import mdes.slick.sui.event.MouseAdapter;
import mdes.slick.sui.event.MouseEvent;
import mdes.slick.sui.skin.simple.SimpleArrowButton;

import org.newdawn.slick.Color;
import org.pokenet.client.GameClient;

/**
 * Friends List
 * @author ZombieBear
 *
 */
public class FriendListDialog extends Frame {
	String[] m_friends;
	Label[] m_shownFriends = new Label[10];
	Button m_up, m_down;
	int m_index;
	PopUp m_popup;
	
	/**
	 * Default Constructor
	 * @param friends
	 */
	public FriendListDialog(String[] friends) {
		m_friends = friends;
		m_index = 0;
		initGUI();
	}

	/**
	 * Initializes the interface
	 */
	public void initGUI() {
		setBackground(new Color(0, 0, 0, 200));
		setTitle("Friends");
		setSize(170, 180);
	
		m_up = new SimpleArrowButton(SimpleArrowButton.FACE_UP);
		m_up.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				scroll(-1);
			}
		});
		m_up.setEnabled(false);
		m_up.setSize(22, 22);
		m_up.setLocation(getWidth() - 26, 3);
		getContentPane().add(m_up);
		m_down = new SimpleArrowButton(SimpleArrowButton.FACE_DOWN);
		if (m_friends.length <= 10)
			m_down.setEnabled(false);
		m_down.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				scroll(1);
			}
		});
		m_down.setSize(22, 22);
		m_down.setLocation(getWidth() - 26, 131);
		getContentPane().add(m_down);
		scroll(0);
		
		setResizable(false);
		this.getTitleBar().getCloseButton().setVisible(false);
	}
	
	/**
	 * Draws the shown friends based on the current scrolling index
	 * @param index
	 */
	public void scroll(int index){
		int y = -15;
		m_index += index;
		if (m_index == 0)
			m_up.setEnabled(false);
		else
			m_up.setEnabled(true);
		
		if (m_index + 10 >= m_friends.length)
			m_down.setEnabled(false);
		else
			m_down.setEnabled(true);
		
		for (int i = 0; i < 10; i++){
			final int j = i;
			if (m_shownFriends[i] != null){
				getContentPane().remove(m_shownFriends[i]);
				m_shownFriends[i] = null;
			}
			try{
				m_shownFriends[i] = new Label(m_friends[i + m_index]);
			} catch (Exception e) {
				m_shownFriends[i] = new Label();
			}
			m_shownFriends[i].pack();
			m_shownFriends[i].setFont(GameClient.getFontSmall());
			m_shownFriends[i].setForeground(Color.white);
			m_shownFriends[i].addMouseListener(new MouseAdapter(){
				@Override
				public void mouseEntered(MouseEvent e) {
					super.mouseEntered(e);
					m_shownFriends[j].setForeground(new Color(255, 215, 0));
				}

				@Override
				public void mouseExited(MouseEvent e) {
					super.mouseExited(e);
					m_shownFriends[j].setForeground(new Color(255, 255, 255));
				}
				
				@Override
				public void mouseReleased(MouseEvent e){
					if (e.getButton() == 1){
						if (GameClient.getInstance().getDisplay().containsChild(m_popup))
							GameClient.getInstance().getDisplay().remove(m_popup);
						m_popup = new PopUp(m_shownFriends[j].getText());
						m_popup.setLocation(e.getAbsoluteX(),e.getAbsoluteY() - 10);
						GameClient.getInstance().getDisplay().add(m_popup);
					}
				}
				});
			getContentPane().add(m_shownFriends[i]);
			y += 15;
			m_shownFriends[i].setLocation(5, y);
		}
	}
}

/**
 * PopUp for right Click
 * @author administrator
 *
 */
class PopUp extends Frame{
	Button m_whisper, m_cancel;
	Label m_name;
	public PopUp(String friend){
		m_name = new Label(friend);
		m_name.setFont(GameClient.getFontSmall());
		m_name.setForeground(Color.white);
		m_name.pack();
		m_name.setLocation(0,0);
		getContentPane().add(m_name);
		m_whisper = new Button("Whisper");
		m_whisper.setSize(100,25);
		m_whisper.setLocation(0, m_name.getY() + m_whisper.getTextHeight());
		getContentPane().add(m_whisper);
		m_cancel = new Button("Cancel");
		m_cancel.setSize(100,25);
		m_cancel.setLocation(0, m_whisper.getY() + m_cancel.getHeight());
		getContentPane().add(m_cancel);
		setBackground(new Color(0,0,0,150));
		setSize(100, 76 + m_name.getTextHeight());
		getTitleBar().setVisible(false);
		setVisible(true);
		setResizable(false);
		setAlwaysOnTop(true);
		m_cancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				destroy();				
			}
		});
	}

	/**
	 * Destroys the popup
	 */
	public void destroy(){
		GameClient.getInstance().getDisplay().remove(this);
	}
}
package org.pokenet.client.ui.frames;

import java.util.ArrayList;
import java.util.List;

import mdes.slick.sui.Button;
import mdes.slick.sui.Frame;
import mdes.slick.sui.Label;
import mdes.slick.sui.event.ActionEvent;
import mdes.slick.sui.event.ActionListener;
import mdes.slick.sui.event.MouseAdapter;
import mdes.slick.sui.event.MouseEvent;
import mdes.slick.sui.skin.simple.SimpleArrowButton;

import org.newdawn.slick.Color;
import org.pokenet.client.GameClient;
import org.pokenet.client.ui.base.ConfirmationDialog;

/**
 * Friends List
 * @author ZombieBear
 *
 */
@SuppressWarnings("deprecation")
public class FriendListDialog extends Frame {
	List<String> m_friends, m_online;
	Label[] m_shownFriends = new Label[10];
	Button m_up, m_down;
	int m_index;
	PopUp m_popup;
	
	/**
	 * Default Constructor
	 * @param friends
	 */
	public FriendListDialog() {
		m_friends = new ArrayList<String>();
		m_online = new ArrayList<String>();
		m_index = 0;
		initGUI();
	}

	/**
	 * Initializes the interface
	 */
	public void initGUI() {
		getContentPane().setX(getContentPane().getX() - 1);
		getContentPane().setY(getContentPane().getY() + 1);
		setBackground(new Color(0, 0, 0, 85));
		setTitle("Friends");
		setSize(170, 180);
	
		m_up = new SimpleArrowButton(SimpleArrowButton.FACE_UP);
		m_up.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				scroll(-1);
			}
		});
		m_up.setEnabled(false);
		m_up.setSize(15, 15);
		m_up.setLocation(getWidth() - 15, 0);
		getContentPane().add(m_up);
		m_down = new SimpleArrowButton(SimpleArrowButton.FACE_DOWN);
		if (m_friends.size() <= 10)
			m_down.setEnabled(false);
		m_down.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				scroll(1);
			}
		});
		m_down.setSize(15, 15);
		m_down.setLocation(getWidth() - 15, getHeight() - 15 - getTitleBar().getHeight());
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
		
		if (m_index + 10 >= m_friends.size())
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
				m_shownFriends[i] = new Label(m_friends.get(i + m_index));
			} catch (Exception e) {
				m_shownFriends[i] = new Label();
			}
			m_shownFriends[i].pack();
			m_shownFriends[i].setFont(GameClient.getFontSmall());
			if (m_online.contains(m_shownFriends[i]))
				m_shownFriends[i].setForeground(Color.white);
			else
				m_shownFriends[i].setForeground(Color.gray);
			m_shownFriends[i].addMouseListener(new MouseAdapter(){
				@Override
				public void mouseEntered(MouseEvent e) {
					super.mouseEntered(e);
					m_shownFriends[j].setForeground(new Color(255, 215, 0));
				}

				@Override
				public void mouseExited(MouseEvent e) {
					super.mouseExited(e);
					if (m_online.contains(m_shownFriends[j]))
						m_shownFriends[j].setForeground(Color.white);
					else
						m_shownFriends[j].setForeground(Color.gray);
				}
				
				@Override
				public void mouseReleased(MouseEvent e){
					if (e.getButton() == 1){
						if (GameClient.getInstance().getDisplay().containsChild(m_popup))
							GameClient.getInstance().getDisplay().remove(m_popup);
						m_popup = new PopUp(m_shownFriends[j].getText(), m_online.contains(m_shownFriends[j].getText()));
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
	
	/**
	 * Removes a friend from the list
	 * @param friend
	 */
	public void removeFriend(String friend){
		m_friends.remove(friend);
		scroll(0);
	}
	
	/**
	 * Adds a friend from the list
	 * @param friend
	 */
	public void addFriend(String friend){
		m_friends.add(friend);
		scroll(0);
	}
	
	/**
	 * Sets the online/offline status for 
	 * @param isOnline
	 */
	public void setFriendOnline(String friend, boolean isOnline){
		if (isOnline){
			if (!m_online.contains(friend))
				m_online.add(friend);
		} else if (m_online.contains(friend))
			m_online.remove(friend);
		
		scroll(0);
	}
}

/**
 * PopUp for right Click
 * @author ZombieBear
 *
 */
class PopUp extends Frame{
	Button m_remove, m_whisper, m_cancel;
	ConfirmationDialog m_confirm;
	Label m_name;
	
	/**
	 * Default Constructor
	 * @param friend
	 */
	public PopUp(String friend, boolean online){
		getContentPane().setX(getContentPane().getX() - 1);
		getContentPane().setY(getContentPane().getY() + 1);
		m_name = new Label(friend);
		m_name.setFont(GameClient.getFontSmall());
		m_name.setForeground(Color.white);
		m_name.pack();
		m_name.setLocation(0,0);
		getContentPane().add(m_name);
		m_remove = new Button("Remove");
		m_remove.setSize(100,25);
		m_remove.setLocation(0, m_name.getY() + m_name.getTextHeight() + 3);
		getContentPane().add(m_remove);
		m_whisper = new Button("Whisper");
		m_whisper.setSize(100,25);
		m_whisper.setLocation(0, m_remove.getY() + m_remove.getHeight());
		m_whisper.setEnabled(online);
		getContentPane().add(m_whisper);
		m_cancel = new Button("Cancel");
		m_cancel.setSize(100,25);
		m_cancel.setLocation(0, m_whisper.getY() + m_cancel.getHeight());
		getContentPane().add(m_cancel);
		setBackground(new Color(0,0,0,150));
		setSize(100, 103 + m_name.getTextHeight());
		getTitleBar().setVisible(false);
		setVisible(true);
		setResizable(false);
		setAlwaysOnTop(true);
		
		m_remove.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				destroy();
				ActionListener m_yes = new ActionListener(){
					public void actionPerformed(ActionEvent e){
						GameClient.getInstance().getPacketGenerator().writeTcpMessage("Fr" + m_name.getText());
						GameClient.getInstance().getDisplay().remove(m_confirm);
						m_confirm = null;
					}
				};
				ActionListener m_no = new ActionListener(){
					public void actionPerformed(ActionEvent evt) {
						GameClient.getInstance().getDisplay().remove(m_confirm);
						m_confirm = null;
					}
				};
				m_confirm = new ConfirmationDialog("Are you sure you want to remove " + m_name.getText() + " from your friends?");
				m_confirm.addYesListener(m_yes);
				m_confirm.addNoListener(m_no);
			}
		});
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
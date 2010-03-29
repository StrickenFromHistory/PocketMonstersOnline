package org.pokenet.client.ui.frames;

import java.util.ArrayList;
import java.util.List;

import mdes.slick.sui.Button;
import mdes.slick.sui.Container;
import mdes.slick.sui.Label;
import mdes.slick.sui.event.ActionEvent;
import mdes.slick.sui.event.ActionListener;
import mdes.slick.sui.skin.simple.SimpleArrowButton;

import org.newdawn.slick.Color;
import org.pokenet.client.GameClient;

/**
 * The widget where the chat is actually displayed
 * @author ZombieBear
 *
 */
@SuppressWarnings("deprecation")
class ChatWidget extends Container{
    private int m_scrollIndex = 0; 
	private int m_maxLines;
	private Button m_up, m_down;
	private Color m_foreColor;

	private List<String> m_contents = new ArrayList<String>();
	private List<Label> m_shownChat = new ArrayList<Label>();
	private List<String> m_wrappedText = new ArrayList<String>();

    /**
     * Default Constructor
     */
	public ChatWidget(){
		m_up = new SimpleArrowButton(SimpleArrowButton.FACE_UP);
		m_down = new SimpleArrowButton(SimpleArrowButton.FACE_DOWN);

		m_up.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				scroll(-1);
			}
		});

		m_down.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				scroll(1);
			}
		});
	}

	/**
	 * Adds the contents to be used for display
	 * @param text
	 */
	public void setContents(List<String> text) {
		m_contents = text;
		wrap();
		if (m_wrappedText.size() - m_maxLines >= 0)
			m_scrollIndex = m_wrappedText.size() - m_maxLines - 1;
		else
			m_scrollIndex = -1;
		scroll(1);
	}

	/**
	 * Scrolls after adding a line
	 * @param line
	 */
	public void addLine() {
		wrap();
		if (m_down.isVisible() && m_down.isEnabled())
			scroll(0);
		else {
			if (m_wrappedText.size() - m_maxLines >= 0)
				m_scrollIndex = m_wrappedText.size() - m_maxLines - 1;
			else
				m_scrollIndex = -1;
			
			scroll(1);
		}
	}
	
    /**
     * Handles scrolling and text display
     * @param indexMod
     */
    public void scroll(int indexMod){
    	// Remove previous lines
    	for (Label l : m_shownChat) {
    		try {
    			l.setText("");
    			remove(l);
    			l = null;
    		} catch (Exception e) {}
    	}
    	m_shownChat.clear();

    	// Sets the scrolling index
    	m_scrollIndex += indexMod;

    	//Handles availability of scroll buttons
		if (m_scrollIndex == 0)
			m_up.setEnabled(false);
		else
			m_up.setEnabled(true);

		if (m_scrollIndex + m_maxLines >= m_wrappedText.size())
			m_down.setEnabled(false);
		else
			m_down.setEnabled(true);

		if (m_wrappedText.size() < m_maxLines){
			m_up.setVisible(false);
			m_down.setVisible(false);
		} else {
			m_up.setVisible(true);
			m_down.setVisible(true);
		}

		// Add new lines
		int y = 0;
    	for (int i = 0; i < m_maxLines; i++){
    		m_shownChat.add(new Label());
    		m_shownChat.get(i).setFont(GameClient.getFontSmall());
    		m_shownChat.get(i).setForeground(m_foreColor);
    		m_shownChat.get(i).setLocation(0, y);
    		try {
    			// Make system messages red
    			if (m_wrappedText.get(m_scrollIndex + i).charAt(0) == '*'){
    				m_shownChat.get(i).setForeground(Color.red);
    				m_wrappedText.set(m_scrollIndex + i, m_wrappedText.get(m_scrollIndex + i).substring(1));
    			}
    			// Make announcements yellow
    			if (m_wrappedText.get(m_scrollIndex + i).charAt(0) == '%'){
    				m_shownChat.get(i).setForeground(Color.yellow);
    				m_wrappedText.set(m_scrollIndex + i, m_wrappedText.get(m_scrollIndex + i).substring(1));
    			}
    			// Highlight chat when named. 
    			if (m_wrappedText.get(m_scrollIndex + i).charAt(0) == '!'){
    				m_shownChat.get(i).setForeground(Color.green);
    				m_wrappedText.set(m_scrollIndex + i, m_wrappedText.get(m_scrollIndex + i).substring(1));
    			}
    			m_shownChat.get(i).setText(m_wrappedText.get(m_scrollIndex + i));
    		} catch (Exception e) {} 
    		m_shownChat.get(i).pack();
    		add(m_shownChat.get(i));
    		y += GameClient.getFontSmall().getHeight("X");
    	} 
    }

    /**
     * Sets the foreground color
     * @param c
     */
    public void setForeColor(Color c) {
    	m_foreColor = c;
    	scroll(0);
    }

    /**
     * Lays out the scrolling buttons
     */
	public void layoutScrollButtons(){
		int buttonWidth = 16;

		m_up.setEnabled(false);
		m_up.setSize(buttonWidth, buttonWidth);
		m_up.setLocation(getWidth() - buttonWidth, 0);

		m_down.setSize(buttonWidth, buttonWidth);
		m_down.setLocation(getWidth() - buttonWidth, getHeight() - buttonWidth);

		add(m_up);
		add(m_down);

		if (m_wrappedText.size() < m_maxLines){
			m_up.setVisible(false);
			m_down.setVisible(false);
		} else {
			m_up.setVisible(true);
			m_down.setVisible(true);
		}
	}

	/**
	 * Returns a List<String> with the wrapped text for the chat labels.
	 * @return a List<String> with the wrapped text for the chat labels.
	 */
	public void wrap(){
		m_wrappedText.clear();
		if (m_contents.size() != 0) {
			for (int i = 0; i < m_contents.size(); i++) {
				try{
					if (GameClient.getFontSmall().getWidth(m_contents.get(i)) <= getWidth()){
						m_wrappedText.add(m_contents.get(i));
					} else {
						String loopLine = new String();
						ArrayList<String> loopList = new ArrayList<String>();
						loopLine = m_contents.get(i);
						loopList.add(m_contents.get(i));
						char messageType = '\u0000';
						if (loopLine.charAt(0) == '*')
							messageType = '*';
						else if (loopLine.charAt(0) == '%')
							messageType = '%';
						else if (loopLine.charAt(0) == '!' || (!loopLine.contains(
								'<' + GameClient.getInstance().getOurPlayer()
								.getUsername() + '>') && loopLine
								.contains(GameClient.getInstance().getOurPlayer()
								.getUsername())))
							messageType = '!';
						while (GameClient.getFontSmall().getWidth(loopLine) > getWidth()){
							int linesToDrop = 1;
							while (GameClient.getFontSmall().getWidth(loopList.get(
									loopList.size() - 1)) > getWidth()){
								loopList.add(loopLine.substring(0, loopLine.length() 
										- linesToDrop));
								linesToDrop++;
							}
							if (linesToDrop == 1)
								m_wrappedText.add(loopList.get(loopList.size() - 1));
							else
								m_wrappedText.add(messageType + loopList.get(loopList.size() - 1));
							loopLine = loopLine.substring(loopList.get(
									loopList.size() - 1).length());
							loopList.add(loopLine);
						}
						m_wrappedText.add(messageType + loopLine);
					}
				} catch (IndexOutOfBoundsException e) {}
				catch (Exception e) {e.printStackTrace();}
			}
		}
	}

    @Override
    public void setSize(float width, float height){
		super.setSize(width, height);
		m_maxLines = (int)(getHeight() / GameClient.getFontSmall().getHeight("X"));
		wrap();
		layoutScrollButtons();
		scroll(0);
    }
}
package org.pokenet.client.ui.frames;

import mdes.slick.sui.Frame;
import mdes.slick.sui.TextArea;
import mdes.slick.sui.TextField;
import mdes.slick.sui.event.ActionEvent;
import mdes.slick.sui.event.ActionListener;
import mdes.slick.sui.event.MouseAdapter;
import mdes.slick.sui.event.MouseEvent;

import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.pokenet.client.GameClient;
import org.pokenet.client.network.PacketGenerator;


/**
 * Chat Frame
 * @author ZombieBear
 *
 */
public class ChatDialog extends Frame {
        static final long serialVersionUID = 8126828445828668638L;

        private String packet;
        private TextArea m_chatList;
        private TextField m_chatType;
        private Font m_dpFont;
        public TextField getChatBox() {
                return m_chatType;
        }
        
        /**
         * Default constructor
         * @param packet
         */
        public ChatDialog(String packet) {
                super();

                initGUI();
        }
        
        /**
         * Initializes the user interface
         */
        private void initGUI() {
                this.setMinimumSize(206, 200);
                try {
                        m_dpFont = new AngelCodeFont("/res/fonts/dp-small.fnt", "/res/fonts/dp-small.png");
                        setTitle("Chat");
                        this.setBackground(new Color(0, 0, 0, 85));
                        this.setForeground(new Color(255, 255, 255));
                        {
                                m_chatList = new TextArea();
                                m_chatList.setSize(380, 250);
                                m_chatList.setBackground(new Color(0, 0, 0, 20));
                                m_chatList.setForeground(new Color(255, 255, 255));
                                m_chatList.setBorderRendered(false);
                                m_chatList.setEditable(false);
                                m_chatList.setFont(m_dpFont);
                                getContentPane().add(m_chatList);
                        }
                        {
                                m_chatType = new TextField();
                                m_chatType.setName("chatType");
                                m_chatType.setSize(350, 25);
                                m_chatType.setLocation(50, 250);
                                getContentPane().add(m_chatType);
                                m_chatType.addActionListener(new ActionListener() {
                                        public void actionPerformed(ActionEvent evt) {
                                                chatTypeActionPerformed(evt);
                                        }
                                });
                        }
                        this.getResizer().addMouseListener(new MouseAdapter() {
                public void mouseDragged(MouseEvent event) {
                                        repositionUI();
                                }
                        });
                        setSize(206, 500);
                        repositionUI();
                        m_chatType.grabFocus();
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        /**
         * Sends the packet over to the server
         * @param evt
         */
        private void chatTypeActionPerformed(ActionEvent evt) {
                if (m_chatType.getText() != null && m_chatType.getText().length() != 0) {
                        GameClient.getInstance().getPacketGenerator().write(packet + m_chatType.getText() + "\r");
                }
                m_chatType.setText("");
                m_chatType.grabFocus();
        }

        /**
         * Handles filters for certain characters
         * @param newChat
         */
        public void appendText(String newChat) {
                int endex = newChat.indexOf(">");
                newChat = newChat.substring(0, endex + 1) + 
                        newChat.substring(endex + 1).replace(">", " is greater than ")
                                                                        .replace("<", " is less than ");
                // prevent having an extra blank line at the top
                if (!m_chatList.getText().equals(""))
                        m_chatList.setText(m_chatList.getText() + "\n" + newChat);
                else
                        m_chatList.setText(newChat);
                // scroll down
                m_chatList.setCaretPosition(m_chatList.getText().length());
                checkChatWindow();
        }

        /**
         * Properly locates the frame within the game container 
         */
        private void repositionUI() {
                m_chatList.setWidth((int)getWidth() - 8);
                m_chatType.setLocation(0, (int)getHeight()- 50);
                m_chatType.setSize((int)getWidth(), 25);
                checkChatWindow();
        }
        
        
        /**
         * Not sure what it does? Someone who understands it better doc it!
         */
        private void checkChatWindow() {
                try {
                        if(m_chatList.getLineCount() >= ( (int)getHeight() - 48) / m_dpFont.getLineHeight()) {
                                String [] s = m_chatList.getLinesAsText();
                                m_chatList.setCaretPosition(0);
                                m_chatList.setText("");
                                //this next line causes the window to be unresizable
                                //we need a way to calculate the max possible amount of lines in resizeable window can fit
                                int adj = (int) ((s.length - (getHeight() - getTitleBar().getHeight() - m_chatType.getHeight())/ m_dpFont.getLineHeight()) + 1);
                                for(int i = adj; i < s.length; i++) {
                                        m_chatList.setText(m_chatList.getText() + s[i]);
                                        m_chatList.setCaretPosition(m_chatList.getText().length());
                                }
                        }}
                catch ( Exception e) { 
                        e.printStackTrace();
                }
                
        }
}

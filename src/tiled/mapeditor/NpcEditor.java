package tiled.mapeditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import tiled.core.Map;
import tiled.core.MapChangeListener;
import tiled.core.MapChangedEvent;
import tiled.core.Tile;
import tiled.core.TileSet;
import tiled.io.MapHelper;
import tiled.mapeditor.actions.CloseMapAction;
import tiled.mapeditor.actions.OpenMapAction;
import tiled.mapeditor.selection.SelectionLayer;
import tiled.mapeditor.widget.TMenuItem;
import tiled.util.TiledConfiguration;
import tiled.view.MapView;

/**
 * An npc editor
 * @author shadowkanji
 *
 */
public class NpcEditor implements ActionListener, MouseListener,
			MouseMotionListener, MapChangeListener, ListSelectionListener,
			ChangeListener, ComponentListener {
	
	/* Current release version. */
    public static final String version = "0.7.2";
    private Map currentMap;
    private MapView mapView;
    private Tile currentTile;
    private boolean bMouseIsDown;
    private Point mousePressLocation;
    private SelectionLayer cursorHighlight;
    private int m_selectX = 0;
    private int m_selectY = 0;
    
    /* Gui Stuff */
    private JPanel      mainPanel;
    private JPanel      dataPanel;
    private JPanel		npcPanel;
    private JPanel      statusBar;
    private JMenuBar    menuBar;
    private JScrollPane mapScrollPane;
    private JFrame      appFrame;
    private JFrame		npcFrame;
    private JFrame		warpFrame;
    private static final Preferences prefs = TiledConfiguration.root();
    private JFileChooser fileSelect;
    
    /* Npc Panel */
    private JTextField m_name;
    private JTextField m_direction;
    private JTextField m_sprite;
    private JTextField m_nx;
    private JTextField m_ny;
    private JTextField m_pokemon;
    private JTextField m_party;
    private JTextField m_badge;
    private JTextField m_speech;
    private JTextField m_heals;
    private JTextField m_boxes;
    private JTextField m_shop;
    
    /* Warp Panel */
    private JTextField m_wx;
    private JTextField m_wy;
    private JTextField m_wwx;
    private JTextField m_wwy;
    private JTextField m_wmx;
    private JTextField m_wmy;
    private JTextField m_wb;
    
    /* Data */
    private ArrayList<NpcData> m_npcs;
    private ArrayList<WarpData> m_warps;
    private int m_x;
    private int m_y;
    private int m_currentNpc;
    
    /* Popup Menu */
    private JWindow m_popup;
    
    /**
     * Default constructor
     */
    public NpcEditor() {
    	m_name = new JTextField();
    	m_name.setSize(64, 32);
    	
    	m_direction = new JTextField();
    	m_direction.setSize(64, 32);
    	
    	m_sprite = new JTextField();
    	m_sprite.setSize(64, 32);
    	
    	m_nx = new JTextField();
    	m_nx.setSize(64, 32);
    	
    	m_ny = new JTextField();
    	m_ny.setSize(64, 32);
    	
    	m_pokemon = new JTextField();
    	m_pokemon.setSize(64, 32);
    	
    	m_party = new JTextField();
    	m_party.setSize(64, 32);
    	
    	m_badge = new JTextField();
    	m_badge.setSize(64, 32);
    	
    	m_speech = new JTextField();
    	m_speech.setSize(64, 32);
    	
    	m_heals = new JTextField();
    	m_heals.setSize(64, 32);
    	
    	m_boxes = new JTextField();
    	m_boxes.setSize(64, 32);
    	
    	m_shop = new JTextField();
    	m_shop.setSize(64, 32);
    	
    	m_wx = new JTextField();
    	m_wy = new JTextField();
    	m_wwx = new JTextField();
    	m_wwy = new JTextField();
    	m_wmx = new JTextField();
    	m_wmy = new JTextField();
    	m_wb = new JTextField();
    	
    	cursorHighlight = new SelectionLayer(1, 1);
        cursorHighlight.select(0, 0);
        cursorHighlight.setVisible(true);
    	
    	appFrame = new JFrame("Pokenet Event Editor");
        appFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        appFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                //exitAction.actionPerformed(null);
            }
        });
        appFrame.setContentPane(createContentPane());
        createMenuBar();
        appFrame.setJMenuBar(menuBar);
        appFrame.setSize(640, 480);
        appFrame.setVisible(true);
        
        
    }
    
    /**
     * Creates the main panel
     * @return
     */
    private JPanel createContentPane() {
    	createStatusBar();
    	mapScrollPane = new JScrollPane(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        mapScrollPane.setBorder(null);
        
        npcPanel = new JPanel();
        npcPanel.setLayout(new GridLayout(0, 1));
        
        npcPanel.add(new JLabel("Name:"));
        npcPanel.add(m_name);
        npcPanel.add(new JLabel("Direction:"));
        npcPanel.add(m_direction);
        npcPanel.add(new JLabel("Sprite:"));
        npcPanel.add(m_sprite);
        npcPanel.add(new JLabel("X:"));
        npcPanel.add(m_nx);
        npcPanel.add(new JLabel("Y:"));
        npcPanel.add(m_ny);
        npcPanel.add(new JLabel("Pokemon:"));
        npcPanel.add(m_pokemon);
        npcPanel.add(new JLabel("Min Party Size:"));
        npcPanel.add(m_party);
        npcPanel.add(new JLabel("Badge:"));
        npcPanel.add(m_badge);
        npcPanel.add(new JLabel("Speech:"));
        npcPanel.add(m_speech);
        npcPanel.add(new JLabel("Heals:"));
        npcPanel.add(m_heals);
        npcPanel.add(new JLabel("Box:"));
        npcPanel.add(m_boxes);
        npcPanel.add(new JLabel("Shop:"));
        npcPanel.add(m_shop);
        JButton s = new JButton("Save");
        s.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				m_npcs.get(m_currentNpc).name = m_name.getText();
				m_npcs.get(m_currentNpc).direction = m_direction.getText();
				m_npcs.get(m_currentNpc).sprite = m_sprite.getText();
				m_npcs.get(m_currentNpc).x = m_nx.getText();
				m_npcs.get(m_currentNpc).y = m_ny.getText();
				m_npcs.get(m_currentNpc).pokemon = m_pokemon.getText();
				m_npcs.get(m_currentNpc).party = m_party.getText();
				m_npcs.get(m_currentNpc).badge = m_badge.getText();
				m_npcs.get(m_currentNpc).speech = m_speech.getText();
				m_npcs.get(m_currentNpc).boxes = m_boxes.getText();
				m_npcs.get(m_currentNpc).heals = m_heals.getText();
				m_npcs.get(m_currentNpc).shop = m_shop.getText();
				npcFrame.setVisible(false);
			}
        });
        npcPanel.add(s);
        
        npcFrame = new JFrame("NPC Editor");
        npcFrame.setContentPane(npcPanel);
        npcFrame.setSize(240, 600);
        npcFrame.setLocation(640, 0);
        npcFrame.setVisible(false);
        npcFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        warpFrame = new JFrame("Warp Editor");
        warpFrame.getContentPane().setLayout(new GridLayout(0, 1));
        warpFrame.getContentPane().add(new JLabel("X:"));
        warpFrame.getContentPane().add(m_wx);
        warpFrame.getContentPane().add(new JLabel("Y:"));
        warpFrame.getContentPane().add(m_wy);
        warpFrame.getContentPane().add(new JLabel("Warp X:"));
        warpFrame.getContentPane().add(m_wwx);
        warpFrame.getContentPane().add(new JLabel("Warp Y:"));
        warpFrame.getContentPane().add(m_wwy);
        warpFrame.getContentPane().add(new JLabel("Warp To Map (X):"));
        warpFrame.getContentPane().add(m_wmx);
        warpFrame.getContentPane().add(new JLabel("Warp To Map (Y):"));
        warpFrame.getContentPane().add(m_wmy);
        warpFrame.getContentPane().add(new JLabel("Badge Requirement:"));
        warpFrame.getContentPane().add(m_wb);
        JButton s2 = new JButton("Save");
        s2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				m_warps.get(m_currentNpc).x = m_wx.getText();
				m_warps.get(m_currentNpc).y = m_wy.getText();
				m_warps.get(m_currentNpc).warpX = m_wwx.getText();
				m_warps.get(m_currentNpc).warpY = m_wwy.getText();
				m_warps.get(m_currentNpc).warpMapX = m_wmx.getText();
				m_warps.get(m_currentNpc).warpMapY = m_wmy.getText();
				m_warps.get(m_currentNpc).badges = m_wb.getText();
				warpFrame.setVisible(false);
			}
        });
        warpFrame.add(s2);
        warpFrame.setSize(240, 480);
        warpFrame.setLocation(640, 0);
        warpFrame.setVisible(false);
        warpFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(mapScrollPane, BorderLayout.CENTER);
        mainPanel.add(statusBar, BorderLayout.SOUTH);

        return mainPanel;
    }
    
    /**
     * Creates the status bar
     */
    private void createStatusBar() {
    	statusBar = new JPanel();
        statusBar.setLayout(new BoxLayout(statusBar, BoxLayout.X_AXIS));

        statusBar.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        JPanel largePart = new JPanel();

        statusBar.add(largePart);
        statusBar.add(Box.createRigidArea(new Dimension(20, 0)));
    }
    
    /**
     * Creates the menu bar
     */
    private void createMenuBar() {
    	JMenuItem open = new JMenuItem();
    	open.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fileSelect = new JFileChooser();
				
				int returnVal = fileSelect.showOpenDialog(appFrame);

	            if (returnVal == JFileChooser.APPROVE_OPTION) {
	            	try {
						loadMap(fileSelect.getSelectedFile().getCanonicalPath());
					} catch (IOException e1) {
						e1.printStackTrace();
					}
	            }
			}
    	});
    	open.setText("Open Map");
    	
    	JMenuItem save = new JMenuItem();
    	save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveData();
			}
    	});
    	save.setText("Save Map");
    	
    	JMenuItem close = new JMenuItem();
    	close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
    	});
    	close.setText("Exit");
    	
    	JMenu fileMenu = new JMenu("File");
    	fileMenu.add(open);
    	fileMenu.add(save);
    	fileMenu.add(close);
    	
    	JMenu helpMenu = new JMenu("Help");
    	JMenuItem about = new JMenuItem();
    	about.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "Pokenet Event Editor\nVersion 0.1");
			}
    	});
    	about.setText("About");
    	helpMenu.add(about);
    	
    	menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
    }
    
    /**
     * Saves the map npc data
     */
    public void saveData() {
    	try {
    		File f1 = new File("./server/" + m_x + "." + m_y + ".txt");
    		if(f1.exists())
    			f1.delete();
    		File f2 = new File("./client/" + m_x + "." + m_y + ".txt");
    		if(f2.exists())
    			f2.delete();
        	PrintWriter writer = new PrintWriter(f1);
        	for(int i = 0; i < m_warps.size(); i++) {
        		writer.println("[warp]");
        		writer.println(m_warps.get(i).x);
        		writer.println(m_warps.get(i).y);
        		writer.println(m_warps.get(i).warpX);
        		writer.println(m_warps.get(i).warpY);
        		writer.println(m_warps.get(i).warpMapX);
        		writer.println(m_warps.get(i).warpMapY);
        		writer.println(m_warps.get(i).badges);
        		writer.println("[/warp]");
        	}
        	
        	PrintWriter sw = new PrintWriter(f2);
        	int lineCount = 0;
        	for(int i = 0; i < m_npcs.size(); i++) {
        		writer.println("[npc]");
        		writer.println(m_npcs.get(i).name);
        		writer.println(m_npcs.get(i).direction);
        		writer.println(m_npcs.get(i).sprite);
        		writer.println(m_npcs.get(i).x);
        		writer.println(m_npcs.get(i).y);
        		writer.println(m_npcs.get(i).pokemon);
        		writer.println(m_npcs.get(i).party);
        		writer.println(m_npcs.get(i).badge);
        		char c = 'n';
        		for(int j = 0; j < m_npcs.get(i).speech.length(); j++) {
        			if(m_npcs.get(i).speech.charAt(j) == '\\' && m_npcs.get(i).speech.charAt(j + 1) == 'n') {
        				sw.println();
        				writer.print(lineCount + ",");
        				lineCount++;
        				j = j + 1;
        				continue;
        			}
        			sw.print(m_npcs.get(i).speech.charAt(j));
        		}
        		writer.println();
        		writer.println(m_npcs.get(i).heals);
        		writer.println(m_npcs.get(i).boxes);
        		writer.println(m_npcs.get(i).shop);
        		writer.println("[/npc]");
        	}
        	writer.flush();
        	sw.flush();
        	writer.close();
        	sw.close();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    /**
     * Loads NPC Data for this map if it exists already
     */
    private void loadData(String file) {
    	/*
    	 * Wipe any previous npcs
    	 */
    	m_npcs = new ArrayList<NpcData>();
    	m_warps = new ArrayList<WarpData>();
    	/*
    	 * Get the co-ordinates
    	 */
    	String mapName = file.substring(file.lastIndexOf('/') + 1);
    	m_x = Integer.parseInt(mapName.substring(0, mapName.indexOf('.')));
    	m_y = Integer.parseInt(mapName.substring(mapName.indexOf('.') + 1, mapName.lastIndexOf('.')));
    	
    	/*
    	 * Read all npcs
    	 */
    	try {
        	File f = new File(".");
        	String path = f.getCanonicalPath();
        	f = new File(path + "/server/" + m_x + "." + m_y + ".txt");
        	File f2 = new File(path + "/client/" + m_x + "." + m_y + ".txt");
        	if(f.exists() && f2.exists()) {
        		Scanner s = new Scanner(f);
            	Scanner s2 = new Scanner(f2);
            	
            	ArrayList<String> m_speech = new ArrayList<String>();
            	while(s2.hasNextLine()) {
            		m_speech.add(s2.nextLine());
            	}
            	
            	NpcData n = new NpcData();;
            	WarpData w = new WarpData();
            	while(s.hasNextLine()) {
            		String line = s.nextLine();
            		if(line.equalsIgnoreCase("[npc]")) {
            			n = new NpcData();
            			n.name = s.nextLine();
            			n.direction = s.nextLine();
            			n.sprite = s.nextLine();
            			n.x = s.nextLine();
            			n.y = s.nextLine();
            			n.pokemon = s.nextLine();
            			n.party = s.nextLine();
            			n.badge = s.nextLine();
            			String temp = s.nextLine();
            			String [] sp = temp.split(",");
            			String fspeech = "";
            			for(int i = 0; i < sp.length; i++) {
            				fspeech = fspeech + m_speech.get(Integer.parseInt(sp[i])) + "\n";
            			}
            			n.speech = fspeech;
            			n.heals = s.nextLine();
            			n.boxes = s.nextLine();
            			n.shop = s.nextLine();
            			s.nextLine();
            			m_npcs.add(n);
            		} else if(line.equalsIgnoreCase("[warp]")) {
            			w = new WarpData();
            			w.x = s.nextLine();
            			w.y = s.nextLine();
            			w.warpX = s.nextLine();
            			w.warpY = s.nextLine();
            			w.warpMapX = s.nextLine();
            			w.warpMapY = s.nextLine();
            			w.badges = s.nextLine();
            			s.nextLine();
            			m_warps.add(w);
            		}
            	}
        	}
    	} catch (Exception e) {
    		e.printStackTrace();
    	}

    }
    
    /**
     * Loads map data for a given file
     * @param file
     * @return
     */
    public boolean loadMap(String file) {
    	/*
    	 * If the file doesn't exist, throw an error
    	 */
    	File exist = new File(file);
        if (!exist.exists()) {
            JOptionPane.showMessageDialog(appFrame,
                    Resources.getString("general.file.notexists.message"),
                    Resources.getString("dialog.openmap.error.title"),
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            Map map = MapHelper.loadMap(file);
            loadData(file);
            if (map != null) {
                setCurrentMap(map);
                return true;
            } else {
                JOptionPane.showMessageDialog(appFrame,
                        Resources.getString("general.file.failed"),
                        Resources.getString("dialog.openmap.error.title"),
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(appFrame,
                    "Error while loading " + file + ": " +
                    e.getLocalizedMessage() + (e.getCause() != null ? "\nCause: " +
                        e.getCause().getLocalizedMessage() : ""),
                    Resources.getString("dialog.openmap.error.title"),
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        return false;
    }
    
    private void setCurrentMap(Map newMap) {
    	currentMap = newMap;
    	mapView = MapView.createViewforMap(currentMap);
    	mapView.addMouseListener(this);
        mapView.addMouseMotionListener(this);
        mapView.addComponentListener(this);
        final Preferences display = prefs.node("display");
        mapView.setGridOpacity(display.getInt("gridOpacity", 255));
        mapView.setAntialiasGrid(display.getBoolean("gridAntialias", true));
        mapView.setGridColor(new Color(display.getInt("gridColor",
                MapView.DEFAULT_GRID_COLOR.getRGB())));
        mapView.setShowGrid(display.getBoolean("showGrid", false));
        JViewport mapViewport = new JViewport();
        mapViewport.setView(mapView);
        mapViewport.addChangeListener(this);
        mapScrollPane.setViewport(mapViewport);
    }

	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mousePressed(MouseEvent e) {
		m_selectX = e.getX();
		m_selectY = e.getY();
		
		m_popup = new JWindow();
		m_popup.setLocation(e.getX(), e.getY());
		m_popup.setSize(128, 64);
		m_popup.setLayout(new GridLayout(0, 1));
		JButton b1 = new JButton("NPC");
		b1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Point tile = mapView.screenToTileCoords(m_selectX, m_selectY);
				displayNpcData(tile.x, tile.y);
				m_popup.setVisible(false);
			}
		});
		m_popup.add(b1);
		JButton b2 = new JButton("WarpTile");
		b2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Point tile = mapView.screenToTileCoords(m_selectX, m_selectY);
				displayWarpData(tile.x, tile.y);
				m_popup.setVisible(false);
			}
		});
		m_popup.add(b2);
		m_popup.setVisible(true);
		
	}
	
	/**
	 * Displays NPC data in the npc editor
	 * @param x
	 * @param y
	 */
	public void displayNpcData(int x, int y) {
		/*
		 * If there is an npc at this tile, show the data
		 */
		for(int i = 0; i < m_npcs.size(); i++) {
			if(m_npcs.get(i).x.equalsIgnoreCase(String.valueOf(x)) && 
					m_npcs.get(i).y.equalsIgnoreCase(String.valueOf(y))) {
				NpcData n = m_npcs.get(i);
				m_currentNpc = i;
				warpFrame.setVisible(false);
				m_name.setText(n.name);
				m_direction.setText(n.direction);
				m_sprite.setText(n.sprite);
				m_nx.setText(n.x);
				m_ny.setText(n.y);
				m_pokemon.setText(n.pokemon);
				m_party.setText(n.party);
				m_badge.setText(n.badge);
				m_speech.setText(n.speech);
				m_shop.setText(n.shop);
				m_heals.setText(n.heals);
				m_boxes.setText(n.boxes);
				npcFrame.setVisible(true);
				return;
			}
		}
		/*
		 * Else, generate a new npc
		 */
		m_npcs.add(new NpcData());
		m_currentNpc = m_npcs.size() - 1;
		warpFrame.setVisible(false);
		m_name.setText("NULL");
		m_direction.setText("Down");
		m_sprite.setText("15");
		m_nx.setText(String.valueOf(x));
		m_ny.setText(String.valueOf(y));
		m_pokemon.setText("NULL");
		m_party.setText("1");
		m_badge.setText("-1");
		m_speech.setText("Do u liek Mudkipz?");
		m_shop.setText("false");
		m_heals.setText("false");
		m_boxes.setText("false");
		npcFrame.setVisible(true);
	}
	
	/**
	 * Display warptile data in the warp editor
	 * @param x
	 * @param y
	 */
	public void displayWarpData(int x, int y) {
		/*
		 * If there is an npc at this tile, show the data
		 */
		for(int i = 0; i < m_warps.size(); i++) {
			if(m_warps.get(i).x.equalsIgnoreCase(String.valueOf(x)) && 
					m_warps.get(i).y.equalsIgnoreCase(String.valueOf(y))) {
				WarpData w = m_warps.get(i);
				npcFrame.setVisible(false);
				m_currentNpc = i;
				m_wx.setText(w.x);
				m_wy.setText(w.y);
				m_wwx.setText(w.warpX);
				m_wwy.setText(w.warpY);
				m_wmx.setText(w.warpMapX);
				m_wmy.setText(w.warpMapY);
				m_wb.setText(w.badges);
				warpFrame.setVisible(true);
				return;
			}
		}
		/*
		 * Else, generate a new npc
		 */
		m_warps.add(new WarpData());
		npcFrame.setVisible(false);
		m_currentNpc = m_warps.size() - 1;
		m_wx.setText(String.valueOf(x));
		m_wy.setText(String.valueOf(y));
		m_wwx.setText("0");
		m_wwy.setText("0");
		m_wmx.setText("0");
		m_wmy.setText("0");
		m_wb.setText("0");
		warpFrame.setVisible(true);
	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseMoved(MouseEvent e) {
		Point tile = mapView.screenToTileCoords(e.getX(), e.getY());
		updateCursorHighlight(tile);
	}

	private void updateCursorHighlight(Point tile) {
		Rectangle redraw = new Rectangle(tile.x, tile.y, 16, 16);
        mapView.repaintRegion(redraw);
        mapView.repaintRegion(redraw);
	}

	public void mapChanged(MapChangedEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void tilesetAdded(MapChangedEvent e, TileSet tileset) {
		// TODO Auto-generated method stub
		
	}

	public void tilesetRemoved(MapChangedEvent e, int index) {
		// TODO Auto-generated method stub
		
	}

	public void tilesetsSwapped(MapChangedEvent e, int index0, int index1) {
		// TODO Auto-generated method stub
		
	}

	public void valueChanged(ListSelectionEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void componentResized(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	public static void main(String [] args) {
		NpcEditor n = new NpcEditor();
	}
}

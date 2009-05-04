/*
 *  Tiled Map Editor, (c) 2004-2008
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  Adam Turk <aturk@biggeruniverse.com>
 *  Bjorn Lindeijer <bjorn@lindeijer.nl>
 */

package tiled.mapeditor;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Area;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Stack;
import java.util.Vector;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.undo.UndoableEditSupport;

import tiled.core.*;
import tiled.io.MapHelper;
import tiled.io.MapReader;
import tiled.mapeditor.actions.*;
import tiled.mapeditor.brush.AbstractBrush;
import tiled.mapeditor.brush.Brush;
import tiled.mapeditor.brush.CustomBrush;
import tiled.mapeditor.brush.ShapeBrush;
import tiled.mapeditor.dialogs.*;
import tiled.mapeditor.plugin.PluginClassLoader;
import tiled.mapeditor.selection.SelectionLayer;
import tiled.mapeditor.undo.*;
import tiled.mapeditor.util.*;
import tiled.mapeditor.widget.*;
import tiled.util.TiledConfiguration;
import tiled.view.MapView;

/**
 * The main class for the Tiled Map Editor.
 */
public class MapEditor implements ActionListener, MouseListener,
        MouseMotionListener, MapChangeListener, ListSelectionListener,
        ChangeListener, ComponentListener
{
    // Constants and the like
    private static final int PS_POINT     = 0;
    private static final int PS_PAINT     = 1;
    private static final int PS_ERASE     = 2;
    private static final int PS_POUR      = 3;
    private static final int PS_EYED      = 4;
    private static final int PS_MARQUEE   = 5;
    private static final int PS_MOVE      = 6;
    private static final int PS_ADDOBJ    = 7;
    private static final int PS_REMOVEOBJ = 8;
    private static final int PS_MOVEOBJ   = 9;

    private static final int APP_WIDTH = 800;
    private static final int APP_HEIGHT = 600;

    private Cursor curDefault;
    private Cursor curEyed;

    /** Current release version. */
    public static final String version = "0.7.2";

    private Map currentMap;
    private MapView mapView;
    private final UndoHandler undoHandler;
    private final UndoableEditSupport undoSupport;
    private final MapEventAdapter mapEventAdapter;
    private final PluginClassLoader pluginLoader;
    private static final Preferences prefs = TiledConfiguration.root();

    private int currentPointerState;
    private Tile currentTile;
    private MapObject currentObject = null;
    private int currentLayer = -1;
    private boolean bMouseIsDown, bMouseIsDragging;
    private SelectionLayer cursorHighlight;
    private Point mousePressLocation, mouseInitialPressLocation;
    private Point mouseLastPixelLocation;
    private Point mouseInitialScreenLocation;
    private Point moveDist;
    private int mouseButton;
    private AbstractBrush currentBrush;
    private SelectionLayer marqueeSelection;
    private MapLayer clipboardLayer;
    private float relativeMidX, relativeMidY;

    // GUI components
    private JPanel      mainPanel;
    private JPanel      dataPanel;
    private JPanel      statusBar;
    private JMenuBar    menuBar;
    private JCheckBoxMenuItem gridMenuItem, boundaryMenuItem, cursorMenuItem;
    private JCheckBoxMenuItem coordinatesMenuItem;
    private JMenu       recentMenu;
    private JScrollPane mapScrollPane;
    private JTable      layerTable;
    //private JList       editHistoryList;
    private MiniMapViewer miniMap;

    private BrushPreview brushPreview;
    private JFrame      appFrame;
    private JSlider     opacitySlider;
    private JLabel      zoomLabel, tileCoordsLabel;

    private AbstractButton paintButton, eraseButton, pourButton;
    private AbstractButton eyedButton, marqueeButton, moveButton;
    private AbstractButton objectMoveButton, objectAddButton;
    private AbstractButton objectRemoveButton;

    private TabbedTilesetsPane tabbedTilesetsPane;
    private AboutDialog aboutDialog;
    private MapLayerEdit paintEdit;

    private FloatablePanel layersPanel;
    private FloatablePanel tilesetsPanel;

    //private TileInstancePropertiesDialog tileInstancePropertiesDialog;
    //private JButton tileInstancePropertiesButton;

    /** Available brushes */
    private Vector brushes = new Vector();
    private Brush eraserBrush;

    // Actions
    private final SaveAction saveAction;
    private final SaveAsAction saveAsAction;
    private final SaveAsImageAction saveAsImageAction;
    private final Action exitAction;
    private final Action zoomInAction, zoomOutAction, zoomNormalAction;
    private final Action rot90Action, rot180Action, rot270Action;
    private final Action flipHorAction, flipVerAction;
    private final Action selectAllAction, inverseAction, cancelSelectionAction;
    private final Action addLayerAction, cloneLayerAction, deleteLayerAction;
    private final Action moveLayerDownAction, moveLayerUpAction;
    private final Action mergeLayerDownAction, mergeAllLayersAction;
    private final Action addObjectGroupAction;

    private static final String IMPORT_ERROR_MSG = Resources.getString("dialog.newtileset.import.error.message");

    private static final String PANEL_TILE_PALETTE = Resources.getString("panel.tilepalette.title");
    private static final String PANEL_LAYERS = Resources.getString("panel.layers.title");

    private static final String TOOL_PAINT = Resources.getString("tool.paint.name");
    private static final String TOOL_ERASE = Resources.getString("tool.erase.name");
    private static final String TOOL_FILL = Resources.getString("tool.fill.name");
    private static final String TOOL_EYE_DROPPER = Resources.getString("tool.eyedropper.name");
    private static final String TOOL_SELECT = Resources.getString("tool.select.name");
    private static final String TOOL_MOVE_LAYER = Resources.getString("tool.movelayer.name");
    private static final String TOOL_ADD_OBJECT = Resources.getString("tool.addobject.name");
    private static final String TOOL_REMOVE_OBJECT = Resources.getString("tool.removeobject.name");
    private static final String TOOL_MOVE_OBJECT = Resources.getString("tool.moveobject.name");

    public MapEditor() {
        /*
        eraserBrush = new Eraser();
        brushes.add(eraserBrush());
        setBrush(eraserBrush);
        */

        /*
        try {
            Image imgPaintCursor = Resources.getImage("cursor-pencil.png");

            curPaint = Toolkit.getDefaultToolkit().createCustomCursor(
                    imgPaintCursor, new Point(0,0), "paint");
        } catch (Exception e) {
            System.out.println("Error while loading custom cursors!");
            e.printStackTrace();
        }
        */

        curEyed = new Cursor(Cursor.CROSSHAIR_CURSOR);
        curDefault = new Cursor(Cursor.DEFAULT_CURSOR);

        undoHandler = new UndoHandler(this);
        undoSupport = new UndoableEditSupport();
        undoSupport.addUndoableEditListener(undoHandler);

        cursorHighlight = new SelectionLayer(1, 1);
        cursorHighlight.select(0, 0);
        cursorHighlight.setVisible(prefs.getBoolean("cursorhighlight", true));

        mapEventAdapter = new MapEventAdapter();

        // Create the actions
        saveAction = new SaveAction(this);
        saveAsAction = new SaveAsAction(this);
        saveAsImageAction = new SaveAsImageAction(this);
        exitAction = new ExitAction(this, saveAction);
        zoomInAction = new ZoomInAction();
        zoomOutAction = new ZoomOutAction();
        zoomNormalAction = new ZoomNormalAction();
        rot90Action = new LayerTransformAction(MapLayer.ROTATE_90);
        rot180Action = new LayerTransformAction(MapLayer.ROTATE_180);
        rot270Action = new LayerTransformAction(MapLayer.ROTATE_270);
        flipHorAction = new LayerTransformAction(MapLayer.MIRROR_HORIZONTAL);
        flipVerAction = new LayerTransformAction(MapLayer.MIRROR_VERTICAL);
        selectAllAction = new SelectAllAction();
        cancelSelectionAction = new CancelSelectionAction();
        inverseAction = new InverseSelectionAction();
        addLayerAction = new AddLayerAction(this);
        cloneLayerAction = new CloneLayerAction(this);
        deleteLayerAction = new DeleteLayerAction(this);
        moveLayerUpAction = new MoveLayerUpAction(this);
        moveLayerDownAction = new MoveLayerDownAction(this);
        mergeLayerDownAction = new MergeLayerDownAction(this);
        mergeAllLayersAction = new MergeAllLayersAction(this);
        addObjectGroupAction = new AddObjectGroupAction(this);

        // Create our frame
        appFrame = new JFrame(Resources.getString("dialog.main.title"));
        appFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        appFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                exitAction.actionPerformed(null);
            }
        });
        appFrame.setContentPane(createContentPane());
        createMenuBar();
        appFrame.setJMenuBar(menuBar);

        final Preferences mainDialogPrefs = prefs.node("dialog/main");
        final int width = mainDialogPrefs.getInt("width", APP_WIDTH);
        final int height = mainDialogPrefs.getInt("width", APP_HEIGHT);
        appFrame.setSize(width, height);

        setCurrentMap(null);
        updateRecent(null);

        appFrame.setVisible(true);

        //tileInstancePropertiesDialog = new TileInstancePropertiesDialog(this);

        // Restore the state of the main frame. This needs to happen after
        // making the frame visible, otherwise it has no effect (in Linux).
        final int state = mainDialogPrefs.getInt("state", Frame.NORMAL);
        if (state != Frame.ICONIFIED) {
            appFrame.setExtendedState(state);
        }

        // Restore the size and position of the layers and tileset panels.
        layersPanel.restore();
        tilesetsPanel.restore();

        // Load plugins
        pluginLoader  = PluginClassLoader.getInstance();
        try {
            pluginLoader.readPlugins(null, appFrame);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(appFrame,
                    e.toString(), "Plugin loader",
                    JOptionPane.WARNING_MESSAGE);
        }
        MapHelper.init(pluginLoader);


        // Make sure the map view is redrawn when grid preferences change.
        // todo: move this functionality out of here somehow, but not back into
        // MapView
        final Preferences display = prefs.node("display");
        display.addPreferenceChangeListener(new PreferenceChangeListener() {
            public void preferenceChange(PreferenceChangeEvent event) {
                if (mapView == null) return;

                String key = event.getKey();
                if ("gridOpacity".equals(key)) {
                    mapView.setGridOpacity(display.getInt("gridOpacity", 255));
                }
                else if ("gridAntialias".equals(key)) {
                    mapView.setAntialiasGrid(display.getBoolean("gridAntialias", true));
                }
                else if ("gridColor".equals(key)) {
                    mapView.setGridColor(new Color(display.getInt("gridColor",
                            MapView.DEFAULT_GRID_COLOR.getRGB())));
                }
                else if ("showGrid".equals(key)) {
                    mapView.setShowGrid(display.getBoolean("showGrid", false));
                }
            }
        });
    }

    private JPanel createContentPane() {
        mapScrollPane = new JScrollPane(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        mapScrollPane.setBorder(null);

        createData();
        createStatusBar();

        // todo: Make continuouslayout an option. Because it can be slow, some
        // todo: people may prefer not to have that.
        layersPanel = new FloatablePanel(
                getAppFrame(), dataPanel, PANEL_LAYERS, "layers");
        JSplitPane mainSplit = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT, true, mapScrollPane,
                layersPanel);
        mainSplit.setOneTouchExpandable(true);
        mainSplit.setResizeWeight(1.0);
        mainSplit.setBorder(null);

        tabbedTilesetsPane = new TabbedTilesetsPane(this);
        tilesetsPanel = new FloatablePanel(
                getAppFrame(), tabbedTilesetsPane, PANEL_TILE_PALETTE,
                "tilesets");
        JSplitPane paletteSplit = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT, true, mainSplit, tilesetsPanel);
        paletteSplit.setOneTouchExpandable(true);
        paletteSplit.setResizeWeight(1.0);

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(createToolBar(), BorderLayout.WEST);
        mainPanel.add(paletteSplit, BorderLayout.CENTER);
        mainPanel.add(statusBar, BorderLayout.SOUTH);

        return mainPanel;
    }

    /**
     * Creates all the menus and submenus of the top menu bar. Handles
     * assigning listeners and tooltips as well.
     */
    private void createMenuBar() {
        JMenuItem save = new TMenuItem(saveAction);
        JMenuItem saveAs = new TMenuItem(saveAsAction);
        JMenuItem saveAsImage = new TMenuItem(saveAsImageAction);
        JMenuItem close = new TMenuItem(new CloseMapAction(this, saveAction));

        recentMenu = new JMenu(Resources.getString("menu.file.recent"));

        mapEventAdapter.addListener(save);
        mapEventAdapter.addListener(saveAs);
        mapEventAdapter.addListener(saveAsImage);
        mapEventAdapter.addListener(close);

        JMenu fileMenu = new JMenu(Resources.getString("menu.file"));
        fileMenu.add(new TMenuItem(new NewMapAction(this, saveAction)));
        fileMenu.add(new TMenuItem(new OpenMapAction(this, saveAction)));
        fileMenu.add(recentMenu);
        fileMenu.add(save);
        fileMenu.add(saveAs);
        fileMenu.add(saveAsImage);
        fileMenu.addSeparator();
        fileMenu.add(close);
        fileMenu.add(new TMenuItem(exitAction));

        JMenuItem copyMenuItem = new TMenuItem(new CopyAction());
        JMenuItem copyAllMenuItem = new TMenuItem(new CopyAllAction());
        JMenuItem cutMenuItem = new TMenuItem(new CutAction());
        JMenuItem pasteMenuItem = new TMenuItem(new PasteAction());
        copyMenuItem.setEnabled(false);
        copyAllMenuItem.setEnabled(false);
        cutMenuItem.setEnabled(false);
        pasteMenuItem.setEnabled(false);

        JMenu transformSub = new JMenu(Resources.getString("menu.edit.transform"));
        transformSub.add(new TMenuItem(rot90Action, true));
        transformSub.add(new TMenuItem(rot180Action, true));
        transformSub.add(new TMenuItem(rot270Action, true));
        transformSub.addSeparator();
        transformSub.add(new TMenuItem(flipHorAction, true));
        transformSub.add(new TMenuItem(flipVerAction, true));
        mapEventAdapter.addListener(transformSub);

        JMenu editMenu = new JMenu(Resources.getString("menu.edit"));
        editMenu.add(new TMenuItem(undoHandler.getUndoAction()));
        editMenu.add(new TMenuItem(undoHandler.getRedoAction()));
        editMenu.addSeparator();
        editMenu.add(copyMenuItem);
        editMenu.add(copyAllMenuItem);
        editMenu.add(cutMenuItem);
        editMenu.add(pasteMenuItem);
        editMenu.addSeparator();
        editMenu.add(transformSub);
        editMenu.addSeparator();
        editMenu.add(createMenuItem(Resources.getString("menu.edit.preferences"),
                null, Resources.getString("menu.edit.preferences.tooltip"), null));
        editMenu.add(createMenuItem(Resources.getString("menu.edit.brush"), null,
                Resources.getString("menu.edit.brush.tooltip"),
                "control B"));

        mapEventAdapter.addListener(copyMenuItem);
        mapEventAdapter.addListener(copyAllMenuItem);
        mapEventAdapter.addListener(cutMenuItem);
        mapEventAdapter.addListener(pasteMenuItem);


        JMenu mapMenu = new JMenu(Resources.getString("menu.map"));
        mapMenu.add(createMenuItem(Resources.getString("menu.map.resize"), null,
                Resources.getString("menu.map.resize.tooltip")));
        mapMenu.add(createMenuItem(Resources.getString("menu.map.search"), null,
                Resources.getString("menu.map.search.tooltip")));
        mapMenu.addSeparator();
        mapMenu.add(createMenuItem(Resources.getString("menu.map.properties"), null,
                Resources.getString("menu.map.properties.tooltip")));
        mapEventAdapter.addListener(mapMenu);


        JMenu layerMenu = new JMenu(Resources.getString("menu.layer"));
        JMenuItem layerAdd = new TMenuItem(addLayerAction);
        mapEventAdapter.addListener(layerAdd);
        layerMenu.add(layerAdd);
        layerMenu.add(new TMenuItem(cloneLayerAction));
        layerMenu.add(new TMenuItem(deleteLayerAction));
        layerMenu.addSeparator();
        layerMenu.add(new TMenuItem(addObjectGroupAction));
        layerMenu.addSeparator();
        layerMenu.add(new TMenuItem(moveLayerUpAction));
        layerMenu.add(new TMenuItem(moveLayerDownAction));
        layerMenu.addSeparator();
        layerMenu.add(new TMenuItem(mergeLayerDownAction));
        layerMenu.add(new TMenuItem(mergeAllLayersAction));
        layerMenu.addSeparator();
        layerMenu.add(createMenuItem(
                Resources.getString("menu.layer.properties"), null,
                Resources.getString("menu.layer.properties.tooltip")));

        JMenu tilesetMenu = new JMenu(Resources.getString("menu.tilesets"));
        tilesetMenu.add(createMenuItem(
                Resources.getString("menu.tilesets.new"), null,
                Resources.getString("menu.tilesets.new.tooltip")));
        tilesetMenu.add(createMenuItem(
                Resources.getString("menu.tilesets.import"), null,
                Resources.getString("menu.tilesets.import.tooltip")));
        tilesetMenu.addSeparator();
        tilesetMenu.add(createMenuItem(
                Resources.getString("menu.tilesets.refresh"), null,
                Resources.getString("menu.tilesets.refresh.tooltip"), "F5"));
        tilesetMenu.addSeparator();
        tilesetMenu.add(createMenuItem(
                Resources.getString("menu.tilesets.manager"), null,
                Resources.getString("menu.tilesets.manager.tooltip")));


        /*
        objectMenu = new JMenu("Objects");
        objectMenu.add(createMenuItem("Add Object", null, "Add an object"));
        mapEventAdapter.addListener(objectMenu);

        JMenu modifySub = new JMenu("Modify");
        modifySub.add(createMenuItem("Expand Selection", null, ""));
        modifySub.add(createMenuItem("Contract Selection", null, ""));
        */

        JMenu selectMenu = new JMenu(Resources.getString("menu.select"));
        selectMenu.add(new TMenuItem(selectAllAction, true));
        selectMenu.add(new TMenuItem(cancelSelectionAction, true));
        selectMenu.add(new TMenuItem(inverseAction, true));
        //selectMenu.addSeparator();
        //selectMenu.add(modifySub);


        gridMenuItem = new JCheckBoxMenuItem(Resources.getString("menu.view.grid"));
        gridMenuItem.addActionListener(this);
        gridMenuItem.setToolTipText(Resources.getString("menu.view.grid.tooltip"));
        gridMenuItem.setAccelerator(KeyStroke.getKeyStroke("control G"));

        cursorMenuItem = new JCheckBoxMenuItem(Resources.getString("menu.view.cursor"));
        cursorMenuItem.setSelected(prefs.getBoolean("cursorhighlight", true));
        cursorMenuItem.addActionListener(this);
        cursorMenuItem.setToolTipText(
                Resources.getString("menu.view.cursor.tooltip"));

        boundaryMenuItem = new JCheckBoxMenuItem(Resources.getString("menu.view.boundaries"));
        boundaryMenuItem.addActionListener(this);
        boundaryMenuItem.setToolTipText(Resources.getString("menu.view.boundaries.tooltip"));
        boundaryMenuItem.setAccelerator(KeyStroke.getKeyStroke("control E"));

        coordinatesMenuItem = new JCheckBoxMenuItem(Resources.getString("menu.view.coordinates"));
        coordinatesMenuItem.addActionListener(this);
        coordinatesMenuItem.setToolTipText(Resources.getString("menu.view.coordinates.tooltip"));

        JMenu viewMenu = new JMenu(Resources.getString("menu.view"));
        viewMenu.add(new TMenuItem(zoomInAction));
        viewMenu.add(new TMenuItem(zoomOutAction));
        viewMenu.add(new TMenuItem(zoomNormalAction));
        viewMenu.addSeparator();
        viewMenu.add(gridMenuItem);
        viewMenu.add(cursorMenuItem);
        //TODO: Enable when boudary drawing code finished.
        //viewMenu.add(boundaryMenuItem);
        viewMenu.add(coordinatesMenuItem);

        mapEventAdapter.addListener(layerMenu);
        mapEventAdapter.addListener(tilesetMenu);
        mapEventAdapter.addListener(selectMenu);
        mapEventAdapter.addListener(viewMenu);

        JMenu helpMenu = new JMenu(Resources.getString("menu.help"));
        helpMenu.add(createMenuItem(Resources.getString("menu.help.plugins"), null,
                Resources.getString("menu.help.plugins.tooltip")));
        helpMenu.add(createMenuItem(Resources.getString("menu.help.about"), null, Resources.getString("menu.help.about.tooltip")));

        menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(selectMenu);
        menuBar.add(viewMenu);
        menuBar.add(mapMenu);
        menuBar.add(layerMenu);
        menuBar.add(tilesetMenu);
        //menuBar.add(objectMenu);
        menuBar.add(helpMenu);
    }

    /**
     * Creates the tool bar.
     *
     * @return the created tool bar
     */
    private JToolBar createToolBar() {
        Icon iconMove = Resources.getIcon("gimp-tool-move-22.png");
        Icon iconPaint = Resources.getIcon("gimp-tool-pencil-22.png");
        Icon iconErase = Resources.getIcon("gimp-tool-eraser-22.png");
        Icon iconPour = Resources.getIcon("gimp-tool-bucket-fill-22.png");
        Icon iconEyed = Resources.getIcon("gimp-tool-color-picker-22.png");
        Icon iconMarquee = Resources.getIcon("gimp-tool-rect-select-22.png");
        Icon iconAddObject = Resources.getIcon("gnome-list-add-22.png");
        Icon iconRemoveObject = Resources.getIcon("gnome-list-remove-22.png");
        Icon iconMoveObject = Resources.getIcon("gimp-tool-object-move-22.png");

        paintButton = createToggleButton(iconPaint, "paint", TOOL_PAINT);
        eraseButton = createToggleButton(iconErase, "erase", TOOL_ERASE);
        pourButton = createToggleButton(iconPour, "pour", TOOL_FILL);
        eyedButton = createToggleButton(iconEyed, "eyed", TOOL_EYE_DROPPER);
        marqueeButton = createToggleButton(iconMarquee, "marquee", TOOL_SELECT);
        moveButton = createToggleButton(iconMove, "move", TOOL_MOVE_LAYER);
        objectAddButton = createToggleButton(iconAddObject, "addobject", TOOL_ADD_OBJECT);
        objectRemoveButton = createToggleButton(iconRemoveObject, "removeobject", TOOL_REMOVE_OBJECT);
        objectMoveButton = createToggleButton(iconMoveObject, "moveobject", TOOL_MOVE_OBJECT);

        mapEventAdapter.addListener(moveButton);
        mapEventAdapter.addListener(paintButton);
        mapEventAdapter.addListener(eraseButton);
        mapEventAdapter.addListener(pourButton);
        mapEventAdapter.addListener(eyedButton);
        mapEventAdapter.addListener(marqueeButton);
        mapEventAdapter.addListener(objectMoveButton);

        JToolBar toolBar = new JToolBar(JToolBar.VERTICAL);
        toolBar.setFloatable(true);
        toolBar.add(moveButton);
        toolBar.add(paintButton);
        toolBar.add(eraseButton);
        toolBar.add(pourButton);
        toolBar.add(eyedButton);
        toolBar.add(marqueeButton);
        toolBar.add(Box.createRigidArea(new Dimension(5, 5)));
        toolBar.add(objectAddButton);
        toolBar.add(objectRemoveButton);
        toolBar.add(objectMoveButton);
        toolBar.add(Box.createRigidArea(new Dimension(0, 5)));
        toolBar.add(new TButton(zoomInAction));
        toolBar.add(new TButton(zoomOutAction));
        toolBar.add(Box.createRigidArea(new Dimension(5, 5)));
        toolBar.add(Box.createGlue());

        brushPreview = new BrushPreview();
        mapEventAdapter.addListener(brushPreview);
        toolBar.add(brushPreview);

        return toolBar;
    }

    private void createData() {
        dataPanel = new JPanel(new BorderLayout());

        //navigation and tool options
        // TODO: the minimap is prohibitively slow, need to speed this up
        // before it can be used
        miniMap = new MiniMapViewer();
        //miniMap.setMainPanel(mapScrollPane);
        JScrollPane miniMapSp = new JScrollPane();
        miniMapSp.getViewport().setView(miniMap);
        miniMapSp.setMinimumSize(new Dimension(0, 120));

        // Layer table
        layerTable = new JTable(new LayerTableModel());
        layerTable.getColumnModel().getColumn(0).setPreferredWidth(32);
        layerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        layerTable.getSelectionModel().addListSelectionListener(this);

        // Opacity slider
        opacitySlider = new JSlider(0, 100, 100);
        opacitySlider.addChangeListener(this);
        JLabel opacityLabel = new JLabel(
                Resources.getString("dialog.main.opacity.label"));
        opacityLabel.setLabelFor(opacitySlider);

        JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.X_AXIS));
        sliderPanel.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 3));
        sliderPanel.add(opacityLabel);
        sliderPanel.add(opacitySlider);
        sliderPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE,
                    sliderPanel.getPreferredSize().height));

        // Layer buttons
        AbstractButton layerAddButton = new TButton(addLayerAction);
        mapEventAdapter.addListener(layerAddButton);

        JPanel layerButtons = new JPanel();
        layerButtons.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        layerButtons.add(layerAddButton, c);
        layerButtons.add(new TButton(moveLayerUpAction), c);
        layerButtons.add(new TButton(moveLayerDownAction), c);
        layerButtons.add(new TButton(cloneLayerAction), c);
        layerButtons.add(new TButton(deleteLayerAction), c);
        layerButtons.setMaximumSize(new Dimension(Integer.MAX_VALUE,
                    layerButtons.getPreferredSize().height));

        //tileInstancePropertiesButton = new JButton("Properties");
        //tileInstancePropertiesButton.setActionCommand("tileInstanceProperties");
        //mapEventAdapter.addListener(tileInstancePropertiesButton);
        //tileInstancePropertiesButton.addActionListener(this);

        // Edit history
        /*
        JScrollPane editSp = new JScrollPane();
        editHistoryList = new JList();
        editSp.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        editSp.getViewport().setView(editHistoryList);
        */

        JPanel layerPanel = new JPanel();
        layerPanel.setLayout(new GridBagLayout());
        layerPanel.setPreferredSize(new Dimension(120, 120));
        c = new GridBagConstraints();
        c.insets = new Insets(3, 0, 0, 0); c.weightx = 1; c.weighty = 0;
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0; c.gridy = 0;
        //layerPanel.add(miniMapSp, c);
        c.weighty = 0; c.gridy += 1;
        layerPanel.add(sliderPanel, c);
        c.weighty = 1; c.gridy += 1;
        layerPanel.add(new JScrollPane(layerTable), c);
        c.weighty = 0; c.insets = new Insets(0, 0, 0, 0); c.gridy += 1;
        layerPanel.add(layerButtons, c);
        //c.gridy += 1;
        //layerPanel.add(tileInstancePropertiesButton, c);
        /*
        c.weighty = 0.25; c.insets = new Insets(3, 0, 0, 0); c.gridy += 1;
        layerPanel.add(editSp, c);
        */

        // Create paint panel
        TilePalettePanel tilePalettePanel = new TilePalettePanel();

        JPanel brushesPanel = new JPanel();

        JTabbedPane paintPanel = new JTabbedPane();
        paintPanel.add("Palette", tilePalettePanel);
        paintPanel.add("Brushes", brushesPanel);
        paintPanel.setSelectedIndex(1);

        JToolBar tabsPanel = new JToolBar();
        tabsPanel.add(paintPanel);

        dataPanel.add(layerPanel);
    }

    private void createStatusBar() {
        statusBar = new JPanel();
        statusBar.setLayout(new BoxLayout(statusBar, BoxLayout.X_AXIS));

        zoomLabel = new JLabel("100%");
        zoomLabel.setPreferredSize(zoomLabel.getPreferredSize());
        tileCoordsLabel = new JLabel(" ", SwingConstants.CENTER);

        statusBar.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        JPanel largePart = new JPanel();

        statusBar.add(largePart);
        statusBar.add(tileCoordsLabel);
        statusBar.add(Box.createRigidArea(new Dimension(20, 0)));
        statusBar.add(zoomLabel);
    }

    private void updateLayerTable() {
        int cl = currentLayer;
        if (layerTable.isEditing()) {
            layerTable.getCellEditor(layerTable.getEditingRow(),
                    layerTable.getEditingColumn()).cancelCellEditing();
        }
        ((LayerTableModel)layerTable.getModel()).setMap(currentMap);

        if (currentMap != null) {
            if (currentMap.getTotalLayers() > 0 && cl == -1) {
                cl = 0;
            }

            setCurrentLayer(cl);
        }

        updateLayerOperations();
    }

    private void updateLayerOperations() {
        int nrLayers = 0;

        if (currentMap != null) {
            nrLayers = currentMap.getTotalLayers();
        }

        final boolean validSelection = currentLayer >= 0;
        final boolean notBottom = currentLayer > 0;
        final boolean notTop = currentLayer < nrLayers - 1 && validSelection;
        final boolean tileLayer =
                validSelection && getCurrentLayer() instanceof TileLayer;
        final boolean objectGroup =
                validSelection && getCurrentLayer() instanceof ObjectGroup;

        paintButton.setEnabled(tileLayer);
        eraseButton.setEnabled(tileLayer);
        pourButton.setEnabled(tileLayer);
        eyedButton.setEnabled(tileLayer);
        marqueeButton.setEnabled(tileLayer);
        moveButton.setEnabled(validSelection);
        objectAddButton.setEnabled(objectGroup);
        objectRemoveButton.setEnabled(objectGroup);
        objectMoveButton.setEnabled(objectGroup);

        cloneLayerAction.setEnabled(validSelection);
        deleteLayerAction.setEnabled(validSelection);
        moveLayerUpAction.setEnabled(notTop);
        moveLayerDownAction.setEnabled(notBottom);
        mergeLayerDownAction.setEnabled(notBottom);
        mergeAllLayersAction.setEnabled(nrLayers > 1);

        opacitySlider.setEnabled(validSelection);
    }

    private JMenuItem createMenuItem(String name, Icon icon, String tt) {
        JMenuItem menuItem = new JMenuItem(name);
        menuItem.addActionListener(this);
        if (icon != null) {
            menuItem.setIcon(icon);
        }
        if (tt != null) {
            menuItem.setToolTipText(tt);
        }
        return menuItem;
    }

    private JMenuItem createMenuItem(String name, Icon icon, String tt,
            String keyStroke) {
        JMenuItem menuItem = createMenuItem(name, icon, tt);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(keyStroke));
        return menuItem;
    }

    private AbstractButton createToggleButton(Icon icon, String command,
            String tt) {
        AbstractButton button;
        button = new JToggleButton("", icon);
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setActionCommand(command);
        button.addActionListener(this);
        if (tt != null) {
            button.setToolTipText(tt);
        }
        return button;
    }

    /**
     * Returns the currently selected tile.
     *
     * @return the currently selected tile
     */
    public Tile getCurrentTile() {
        return currentTile;
    }

    /**
     * Returns the current map.
     *
     * @return the currently selected map
     * @see #setCurrentMap(Map)
     */
    public Map getCurrentMap() {
        return currentMap;
    }

    /**
     * Returns the currently selected layer.
     *
     * @return the currently selected layer
     */
    public MapLayer getCurrentLayer() {
        return currentMap.getLayer(currentLayer);
    }

    /**
     * Returns the currently selected layer index.
     *
     * @return the currently selected layer index
     */
    public int getCurrentLayerIndex() {
        return currentLayer;
    }

    /**
     * Returns the {@link UndoableEditSupport} instance.
     * @return the undo support
     */
    public UndoableEditSupport getUndoSupport() {
        return undoSupport;
    }

    /**
     * Returns the {@link UndoHandler} instance.
     * @return the undo stack
     */
    public UndoHandler getUndoHandler() {
        return undoHandler;
    }

    /**
     * Returns the {@link PluginClassLoader} instance.
     * @return the plugin class loader
     */
    public PluginClassLoader getPluginLoader() {
        return pluginLoader;
    }

    /**
     * Returns the main application frame.
     *
     * @return the frame of the main application
     */
    public Frame getAppFrame() {
        return appFrame;
    }

    private void doMouse(MouseEvent event) {
        if (currentMap == null || currentLayer < 0) {
            return;
        }

        Point tile = mapView.screenToTileCoords(event.getX(), event.getY());
        MapLayer layer = getCurrentLayer();

        if (layer == null) {
            return;
        } else if (mouseButton == MouseEvent.BUTTON3) {
            if (layer instanceof TileLayer) {
                if (!bMouseIsDragging) {
                    // Click event is sent before the drag event
                    // so this one always happens
                    Tile newTile = ((TileLayer)layer).getTileAt(tile.x, tile.y);
                    setCurrentTile(newTile);
                } else if (currentPointerState == PS_PAINT) {
                    // In case we are dragging to create a custom brush, let
                    // the user know where we are creating it from
                    if (marqueeSelection == null) {
                        marqueeSelection = new SelectionLayer(
                                currentMap.getWidth(), currentMap.getHeight());
                        currentMap.addLayerSpecial(marqueeSelection);
                    }

                    Point limp = mouseInitialPressLocation;
                    Rectangle oldArea =
                        marqueeSelection.getSelectedAreaBounds();
                    int minx = Math.min(limp.x, tile.x);
                    int miny = Math.min(limp.y, tile.y);

                    Rectangle selRect = new Rectangle(
                            minx, miny,
                            (Math.max(limp.x, tile.x) - minx)+1,
                            (Math.max(limp.y, tile.y) - miny)+1);

                    marqueeSelection.selectRegion(selRect);
                    if (oldArea != null) {
                        oldArea.add(marqueeSelection.getSelectedAreaBounds());
                        mapView.repaintRegion(oldArea);
                    }
                }
            } else if (layer instanceof ObjectGroup && !bMouseIsDragging) {
                // Get the object on this location and display the relative options dialog
                ObjectGroup group = (ObjectGroup) layer;
                Point pos = mapView.screenToPixelCoords(
                        event.getX(), event.getY());
                MapObject obj = group.getObjectNear(pos.x, pos.y, mapView.getZoom());
                if (obj != null) {
                    ObjectDialog od = new ObjectDialog(appFrame, obj, undoSupport);
                    od.getProps();
                }
            }
        } else if (mouseButton == MouseEvent.BUTTON2 ||
                (mouseButton == MouseEvent.BUTTON1 &&
                 (event.getModifiersEx() & MouseEvent.ALT_DOWN_MASK ) != 0)) {
            // Scroll with middle mouse button
            int dx = event.getX() - mouseInitialScreenLocation.x;
            int dy = event.getY() - mouseInitialScreenLocation.y;
            JViewport mapViewPort = mapScrollPane.getViewport();
            Point currentPosition = mapViewPort.getViewPosition();
            mouseInitialScreenLocation = new Point(
                    event.getX() - dx,
                    event.getY() - dy);

            Point newPosition = new Point(
                    currentPosition.x - dx,
                    currentPosition.y - dy);

            // Take into account map boundaries in order to prevent
            // scrolling past them
            int maxX = mapView.getWidth() - mapViewPort.getWidth();
            int maxY = mapView.getHeight() - mapViewPort.getHeight();
            newPosition.x = Math.min(maxX, Math.max(0, newPosition.x));
            newPosition.y = Math.min(maxY, Math.max(0, newPosition.y));

            mapViewPort.setViewPosition(newPosition);
        } else if (mouseButton == MouseEvent.BUTTON1) {
            switch (currentPointerState) {
                case PS_PAINT:
                    paintEdit.setPresentationName(TOOL_PAINT);
                    if (layer instanceof TileLayer) {
                        try {
                            mapView.repaintRegion(
                                    currentBrush.doPaint(tile.x, tile.y));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case PS_ERASE:
                    paintEdit.setPresentationName(TOOL_ERASE);
                    if (layer instanceof TileLayer) {
                        ((TileLayer) layer).setTileAt(tile.x, tile.y, null);
                        mapView.repaintRegion(new Rectangle(
                                tile.x, tile.y, 1, 1));
                    }
                    break;
                case PS_POUR:
                    paintEdit = null;
                    if (layer instanceof TileLayer) {
                        TileLayer tileLayer = (TileLayer) layer;
                        Tile oldTile = tileLayer.getTileAt(tile.x, tile.y);
                        pour(tileLayer, tile.x, tile.y, currentTile, oldTile);
                        mapView.repaint();
                    }
                    break;
                case PS_EYED:
                    if (layer instanceof TileLayer) {
                        TileLayer tileLayer = (TileLayer) layer;
                        Tile newTile = tileLayer.getTileAt(tile.x, tile.y);
                        setCurrentTile(newTile);
                    }
                    break;
                case PS_MOVE: {
                    Point translation = new Point(
                            tile.x - mousePressLocation.x,
                            tile.y - mousePressLocation.y);

                    layer.translate(translation.x, translation.y);
                    moveDist.translate(translation.x, translation.y);
                    mapView.repaint();
                    break;
                }
                case PS_MARQUEE:
                    if (!(layer instanceof TileLayer)) {
                        break;
                    }
                    if (marqueeSelection != null) {
                        Point limp = mouseInitialPressLocation;
                        Rectangle oldArea =
                            marqueeSelection.getSelectedAreaBounds();
                        int minx = Math.min(limp.x, tile.x);
                        int miny = Math.min(limp.y, tile.y);

                        Rectangle selRect = new Rectangle(
                                minx, miny,
                                (Math.max(limp.x, tile.x) - minx)+1,
                                (Math.max(limp.y, tile.y) - miny)+1);

                        if (event.isShiftDown()) {
                            marqueeSelection.add(new Area(selRect));
                        } else if (event.isControlDown()) {
                            marqueeSelection.subtract(new Area(selRect));
                        } else {
                            marqueeSelection.selectRegion(selRect);
                        }
                        if (oldArea != null) {
                            oldArea.add(
                                    marqueeSelection.getSelectedAreaBounds());
                            mapView.repaintRegion(oldArea);
                        }
                    }
                    break;
                case PS_ADDOBJ:
                    if (layer instanceof  ObjectGroup) {
                        if (marqueeSelection == null) {
                            marqueeSelection = new SelectionLayer(
                                    currentMap.getWidth(),
                                    currentMap.getHeight());
                            currentMap.addLayerSpecial(marqueeSelection);
                        }

                        Point limp = mouseInitialPressLocation;
                        Rectangle oldArea =
                            marqueeSelection.getSelectedAreaBounds();
                        int minx = Math.min(limp.x, tile.x);
                        int miny = Math.min(limp.y, tile.y);

                        Rectangle selRect = new Rectangle(
                                minx, miny,
                                (Math.max(limp.x, tile.x) - minx) + 1,
                                (Math.max(limp.y, tile.y) - miny) + 1);

                        marqueeSelection.selectRegion(selRect);
                        if (oldArea != null) {
                            oldArea.add(marqueeSelection.getSelectedAreaBounds());
                            mapView.repaintRegion(oldArea);
                        }
                    }
                    break;
                case PS_REMOVEOBJ:
                    if (layer instanceof ObjectGroup) {
                        ObjectGroup group = (ObjectGroup) layer;
                        Point pos = mapView.screenToPixelCoords(
                                event.getX(), event.getY());
                        MapObject obj = group.getObjectNear(pos.x, pos.y, mapView.getZoom());
                        if (obj != null) {
                            undoSupport.postEdit(new RemoveObjectEdit(group, obj));
                            group.removeObject(obj);
                            // TODO: repaint only affected area
                            mapView.repaint();
                        }
                    }
                    break;
                case PS_MOVEOBJ:
                    if (layer instanceof ObjectGroup) {
                        Point pos = mapView.screenToPixelCoords(
                                event.getX(), event.getY());
                        if (currentObject == null) {
                            ObjectGroup group = (ObjectGroup) layer;
                            currentObject = group.getObjectNear(pos.x, pos.y, mapView.getZoom());
                            if (currentObject == null) { // No object to move
                                break;
                            }
                            mouseLastPixelLocation = pos;
                            moveDist = new Point(0, 0);
                            break;
                        }
                        Point translation = new Point(
                                pos.x - mouseLastPixelLocation.x,
                                pos.y - mouseLastPixelLocation.y);
                        currentObject.translate(translation.x, translation.y);
                        moveDist.translate(translation.x, translation.y);
                        mouseLastPixelLocation = pos;
                        mapView.repaint();
                    }
                    break;
            }
        }
    }

    public void mouseExited(MouseEvent e) {
        tileCoordsLabel.setText(" ");
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        Point tile = mapView.screenToTileCoords(e.getX(), e.getY());
        mouseButton = e.getButton();
        bMouseIsDown = true;
        bMouseIsDragging = false;
        mousePressLocation = mapView.screenToTileCoords(e.getX(), e.getY());
        mouseInitialPressLocation = mousePressLocation;

        MapLayer layer = getCurrentLayer();

        if (mouseButton == MouseEvent.BUTTON2 ||
                (mouseButton == MouseEvent.BUTTON1 &&
                        (e.getModifiersEx() & MouseEvent.ALT_DOWN_MASK) != 0)) {
            // Remember screen location for scrolling with middle mouse button
            mouseInitialScreenLocation = new Point(e.getX(), e.getY());
        }
        else if (mouseButton == MouseEvent.BUTTON1) {
            switch (currentPointerState) {
                case PS_PAINT:
                    if (layer instanceof TileLayer) {
                        currentBrush.startPaint(currentMap, tile.x, tile.y,
                                                mouseButton, currentLayer);
                    }
                case PS_ERASE:
                case PS_POUR:
                    paintEdit =
                            new MapLayerEdit(layer, createLayerCopy(layer),
                                             null);
                    break;
                default:
            }
        }

        if (currentPointerState == PS_MARQUEE) {
            boolean contains = false;
            if (marqueeSelection != null && marqueeSelection.getSelectedArea().contains(tile.x,tile.y)) {
                contains = true;
            }
            if (marqueeSelection == null && !contains) {
                marqueeSelection = new SelectionLayer(
                        currentMap.getWidth(), currentMap.getHeight());
                currentMap.addLayerSpecial(marqueeSelection);
            }
            else if (marqueeSelection != null && e.getModifiers() == InputEvent.BUTTON1_MASK) {
                currentMap.removeLayerSpecial(marqueeSelection);
                if (contains) {
                    marqueeSelection = null;
                }
                else {
                    marqueeSelection = new SelectionLayer(
                            currentMap.getWidth(), currentMap.getHeight());
                    currentMap.addLayerSpecial(marqueeSelection);
                }
            }
        } else if (currentPointerState == PS_MOVE) {
            // Initialize move distance to (0, 0)
            moveDist = new Point(0, 0);
        }

        doMouse(e);
    }

    public void mouseReleased(MouseEvent event) {
        final MapLayer layer = getCurrentLayer();
        final Point limp = mouseInitialPressLocation;

        if (currentPointerState == PS_MARQUEE) {
            // Uncommented to allow single tile selections
            /*
            Point tile = mapView.screenToTileCoords(event.getX(), event.getY());
            if (tile.y - limp.y == 0 && tile.x - limp.x == 0) {
                if (marqueeSelection != null) {
                    currentMap.removeLayerSpecial(marqueeSelection);
                    marqueeSelection = null;
                }
            }
            */

            // There should be a proper notification mechanism for this...
            //if (marqueeSelection != null) {
            //    tileInstancePropertiesDialog.setSelection(marqueeSelection);
            //}
        } else if (currentPointerState == PS_MOVE) {
            if (layer != null && (moveDist.x != 0 || moveDist.x != 0)) {
                undoSupport.postEdit(new MoveLayerEdit(layer, moveDist));
            }
        } else if (currentPointerState == PS_PAINT) {
            if (layer instanceof TileLayer) {
                currentBrush.endPaint();
            }
        } else if (currentPointerState == PS_MOVEOBJ) {
            if (layer instanceof ObjectGroup && currentObject != null &&
                    (moveDist.x != 0 || moveDist.x != 0)) {
                undoSupport.postEdit(
                        new MoveObjectEdit(currentObject, moveDist));
            }
        }


        if (/*bMouseIsDragging && */currentPointerState == PS_PAINT ||
                currentPointerState == PS_ADDOBJ)
        {
            Point tile = mapView.screenToTileCoords(
                    event.getX(), event.getY());
            int minx = Math.min(limp.x, tile.x);
            int miny = Math.min(limp.y, tile.y);

            Rectangle bounds = new Rectangle(
                    minx, miny,
                    (Math.max(limp.x, tile.x) - minx) + 1,
                    (Math.max(limp.y, tile.y) - miny) + 1);

            // STAMP
            if (mouseButton == MouseEvent.BUTTON3 &&
                    layer instanceof TileLayer) {
                // Right mouse button dragged: create and set custom brush
                TileLayer brushLayer = new TileLayer(bounds);
                brushLayer.copyFrom(getCurrentLayer());
                brushLayer.setOffset(tile.x - (int) bounds.getWidth() / 2,
                                     tile.y - (int) bounds.getHeight() / 2);

                // Do a quick check to make sure the selection is not empty
                if (brushLayer.isEmpty()) {
                    JOptionPane.showMessageDialog(appFrame,
                            Resources.getString("dialog.selection.empty"),
                            Resources.getString("dialog.selection.empty"),
                            JOptionPane.WARNING_MESSAGE);
                } else {
                    setBrush(new CustomBrush(brushLayer));
                    cursorHighlight.setOffset(
                            tile.x - (int) bounds.getWidth() / 2,
                            tile.y - (int) bounds.getHeight() / 2);
                }
            }
            else if (mouseButton == MouseEvent.BUTTON1 &&
                    layer instanceof ObjectGroup) {
                // TODO: Fix this to use pixels in the first place
                // (with optional snap to grid)
                int w = currentMap.getTileWidth();
                int h = currentMap.getTileHeight();
                MapObject object = new MapObject(
                        bounds.x * w,
                        bounds.y * h,
                        bounds.width * w,
                        bounds.height * h);
                /*Point pos = mapView.screenToPixelCoords(
                        event.getX(), event.getY());*/
                ObjectGroup group = (ObjectGroup) layer;
                undoSupport.postEdit(new AddObjectEdit(group, object));
                group.addObject(object);
                mapView.repaint();
            }

            //get rid of any visible marquee
            if (marqueeSelection != null) {
                currentMap.removeLayerSpecial(marqueeSelection);
                marqueeSelection = null;
            }
        }

        if (paintEdit != null) {
            if (layer != null) {
                try {
                    MapLayer endLayer = paintEdit.getStart().createDiff(layer);
                    paintEdit.end(endLayer);
                    undoSupport.postEdit(paintEdit);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            paintEdit = null;
        }

        currentObject = null;

        mouseButton = MouseEvent.NOBUTTON;
        bMouseIsDown = false;
        bMouseIsDragging = false;
    }

    public void mouseMoved(MouseEvent e) {
        // Update state of mouse buttons
        bMouseIsDown = e.getButton() != MouseEvent.NOBUTTON;
        if (bMouseIsDown) {
            doMouse(e);
        }

        Point tile = mapView.screenToTileCoords(e.getX(), e.getY());
        updateTileCoordsLabel(tile);
        updateCursorHighlight(tile);
    }

    public void mouseDragged(MouseEvent e) {
        bMouseIsDragging = true;

        doMouse(e);

        mousePressLocation = mapView.screenToTileCoords(e.getX(), e.getY());
        Point tile = mapView.screenToTileCoords(e.getX(), e.getY());

        updateTileCoordsLabel(tile);
        updateCursorHighlight(tile);
    }

    private void updateTileCoordsLabel(Point tile) {
        if (currentMap.inBounds(tile.x, tile.y)) {
            tileCoordsLabel.setText(String.valueOf(tile.x) + ", " + tile.y);
        } else {
            tileCoordsLabel.setText(" ");
        }
    }

    private void updateCursorHighlight(Point tile) {
        if (prefs.getBoolean("cursorhighlight", true)) {
            Rectangle redraw = cursorHighlight.getBounds();
            Rectangle brushRedraw = currentBrush.getBounds();

            brushRedraw.x = tile.x - brushRedraw.width / 2;
            brushRedraw.y = tile.y - brushRedraw.height / 2;

            if (!redraw.equals(brushRedraw)) {
                if (currentBrush instanceof CustomBrush) {
                    CustomBrush customBrush = (CustomBrush) currentBrush;
                    ListIterator<MapLayer> layers = customBrush.getLayers();
                    while (layers.hasNext()) {
                        MapLayer layer = layers.next();
                        layer.setOffset(brushRedraw.x, brushRedraw.y);
                    }
                }
                mapView.repaintRegion(redraw);
                cursorHighlight.setOffset(brushRedraw.x, brushRedraw.y);
                //cursorHighlight.selectRegion(currentBrush.getShape());
                mapView.repaintRegion(brushRedraw);
            }
        }
    }

    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();

        if ("paint".equals(command)) {
            setCurrentPointerState(PS_PAINT);
            resetBrush();
        } else if ("erase".equals(command)) {
            setCurrentPointerState(PS_ERASE);
            resetBrush();
        } else if ("point".equals(command)) {
            setCurrentPointerState(PS_POINT);
            resetBrush();
        } else if ("pour".equals(command)) {
            setCurrentPointerState(PS_POUR);
            resetBrush();
        } else if ("eyed".equals(command)) {
            setCurrentPointerState(PS_EYED);
            resetBrush();
        } else if ("marquee".equals(command)) {
            setCurrentPointerState(PS_MARQUEE);
            resetBrush();
        } else if ("move".equals(command)) {
            setCurrentPointerState(PS_MOVE);
            resetBrush();
        } else if ("addobject".equals(command)) {
            setCurrentPointerState(PS_ADDOBJ);
            resetBrush();
        } else if ("removeobject".equals(command)) {
            setCurrentPointerState(PS_REMOVEOBJ);
            resetBrush();
        } else if ("moveobject".equals(command)) {
            setCurrentPointerState(PS_MOVEOBJ);
            resetBrush();
        //} else if ("tileInstanceProperties".equals(command)) {
        //    if (currentMap != null) {
        //        tileInstancePropertiesDialog.setVisible(true);
        //    }
        } else {
            handleEvent(event);
        }
    }

    // TODO: Most if not all of the below should be moved into action objects
    private void handleEvent(ActionEvent event) {
        String command = event.getActionCommand();

        if (command.equals(Resources.getString("menu.edit.brush"))) {
            BrushDialog bd = new BrushDialog(this, appFrame, currentBrush);
            bd.setVisible(true);
        } else if (command.equals(Resources.getString("menu.tilesets.new"))) {
            if (currentMap != null) {
                NewTilesetDialog dialog =
                    new NewTilesetDialog(appFrame, currentMap);
                TileSet newSet = dialog.create();
                if (newSet != null) {
                    currentMap.addTileset(newSet);
                }
            }
        } else if (command.equals(Resources.getString("menu.tilesets.import"))) {
            if (currentMap != null) {
                JFileChooser chooser = new JFileChooser(currentMap.getFilename());
                MapReader[] readers = pluginLoader.getReaders();
                for (MapReader reader : readers) {
                    try {
                        chooser.addChoosableFileFilter(new TiledFileFilter(
                                reader.getFilter(),
                                reader.getName()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                chooser.addChoosableFileFilter(
                        new TiledFileFilter(TiledFileFilter.FILTER_TSX));

                int ret = chooser.showOpenDialog(appFrame);
                if (ret == JFileChooser.APPROVE_OPTION) {
                    String filename = chooser.getSelectedFile().getAbsolutePath();
                    try {
                        TileSet set = MapHelper.loadTileset(filename);
                        currentMap.addTileset(set);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if (command.equals(Resources.getString("menu.tilesets.refresh"))) {
            if (currentMap != null) {
                Vector<TileSet> tilesets = currentMap.getTilesets();
                for (TileSet tileset : tilesets) {
                    try {
                        tileset.checkUpdate();
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(appFrame,
                                e.getLocalizedMessage(),
                                IMPORT_ERROR_MSG, JOptionPane.WARNING_MESSAGE);
                    }
                }
                mapView.repaint();
                brushPreview.setBrush(currentBrush);
            }
        } else if (command.equals(Resources.getString("menu.tilesets.manager"))) {
            if (currentMap != null) {
                TilesetManager manager = new TilesetManager(appFrame, currentMap);
                manager.setVisible(true);
            }
        } else if (command.equals(Resources.getString("menu.map.properties"))) {
            PropertiesDialog pd = new PropertiesDialog(appFrame,
                    currentMap.getProperties());
            pd.setTitle(Resources.getString("dialog.properties.map.title"));
            pd.getProps();
        } else if (command.equals(Resources.getString("menu.layer.properties"))) {
            MapLayer layer = getCurrentLayer();
            PropertiesDialog lpd =
                new PropertiesDialog(appFrame, layer.getProperties());
            lpd.setTitle(layer.getName() + " " + Resources.getString("dialog.properties.title"));
            lpd.getProps();
        } else if (command.equals(Resources.getString("menu.view.boundaries")) ||
                command.equals("Hide Boundaries")) {
            mapView.toggleMode(MapView.PF_BOUNDARYMODE);
        } else if (command.equals(Resources.getString("menu.view.grid"))) {
            // Toggle grid
            Preferences displayPrefs = prefs.node("display");
            boolean showGrid = displayPrefs.getBoolean("showGrid", false);
            displayPrefs.putBoolean("showGrid", !showGrid);
        } else if (command.equals(Resources.getString("menu.view.coordinates"))) {
            // Toggle coordinates
            mapView.toggleMode(MapView.PF_COORDINATES);
        } else if (command.equals(Resources.getString("menu.view.cursor"))) {
            prefs.putBoolean("cursorhighlight", cursorMenuItem.isSelected());
            cursorHighlight.setVisible(cursorMenuItem.isSelected());
        } else if (command.equals(Resources.getString("menu.map.resize"))) {
            ResizeDialog rd = new ResizeDialog(appFrame, this);
            rd.setVisible(true);
        }  else if (command.equals(Resources.getString("menu.map.search"))) {
            SearchDialog sd = new SearchDialog(appFrame, currentMap);
            sd.setVisible(true);
        } else if (command.equals(Resources.getString("menu.help.about"))) {
            showAboutDialog();
        } else if (command.equals(Resources.getString("menu.help.plugins"))) {
            PluginDialog pluginDialog =
                new PluginDialog(appFrame, pluginLoader);
            pluginDialog.setVisible(true);
        } else if (command.equals(Resources.getString("menu.edit.preferences"))) {
            ConfigurationDialog dialog = new ConfigurationDialog(appFrame);
            dialog.configure();
        } else {
            System.out.println(event);
        }
    }

    public void componentHidden(ComponentEvent event) {
    }

    public void componentMoved(ComponentEvent event) {
    }

    public void componentResized(ComponentEvent event) {
        // This can currently only happen when the map changes size
        String s = String.valueOf((int) (mapView.getZoom() * 100)) + "%";
        zoomLabel.setText(s);

        // Restore the midpoint
        JViewport mapViewPort = mapScrollPane.getViewport();
        Rectangle viewRect = mapViewPort.getViewRect();
        int absMidX = Math.max(0, Math.round(relativeMidX * mapView.getWidth()) - viewRect.width / 2);
        int absMidY = Math.max(0, Math.round(relativeMidY * mapView.getHeight()) - viewRect.height / 2);
        mapViewPort.setViewPosition(new Point(absMidX, absMidY));
    }

    public void componentShown(ComponentEvent event) {
    }

    public void mapChanged(MapChangedEvent e) {
        if (e.getMap() == currentMap) {
            mapScrollPane.setViewportView(mapView);
            updateLayerTable();
            mapView.repaint();
        }
    }

    public void tilesetAdded(MapChangedEvent e, TileSet tileset) {
    }

    public void tilesetRemoved(MapChangedEvent e, int index) {
        mapView.repaint();
    }

    public void tilesetsSwapped(MapChangedEvent e, int index0, int index1) {
    }

    public void valueChanged(ListSelectionEvent e) {
        int selectedRow = layerTable.getSelectedRow();

        // At the moment, this can only be a new layer selection
        if (currentMap != null && selectedRow >= 0) {
            currentLayer = currentMap.getTotalLayers() - selectedRow - 1;

            float opacity = getCurrentLayer().getOpacity();
            opacitySlider.setValue((int)(opacity * 100));
        } else {
            currentLayer = -1;
        }

        updateLayerOperations();
    }

    public void stateChanged(ChangeEvent e) {
        JViewport mapViewport = mapScrollPane.getViewport();

        if (e.getSource() == opacitySlider) {
            if (currentMap != null && currentLayer >= 0) {
                MapLayer layer = getCurrentLayer();
                layer.setOpacity(opacitySlider.getValue() / 100.0f);

                /*
                MapLayerStateEdit mlse = new MapLayerStateEdit(currentMap);
                mlse.setPresentationName("Opacity Change");
                undoSupport.postEdit(mlse);
                */
            }
        }
        else if (e.getSource() == mapViewport && mapView != null) {
            // Store the point in the middle for zooming purposes
            Rectangle viewRect = mapViewport.getViewRect();
            relativeMidX = Math.min(1, (viewRect.x + viewRect.width / 2) / (float)mapView.getWidth());
            relativeMidY = Math.min(1, (viewRect.y + viewRect.height / 2) / (float)mapView.getHeight());
        }
    }

    /**
     * Returns the map view. Currently only used by the {@link UndoHandler}
     * to be able to order a repaint when doing an undo or redo. This should
     * become obsolete when we add automatic repainting based on layer
     * changes.
     *
     * @return the map view.
     */
    public MapView getMapView() {
        return mapView;
    }

    /**
     * Called when the editor is exiting.
     */
    public void shutdown() {
        // Save the extended window state if the window isn't minimized
        final int extendedState = appFrame.getExtendedState();
        final Preferences mainDialogPrefs = prefs.node("dialog/main");
        mainDialogPrefs.putInt("state", extendedState);
        if (extendedState == Frame.NORMAL) {
            mainDialogPrefs.putInt("width", appFrame.getWidth());
            mainDialogPrefs.putInt("height", appFrame.getHeight());
        }

        // Allow the floatable panels to save their position and size
        layersPanel.save();
        tilesetsPanel.save();
    }

    private void showAboutDialog() {
        if (aboutDialog == null) {
            aboutDialog = new AboutDialog(appFrame);
        }
        aboutDialog.setVisible(true);
    }

    private class LayerTransformAction extends AbstractAction {
        private final int transform;
        public LayerTransformAction(int transform) {
            this.transform = transform;
            switch (transform) {
                case MapLayer.ROTATE_90:
                    putValue(NAME, Resources.getString("action.layer.transform.rotate90.name"));
                    putValue(SHORT_DESCRIPTION,
                            Resources.getString("action.layer.transform.rotate90.tooltip"));
                    putValue(SMALL_ICON,
                            Resources.getIcon("gimp-rotate-90-16.png"));
                    break;
                case MapLayer.ROTATE_180:
                    putValue(NAME, Resources.getString("action.layer.transform.rotate180.name"));
                    putValue(SHORT_DESCRIPTION,
                            Resources.getString("action.layer.transform.rotate180.tooltip"));
                    putValue(SMALL_ICON,
                            Resources.getIcon("gimp-rotate-180-16.png"));
                    break;
                case MapLayer.ROTATE_270:
                    putValue(NAME, Resources.getString("action.layer.transform.rotate270.name"));
                    putValue(SHORT_DESCRIPTION,
                            Resources.getString("action.layer.transform.rotate270.tooltip"));
                    putValue(SMALL_ICON,
                            Resources.getIcon("gimp-rotate-270-16.png"));
                    break;
                case MapLayer.MIRROR_VERTICAL:
                    putValue(NAME, Resources.getString("action.layer.transform.vertical.name"));
                    putValue(SHORT_DESCRIPTION, Resources.getString("action.layer.transform.vertical.tooltip"));
                    putValue(SMALL_ICON,
                            Resources.getIcon("gimp-flip-vertical-16.png"));
                    break;
                case MapLayer.MIRROR_HORIZONTAL:
                    putValue(NAME, Resources.getString("action.layer.transform.horizontal.name"));
                    putValue(SHORT_DESCRIPTION, Resources.getString("action.layer.transform.horizontal.tooltip"));
                    putValue(SMALL_ICON,
                            Resources.getIcon("gimp-flip-horizontal-16.png"));
                    break;
            }
        }
        public void actionPerformed(ActionEvent evt) {
            MapLayer currentLayer = getCurrentLayer();
            MapLayer layer = currentLayer;
            MapLayerEdit transEdit;
            transEdit = new MapLayerEdit(
                    currentLayer, createLayerCopy(currentLayer));

            if (marqueeSelection != null) {
                if (currentLayer instanceof TileLayer) {
                    layer = new TileLayer(
                            marqueeSelection.getSelectedAreaBounds());
                } else if (currentLayer instanceof ObjectGroup) {
                    layer = new ObjectGroup(
                            marqueeSelection.getSelectedAreaBounds());
                }
                layer.setMap(currentMap);
                layer.maskedCopyFrom(
                        currentLayer,
                        marqueeSelection.getSelectedArea());
            }

            switch (transform) {
                case MapLayer.ROTATE_90:
                case MapLayer.ROTATE_180:
                case MapLayer.ROTATE_270:
                    transEdit.setPresentationName("Rotate");
                    layer.rotate(transform);
                    //if(marqueeSelection != null) marqueeSelection.rotate(transform);
                    break;
                case MapLayer.MIRROR_VERTICAL:
                    transEdit.setPresentationName("Vertical Flip");
                    layer.mirror(MapLayer.MIRROR_VERTICAL);
                    //if(marqueeSelection != null) marqueeSelection.mirror(transform);
                    break;
                case MapLayer.MIRROR_HORIZONTAL:
                    transEdit.setPresentationName("Horizontal Flip");
                    layer.mirror(MapLayer.MIRROR_HORIZONTAL);
                    //if(marqueeSelection != null) marqueeSelection.mirror(transform);
                    break;
            }

            if (marqueeSelection != null ) {
                layer.mergeOnto(currentLayer);
            }

            transEdit.end(createLayerCopy(currentLayer));
            undoSupport.postEdit(transEdit);
            mapView.repaint();
        }
    }

    private class CancelSelectionAction extends AbstractAction {
        public CancelSelectionAction() {
            super(Resources.getString("action.select.none.name"));
            putValue(ACCELERATOR_KEY,
                    KeyStroke.getKeyStroke("control shift A"));
            putValue(SHORT_DESCRIPTION,
                     Resources.getString("action.select.none.tooltip"));
        }

        public void actionPerformed(ActionEvent e) {
            if (currentMap != null) {
                if (marqueeSelection != null) {
                    currentMap.removeLayerSpecial(marqueeSelection);
                }

                marqueeSelection = null;
            }
        }
    }

    private class SelectAllAction extends AbstractAction {
        public SelectAllAction() {
            super(Resources.getString("action.select.all.name"));
            putValue(ACCELERATOR_KEY,
                    KeyStroke.getKeyStroke("control A"));
            putValue(SHORT_DESCRIPTION,
                     Resources.getString("action.select.all.tooltip"));
        }

        public void actionPerformed(ActionEvent e) {
            if (currentMap != null) {
                if (marqueeSelection != null) {
                    currentMap.removeLayerSpecial(marqueeSelection);
                }
                marqueeSelection = new SelectionLayer(
                        currentMap.getWidth(), currentMap.getHeight());
                marqueeSelection.selectRegion(marqueeSelection.getBounds());
                currentMap.addLayerSpecial(marqueeSelection);
            }
        }
    }

    private class InverseSelectionAction extends AbstractAction {
        public InverseSelectionAction() {
            super(Resources.getString("action.select.invert.name"));
            putValue(ACCELERATOR_KEY,
                    KeyStroke.getKeyStroke("control I"));
            putValue(SHORT_DESCRIPTION,
                     Resources.getString("action.select.invert.tooltip"));
        }

        public void actionPerformed(ActionEvent e) {
            if (marqueeSelection != null) {
                marqueeSelection.invert();
                mapView.repaint();
            }
        }
    }

    private class ZoomInAction extends AbstractAction {
        public ZoomInAction() {
            super(Resources.getString("action.zoom.in.name"));
            putValue(ACCELERATOR_KEY,
                    KeyStroke.getKeyStroke("control EQUALS"));
            putValue(SHORT_DESCRIPTION,
                     Resources.getString("action.zoom.in.tooltip"));
            putValue(SMALL_ICON, Resources.getIcon("gnome-zoom-in.png"));
        }
        public void actionPerformed(ActionEvent evt) {
            if (currentMap != null) {
                zoomOutAction.setEnabled(true);
                if (!mapView.zoomIn()) {
                    setEnabled(false);
                }
                zoomNormalAction.setEnabled(mapView.getZoomLevel() !=
                        MapView.ZOOM_NORMALSIZE);
            }
        }
    }

    private class ZoomOutAction extends AbstractAction {
        public ZoomOutAction() {
            super(Resources.getString("action.zoom.out.name"));
            putValue(ACCELERATOR_KEY,
                    KeyStroke.getKeyStroke("control MINUS"));
            putValue(SHORT_DESCRIPTION,
                     Resources.getString("action.zoom.out.tooltip"));
            putValue(SMALL_ICON, Resources.getIcon("gnome-zoom-out.png"));
        }
        public void actionPerformed(ActionEvent evt) {
            if (currentMap != null) {
                zoomInAction.setEnabled(true);
                if (!mapView.zoomOut()) {
                    setEnabled(false);
                }
                zoomNormalAction.setEnabled(mapView.getZoomLevel() !=
                        MapView.ZOOM_NORMALSIZE);
            }
        }
    }

    private class ZoomNormalAction extends AbstractAction {
        public ZoomNormalAction() {
            super(Resources.getString("action.zoom.normal.name"));
            putValue(ACCELERATOR_KEY,
                    KeyStroke.getKeyStroke("control 0"));
            putValue(SHORT_DESCRIPTION,
                     Resources.getString("action.zoom.normal.tooltip"));
        }
        public void actionPerformed(ActionEvent evt) {
            if (currentMap != null) {
                zoomInAction.setEnabled(true);
                zoomOutAction.setEnabled(true);
                setEnabled(false);
                mapView.setZoomLevel(MapView.ZOOM_NORMALSIZE);
            }
        }
    }

    private class CopyAction extends AbstractAction {
        public CopyAction() {
            super(Resources.getString("action.copy.name"));
            putValue(ACCELERATOR_KEY,
                    KeyStroke.getKeyStroke("control C"));
            putValue(SHORT_DESCRIPTION,
                     Resources.getString("action.copy.tooltip"));
        }
        public void actionPerformed(ActionEvent evt) {
            if (currentMap != null && marqueeSelection != null) {
                if (getCurrentLayer() instanceof TileLayer) {
                    clipboardLayer = new TileLayer(
                            marqueeSelection.getSelectedAreaBounds());
                } else if (getCurrentLayer() instanceof ObjectGroup) {
                    clipboardLayer = new ObjectGroup(
                            marqueeSelection.getSelectedAreaBounds());
                }
                clipboardLayer.maskedCopyFrom(
                        getCurrentLayer(),
                        marqueeSelection.getSelectedArea());
            }
        }
    }

    private class CopyAllAction extends AbstractAction {
        public CopyAllAction() {
            super(Resources.getString("action.copyall.name"));
            putValue(ACCELERATOR_KEY,
                    KeyStroke.getKeyStroke("shift control C"));
            putValue(SHORT_DESCRIPTION,
                     Resources.getString("action.copyall.tooltip"));
        }
        public void actionPerformed(ActionEvent evt) {
            //FIXME: only works for TileLayers
            if (currentMap != null && marqueeSelection != null) {
                clipboardLayer = new TileLayer(
                        marqueeSelection.getSelectedAreaBounds());
                ListIterator itr = currentMap.getLayers();
                while(itr.hasNext()) {
                    MapLayer layer = (MapLayer) itr.next();
                    if (layer instanceof TileLayer) {
                        clipboardLayer.maskedMergeOnto(
                                layer,
                                marqueeSelection.getSelectedArea());
                    }
                }
            }
        }
    }

    private class CutAction extends AbstractAction {
        public CutAction() {
            super(Resources.getString("action.cut.name"));
            putValue(ACCELERATOR_KEY,
                    KeyStroke.getKeyStroke("control X"));
            putValue(SHORT_DESCRIPTION,
                     Resources.getString("action.cut.tooltip"));
        }
        public void actionPerformed(ActionEvent evt) {
            if (currentMap != null && marqueeSelection != null) {
                MapLayer ml = getCurrentLayer();

                if (getCurrentLayer() instanceof TileLayer) {
                    clipboardLayer = new TileLayer(
                            marqueeSelection.getSelectedAreaBounds());
                } else if (getCurrentLayer() instanceof ObjectGroup) {
                    clipboardLayer = new ObjectGroup(
                            marqueeSelection.getSelectedAreaBounds());
                }
                clipboardLayer.maskedCopyFrom(
                        ml, marqueeSelection.getSelectedArea());

                Rectangle area = marqueeSelection.getSelectedAreaBounds();
                Area mask = marqueeSelection.getSelectedArea();
                if (ml instanceof TileLayer) {
                    TileLayer tl = (TileLayer)ml;
                    for (int i = area.y; i < area.height+area.y; i++) {
                        for (int j = area.x; j < area.width + area.x; j++){
                            if (mask.contains(j,i)) {
                                tl.setTileAt(j, i, null);
                            }
                        }
                    }
                }
                mapView.repaintRegion(area);
            }
        }
    }

    private class PasteAction extends AbstractAction {
        public PasteAction() {
            super(Resources.getString("action.paste.name"));
            putValue(ACCELERATOR_KEY,
                    KeyStroke.getKeyStroke("control V"));
            putValue(SHORT_DESCRIPTION,
                     Resources.getString("action.paste.tooltip"));
        }
        public void actionPerformed(ActionEvent evt) {
            if (currentMap != null && clipboardLayer != null) {
                Vector<MapLayer> layersBefore = currentMap.getLayerVector();
                MapLayer ml = createLayerCopy(clipboardLayer);
                ml.setName(Resources.getString("general.layer.layer")+" " + currentMap.getTotalLayers());
                currentMap.addLayer(ml);
                undoSupport.postEdit(
                        new MapLayerStateEdit(currentMap, layersBefore,
                            new Vector<MapLayer>(currentMap.getLayerVector()),
                            "Paste Selection"));
            }
        }
    }

    private void pour(TileLayer layer, int x, int y,
            Tile newTile, Tile oldTile) {
        if (newTile == oldTile || !layer.canEdit()) return;

        Rectangle area;
        TileLayer before = (TileLayer) createLayerCopy(layer);
        TileLayer after;

        // Check that the copy was succesfully created
        if (before == null) {
            return;
        }

        if (marqueeSelection == null) {
            area = new Rectangle(new Point(x, y));
            Stack<Point> stack = new Stack<Point>();

            stack.push(new Point(x, y));
            while (!stack.empty()) {
                // Remove the next tile from the stack
                Point p = stack.pop();

                // If the tile it meets the requirements, set it and push its
                // neighbouring tiles on the stack.
                if (layer.contains(p.x, p.y) &&
                        layer.getTileAt(p.x, p.y) == oldTile)
                {
                    layer.setTileAt(p.x, p.y, newTile);
                    area.add(p);

                    stack.push(new Point(p.x, p.y - 1));
                    stack.push(new Point(p.x, p.y + 1));
                    stack.push(new Point(p.x + 1, p.y));
                    stack.push(new Point(p.x - 1, p.y));
                }
            }
        } else {
            if (marqueeSelection.getSelectedArea().contains(x, y)) {
                area = marqueeSelection.getSelectedAreaBounds();
                for (int i = area.y; i < area.height + area.y; i++) {
                    for (int j = area.x; j < area.width + area.x; j++) {
                        if (marqueeSelection.getSelectedArea().contains(j, i)){
                            layer.setTileAt(j, i, newTile);
                        }
                    }
                }
            } else {
                return;
            }
        }

        Rectangle bounds = new Rectangle(
                area.x, area.y, area.width + 1, area.height + 1);
        after = new TileLayer(bounds);
        after.copyFrom(layer);

        MapLayerEdit mle = new MapLayerEdit(layer, before, after);
        mle.setPresentationName(TOOL_FILL);
        undoSupport.postEdit(mle);
    }

    public AbstractBrush getBrush() {
        return currentBrush;
    }

    public void resetBrush() {
        //FIXME: this is an in-elegant hack, but it gets the user out
        //       of custom brush mode
        //(reset the brush if necessary)
        if (currentBrush instanceof CustomBrush) {
            ShapeBrush sb = new ShapeBrush();
            sb.makeQuadBrush(new Rectangle(0, 0, 1, 1));
            sb.setTile(currentTile);
            setBrush(sb);
        }
    }

    public void setBrush(AbstractBrush brush) {
        // Make sure a possible current highlight gets erased from screen
        if (mapView != null && prefs.getBoolean("cursorhighlight", true)) {
            Rectangle redraw = cursorHighlight.getBounds();
            mapView.repaintRegion(redraw);
        }

        currentBrush = brush;

        // Resize and select the region
        Rectangle brushRedraw = currentBrush.getBounds();
        cursorHighlight.resize(brushRedraw.width, brushRedraw.height, 0, 0);
        cursorHighlight.selectRegion(currentBrush.getShape());
        if (mapView != null) {
            mapView.setBrush(currentBrush);
        }
    }

    public void updateTitle() {
        String title = Resources.getString("dialog.main.title");

        if (currentMap != null) {
            String filename = currentMap.getFilename();
            title += " - ";
            if (filename != null) {
                title += currentMap.getFilename();
            } else {
                title += Resources.getString("general.file.untitled");
            }
            if (unsavedChanges()) {
                title += "*";
            }
        }

        appFrame.setTitle(title);
    }

    /**
     * Checks to see if the undo stack is empty
     *
     * @return <code>true</code> if there is an undo history, <code>false</code> otherwise.
     */
    public boolean unsavedChanges() {
        return currentMap != null && undoHandler.canUndo() &&
                !undoHandler.isAllSaved();
    }

    /**
     * Loads a map.
     *
     * @param file filename of map to load
     * @return <code>true</code> if the file was loaded, <code>false</code> if
     *         an error occured
     */
    public boolean loadMap(String file) {
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

            if (map != null) {
                setCurrentMap(map);
                updateRecent(file);
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

    private static MapLayer createLayerCopy(MapLayer layer) {
        try {
            return (MapLayer) layer.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void updateRecent(String filename) {
        // If a filename is given, add it to the recent files
        if (filename != null) {
            TiledConfiguration.addToRecentFiles(filename);
        }

        java.util.List<String> files = TiledConfiguration.getRecentFiles();

        recentMenu.removeAll();

        for (String file : files) {
            recentMenu.add(new TMenuItem(new OpenRecentAction(this, saveAction, file)));
        }
    }

    public void setCurrentMap(Map newMap) {
        // Cancel any active selection
        if (marqueeSelection != null && currentMap != null) {
            currentMap.removeLayerSpecial(marqueeSelection);
        }
        marqueeSelection = null;

        currentMap = newMap;
        boolean mapLoaded = currentMap != null;

        // Create a default brush (protect against a bug with custom brushes)
        ShapeBrush sb = new ShapeBrush();
        sb.makeQuadBrush(new Rectangle(0, 0, 1, 1));
        setBrush(sb);

        tabbedTilesetsPane.setMap(currentMap);

        if (!mapLoaded) {
            mapEventAdapter.fireEvent(MapEventAdapter.ME_MAPINACTIVE);
            mapView = null;
            mapScrollPane.setViewportView(Box.createRigidArea(
                        new Dimension(0,0)));
            setCurrentPointerState(PS_POINT);
            tileCoordsLabel.setPreferredSize(null);
            tileCoordsLabel.setText(" ");
            zoomLabel.setText(" ");
            setCurrentTile(null);
        } else {
            final Preferences display = prefs.node("display");
            mapEventAdapter.fireEvent(MapEventAdapter.ME_MAPACTIVE);
            mapView = MapView.createViewforMap(currentMap);
            mapView.addMouseListener(this);
            mapView.addMouseMotionListener(this);
            mapView.addComponentListener(this);
            mapView.setGridOpacity(display.getInt("gridOpacity", 255));
            mapView.setAntialiasGrid(display.getBoolean("gridAntialias", true));
            mapView.setGridColor(new Color(display.getInt("gridColor",
                    MapView.DEFAULT_GRID_COLOR.getRGB())));
            mapView.setShowGrid(display.getBoolean("showGrid", false));
            JViewport mapViewport = new JViewport();
            mapViewport.setView(mapView);
            mapViewport.addChangeListener(this);
            mapScrollPane.setViewport(mapViewport);
            setCurrentPointerState(PS_PAINT);

            currentMap.addMapChangeListener(this);

            gridMenuItem.setState(mapView.getShowGrid());
            coordinatesMenuItem.setState(
                    mapView.getMode(MapView.PF_COORDINATES));

            tileCoordsLabel.setText(String.valueOf(currentMap.getWidth() - 1)
                    + ", " + (currentMap.getHeight() - 1));
            tileCoordsLabel.setPreferredSize(null);
            Dimension size = tileCoordsLabel.getPreferredSize();
            tileCoordsLabel.setText(" ");
            tileCoordsLabel.setMinimumSize(new Dimension(20, 50));
            tileCoordsLabel.setPreferredSize(size);
            zoomLabel.setText(
                    String.valueOf((int) (mapView.getZoom() * 100)) + "%");

            // Get the first non-null tile from the first tileset containing
            // non-null tiles.
            Vector<TileSet> tilesets = currentMap.getTilesets();
            Tile firstTile = null;
            if (!tilesets.isEmpty()) {
                Iterator<TileSet> it = tilesets.iterator();
                while (it.hasNext() && firstTile == null) {
                    firstTile = it.next().getFirstTile();
                }
            }
            setCurrentTile(firstTile);

            currentMap.addLayerSpecial(cursorHighlight);
        }

        zoomInAction.setEnabled(mapLoaded);
        zoomOutAction.setEnabled(mapLoaded);
        zoomNormalAction.setEnabled(mapLoaded && mapView.getZoomLevel() !=
                MapView.ZOOM_NORMALSIZE);

        /*
        if (miniMap != null && currentMap != null) {
            miniMap.setView(MapView.createViewforMap(currentMap));
        }
        */

        undoHandler.discardAllEdits();
        updateLayerTable();
        updateTitle();
    }

    public void setCurrentLayer(int index) {
        if (currentMap != null) {
            int totalLayers = currentMap.getTotalLayers();
            if (totalLayers > index && index >= 0) {
                /*
                if (paintEdit != null) {
                    MapLayer layer = getCurrentLayer();
                    try {
                        MapLayer endLayer =
                            paintEdit.getStart().createDiff(layer);
                        if (endLayer != null) {
                            endLayer.setId(layer.getId());
                            endLayer.setOffset(layer.getBounds().x,layer.getBounds().y);
                        }
                        paintEdit.end(endLayer);
                        undoSupport.postEdit(paintEdit);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                */
                currentLayer = index;
                layerTable.changeSelection(totalLayers - currentLayer - 1, 0,
                        false, false);
            }
        }
    }

    /**
     * Changes the currently selected tile.
     *
     * @param tile the new tile to be selected
     */
    public void setCurrentTile(Tile tile) {
        resetBrush();

        if (currentTile != tile) {
            currentTile = tile;
            if (currentBrush instanceof ShapeBrush) {
                ((ShapeBrush) currentBrush).setTile(tile);
            }
            brushPreview.setBrush(currentBrush);
        }
    }

    private void setCurrentPointerState(int state) {
        /*
        if (currentPointerState == PS_MARQUEE && state != PS_MARQUEE) {
            // Special logic for selection
            if (marqueeSelection != null) {
                currentMap.removeLayerSpecial(marqueeSelection);
                marqueeSelection = null;
            }
        }
        */

        currentPointerState = state;

        // Select the matching button
        paintButton.setSelected(state == PS_PAINT);
        eraseButton.setSelected(state == PS_ERASE);
        pourButton.setSelected(state == PS_POUR);
        eyedButton.setSelected(state == PS_EYED);
        marqueeButton.setSelected(state == PS_MARQUEE);
        moveButton.setSelected(state == PS_MOVE);
        objectAddButton.setSelected(state == PS_ADDOBJ);
        objectRemoveButton.setSelected(state == PS_REMOVEOBJ);
        objectMoveButton.setSelected(state == PS_MOVEOBJ);

        // Set the matching cursor
        if (mapView != null) {
            switch (currentPointerState) {
                case PS_PAINT:
                case PS_ERASE:
                case PS_POINT:
                case PS_POUR:
                case PS_MARQUEE:
                    mapView.setCursor(curDefault);
                    break;
                case PS_EYED:
                    mapView.setCursor(curEyed);
                    break;
            }
        }
    }

    /**
     * Starts Tiled.
     *
     * @param args the first argument may be a map file
     */
    public static void main(String[] args) {
        MapEditor editor = new MapEditor();

        if (args.length > 0) {
            File file = new File(args[0]);

            try {
                editor.loadMap(file.getCanonicalPath());
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (prefs.node("io").getBoolean("autoOpenLast", false)) {
            // Load last map if it still exists
            java.util.List<String> recent = TiledConfiguration.getRecentFiles();
            if (!recent.isEmpty()) {
                String filename = recent.get(0);
                if (new File(filename).exists()) {
                    editor.loadMap(filename);
                }
            }
        }
    }
}

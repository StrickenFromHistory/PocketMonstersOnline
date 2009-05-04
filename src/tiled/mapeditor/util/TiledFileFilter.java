/*
 *  Tiled Map Editor, (c) 2004-2006
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  Adam Turk <aturk@biggeruniverse.com>
 *  Bjorn Lindeijer <bjorn@lindeijer.nl>
 */

package tiled.mapeditor.util;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;

import tiled.io.PluggableMapIO;
import tiled.io.xml.XMLMapWriter;
import tiled.mapeditor.Resources;

/**
 * @version $Id$
 */
public class TiledFileFilter extends ConfirmableFileFilter
{
    public static final int FILTER_EXT  = 0;
    public static final int FILTER_TMX  = 1;
    public static final int FILTER_TSX  = 2;
    public static final int FILTER_BOTH = 3;
    public static final int FILTER_PLUG = 4;

    private String desc;
    private LinkedList<String> exts;
    private PluggableMapIO pmio;
    private int type = FILTER_EXT;

    private static final String FILETYPE_TILED = Resources.getString("general.filetype.tiled");
    private static final String FILETYPE_TMX = Resources.getString("general.filetype.tiledmap");
    private static final String FILETYPE_TSX = Resources.getString("general.filetype.tiledtileset");
    private static final String FILETYPE_EXT = Resources.getString("general.filetype.byextension");

    public TiledFileFilter() {
        desc = FILETYPE_TILED;
        exts = new LinkedList<String>();
        exts.add("tmx");
        exts.add("tmx.gz");
        exts.add("tsx");
        pmio = new XMLMapWriter();
    }

    public TiledFileFilter(int filter) {
        exts = new LinkedList<String>();
        desc = "";
        type = filter;

        if ((filter & FILTER_TMX) != 0) {
            desc = FILETYPE_TMX;
            exts.add("tmx");
            exts.add("tmx.gz");
            pmio = new XMLMapWriter();
        }

        if ((filter & FILTER_TSX) != 0) {
            desc += FILETYPE_TSX;
            exts.add("tsx");
            if (pmio == null) {
                pmio = new XMLMapWriter();
            }
        }

        if (filter == FILTER_EXT) {
            desc = FILETYPE_EXT;
        }
    }

    public TiledFileFilter(PluggableMapIO p) throws Exception {
        exts = new LinkedList<String>();
        pmio = p;
        buildFilter(p.getFilter(), p.getName());
    }

    public TiledFileFilter(String filter, String desc) {
        exts = new LinkedList<String>();
        buildFilter(filter, desc);
    }

    private void buildFilter(String filter, String desc) {
        this.desc = desc;
        String[] extensions = filter.split(",");
        for (String extension : extensions) {
            exts.add(extension.substring(extension.indexOf('.') + 1));
        }
    }

    public void setDescription(String description) {
        desc = description;
    }

    public void addExtention(String extension) {
        exts.add(extension);
    }

    public PluggableMapIO getPlugin() {
        return pmio;
    }

    public String getDefaultExtension() {
        if (!exts.isEmpty()) {
            return exts.getFirst();
        }
        else {
            return null;
        }
    }

    public int getType() {
        return type;
    }

    public boolean accept(File file) {
        // todo: Verify that the "!file.exists()" check is rather weird.
        if (type != FILTER_EXT && (file.isFile() || !file.exists())) {
            String fileName = file.getPath().toLowerCase();

            for (String ext : exts) {
                if (fileName.endsWith("." + ext)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    public String getDescription() {
        StringBuffer filter = new StringBuffer();

        if (!exts.isEmpty()) {
            filter.append(" (");
            Iterator itr = exts.iterator();
            while (itr.hasNext()) {
                filter.append("*.").append((String) itr.next());
                if (itr.hasNext()) {
                    filter.append(",");
                }
            }

            filter.append(")");
        }

        return desc + filter;
    }
}

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

package tiled.mapeditor.plugin;

import java.io.File;
import java.io.FileFilter;

import tiled.io.MapReader;
import tiled.io.MapWriter;
import tiled.io.PluggableMapIO;
import tiled.io.PluginLogger;

/**
 * A true "plugin" implementation that handles both reading and
 * writing as the case may be. Instantiated for every reader/writer pair
 * by the {@link PluginClassLoader} when all plugins are loaded.
 */
public class TiledPlugin implements PluggableMapIO, FileFilter
{
    private MapReader reader;
    private MapWriter writer;

    /**
     * Instantiates a new plugin to be used internally by Tiled.
     * 
     * @param reader The {@link MapReader} implementor for the plugin
     * @param writer The {@link MapWriter} implementor for the plugin
     */
    public TiledPlugin(MapReader reader, MapWriter writer) {
        this.reader = reader;
        this.writer = writer;
    }
    
    /**
     * Convenience constructor if only a reader exists
     * 
     * @param reader
     */
    public TiledPlugin(MapReader reader) {
        this(reader, null);
    }
    
    /**
     * Convenience constructor if only a writer exists
     * 
     * @param writer
     */
    public TiledPlugin(MapWriter writer) {
        this(null, writer);
    }
    
    /**
     * Returns the filters of both the reader and writer as a
     * comma delimited String.
     * 
     * @see tiled.io.PluggableMapIO#getFilter()
     * @return String A comma delimited String of the filtered file extensions
     */
    public String getFilter() throws Exception {
        String filter = "";

        if (reader != null) {
            filter += reader.getFilter();
        }
        
        if (writer != null) {
            filter += (filter.length() > 0 ? "," : "") + writer.getFilter();
        }
        
        return filter;
    }

    /**
     * Returns the name of the plugin as set in the reader,
     * or, if the reader does not exist, from the writer.
     * 
     * @see PluggableMapIO#getName()
     */
    public String getName() {
        if (reader != null) {
            return reader.getName();
        }
        
        return writer.getName();
    }

    public String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getPluginPackage() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean accept(File pathname) {
        return reader != null && reader.accept(pathname) ||
                writer != null && writer.accept(pathname);
    }

    
    
    /* (non-Javadoc)
     * @see tiled.io.PluggableMapIO#setErrorStack(java.util.Stack)
     */
    public void setLogger(PluginLogger logger) {
        // TODO Auto-generated method stub
    }
    
    public MapReader getReader() {
        return reader;
    }
    
    public MapWriter getWriter() {
        return writer;
    }
}

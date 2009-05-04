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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Hashtable;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.swing.JFrame;
import javax.swing.ProgressMonitor;

import tiled.io.MapReader;
import tiled.io.MapWriter;
import tiled.io.PluggableMapIO;
import tiled.util.TiledConfiguration;

/**
 * The plugin class loader searches and loads available reader and writer
 * plugins.
 */
public final class PluginClassLoader extends URLClassLoader
{
    private final Vector plugins;
    private final Vector readers, writers;
    private final Hashtable<String, String> readerFormats;
    private final Hashtable<String, String> writerFormats;
    private static PluginClassLoader instance;

    private PluginClassLoader() {
        super(new URL[0]);
        plugins = new Vector();
        readers = new Vector();
        writers = new Vector();
        readerFormats = new Hashtable();
        writerFormats = new Hashtable();
    }

    public static synchronized PluginClassLoader getInstance() {
        if (instance == null) {
            instance = new PluginClassLoader();
        }
        return instance;
    }

    public void readPlugins(String base, JFrame parent) throws Exception {
        String baseURL = base;
        ProgressMonitor monitor;

        if (base == null) {
            baseURL = TiledConfiguration.root().get("pluginsDir", "plugins");
        }

        File dir = new File(baseURL);
        if (!dir.exists() || !dir.canRead()) {
            //FIXME: removed for webstart
            //throw new Exception(
            //        "Could not open directory for reading plugins: " +
            //        baseURL);
            return;
        }

        int total = 0;
        File[] files = dir.listFiles();
        for (File file : files) {
            String aPath = file.getAbsolutePath();
            if (aPath.endsWith(".jar")) {
                total++;
            }
        }

        // Start the progress monitor
        monitor = new ProgressMonitor(
                parent, "Loading plugins", "", 0, total - 1);
        monitor.setProgress(0);
        monitor.setMillisToPopup(0);
        monitor.setMillisToDecideToPopup(0);

        for (int i = 0; i < files.length; i++) {
            String aPath = files[i].getAbsolutePath();
            String aName =
                aPath.substring(aPath.lastIndexOf(File.separatorChar) + 1);

            // Skip non-jar files.
            if (!aPath.endsWith(".jar")) {
                continue;
            }

            try {
                monitor.setNote("Reading " + aName + "...");
                JarFile jf = new JarFile(files[i]);

                monitor.setProgress(i);

                if (jf.getManifest() == null)
                    continue;

                String readerClassName =
                    jf.getManifest().getMainAttributes().getValue(
                            "Reader-Class");
                String writerClassName =
                    jf.getManifest().getMainAttributes().getValue(
                            "Writer-Class");

                Class readerClass = null;
                Class writerClass = null;

                // Verify that the jar has the necessary files to be a
                // plugin
                if (readerClassName == null && writerClassName == null) {
                    continue;
                }

                monitor.setNote("Loading " + aName + "...");
                addURL(new File(aPath).toURI().toURL());

                if (readerClassName != null) {
                    JarEntry reader = jf.getJarEntry(
                            readerClassName.replace('.', '/') + ".class");

                    if (reader != null) {
                        readerClass = loadFromJar(
                                jf, reader, readerClassName);
                    }else System.err.println("Manifest entry "+readerClassName+" does not match any class in the jar.");
                }
                if (writerClassName != null) {
                    JarEntry writer = jf.getJarEntry(
                            writerClassName.replace('.', '/') + ".class");

                    if (writer != null) {
                        writerClass = loadFromJar(
                                jf, writer, writerClassName);
                    } else System.err.println("Manifest entry "+writerClassName+" does not match any class in the jar.");
                }

                boolean bPlugin = false;
                if (isReader(readerClass)) {
                    bPlugin = true;
                }
                if (isWriter(writerClass)) {
                    bPlugin = true;
                }

                if (bPlugin) {
                    if (readerClass != null) _add(readerClass);
                    if (writerClass != null) _add(writerClass);
                    //System.out.println(
                    //        "Added " + files[i].getCanonicalPath());
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public MapReader[] getReaders() {
        return (MapReader[]) readers.toArray(new MapReader[readers.size()]);
    }

    public MapWriter[] getWriters() {
        return (MapWriter[]) writers.toArray(new MapWriter[writers.size()]);
    }

    public Object getReaderFor(String file) throws Exception {
        for (String key : readerFormats.keySet()) {
            String ext = key.substring(1);
            if (file.toLowerCase().endsWith(ext)) {
                return loadClass(readerFormats.get(key)).newInstance();
            }
        }
        throw new Exception(
                "No reader plugin exists for this file type.");
    }

    public Object getWriterFor(String file) throws Exception {
        for (String key : writerFormats.keySet()) {
            String ext = key.substring(1);
            if (file.toLowerCase().endsWith(ext)) {
                return loadClass(writerFormats.get(key)).newInstance();
            }
        }
        throw new Exception(
                "No writer plugin exists for this file type.");
    }

    public Class loadFromJar(JarFile jf, JarEntry je, String className)
        throws IOException
    {
        byte[] buffer = new byte[(int)je.getSize()];
        int n;

        InputStream in = jf.getInputStream(je);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while ((n = in.read(buffer)) > 0) {
            baos.write(buffer, 0, n);
        }
        buffer = baos.toByteArray();

        if (buffer.length < je.getSize()) {
            throw new IOException(
                    "Failed to read entire entry! (" + buffer.length + "<" +
                    je.getSize() + ")");
        }

        return defineClass(className, buffer, 0, buffer.length);
    }

    private static boolean doesImplement(Class klass, String interfaceName)
        throws Exception
    {
        if (klass == null) {
            return false;
        }

        Class[] interfaces = klass.getInterfaces();
        for (Class anInterface : interfaces) {
            String name = anInterface.toString();
            if (name.substring(name.indexOf(' ') + 1).equals(interfaceName)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isReader(Class klass) throws Exception {
        return doesImplement(klass, "tiled.io.MapReader");
    }

    private static boolean isWriter(Class writerClass) throws Exception {
        return doesImplement(writerClass, "tiled.io.MapWriter");
    }

    private void _add(Class klass) throws Exception{
        try {
            PluggableMapIO p = (PluggableMapIO) klass.newInstance();
            String clname = klass.toString();
            clname = clname.substring(clname.indexOf(' ') + 1);
            String filter = p.getFilter();
            String[] extensions = filter.split(",");

            if (isReader(klass)) {
                for (String extension : extensions) {
                    readerFormats.put(extension, clname);
                }
                readers.add(p);
            } else if (isWriter(klass)) {
                for (String extension : extensions) {
                    writerFormats.put(extension, clname);
                }
                writers.add(p);
            }
        } catch (NoClassDefFoundError e) {
            System.err.println("**Failed loading plugin: " + e.toString());
        }
    }
}

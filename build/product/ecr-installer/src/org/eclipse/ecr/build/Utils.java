/*
 * Copyright (c) 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     bstefanescu
 */
package org.eclipse.ecr.build;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class Utils {

    public static void writeTo(String data, File file) throws IOException {
        FileWriter w = new FileWriter(file);
        try {
            w.write(data);
        } finally {
            w.close();
        }
    }

    public static void copyFile(File src, File dst) throws IOException {
        FileInputStream in = new FileInputStream(src);
        try {
            writeTo(in, dst);
        } finally {
            in.close();
        }
    }

    public static void writeTo(String data, File file, String charset) throws IOException {
        FileOutputStream out = new FileOutputStream(file);
        BufferedWriter w = new BufferedWriter(new OutputStreamWriter(out, charset));
        try {
            w.write(data);
        } finally {
            w.close();
        }
    }

    public static void writeTo(InputStream in, File file) throws IOException {
        FileOutputStream out = new FileOutputStream(file);
        try {
            byte[] buf = new byte[1024*1024];
            int r = -1;
            while ((r = in.read(buf)) > -1) {
                if (r > 0) {
                    out.write(buf, 0, r);
                }
            }
        } catch (IOException e) {
            file.delete();
            throw e;
        } finally {
            out.close();
        }
    }

    public static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buf = new byte[1024*1024];
        int r = -1;
        while ((r = in.read(buf)) > -1) {
            if (r > 0) {
                out.write(buf, 0, r);
            }
        }
    }

    public static void unzip(File zip, File toDir) throws IOException {
        ZipFile zf = new ZipFile(zip);
        try {
            Enumeration<? extends ZipEntry> entries = zf.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                File file = new File(toDir, entry.getName());
                file.getParentFile().mkdirs();
                if (entry.isDirectory()) {
                    file.mkdir();
                } else {
                    InputStream  in = zf.getInputStream(entry);
                    writeTo(in, file);
                    in.close();
                }
            }
        } finally {
            zf.close();
        }
    }

    public static void deleteTree(File dir) throws IOException {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteTree(file);
                }
            }
        }
        dir.delete();
    }

    public static File toFile(URL url) {
        File f;
        try {
            f = new File(url.toURI());
        } catch(URISyntaxException e) {
            f = new File(url.getPath());
        }
        return f;
    }

    public static void zip(File zipFile, File[] files) throws IOException {
        if (files == null) {
            return;
        }
        ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(zipFile));
        try {
            for (File file : files) {
                _zip(file.getName(), file, zip);
            }
        } finally {
            zip.finish();
            zip.close();
        }
    }

    public static void zip(File zipFile, File file) throws IOException {
        if (file.isDirectory()) {
            zipDir(zipFile, file, true);
        } else {
            zip(zipFile, new File[] {file});
        }
    }

    public static void zipDir(File zipFile, File dir, boolean includeRoot) throws IOException {
        if (!dir.isDirectory()) {
            return;
        }
        ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(zipFile));
        try {
            if (includeRoot) {
                _zip(dir.getName(), dir, zip);
            } else {
                File[] files = dir.listFiles();
                if (files != null) {
                    for (File file : files) {
                        _zip(file.getName(), file, zip);
                    }
                }
            }
        } finally {
            zip.finish();
            zip.close();
        }
    }

    protected static void _zip(String entryName, File file, ZipOutputStream out) throws IOException {
        if (file.isDirectory()) {
            entryName += '/';
            ZipEntry zentry = new ZipEntry(entryName);
            out.putNextEntry(zentry);
            out.closeEntry();
            File[] files = file.listFiles();
            if (files != null) {
                for (int i = 0, len = files.length; i < len; i++) {
                    _zip(entryName + files[i].getName(), files[i], out);
                }
            }
        } else {
            InputStream in = null;
            try {
                in = new FileInputStream(file);
                ZipEntry zentry = new ZipEntry(entryName);
                out.putNextEntry(zentry);
                // Transfer bytes from the input stream to the ZIP file
                copy(in, out);
                out.closeEntry();
            } finally {
                if (in != null) {
                    in.close();
                }
            }
        }
    }

    public static String read(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        int size = in.available();
        if (size <1 || size > 64*1024) {
            size = 64*1024;
        }
        byte[] buffer = new byte[size];
        try {
            int read;
            while ((read = in.read(buffer)) != -1) {
                sb.append(new String(buffer, 0, read));
            }
        } finally {
            in.close();
        }
        return sb.toString();
    }

}

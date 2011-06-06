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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class Installer {

    protected File installDir;

    protected URL repoUrl;

    protected Graph graph;

    protected ProfileManager profileMgr;

    public Installer(File installDir, String repoUrl) throws Exception {
        this.installDir = installDir;
        try {
            this.repoUrl = new URL(repoUrl);
        } catch (MalformedURLException e) {
            this.repoUrl = new File(repoUrl).toURI().toURL();
        }
        String url = this.repoUrl.toExternalForm();
        String profilesUrl = url;
        if (url.endsWith("/")) {
            url = url + "content.jar";
            profilesUrl = profilesUrl + "profiles.xml";
        } else {
            url = url + "/content.jar";
            profilesUrl = profilesUrl + "/profiles.xml";
        }
        graph = new GraphLoader().loadZip(new URL(url));
        profileMgr = new ProfileManager(profilesUrl);
    }

    /**
     * @return the installDir
     */
    public File getInstallDir() {
        return installDir;
    }

    public Set<Node> getResolvedNodes(String profile) throws Exception {
        return graph.getResolver().resolveProfile(profileMgr, profile);
    }

    public void install(String profile) throws Exception {
        install(profile, false);
    }

    public void install(String profile, boolean zipIt) throws Exception {
        if (profile == null) {
            profile = "default";
        }
        System.out.println("Using profile: "+profile);
        installDir.mkdirs();
        Set<Node> nodes = getResolvedNodes(profile);
        List<Node> sortedNodes = new ArrayList<Node>(nodes);
        Collections.sort(sortedNodes);

        System.out.println("Installing plugins");
        File dir = new File(installDir, "plugins");
        dir.mkdirs();
        if (repoUrl.getProtocol().startsWith("http")) {
            downloadPlugins(sortedNodes, dir);
        } else {
            copyPlugins(sortedNodes, dir);
        }
        System.out.println("Generating configuration");
        Map<String, String> vars = generateConfigFile(sortedNodes);
        System.out.println("Generating launch scripts");
        copyTemplateResource("scripts/run.sh", "run.sh", vars).setExecutable(true);
        copyTemplateResource("scripts/run.bat", "run.bat", vars).setExecutable(true);
        copyResource("epl-v10.html", "epl-v10.html");
        copyResource("notice.html", "notice.html");
        File target = installDir;
        if (zipIt) {
            System.out.println("Zipping");
            target = new File(installDir.getParentFile(), installDir.getName()+".zip");
            Utils.zipDir(target, installDir, true);
            Utils.deleteTree(installDir);
        }
        System.out.println("Done.");
        System.out.println();
        System.out.println("Product installed in "+target);
        System.out.println();
    }

    public void copyPlugins(Collection<Node> nodes, File toDir) throws Exception {
        File plugins = new File(Utils.toFile(repoUrl), "plugins");
        for (Node node : nodes) {
            String name = node.getFileName();
            Utils.copyFile(new File(plugins, name), new File(toDir, name));
        }
    }

    public void downloadPlugins(Collection<Node> nodes, File toDir) throws Exception {
        Downloader downloader = new Downloader();
        String plugins = repoUrl.toExternalForm();
        if (plugins.endsWith("/")) {
            plugins = plugins +"plugins/";
        } else {
            plugins = plugins +"/plugins/";
        }
        for (Node node : nodes) {
            String name = node.getFileName();
            URL url = new URL(plugins+name);
            downloader.download(url, new File(toDir, name));
        }
        downloader.awaitTermination();
    }


    public Map<String,String> generateConfigFile(Collection<Node> nodes) throws IOException {
        HashMap<String, String> vars = new HashMap<String, String>();
        File file = new File(installDir, "configuration");
        file.mkdirs();
        file = new File(file, "config.ini");
        String sysBundle = null;
        StringBuilder bundles = new StringBuilder();
        for (Node node : nodes) {
            String name = node.getFileName();
            if (name.startsWith("org.eclipse.osgi_")) {
                sysBundle = "file\\:plugins/"+name;
                vars.put("system.bundle", name);
            } else {
                if (name.startsWith("org.eclipse.equinox.launcher_")) {
                    vars.put("launcher.bundle", name);
                }
                bundles.append(",reference\\:file\\:").append(name);
                int startLevel = node.getStartLevel();
                if (startLevel > -1) {
                    if (node.isAutostart()) {
                        bundles.append("@").append(startLevel).append("\\:start");
                    } else {
                        bundles.append("@").append(startLevel);
                    }
                } else if (node.isAutostart()) {
                    bundles.append("@start");
                }
            }
        }
        if (sysBundle == null) {
            throw new RuntimeException("No system bundle found!");
        }
        if (bundles.length() == 0) {
            throw new RuntimeException("No bundles found!");
        }

        FileWriter writer = new FileWriter(file);
        try {
            writer.write("osgi.configuration.cascaded=false\r\n");
            writer.write("osgi.framework="+sysBundle);
            writer.write("\r\n");
            writer.write("osgi.bundles="+bundles.substring(1));
            writer.write("\r\n");
        } finally {
            writer.close();
        }
        return vars;
    }

    protected void copyResource(String path, String toName) throws IOException {
        InputStream in = Installer.class.getClassLoader().getResourceAsStream(path);
        File file = new File(installDir, toName);
        try {
            Utils.writeTo(in, file);
            file.setExecutable(true);
        } finally {
            in.close();
        }
    }

    protected File copyTemplateResource(String path, String toName, Map<String,String> vars) throws IOException {
        InputStream in = Installer.class.getClassLoader().getResourceAsStream(path);
        String data = Utils.read(in);
        data = Vars.expand(data, vars);
        File file = new File(installDir, toName);
        Utils.writeTo(data, file);
        return file;
    }



}

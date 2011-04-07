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
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.ecr.build.Profile.Unit;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class ProfileManager {

    protected URL config;

    protected Map<String, Profile> profiles;

    protected Set<String> systemPackages;

    public ProfileManager(String configFile) throws Exception {
        systemPackages = new HashSet<String>();
        profiles = new HashMap<String, Profile>();
        try {
            config = new URL(configFile);
        } catch (MalformedURLException e) {
            config = new File(configFile).toURI().toURL();
        }
        InputStream in = config.openStream();
        try {
            loadProfiles(in);
        } finally {
            in.close();
        }
    }

    protected void loadProfiles(InputStream in) throws Exception {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(in);
            loadProfiles(document.getDocumentElement());
        } catch (IOException e) {
            throw e;
        } catch (Throwable t) {
            throw new IOException("Failed to load profiles from stream", t);
        }
    }

    protected void loadProfiles(Element root) {
        Node node = root.getFirstChild();
        while (node != null) {
            if (Node.ELEMENT_NODE == node.getNodeType()) {
                String name = node.getNodeName();
                if ("profile".equals(name)) {
                    Element element = (Element)node;
                    Profile profile = new Profile(element.getAttribute("name"));
                    loadProfileContent(profile, element);
                    profiles.put(profile.getName(), profile);
                } else if ("system".equals(name)) {
                    loadSystemPackages((Element)node);
                }
            }
            node = node.getNextSibling();
        }
    }

    protected void loadSystemPackages(Element element) {
        Node node = element.getFirstChild();
        while (node != null) {
            if (Node.ELEMENT_NODE == node.getNodeType()) {
                if ("package".equals(node.getNodeName())) {
                    Element pkgEl = (Element)node;
                    systemPackages.add(pkgEl.getAttribute("name"));
                }
            }
            node = node.getNextSibling();
        }
    }


    protected void loadProfileContent(Profile profile, Element element) {
        Node node = element.getFirstChild();
        while (node != null) {
            if (Node.ELEMENT_NODE == node.getNodeType()) {
                if ("require".equals(node.getNodeName())) {
                    Element reqEl = (Element)node;
                    profile.addRequire(reqEl.getTextContent().trim());
                } else if ("artifact".equals(node.getNodeName())) {
                    Element artifactEl = (Element)node;
                    String name = artifactEl.getAttribute("name");
                    boolean autostart = Boolean.parseBoolean(artifactEl.getAttribute("autostart"));
                    String startLevel = artifactEl.getAttribute("startLevel");
                    Unit unit = new Unit(name, autostart);
                    if (startLevel != null && startLevel.length() > 0) {
                        unit.setStartLevel(Integer.parseInt(startLevel));
                    }
                    profile.addInstallableUnit(unit);
                }
            }
            node = node.getNextSibling();
        }
    }

    public Map<String, Profile> getProfiles() {
        return profiles;
    }

    public Profile getProfile(String name) {
        return profiles.get(name);
    }

    public Set<Unit> getInstallableUnits(String expr) {
        if (expr.indexOf(',') > -1) {
            Set<Unit> artifacts = new HashSet<Unit>();
            String[] ar = expr.split(",");
            for (String key : ar) {
                Profile profile = getProfile(key);
                if (profile == null) {
                    throw new ResolveException("Profile could not be found: "+key);
                }
                artifacts.addAll(getInstallableUnits(profile));
            }
            return artifacts;
        } else {
            Profile profile = getProfile(expr);
            if (profile == null) {
                throw new ResolveException("Profile could not be found: "+expr);
            }
            return getInstallableUnits(profile);
        }
    }

    public Set<Unit> getInstallableUnits(Profile profile) {
        Set<Unit> artifacts = new HashSet<Unit>();
        artifacts.addAll(profile.getInstallableUnits());
        for (String req : profile.getRequires()) {
            Profile p = getProfile(req);
            if (p == null) {
                throw new ResolveException("Profile could not be resolved: "+profile+", missing requirement "+req);
            }
            artifacts.addAll(getInstallableUnits(p));
        }
        return artifacts;
    }

    public String[] getSystemPackages() {
        return systemPackages.toArray(new String[systemPackages.size()]);
    }

}
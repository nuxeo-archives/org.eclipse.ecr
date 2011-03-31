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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class Node extends Artifact {

    protected HashSet<Artifact> exports;

    protected List<Artifact> imports;

    protected Set<Artifact> requires;

    protected boolean autostart;

    protected int startLevel = -1; // the default start level

    public Node(String id) {
        this (id, null);
    }

    public Node(String id, String version) {
        super (id, version);
        exports = new HashSet<Artifact>();
        imports = new ArrayList<Artifact>();
        requires = new HashSet<Artifact>();
    }

    public List<Artifact> getImports() {
        return imports;
    }

    public Set<Artifact> getExports() {
        return exports;
    }

    public Set<Artifact> getRequires() {
        return requires;
    }

    public void addExport(Artifact artifact) {
        exports.add(artifact);
    }

    public void addImport(Artifact artifact) {
        imports.add(artifact);
    }

    public void addRequire(Artifact artifact) {
        requires.add(artifact);
    }

    public boolean isExporting(Artifact artifact) {
        return exports.contains(artifact);
    }

    public boolean isAutostart() {
        return autostart;
    }

    public void setAutostart(boolean autostart) {
        this.autostart = autostart;
    }

    public int getStartLevel() {
        return startLevel;
    }

    public void setStartLevel(int startLevel) {
        this.startLevel = startLevel;
    }

    public String getFileName() {
        return new StringBuilder(name).append("_").append(version).append(".jar").toString();
    }
}


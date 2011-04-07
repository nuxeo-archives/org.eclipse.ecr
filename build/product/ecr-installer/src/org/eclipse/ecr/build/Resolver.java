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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.ecr.build.Profile.Unit;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class Resolver {

    protected Graph graph;

    protected Set<Node> artifacts;

    protected String[] systemPackages;


    public Resolver(Graph graph) {
        this.graph = graph;
        this.artifacts = new HashSet<Node>();
    }

    public Graph getGraph() {
        return graph;
    }

    public void setSystemPackages(String[] systemPackages) {
        this.systemPackages = systemPackages;
    }

    public String[] getSystemPackages() {
        return systemPackages;
    }

    public Set<Node> resolveProfile(ProfileManager profileMgr, String profile) {
        systemPackages = profileMgr.getSystemPackages();
        Set<Unit> artifacts = profileMgr.getInstallableUnits(profile);
        for (Unit unit : artifacts) {
            resolve(unit);
        }
        return getResolvedNodes();
    }

    public boolean resolve(String name) throws ResolveException {
        Node node = graph.getNodeByName(name);
        if (node == null) {
            throw new ResolveException("Artifact was not found in graph: "+name);
        }
        return resolve(node);
    }

    public boolean resolve(Unit unit) throws ResolveException {
        Node node = graph.getNodeByName(unit.getName());
        if (node == null) {
            throw new ResolveException("Artifact was not found in graph: "+unit.getName());
        }
        node.setAutostart(unit.isAutostart());
        node.setStartLevel(unit.getStartLevel());
        return resolve(node);
    }

    public boolean resolve(Artifact artifact) throws ResolveException {
        if (artifacts.contains(artifact)) {
            return false;
        }
        Node node = graph.findNode(artifact);
        if (node == null) {
            if (!artifact.isOptional()) {
                throw new ResolveException("Artifact was not found in graph: "+artifact);
            }
            return false;
        }
        return resolve(node);
    }

    public boolean resolve(Node node) throws ResolveException {
        if (!artifacts.add(node)) {
            return false;
        }
        for (Artifact imp : node.getImports()) {
            if (ignoreImport(imp)) {
                continue;
            }
            Node req = graph.getNodeProviding(imp);
            if (req != null) {
                try {
                    resolve(req);
                } catch (ResolveException e) {
                    if (!imp.isOptional()) {
                        throw e;
                    }
                }
            } else if (!imp.isOptional()) {
                throw new ResolveException("Import not provided by any artifacts in graph: "+imp);
            }
        }
        for (Artifact required : node.getRequires()) {
            resolve(required);
        }
        return true;
    }

    public Set<Node> getResolvedNodes() {
        return artifacts;
    }

    public void reset() {
        artifacts = new HashSet<Node>();
    }

    public boolean ignoreImport(Artifact artifact) {
        if (systemPackages == null) {
            return false;
        }
        String name = artifact.getName();
        for (String pkg : systemPackages) {
            if (name.startsWith(pkg)) {
                return true;
            }
        }
        return false;
    }
}

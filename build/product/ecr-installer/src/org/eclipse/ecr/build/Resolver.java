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

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class Resolver {

    protected Graph graph;

    protected Set<Node> artifacts;

    public Resolver(Graph graph) {
        this.graph = graph;
        this.artifacts = new HashSet<Node>();
    }

    public Graph getGraph() {
        return graph;
    }

    public boolean resolve(String name) throws ResolveException {
        Node node = graph.getNodeByName(name);
        if (node == null) {
            throw new ResolveException("Artifact was not found in graph: "+name);
        }
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
        String name = artifact.getName();
        if (name.startsWith("javax.swing")
                || name.startsWith("javax.xml.ws")
                || name.startsWith("javax.jws")
                || name.startsWith("com.sun.xml.ws")
                || name.startsWith("javax.xml.bind")
                || name.startsWith("javax.naming")
                || name.startsWith("javax.management")
                || name.startsWith("javax.sql")
                || name.startsWith("javax.imageio")
                || name.startsWith("javax.net")
                || name.startsWith("javax.script")
                || name.startsWith("javax.security")) {
            return true;
        }
        return false;
    }
}

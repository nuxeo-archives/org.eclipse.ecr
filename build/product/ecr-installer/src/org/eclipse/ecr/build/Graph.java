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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class Graph {

    protected Map<String, Node> nodes;
    protected Map<String, List<Node>> nodesByName;
    protected Map<String, List<Node>> exports; // TODO sorted set? to use versions?

    public Graph() {
        nodes = new HashMap<String, Node>();
        exports = new HashMap<String, List<Node>>();
        nodesByName = new HashMap<String, List<Node>>();
    }

    public Map<String,Node> getNodes() {
        return nodes;
    }

    public void addNode(Node node) {
        nodes.put(node.getId(), node);
        List<Node> list = nodesByName.get(node.getName());
        if (list == null) {
            list = new ArrayList<Node>();
            nodesByName.put(node.getName(), list);
        }
        list.add(node);
        for (Artifact artifact : node.getExports()) {
            String name = artifact.getName();
            List<Node> providers = exports.get(name);
            if (providers == null) {
                providers = new ArrayList<Node>();
                exports.put(name, providers);
            }
            providers.add(node);
        }
    }

    public Node getNodeById(String id) {
        return nodes.get(id);
    }

    public Node findNode(Artifact artifact) {
//        String version = artifact.getVersion();
        //TODO
//        if (version == null || "".equals(version)) {
//        }
        return getNodeByName(artifact.name);
    }


    public Node getNodeByName(String name) {
        List<Node> list = nodesByName.get(name);
        return list != null ? list.get(0) : null;
    }

    public Resolver getResolver() {
        return new Resolver(this);
    }

    /**
     * TODO use version too
     * @param artifact
     * @return
     */
    public Node getNodeProviding(Artifact artifact) {
        List<Node> providers = exports.get(artifact.getName());
        if (providers == null || providers.isEmpty()) {
            return null;
        }
        //TODO use version
        return providers.get(0);
    }



}

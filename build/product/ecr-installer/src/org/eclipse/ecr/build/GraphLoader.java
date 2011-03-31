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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class GraphLoader {

    public Graph loadZip(File file) throws IOException {
        FileInputStream in = new FileInputStream(file);
        try {
            return loadZip(in);
        } finally {
            in.close();
        }
    }

    public Graph loadZip(URL url) throws IOException {
        InputStream in = url.openStream();
        try {
            return loadZip(in);
        } finally {
            in.close();
        }
    }

    public Graph loadXml(File file) throws IOException {
        FileInputStream in = new FileInputStream(file);
        try {
            return loadXml(in);
        } finally {
            in.close();
        }
    }

    public Graph loadXml(URL url) throws IOException {
        InputStream in = url.openStream();
        try {
            return loadXml(in);
        } finally {
            in.close();
        }
    }

    public Graph loadZip(InputStream in) throws IOException {
        ZipInputStream zin = new ZipInputStream(in);
        ZipEntry entry = zin.getNextEntry();
        if (entry == null) {
            throw new IOException("Invalid zip stream: no file entry found");
        }
        try {
            return loadXml(zin);
        } finally {
//            zin.closeEntry();
            zin.close();
        }
    }

    public Graph loadXml(InputStream in) throws IOException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(in);
            return load(document.getDocumentElement());
        } catch (IOException e) {
            throw e;
        } catch (Throwable t) {
            throw new IOException("Failed to load graph from stream", t);
        }
    }

    public Graph load(Element root) {
        org.w3c.dom.Node node = root.getFirstChild();
        while (node != null) {
            if (node.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                if ("units".equals(node.getNodeName())) {
                    return loadUnits((Element)node);
                }
            }
            node = node.getNextSibling();
        }
        return null;
    }

    protected Graph loadUnits(Element units) {
        Graph graph = new Graph();
        org.w3c.dom.Node node = units.getFirstChild();
        while (node != null) {
            if (node.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                if ("unit".equals(node.getNodeName())) {
                    loadUnit(graph, (Element)node);
                }
            }
            node = node.getNextSibling();
        }
        return graph;
    }

    protected void loadUnit(Graph graph, Element unit) {
        Node artifact = new Node(unit.getAttribute("id"), unit.getAttribute("version"));
        org.w3c.dom.Node node = unit.getFirstChild();
        while (node != null) {
            if (node.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                if ("provides".equals(node.getNodeName())) {
                    loadProvides(artifact, (Element)node);
                } else if ("requires".equals(node.getNodeName())) {
                    loadRequires(artifact, (Element)node);
                }
            }
            node = node.getNextSibling();
        }
        graph.addNode(artifact);
    }

    protected void loadProvides(Node artifact, Element provides) {
        org.w3c.dom.Node node = provides.getFirstChild();
        while (node != null) {
            if (node.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                if ("provided".equals(node.getNodeName())) {
                    Element element = (Element)node;
                    String ns = element.getAttribute("namespace");
                    if ("java.package".equals(ns)) {
                        artifact.addExport(
                                new Artifact(element.getAttribute("name"),
                                        element.getAttribute("version")));
                    }
                }
            }
            node = node.getNextSibling();
        }
    }

    protected void loadRequires(Node artifact, Element requires) {
        org.w3c.dom.Node node = requires.getFirstChild();
        while (node != null) {
            if (node.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                if ("required".equals(node.getNodeName())) {
                    Element element = (Element)node;
                    String ns = element.getAttribute("namespace");
                    if ("java.package".equals(ns)) {
                        Artifact imp = new Artifact(element.getAttribute("name"),
                                element.getAttribute("range"));
                        if ("true".equals(element.getAttribute("optional"))) {
                            imp.setOptional(true);
                        }
                        artifact.addImport(imp);
                    } else if ("osgi.bundle".equals(ns)) {
                        Artifact imp = new Artifact(element.getAttribute("name"),
                                element.getAttribute("range"));
                        if ("true".equals(element.getAttribute("optional"))) {
                            imp.setOptional(true);
                        }
                        artifact.addRequire(imp);
                    }
                }
            }
            node = node.getNextSibling();
        }
    }

    public static void main(String[] args) throws Exception {
        Graph g = new GraphLoader().loadZip(new File("/Users/bstefanescu/work/org.eclipse.ecr/build/repository/target/repository/content.jar"));

        System.out.println(g.nodes.size());
        System.out.println(g.nodes);

        Resolver resolver = g.getResolver();
        resolver.resolve("org.eclipse.ecr.web.jaxrs");

        System.out.println(resolver.getResolvedNodes());
    }

}

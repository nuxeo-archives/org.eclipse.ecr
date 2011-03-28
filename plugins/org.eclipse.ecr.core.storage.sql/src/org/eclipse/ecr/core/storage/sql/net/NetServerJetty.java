/*
 * Copyright (c) 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Florent Guillaume
 *     Hugues Malphettes
 */
package org.eclipse.ecr.core.storage.sql.net;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.eclipse.ecr.core.storage.sql.Activator;
import org.eclipse.ecr.core.storage.sql.BinaryManager;
import org.eclipse.ecr.core.storage.sql.Mapper;
import org.eclipse.ecr.core.storage.sql.RepositoryDescriptor.ServerDescriptor;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlet.ServletMapping;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * Network server for a repository. Can receive remote connections for
 * {@link Mapper} and {@link BinaryManager} access (depending on the registered
 * servlets).
 * <p>
 * Runs an embedded Jetty server. Version 7 and up.
 */
public class NetServerJetty extends NetServer {

    protected final Server server;
    protected final boolean manageServerHere;

    /**
     * Get the already created jetty server or create one.
     */
    protected NetServerJetty() {
    	BundleContext bundleContext = Activator.getInstance().getContext();
    	ServiceReference sr = bundleContext.getServiceReference(Server.class.getName());
    	if (sr != null) {
    		server = (Server)bundleContext.getService(sr);
    		manageServerHere = false;
    	} else {
    		server = new Server();
    		manageServerHere = true;
    	}
    }
    
	/**
	 * Start the underlying server
	 */
	protected void startServer() {
		if (manageServerHere) {
	        try {
	            server.start();
	        } catch (RuntimeException e) {
	            throw e;
	        } catch (Exception e) {
	            throw new RuntimeException(e);
	        }
		}
	}
	
	/**
	 * Shutdown the underlying server
	 */
	protected void stopServer() {
		if (manageServerHere) {
	        try {
	            server.stop();
	        } catch (RuntimeException e) {
	            throw e;
	        } catch (Exception e) {
	            throw new RuntimeException(e);
	        }
		}
	}


    protected String addRepositoryServer(ServerDescriptor serverDescriptor,
            String servletName, Servlet servlet, String path) {
        try {
            addConnector(serverDescriptor);
            String contextPath = getContextPath(serverDescriptor);
            ServletContextHandler context = addContext(contextPath);
            ServletHandler servletHandler = context.getServletHandler();
            ServletHolder servletHolder = new ServletHolder(servlet);
            servletHolder.setName(servletName);
            if (!path.startsWith("/")) {
                path = '/' + path;
            }
            servletHandler.addServletWithMapping(servletHolder, path);
            return "http://" + serverDescriptor.host + ':'
                    + serverDescriptor.port + contextPath + path;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void removeRepositoryServer(ServerDescriptor serverDescriptor,
            String servletName) {
        try {
            boolean stop = removeConnector(serverDescriptor);
            ServletContextHandler context = removeContext(getContextPath(serverDescriptor));
            ServletHandler servletHandler = context.getServletHandler();

            // remove servlet mapping
            LinkedList<ServletMapping> sml = new LinkedList<ServletMapping>(
                    Arrays.asList(servletHandler.getServletMappings()));
            for (Iterator<ServletMapping> it = sml.iterator(); it.hasNext();) {
                ServletMapping sm = it.next();
                if (sm.getServletName().equals(servletName)) {
                    it.remove();
                    break;
                }
            }
            servletHandler.setServletMappings(sml.toArray(new ServletMapping[0]));

            // remove servlet
            List<ServletHolder> sl = new LinkedList<ServletHolder>(
                    Arrays.asList(servletHandler.getServlets()));
            for (Iterator<ServletHolder> it = sl.iterator(); it.hasNext();) {
                ServletHolder s = it.next();
                if (s.getName().equals(servletName)) {
                    it.remove();
                    break;
                }
            }
            servletHandler.setServlets(sl.toArray(new ServletHolder[0]));

            if (stop) {
                shutDown();
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected Servlet getServlet(ServerDescriptor serverDescriptor, String servletName) {
    	ServletContextHandler context = getContext(getContextPath(serverDescriptor));
        ServletHandler handler = context.getServletHandler();
        ServletHolder holder = handler.getServlet(servletName);
        try {
            return holder.getServlet();
        } catch (ServletException e) {
            throw new Error("No such servlet " + serverDescriptor.getUrl() + ":" + servletName, e);
        }
    }

    protected String getContextPath(ServerDescriptor serverDescriptor) {
        String contextPath = serverDescriptor.path;
        if (!contextPath.startsWith("/")) {
            contextPath = '/' + contextPath;
        }
        return contextPath;
    }

    protected final Map<Connector, AtomicLong> connectorRefs = new HashMap<Connector, AtomicLong>();

    protected void addConnector(ServerDescriptor serverDescriptor)
            throws Exception {
        Connector connector = getConnector(serverDescriptor);
        if (connector == null) {
            connector = new SelectChannelConnector();//new SocketConnector();
            connector.setHost(serverDescriptor.host);
            connector.setPort(serverDescriptor.port);
            server.addConnector(connector);
            connector.start();
            connectorRefs.put(connector, new AtomicLong());
        }
        connectorRefs.get(connector).incrementAndGet();
    }

    /** Returns {@code true} if last connector removed. */
    protected boolean removeConnector(ServerDescriptor serverDescriptor)
            throws Exception {
        Connector connector = getConnector(serverDescriptor);
        if (connector == null) {
            throw new RuntimeException("Unknown connector for: "
                    + serverDescriptor);
        }
        long refs = connectorRefs.get(connector).decrementAndGet();
        if (refs == 0) {
            connectorRefs.remove(connector);
            connector.stop();
            List<Connector> cl = new LinkedList<Connector>(
                    Arrays.asList(server.getConnectors()));
            cl.remove(connector);
            server.setConnectors(cl.toArray(new Connector[0]));
            if (cl.size() == 0) {
                return true;
            }
        }
        return false;
    }

    protected Connector getConnector(ServerDescriptor serverDescriptor) {
        Connector[] connectors = server.getConnectors();
        if (connectors == null) {
            return null;
        }
        for (Connector c : connectors) {
            if (c.getHost().equals(serverDescriptor.host)
                    && c.getPort() == serverDescriptor.port) {
                return c;
            }
        }
        return null;
    }

    protected final Map<ServletContextHandler, AtomicLong> contextRefs = new HashMap<ServletContextHandler, AtomicLong>();

    protected ServletContextHandler addContext(String path) throws Exception {
    	ServletContextHandler context = getContext(path);
        if (context == null) {
            context = new ServletContextHandler(server, path, ServletContextHandler.SESSIONS);
            context.start();
            contextRefs.put(context, new AtomicLong());
        }
        contextRefs.get(context).incrementAndGet();
        return context;
    }

    protected ServletContextHandler removeContext(String path) throws Exception {
    	ServletContextHandler context = getContext(path);
        if (context == null) {
            throw new RuntimeException("Unknown context: " + path);
        }
        long refs = contextRefs.get(context).decrementAndGet();
        if (refs == 0) {
            contextRefs.remove(context);
            context.stop();
            //TODO: [hugues] we are supposed to use the deployment API of jetty
            //we can't really stop things like we used to ion jetty6.
//            server.removeHandler(context);
        }
        return context;
    }

    protected ServletContextHandler getContext(String path) {
        Handler[] handlers = server.getHandlers();
        if (handlers == null) {
            return null;
        }
        for (Handler h : handlers) {
            if (!(h instanceof ServletContextHandler)) {
                continue;
            }
            ServletContextHandler c = (ServletContextHandler) h;
            if (c.getContextPath().equals(path)) {
                return c;
            }
        }
        return null;
    }

}

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

import javax.servlet.Servlet;

import org.eclipse.ecr.core.storage.sql.RepositoryDescriptor.ServerDescriptor;

/**
 * Abstract class to manage http servers and their servlet and webapps.
 */
public abstract class NetServer {

    protected static NetServer instance;
    
    public static String add(ServerDescriptor serverDescriptor,
            String servletName, Servlet servlet, String path) {
        return instance().addRepositoryServer(serverDescriptor, servletName,
                servlet, path);
    }

    public static Servlet get(ServerDescriptor serverDescriptor, String servletName) {
        return instance().getServlet(serverDescriptor, servletName);
    }
    public static void remove(ServerDescriptor serverDescriptor,
            String servletName) {
        instance().removeRepositoryServer(serverDescriptor, servletName);
    }
    
    public static void setInstance(NetServer customInstance) {
    	instance = customInstance;
    }

    protected static synchronized NetServer instance() {
        if (instance == null) {
            instance = new NetServerJetty();
            instance.startServer();
        }
        return instance;
    }

    protected static synchronized void shutDown() {
        if (instance != null) {
            NetServer theinstance = instance;
            instance = null;
            theinstance.stopServer();
        }
    }

	/**
	 * @param serverDescriptor
	 * @param servletName
	 * @param servlet
	 * @param path
	 * @return "http://" + serverDescriptor.host + ':' + serverDescriptor.port + contextPath + path;
	 */
	protected abstract String addRepositoryServer(ServerDescriptor serverDescriptor,
            String servletName, Servlet servlet, String path);
	
	/**
	 * @param serverDescriptor
	 * @param servletName
	 */
	protected abstract void removeRepositoryServer(ServerDescriptor serverDescriptor,
            String servletName);
	
	/**
	 * @param serverDescriptor
	 * @param servletName
	 * @return
	 */
	protected abstract Servlet getServlet(ServerDescriptor serverDescriptor, String servletName);
	
	/**
	 * Start the underlying server
	 */
	protected abstract void startServer();
	
	/**
	 * Shutdown the underlying server
	 */
	protected abstract void stopServer();
	
}

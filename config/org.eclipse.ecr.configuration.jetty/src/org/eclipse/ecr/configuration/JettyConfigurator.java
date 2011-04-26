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
package org.eclipse.ecr.configuration;

import java.util.Dictionary;
import java.util.Hashtable;

import org.eclipse.ecr.application.LifeCycleAdapter;
import org.eclipse.equinox.http.jetty.JettyConstants;
import org.osgi.framework.BundleContext;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class JettyConfigurator extends LifeCycleAdapter {

    @Override
    public void beforeStart(BundleContext context) throws Exception {
        Dictionary<String, Object> settings = createDefaultSettings(context);
        org.eclipse.equinox.http.jetty.JettyConfigurator.startServer("org.eclipse.ecr", settings);
    }

    @Override
    public void afterStop(BundleContext context) throws Exception {
        org.eclipse.equinox.http.jetty.JettyConfigurator.stopServer("org.eclipse.ecr");
    }


    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Dictionary<String, Object> createDefaultSettings(BundleContext context) {
        final String PROPERTY_PREFIX = "org.eclipse.equinox.http.jetty."; //$NON-NLS-1$
        Dictionary defaultSettings = new Hashtable<String, Object>();


        // HTTP Enabled (default is true)
        String httpEnabledProperty = context.getProperty(PROPERTY_PREFIX + JettyConstants.HTTP_ENABLED);
        Boolean httpEnabled = (httpEnabledProperty == null) ? Boolean.TRUE : new Boolean(httpEnabledProperty);
        defaultSettings.put(JettyConstants.HTTP_ENABLED, httpEnabled);

        // HTTP Port
        String httpPortProperty = context.getProperty(PROPERTY_PREFIX + JettyConstants.HTTP_PORT);

        int httpPort = 8080;
        if (httpPortProperty != null) {
            try {
                httpPort = Integer.parseInt(httpPortProperty);
            } catch (NumberFormatException e) {
                //(log this) ignore and use default
            }
        }
        defaultSettings.put(JettyConstants.HTTP_PORT, new Integer(httpPort));

        // HTTP Host (default is 0.0.0.0)
        String httpHost = context.getProperty(PROPERTY_PREFIX + JettyConstants.HTTP_HOST);
        if (httpHost != null)
            defaultSettings.put(JettyConstants.HTTP_HOST, httpHost);

        // HTTPS Enabled (default is false)
        Boolean httpsEnabled = new Boolean(context.getProperty(PROPERTY_PREFIX + JettyConstants.HTTPS_ENABLED));
        defaultSettings.put(JettyConstants.HTTPS_ENABLED, httpsEnabled);

        if (httpsEnabled.booleanValue()) {
            // HTTPS Port
            String httpsPortProperty = context.getProperty(PROPERTY_PREFIX + JettyConstants.HTTPS_PORT);
            int httpsPort = 443;
            if (httpsPortProperty != null) {
                try {
                    httpsPort = Integer.parseInt(httpsPortProperty);
                } catch (NumberFormatException e) {
                    //(log this) ignore and use default
                }
            }
            defaultSettings.put(JettyConstants.HTTPS_PORT, new Integer(httpsPort));

            // HTTPS Host (default is 0.0.0.0)
            String httpsHost = context.getProperty(PROPERTY_PREFIX + JettyConstants.HTTPS_HOST);
            if (httpsHost != null)
                defaultSettings.put(JettyConstants.HTTPS_HOST, httpsHost);

            // SSL SETTINGS
            String keystore = context.getProperty(PROPERTY_PREFIX + JettyConstants.SSL_KEYSTORE);
            if (keystore != null)
                defaultSettings.put(JettyConstants.SSL_KEYSTORE, keystore);

            String password = context.getProperty(PROPERTY_PREFIX + JettyConstants.SSL_PASSWORD);
            if (password != null)
                defaultSettings.put(JettyConstants.SSL_PASSWORD, password);

            String keypassword = context.getProperty(PROPERTY_PREFIX + JettyConstants.SSL_KEYPASSWORD);
            if (keypassword != null)
                defaultSettings.put(JettyConstants.SSL_KEYPASSWORD, keypassword);

            String needclientauth = context.getProperty(PROPERTY_PREFIX + JettyConstants.SSL_NEEDCLIENTAUTH);
            if (needclientauth != null)
                defaultSettings.put(JettyConstants.SSL_NEEDCLIENTAUTH, new Boolean(needclientauth));

            String wantclientauth = context.getProperty(PROPERTY_PREFIX + JettyConstants.SSL_WANTCLIENTAUTH);
            if (wantclientauth != null)
                defaultSettings.put(JettyConstants.SSL_WANTCLIENTAUTH, new Boolean(wantclientauth));

            String protocol = context.getProperty(PROPERTY_PREFIX + JettyConstants.SSL_PROTOCOL);
            if (protocol != null)
                defaultSettings.put(JettyConstants.SSL_PROTOCOL, protocol);

            String algorithm = context.getProperty(PROPERTY_PREFIX + JettyConstants.SSL_ALGORITHM);
            if (algorithm != null)
                defaultSettings.put(JettyConstants.SSL_ALGORITHM, algorithm);

            String keystoretype = context.getProperty(PROPERTY_PREFIX + JettyConstants.SSL_KEYSTORETYPE);
            if (keystoretype != null)
                defaultSettings.put(JettyConstants.SSL_KEYSTORETYPE, keystoretype);
        }

        // Servlet Context Path
        String contextpath = context.getProperty(PROPERTY_PREFIX + JettyConstants.CONTEXT_PATH);
        if (contextpath != null)
            defaultSettings.put(JettyConstants.CONTEXT_PATH, contextpath);

        // Session Inactive Interval (timeout)
        String sessionInactiveInterval = context.getProperty(PROPERTY_PREFIX + JettyConstants.CONTEXT_SESSIONINACTIVEINTERVAL);
        if (sessionInactiveInterval != null) {
            try {
                defaultSettings.put(JettyConstants.CONTEXT_SESSIONINACTIVEINTERVAL, new Integer(sessionInactiveInterval));
            } catch (NumberFormatException e) {
                //(log this) ignore
            }
        }

        // Other Info
        String otherInfo = context.getProperty(PROPERTY_PREFIX + JettyConstants.OTHER_INFO);
        if (otherInfo != null)
            defaultSettings.put(JettyConstants.OTHER_INFO, otherInfo);

        return defaultSettings;
    }

}

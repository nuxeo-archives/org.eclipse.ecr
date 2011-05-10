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
package org.eclipse.ecr.auth;

import java.security.Principal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.ecr.core.api.NuxeoPrincipal;
import org.eclipse.ecr.runtime.api.login.Authenticator;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class SimpleUserRegistry implements Authenticator {

    protected Map<String, SimpleNuxeoPrincipal> users;

    public SimpleUserRegistry() {
        users = new ConcurrentHashMap<String, SimpleNuxeoPrincipal>();
    }

    public void clear() {
        users.clear();
    }

    public NuxeoPrincipal getUser(String name) {
        NuxeoPrincipal user = users.get(name);
        return user;
    }

    public NuxeoPrincipal[] getUsers() {
        return users.values().toArray(new NuxeoPrincipal[users.size()]);
    }

    @Override
    public Principal authenticate(String name, String password) {
        return doAuthenticate(name, password);
    }

    public NuxeoPrincipal doAuthenticate(String name, String password) {
        NuxeoPrincipal principal = getUser(name);
        if (principal == null) {
            return null;
        }
        String pwd = principal.getPassword();
        if (password == null || !password.equals(pwd)) {
            return null;
        }
        return principal;
    }

    @Override
    public boolean checkUsernamePassword(String name, String password) {
        return doAuthenticate(name, password) != null;
    }

    public void add(SimpleNuxeoPrincipal principal) {
        users.put(principal.getName(), principal);
    }

    public void remove(SimpleNuxeoPrincipal principal) {
        users.remove(principal.getName());
    }

}

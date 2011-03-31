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
import java.util.List;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class Profile {

    protected String name;

    protected List<String> requires;

    protected List<String> artifacts;


    public Profile(String name) {
        this.name = name;
        this.requires = new ArrayList<String>();
        this.artifacts = new ArrayList<String>();
    }

    public List<String> getArtifacts() {
        return artifacts;
    }

    public List<String> getRequires() {
        return requires;
    }

    public String getName() {
        return name;
    }

    public void addArtifact(String artifact) {
        this.artifacts.add(artifact);
    }

    public void addRequire(String require) {
        this.requires.add(require);
    }

    @Override
    public String toString() {
        return name;
    }
}

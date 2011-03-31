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

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class Artifact implements Comparable<Artifact> {

    public String id;

    public String name;

    public String version;

    public boolean optional;

    public Artifact(String name, String version) {
        this.name = name;
        this.version = version == null ? "0.0.0" : version;
        id = new StringBuilder(name).append("#").append(this.version).toString();
    }

    public Artifact(String id) {
        this(id, null);
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public boolean isOptional() {
        return optional;
    }

    public final String getId() {
        return id;
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Artifact) {
            return ((Artifact)obj).id.equals(id);
        }
        return false;
    }

    @Override
    public int compareTo(Artifact o) {
        return id.compareTo(o.id);
    }
}

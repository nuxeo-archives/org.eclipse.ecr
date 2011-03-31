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

    protected List<Unit> units;


    public Profile(String name) {
        this.name = name;
        this.requires = new ArrayList<String>();
        this.units = new ArrayList<Unit>();
    }

    public List<Unit> getInstallableUnits() {
        return units;
    }

    public List<String> getRequires() {
        return requires;
    }

    public String getName() {
        return name;
    }

    public void addInstallableUnit(Unit unit) {
        this.units.add(unit);
    }

    public void addRequire(String require) {
        this.requires.add(require);
    }

    @Override
    public String toString() {
        return name;
    }


    public static class Unit {
        protected String name;
        protected boolean autostart;
        protected int startLevel = -1; // the default start level
        public Unit(String name, boolean autostart) {
            this.autostart = autostart;
            this.name = name;
        }
        public String getName() {
            return name;
        }
        public boolean isAutostart() {
            return autostart;
        }
        public int getStartLevel() {
            return startLevel;
        }
        public void setStartLevel(int startLevel) {
            this.startLevel = startLevel;
        }
    }

}

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
package org.eclipse.ecr.ide.shell.views;

import jline.ConsoleReader;
import jline.Terminal;


/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class SWTTerminal extends Terminal {

    protected Console console;

    public SWTTerminal(Console console) {
        this.console = console;
    }

    @Override
    public boolean isSupported() {
        return false;
    }

    @Override
    public boolean getEcho() {
        return true;
    }

    @Override
    public boolean isANSISupported() {
        return false;
    }

    @Override
    public void initializeTerminal() {
        // nothing we need to do (or can do) for windows.
    }

    @Override
    public boolean isEchoEnabled() {
        return true;
    }

    @Override
    public void enableEcho() {
    }

    @Override
    public void disableEcho() {
    }

    /**
     * Always returng 80, since we can't access this info on Windows.
     */
    @Override
    public int getTerminalWidth() {
        return 80;
    }

    /**
     * Always returng 24, since we can't access this info on Windows.
     */
    @Override
    public int getTerminalHeight() {
        return 80;
    }

    @Override
    public void beforeReadLine(ConsoleReader reader, String prompt,
            Character mask) {
        console.setMask(mask);
    }

    @Override
    public void afterReadLine(ConsoleReader reader, String prompt,
            Character mask) {
        console.setMask(null);
    }

}

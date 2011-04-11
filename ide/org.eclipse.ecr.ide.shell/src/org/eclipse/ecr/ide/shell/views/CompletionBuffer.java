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

/**
 * Collect jline console reader while an output of a completion is made.
 * 
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class CompletionBuffer {

    StringBuilder buf = new StringBuilder();

    public CompletionBuffer(String cmdLine) {
        buf.append(cmdLine);
    }

    public void append(char[] cbuf, int offset, int len) {
        for (int i=0; i<len; i++) {
            char c = cbuf[offset+i];
            if (c == 8) {
                int newLen = buf.length()-1;
                if (newLen >= 0) {
                    buf.setLength(newLen);
                }
            } else {
                buf.append(c);
            }
        }
    }

    @Override
    public String toString() {
        return buf.toString();
    }

}

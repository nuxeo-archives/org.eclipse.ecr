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


import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.lang.reflect.Method;

import jline.ConsoleReader;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.nuxeo.shell.Shell;
import org.nuxeo.shell.cmds.ConsoleReaderFactory;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class Console extends StyledText implements ConsoleReaderFactory, KeyListener, VerifyKeyListener, VerifyListener, LineStyleListener {

    protected Method complete;
    protected ConsoleReader reader;
    protected final In in;
    protected final Writer out;
    protected Font font;
    protected CompletionBuffer completionBuf = null;
    protected Character mask;
    protected StringBuilder pwd;

    public Console(Composite parent) throws Exception {
        super (parent, SWT.H_SCROLL | SWT.V_SCROLL);
        in = new In();
        out = new Out();
        reader = new ConsoleReader(in, out, null, new SWTTerminal(this));
        //reader.setCompletionHandler(new SwingCompletionHandler(this));
        complete = reader.getClass().getDeclaredMethod("complete");
        complete.setAccessible(true);
        setMargins(6, 6, 6, 6);
        setBackground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
        setForeground(getDisplay().getSystemColor(SWT.COLOR_GREEN));
        font = new Font(getDisplay(), "Courier", 14, SWT.NONE);
        setFont(font);

        addDisposeListener(new DisposeListener() {
            @Override
            public void widgetDisposed(DisposeEvent e) {
                if (font != null) {
                    font.dispose();
                    font = null;
                    exit();
                }
            }
        });

        addKeyListener(this);
        addVerifyKeyListener(this);
        addVerifyListener(this);
        addLineStyleListener(this);

    }

    protected void resetTerminal() {
        setText("");
        try {
            Shell.get().hello();
        } catch (Exception e) {
            e.printStackTrace();
        }
        in.put("\n");
    }

    /**
     * @param mask the mask to set
     */
    public void setMask(Character mask) {
        this.mask = mask;
    }

    public String getPrompt() {
        return reader.getDefaultPrompt();
    }

    @Override
    public ConsoleReader getConsoleReader() {
        return reader;
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void verifyKey(VerifyEvent e) {
        // if not writing in the last line move to last line
        int rows = getLineCount();
        int offset = getCaretOffset();
        if (getLineAtOffset(offset) != rows-1) {
            if (e.character >= 32 && e.character < 256) {
                setSelection(getCharCount());
            }
        } else if (e.keyCode == SWT.ARROW_LEFT || e.keyCode == SWT.BS) {
            // avoid moving cursor in prompt area
            String text = getLine(rows-1);
            int lineStart = getCharCount() - text.length();
            int cmdStart = lineStart + getPrompt().length();
            if (offset <= cmdStart) {
                e.doit = false;
            }
        } else if (e.keyCode == SWT.ARROW_UP) {
            if (reader.getHistory().previous()) {
                setCommandText(reader.getHistory().current());
            } else {
                beep();
            }
            e.doit = false;
        } else if (e.keyCode == SWT.ARROW_DOWN) {
            if (reader.getHistory().next()) {
                setCommandText(reader.getHistory().current());
            } else {
                beep();
            }
            e.doit = false;
        } else if (e.keyCode == SWT.TAB) {
            complete();
            e.doit = false;
        } else if (e.keyCode == SWT.CR) {
            if (mask != null) {
                String text = pwd == null ? "" : pwd.toString();
                pwd = null;
                in.put(text+"\n");
                return;
            }
            String text = getCommandText().trim();
            if ("exit".equals(text)) {
                resetTerminal();
            } else {
                resetBuf();
                if (text.length() > 0 && reader.getUseHistory()) {
                    reader.getHistory().addToHistory(text);
                    reader.getHistory().moveToEnd();
                }
                in.put(text+"\n");
            }
        }
    }

    protected StyleRange sr = null;
    @Override
    public void verifyText(VerifyEvent e) {
        sr = null;
        //System.out.println("]]]]]] "+e.start+":"+e.end+": "+e.text);
        if (mask != null) {
            if (e.text.length() == 1) {
                if (e.text.charAt(0) >= 32) {
                    if (pwd == null) {
                        pwd = new StringBuilder();
                    }
                    pwd.append(e.text.charAt(0));
                    e.text = ""+mask.charValue();
                }
            }
        } else {
            // remove format tags and store style range info to be used to apply styles
            //            int i = e.text.indexOf("CREDITS");
            //            sr = new StyleRange();
            //            sr.start = e.start+i;
            //            sr.length = "CREDITS".length();
            //            sr.fontStyle = SWT.BOLD;
            //            sr.foreground = getDisplay().getSystemColor(SWT.COLOR_WHITE);
            //TODO
        }
    }

    @Override
    public void lineGetStyle(LineStyleEvent event) {
        if (sr != null) {
            int i = event.lineOffset+event.lineText.length();
            if (sr.start < i && event.lineOffset <= sr.start) {
                event.styles = new StyleRange[] {sr};
                sr = null;
            }
        }
    }


    public void exit() {
        in.put("exit 1\n");
    }

    public void complete() {
        syncBuf();
        completionBuf = new CompletionBuffer(getCommandTextBeforeCaret());
        System.out.println("#start: "+completionBuf.toString());
        try {
            if (!((Boolean) complete.invoke(reader))) {
                beep();
            } else {
                setCommandText(completionBuf.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            completionBuf = null;
        }
    }

    public void setCommandText(String text) {
        setSelection(getCommandLineOffset()+getPrompt().length(), getCharCount());
        insert(text);
        setSelection(getCharCount());
    }

    public void beep() {
        getDisplay().beep();
    }

    public void syncBuf() {
        StringBuffer sb = reader.getCursorBuffer().getBuffer();
        sb.setLength(0);
        sb.append(getCommandText());
        reader.getCursorBuffer().cursor = getCommandCursor();
    }

    public void resetBuf() {
        StringBuffer sb = reader.getCursorBuffer().getBuffer();
        sb.setLength(0);
        reader.getCursorBuffer().cursor = 0;
    }

    public final String getCommandLine() {
        return getLine(getLineCount()-1);
    }

    public final String getCommandText() {
        return getCommandLine().substring(getPrompt().length());
    }

    public final String getCommandTextBeforeCaret() {
        String line = getCommandLine();
        return line.substring(getPrompt().length(), line.length() - (getCharCount() - getCaretOffset()));
    }

    public final int getCommandLineOffset() {
        return getCharCount() - getCommandLine().length();
    }

    public final int getCommandCursor() {
        return getCaretOffset() - getPrompt().length() - getCommandLineOffset();
    }

    class In extends InputStream {
        protected StringBuilder buf = new StringBuilder();

        public synchronized void put(int key) {
            buf.append((char) key);
            notify();
        }

        public synchronized void put(String text) {
            buf.append(text);
            notify();
        }

        @Override
        public synchronized int read() throws IOException {
            if (buf.length() > 0) {
                char c = buf.charAt(0);
                buf.deleteCharAt(0);
                return c;
            }
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (buf.length() == 0) {
                throw new IllegalStateException(
                "invalid state for console input stream");
            }
            char c = buf.charAt(0);
            buf.deleteCharAt(0);
            return c;
        }
    }

    class Out extends Writer {

        protected void _write(final String str) {
            if (Display.getCurrent() != null) {
                // we are in UI thread
                print(str);
            } else {
                getDisplay().asyncExec(new Runnable() {
                    @Override
                    public void run() {
                        print(str);
                    };
                });
            }
        }

        protected void print(String str) {
            Console.this.append(str);
            setSelection(getCharCount());
        }

        @Override
        public void write(char[] cbuf, int off, int len) throws IOException {
            if (len == 0) {
                return;
            }
            //            System.out.print(">in ");
            //            for (int i=0; i<len; i++) {
            //                System.out.print(((int)cbuf[off+i])+"["+cbuf[off+i]+"]");
            //            }
            //            System.out.println();

            if (completionBuf != null) {
                completionBuf.append(cbuf, off, len);
            } else {
                _write(new String(cbuf, off, len));
            }
        }

        @Override
        public void flush() throws IOException {
        }

        @Override
        public void close() throws IOException {
            flush();
        }
    }

}

package org.eclipse.ecr.ide.shell.views;


import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.nuxeo.shell.Shell;
import org.nuxeo.shell.cmds.Interactive;
import org.nuxeo.shell.cmds.InteractiveShellHandler;
import org.nuxeo.shell.swing.ConsolePanel;


public class ShellView extends ViewPart {

    /**
     * The ID of the view as specified by the extension.
     */
    public static final String ID = "org.eclipse.ecr.ide.shell.views.ShellView";


    protected Console console;

    protected ConsolePanel panel;

    protected Thread thread;

    /**
     * The constructor.
     */
    public ShellView() {
    }

    /**
     * This is a callback that will allow us
     * to create the viewer and initialize it.
     */
    @Override
    public void createPartControl(Composite parent) {
        try {
            Shell.get();
            console = new Console(parent);
            Interactive.setConsoleReaderFactory(console);
            Interactive.setHandler(new InteractiveShellHandler() {
                @Override
                public boolean exitInteractiveMode(int code) {
                    return code != 0;
                }
                @Override
                public void enterInteractiveMode() {
                    //Interactive.reset();
                }
            });
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Shell.get().main(new String[] {"http://localhost:8080/ecr/automation"});
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, "ECR Shell");
            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        console.dispose();
        console = null;
        thread = null;
    }

    /**
     * Passing the focus request to the viewer's control.
     */
    @Override
    public void setFocus() {
        console.setFocus();
    }

}
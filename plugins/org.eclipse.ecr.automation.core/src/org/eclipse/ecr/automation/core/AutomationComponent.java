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
package org.eclipse.ecr.automation.core;

import org.eclipse.ecr.automation.AutomationService;
import org.eclipse.ecr.automation.core.events.EventHandler;
import org.eclipse.ecr.automation.core.events.EventHandlerRegistry;
import org.eclipse.ecr.automation.core.events.operations.FireEvent;
import org.eclipse.ecr.automation.core.impl.OperationServiceImpl;
import org.eclipse.ecr.automation.core.operations.FetchContextDocument;
import org.eclipse.ecr.automation.core.operations.RestoreBlobInput;
import org.eclipse.ecr.automation.core.operations.RestoreBlobsInput;
import org.eclipse.ecr.automation.core.operations.RestoreDocumentInput;
import org.eclipse.ecr.automation.core.operations.RestoreDocumentsInput;
import org.eclipse.ecr.automation.core.operations.RunInputScript;
import org.eclipse.ecr.automation.core.operations.RunScript;
import org.eclipse.ecr.automation.core.operations.SetInputAsVar;
import org.eclipse.ecr.automation.core.operations.SetVar;
import org.eclipse.ecr.automation.core.operations.blob.AttachBlob;
import org.eclipse.ecr.automation.core.operations.blob.BlobToFile;
import org.eclipse.ecr.automation.core.operations.blob.BlobToPDF;
import org.eclipse.ecr.automation.core.operations.blob.CreateBlob;
import org.eclipse.ecr.automation.core.operations.blob.CreateZip;
import org.eclipse.ecr.automation.core.operations.blob.GetDocumentBlob;
import org.eclipse.ecr.automation.core.operations.blob.GetDocumentBlobs;
import org.eclipse.ecr.automation.core.operations.blob.PostBlob;
import org.eclipse.ecr.automation.core.operations.blob.SetBlobFileName;
import org.eclipse.ecr.automation.core.operations.document.CheckInDocument;
import org.eclipse.ecr.automation.core.operations.document.CheckOutDocument;
import org.eclipse.ecr.automation.core.operations.document.CopyDocument;
import org.eclipse.ecr.automation.core.operations.document.CreateDocument;
import org.eclipse.ecr.automation.core.operations.document.CreateVersion;
import org.eclipse.ecr.automation.core.operations.document.DeleteDocument;
import org.eclipse.ecr.automation.core.operations.document.FetchByProperty;
import org.eclipse.ecr.automation.core.operations.document.FetchDocument;
import org.eclipse.ecr.automation.core.operations.document.FilterDocuments;
import org.eclipse.ecr.automation.core.operations.document.GetDocumentChild;
import org.eclipse.ecr.automation.core.operations.document.GetDocumentChildren;
import org.eclipse.ecr.automation.core.operations.document.GetDocumentParent;
import org.eclipse.ecr.automation.core.operations.document.LockDocument;
import org.eclipse.ecr.automation.core.operations.document.MoveDocument;
import org.eclipse.ecr.automation.core.operations.document.MultiPublishDocument;
import org.eclipse.ecr.automation.core.operations.document.PublishDocument;
import org.eclipse.ecr.automation.core.operations.document.Query;
import org.eclipse.ecr.automation.core.operations.document.ReloadDocument;
import org.eclipse.ecr.automation.core.operations.document.RemoveDocumentACL;
import org.eclipse.ecr.automation.core.operations.document.RemoveDocumentBlob;
import org.eclipse.ecr.automation.core.operations.document.RemoveProperty;
import org.eclipse.ecr.automation.core.operations.document.SaveDocument;
import org.eclipse.ecr.automation.core.operations.document.SetDocumentACE;
import org.eclipse.ecr.automation.core.operations.document.SetDocumentBlob;
import org.eclipse.ecr.automation.core.operations.document.SetDocumentLifeCycle;
import org.eclipse.ecr.automation.core.operations.document.SetDocumentProperty;
import org.eclipse.ecr.automation.core.operations.document.UnlockDocument;
import org.eclipse.ecr.automation.core.operations.document.UpdateDocument;
import org.eclipse.ecr.automation.core.operations.execution.RunDocumentChain;
import org.eclipse.ecr.automation.core.operations.execution.RunOperation;
import org.eclipse.ecr.automation.core.operations.execution.SaveSession;
import org.eclipse.ecr.automation.core.operations.login.LoginAs;
import org.eclipse.ecr.automation.core.operations.login.Logout;
import org.eclipse.ecr.automation.core.operations.stack.PopBlob;
import org.eclipse.ecr.automation.core.operations.stack.PopBlobList;
import org.eclipse.ecr.automation.core.operations.stack.PopDocument;
import org.eclipse.ecr.automation.core.operations.stack.PopDocumentList;
import org.eclipse.ecr.automation.core.operations.stack.PullBlob;
import org.eclipse.ecr.automation.core.operations.stack.PullBlobList;
import org.eclipse.ecr.automation.core.operations.stack.PullDocument;
import org.eclipse.ecr.automation.core.operations.stack.PullDocumentList;
import org.eclipse.ecr.automation.core.operations.stack.PushBlob;
import org.eclipse.ecr.automation.core.operations.stack.PushBlobList;
import org.eclipse.ecr.automation.core.operations.stack.PushDocument;
import org.eclipse.ecr.automation.core.operations.stack.PushDocumentList;
import org.eclipse.ecr.automation.core.rendering.operations.RenderDocument;
import org.eclipse.ecr.automation.core.rendering.operations.RenderDocumentFeed;
import org.eclipse.ecr.runtime.model.ComponentContext;
import org.eclipse.ecr.runtime.model.ComponentInstance;
import org.eclipse.ecr.runtime.model.DefaultComponent;

/**
 * Nuxeo component that provide an implementation of the
 * {@link AutomationService} and handle extensions registrations.
 *
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
public class AutomationComponent extends DefaultComponent {

    public static final String XP_OPERATIONS = "operations";

    public static final String XP_ADAPTERS = "adapters";

    public static final String XP_CHAINS = "chains";

    public static final String XP_EVENT_HANDLERS = "event-handlers";

    protected AutomationService service;

    protected EventHandlerRegistry handlers;

    @Override
    public void activate(ComponentContext context) throws Exception {
        service = new OperationServiceImpl();
        // register built-in operations
        service.putOperation(FetchContextDocument.class);
        service.putOperation(SetVar.class);
        service.putOperation(PushDocument.class);
        service.putOperation(PushDocumentList.class);
        service.putOperation(PopDocument.class);
        service.putOperation(PopDocumentList.class);
        service.putOperation(SetInputAsVar.class);
        service.putOperation(RestoreDocumentInput.class);
        service.putOperation(RestoreDocumentsInput.class);
        service.putOperation(RestoreBlobInput.class);
        service.putOperation(RestoreBlobsInput.class);
        service.putOperation(RunScript.class);
        service.putOperation(RunOperation.class);
        service.putOperation(RunDocumentChain.class);
        service.putOperation(CopyDocument.class);
        service.putOperation(CreateDocument.class);
        service.putOperation(CreateVersion.class);
        service.putOperation(CheckInDocument.class);
        service.putOperation(CheckOutDocument.class);
        service.putOperation(DeleteDocument.class);
        service.putOperation(FetchDocument.class);
        service.putOperation(LockDocument.class);
        service.putOperation(Query.class);
        service.putOperation(FetchByProperty.class);
        service.putOperation(FilterDocuments.class);
        service.putOperation(UnlockDocument.class);
        service.putOperation(GetDocumentChildren.class);
        service.putOperation(GetDocumentChild.class);
        service.putOperation(GetDocumentParent.class);
        service.putOperation(MoveDocument.class);
        service.putOperation(ReloadDocument.class);
        service.putOperation(SaveDocument.class);
        service.putOperation(SaveSession.class);
        service.putOperation(SetDocumentLifeCycle.class);
        service.putOperation(SetDocumentACE.class);
        service.putOperation(RemoveDocumentACL.class);
        service.putOperation(SetDocumentProperty.class);
        service.putOperation(RemoveProperty.class);
        service.putOperation(UpdateDocument.class);
        service.putOperation(PublishDocument.class);
        service.putOperation(MultiPublishDocument.class);
        service.putOperation(GetDocumentBlob.class);
        service.putOperation(GetDocumentBlobs.class);
        service.putOperation(SetDocumentBlob.class);
        service.putOperation(PostBlob.class);
        service.putOperation(BlobToPDF.class);
        service.putOperation(BlobToFile.class);
        service.putOperation(CreateBlob.class);
        service.putOperation(CreateZip.class);
        service.putOperation(AttachBlob.class);
        service.putOperation(SetBlobFileName.class);
        service.putOperation(RemoveDocumentBlob.class);
        service.putOperation(PushBlob.class);
        service.putOperation(PushBlobList.class);
        service.putOperation(PopBlob.class);
        service.putOperation(PopBlobList.class);

        service.putOperation(PullDocument.class);
        service.putOperation(PullDocumentList.class);
        service.putOperation(PullBlob.class);
        service.putOperation(PullBlobList.class);

        service.putOperation(FireEvent.class);
        service.putOperation(RunInputScript.class);

        service.putOperation(RenderDocument.class);
        service.putOperation(RenderDocumentFeed.class);

        service.putOperation(LoginAs.class);
        service.putOperation(Logout.class);

        // disabled operations
        // service.putOperation(RunScriptFile.class);

        handlers = new EventHandlerRegistry(service);
    }

    @Override
    public void deactivate(ComponentContext context) throws Exception {
        service = null;
        handlers = null;
    }

    @Override
    public void registerContribution(Object contribution,
            String extensionPoint, ComponentInstance contributor)
            throws Exception {
        if (XP_OPERATIONS.equals(extensionPoint)) {
            OperationContribution opc = (OperationContribution) contribution;
            service.putOperation(opc.type, opc.replace);
        } else if (XP_CHAINS.equals(extensionPoint)) {
            OperationChainContribution occ = (OperationChainContribution) contribution;
            service.putOperationChain(
                    occ.toOperationChain(contributor.getContext().getBundle()),
                    occ.replace);
        } else if (XP_ADAPTERS.equals(extensionPoint)) {
            TypeAdapterContribution tac = (TypeAdapterContribution) contribution;
            service.putTypeAdapter(tac.accept, tac.produce,
                    tac.clazz.newInstance());
        } else if (XP_EVENT_HANDLERS.equals(extensionPoint)) {
            EventHandler eh = (EventHandler) contribution;
            if (eh.isPostCommit()) {
                handlers.putPostCommitEventHandler(eh);
            } else {
                handlers.putEventHandler(eh);
            }
        }
    }

    @Override
    public void unregisterContribution(Object contribution,
            String extensionPoint, ComponentInstance contributor)
            throws Exception {
        if (XP_OPERATIONS.equals(extensionPoint)) {
            service.removeOperation(((OperationContribution) contribution).type);
        } else if (XP_CHAINS.equals(extensionPoint)) {
            OperationChainContribution occ = (OperationChainContribution) contribution;
            service.removeOperationChain(occ.id);
        } else if (XP_ADAPTERS.equals(extensionPoint)) {
            TypeAdapterContribution tac = (TypeAdapterContribution) contribution;
            service.removeTypeAdapter(tac.accept, tac.produce);
        } else if (XP_EVENT_HANDLERS.equals(extensionPoint)) {
            EventHandler eh = (EventHandler) contribution;
            if (eh.isPostCommit()) {
                handlers.removePostCommitEventHandler(eh);
            } else {
                handlers.removeEventHandler(eh);
            }
        }
    }

    @Override
    public <T> T getAdapter(Class<T> adapter) {
        if (adapter == AutomationService.class) {
            return adapter.cast(service);
        }
        if (adapter == EventHandlerRegistry.class) {
            return adapter.cast(handlers);
        }
        return null;
    }
}

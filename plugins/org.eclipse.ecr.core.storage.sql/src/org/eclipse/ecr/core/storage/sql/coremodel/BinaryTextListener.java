/*
 * Copyright (c) 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Florent Guillaume
 *     Stephane Lacoin
 */
package org.eclipse.ecr.core.storage.sql.coremodel;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.ecr.convert.api.ConversionService;
import org.eclipse.ecr.core.api.Blob;
import org.eclipse.ecr.core.api.ClientException;
import org.eclipse.ecr.core.api.CoreSession;
import org.eclipse.ecr.core.api.DocumentException;
import org.eclipse.ecr.core.api.DocumentModel;
import org.eclipse.ecr.core.api.DocumentModelList;
import org.eclipse.ecr.core.api.DocumentRef;
import org.eclipse.ecr.core.api.IdRef;
import org.eclipse.ecr.core.api.blobholder.BlobHolder;
import org.eclipse.ecr.core.api.blobholder.SimpleBlobHolder;
import org.eclipse.ecr.core.event.Event;
import org.eclipse.ecr.core.event.EventBundle;
import org.eclipse.ecr.core.event.EventContext;
import org.eclipse.ecr.core.event.PostCommitEventListener;
import org.eclipse.ecr.core.event.ReconnectedEventBundle;
import org.eclipse.ecr.core.storage.sql.Model;
import org.eclipse.ecr.core.storage.sql.ModelFulltext;
import org.eclipse.ecr.core.utils.BlobsExtractor;
import org.eclipse.ecr.runtime.api.Framework;
import org.nuxeo.common.utils.StringUtils;

/**
 * Listener that does fulltext extraction from the blobs of documents whose ids
 * have been recorded in the bundle's events.
 *
 * @author Florent Guillaume
 * @author Stephane Lacoin
 */
public class BinaryTextListener implements PostCommitEventListener {

    private static final Log log = LogFactory.getLog(BinaryTextListener.class);

    public static final String EVENT_NAME = "event_storage_binaries_doc";

    private static final String ANY2TEXT = "any2text";

    protected final ConversionService conversionService;

    public BinaryTextListener() throws ClientException {
        try {
            conversionService = Framework.getService(ConversionService.class);
        } catch (Exception e) {
            throw new ClientException(e);
        }
        if (conversionService == null) {
            throw new ClientException("No conversion service");
        }
    }

    @Override
    public void handleEvent(EventBundle eventBundle) throws ClientException {
        if (! eventBundle.containsEventName(EVENT_NAME)) {
            return;
        }
        if (!(eventBundle instanceof ReconnectedEventBundle)) {
            log.error("Incorrect event bundle type: " + eventBundle);
            return;
        }
        CoreSession session = null;
        ModelFulltext fulltextInfo = null;
        Set<Serializable> ids = new HashSet<Serializable>();
        for (Event event : eventBundle) {
            if (!event.getName().equals(EVENT_NAME)) {
                continue;
            }
            EventContext eventContext = event.getContext();
            fulltextInfo = getFulltextInfoFromEventContext(eventContext);
            ids.addAll(getIdsFromEventContext(eventContext));
            CoreSession s = eventContext.getCoreSession();
            if (session == null) {
                session = s;
            } else if (session != s) {
                // cannot happen given current ReconnectedEventBundleImpl
                throw new ClientException(
                        "Several CoreSessions in one EventBundle");
            }
        }
        if (session == null) {
            if (ids.isEmpty()) {
                return;
            }
            throw new ClientException("Null CoreSession");
        }

        // we have all the info from the bundle, now do the extraction
        boolean save = false;
        BlobsExtractor extractor = new BlobsExtractor();
        for (Serializable id : ids) {
            IdRef docRef = new IdRef((String) id);
            // if the runtime has shutdown (normally because tests are finished)
            // this can happen, see NXP-4009
            if (session.getPrincipal() == null) {
                continue;
            }
            if (!session.exists(docRef)) {
                // doc is gone
                continue;
            }
            DocumentModel indexedDoc = session.getDocument(docRef);
            if (indexedDoc.isProxy()) {
                // proxies don't have any fulltext attached, it's
                // the target document that carries it
                continue;
            }

            // Iterate on each index to set the binaryText column
            for (String indexName : fulltextInfo.indexNames) {
                if (!fulltextInfo.indexesAllBinary.contains(indexName)
                        && fulltextInfo.propPathsByIndexBinary.get(indexName) == null) {
                    // nothing to do: index not configured for blob
                    continue;
                }
                extractor.setExtractorProperties(
                        fulltextInfo.propPathsByIndexBinary.get(indexName),
                        fulltextInfo.propPathsExcludedByIndexBinary.get(indexName),
                        fulltextInfo.indexesAllBinary.contains(indexName));
                List<Blob> blobs = extractor.getBlobs(indexedDoc);
                String text = blobsToText(blobs);
                String impactedQuery =
                    String.format("SELECT * from Document where ecm:fulltextJobId = '%s'",
                            indexedDoc.getId());
                DocumentModelList impactedDocs = session.query(impactedQuery);
                for (DocumentModel impactedDoc : impactedDocs) {
                    try {
                        DocumentRef ref = impactedDoc.getRef();
                        session.setDocumentSystemProp(ref,
                                SQLDocument.FULLTEXT_JOBID_SYS_PROP,
                                null);
                        session.setDocumentSystemProp(ref,
                                SQLDocument.BINARY_TEXT_SYS_PROP + getFulltextIndexSuffix(indexName),
                                text);
                    } catch (DocumentException e) {
                        log.error("Couldn't set fulltext on: " + id, e);
                        continue;
                    }
                }
            }

            save = true;
        }
        if (save) {
            session.save();
        }
    }

    @SuppressWarnings("unchecked")
    protected Set<Serializable> getIdsFromEventContext(EventContext eventContext) {
        return (Set<Serializable>) eventContext.getArguments()[0];
    }

    protected ModelFulltext getFulltextInfoFromEventContext(
            EventContext eventContext) {
        return (ModelFulltext) eventContext.getArguments()[1];
    }

    protected String blobsToText(List<Blob> blobs) {
        List<String> strings = new LinkedList<String>();
        for (Blob blob : blobs) {
            try {
                SimpleBlobHolder bh = new SimpleBlobHolder(blob);
                BlobHolder result = conversionService.convert(ANY2TEXT, bh,
                        null);
                if (result == null) {
                    continue;
                }
                blob = result.getBlob();
                if (blob == null) {
                    continue;
                }
                String string = new String(blob.getByteArray(), "UTF-8");
                // strip '\0 chars from text
                if (string.indexOf('\0') >= 0) {
                    string = string.replace("\0", " ");
                }
                strings.add(string);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                continue;
            }
        }
        return StringUtils.join(strings, " ");
    }

    public String getFulltextIndexSuffix(String indexName) {
        return indexName.equals(Model.FULLTEXT_DEFAULT_INDEX) ? "" : '_' + indexName;
    }

}

/*
 * Copyright (c) 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Nuxeo - initial API and implementation
 *
 * $Id$
 */

package org.eclipse.ecr.core.event.test;

import org.eclipse.ecr.core.api.ClientException;
import org.eclipse.ecr.core.event.EventBundle;
import org.eclipse.ecr.core.event.PostCommitEventListener;

public class DummyPostCommitEventListener implements PostCommitEventListener {

    @Override
    public void handleEvent(EventBundle events) throws ClientException {
        // TODO Auto-generated method stub
    }

}

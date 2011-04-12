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
 */

package org.eclipse.ecr.core.api.pathsegment;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.eclipse.ecr.core.api.DocumentModel;
import org.eclipse.ecr.runtime.api.Framework;
import org.eclipse.ecr.testlib.NXRuntimeTestCase;

public class PathSegmentServiceTest extends NXRuntimeTestCase {

    public static class DocumentModelProxy implements InvocationHandler {

        public static DocumentModel newDocumentModel(String title) {
            return (DocumentModel) Proxy.newProxyInstance(
                    DocumentModelProxy.class.getClassLoader(),
                    new Class<?>[] { DocumentModel.class },
                    new DocumentModelProxy(title));
        }

        public String title;

        public DocumentModelProxy(String title) {
            this.title = title;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args)
                throws Throwable {
            String name = method.getName();
            if (name.equals("getTitle")) {
                return title;
            }
            return null;
        }
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        deployBundle("org.eclipse.ecr.core.schema");
        deployBundle("org.eclipse.ecr.core.api");
    }

    public void testDefault() throws Exception {
        PathSegmentService service = Framework.getService(PathSegmentService.class);
        assertNotNull(service);
        DocumentModel doc = DocumentModelProxy.newDocumentModel("My Document");
        assertEquals("My Document", service.generatePathSegment(doc));
    }

    public void testContrib() throws Exception {
        deployContrib("org.eclipse.ecr.core.api.test",
                "OSGI-INF/test-pathsegment-contrib.xml");
        PathSegmentService service = Framework.getService(PathSegmentService.class);
        assertNotNull(service);
        DocumentModel doc = DocumentModelProxy.newDocumentModel("My Document");
        assertEquals("my-document", service.generatePathSegment(doc));
    }

    public void testContribOverride() throws Exception {
        PathSegmentService service = Framework.getService(PathSegmentService.class);
        deployContrib("org.eclipse.ecr.core.api.test",
                "OSGI-INF/test-pathsegment-contrib.xml");
        DocumentModel doc = DocumentModelProxy.newDocumentModel("My Document");
        assertEquals("my-document", service.generatePathSegment(doc));
        deployContrib("org.eclipse.ecr.core.api.test",
                "OSGI-INF/test-pathsegment-contrib2.xml");
        assertEquals("My Document", service.generatePathSegment(doc));
    }

}

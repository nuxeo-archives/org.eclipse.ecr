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
package org.eclipse.ecr.testlib.runner.web;

import java.io.IOException;

import org.concordion.api.ResultSummary;
import org.concordion.internal.ConcordionBuilder;
import org.junit.Test;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
public class ConcordionFixture {

    protected final ConcordionBuilder concordionBuilder = new ConcordionBuilder();

    @Test public void test() throws IOException {
        ResultSummary resultSummary = concordionBuilder.build().process(this);
        resultSummary.print(System.out);
        resultSummary.assertIsSatisfied();
    }

}

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
package org.eclipse.ecr.automation.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.eclipse.ecr.automation.OperationContext;
import org.eclipse.ecr.core.api.CoreSession;

/**
 * To inject contextual data into an operation field. The following context
 * objects are provided:
 * <ul>
 * <li> The context itself. See {@link OperationContext}
 * <li> A Core Session if available. See {@link CoreSession}
 * <li> A Principal if available. This is the same as
 * {@link CoreSession#getPrincipal()}
 * <li> Any registered Nuxeo Service.
 * <ul>
 * CoreSession or OperationContext into a library instance field.
 *
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.FIELD })
public @interface Context {

}

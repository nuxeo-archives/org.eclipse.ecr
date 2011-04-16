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
package org.eclipse.ecr.application;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public interface Constants {

    public final String ECR_DB = "ecr.db";

    public final String ECR_CONFIGURATOR = "ecr.configurator";

    public final String ECR_CONFIG_SEGMENT = "config";

    public final String ECR_HOME_DIR = "ecr.home.dir";
    public final String ECR_CONFIG_DIR = "ecr.config.dir";
    public final String ECR_DATA_DIR = "ecr.data.dir";
    public final String ECR_LOG_DIR = "ecr.log.dir";

    public final String ECR_DB_DEFAULT = "h2";

    public final String ECR_HOME_DIR_DEFAULT = "${user.home}/.ecr";
    public final String ECR_CONFIG_DIR_DEFAULT = "${"+ECR_HOME_DIR+"}/config";
    public final String ECR_DATA_DIR_DEFAULT = "${"+ECR_HOME_DIR+"}/data";
    public final String ECR_LOG_DIR_DEFAULT = "${"+ECR_HOME_DIR+"}/log";

}

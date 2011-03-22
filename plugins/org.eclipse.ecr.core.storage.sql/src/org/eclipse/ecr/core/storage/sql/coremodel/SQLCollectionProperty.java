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

package org.eclipse.ecr.core.storage.sql.coremodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.eclipse.ecr.core.api.DocumentException;
import org.eclipse.ecr.core.schema.types.ListType;
import org.eclipse.ecr.core.storage.StorageException;
import org.eclipse.ecr.core.storage.sql.CollectionProperty;
import org.eclipse.ecr.core.storage.sql.Model;

/**
 * A {@link SQLCollectionProperty} gives access to a wrapped SQL-level
 * {@link CollectionProperty}.
 *
 * @author Florent Guillaume
 */
public class SQLCollectionProperty extends SQLBaseProperty {

    private final CollectionProperty property;

    private final boolean isArray;

    private final SQLSession session;

    /**
     * Creates a {@link SQLCollectionProperty} to wrap a
     * {@link CollectionProperty}.
     */
    public SQLCollectionProperty(SQLSession session,
            CollectionProperty property, ListType type, boolean readonly) {
        super(type, property == null ? null : property.getName(), readonly);
        this.session = session;
        this.property = property;
        this.isArray = type == null || type.isArray();
    }

    /*
     * ----- org.eclipse.ecr.core.model.Property -----
     */

    @Override
    public String getName() {
        return property.getName();
    }

    @Override
    public Object getValue() throws DocumentException {
        try {
            Serializable[] value = property.getValue();
            if (isArray) {
                return value;
            } else {
                return new ArrayList<Serializable>(Arrays.asList(value));
            }
        } catch (StorageException e) {
            throw new DocumentException(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setValue(Object value) throws DocumentException {
        checkWritable();
        if (Model.ACL_PROP.equals(property.getName())) {
            session.requireReadAclsUpdate();
        }
        if (value != null && !(value instanceof Object[])) {
            if (isArray) {
                throw new DocumentException("Value is not Object[] but "
                        + value.getClass().getName() + ": " + value);
            }
            // accept also any List
            if (!(value instanceof Collection)) {
                throw new DocumentException(
                        "Value is not Object[] or Collection but "
                                + value.getClass().getName() + ": " + value);
            }
            value = property.type.getArrayBaseType().collectionToArray(
                    (Collection<Serializable>) value);
        }
        try {
            property.setValue((Object[]) value);
        } catch (StorageException e) {
            throw new DocumentException(e);
        }
    }

}

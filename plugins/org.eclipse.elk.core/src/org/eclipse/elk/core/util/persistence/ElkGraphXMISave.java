/*******************************************************************************
 * Copyright (c) 2017 cds and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    cds - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core.util.persistence;

import org.eclipse.elk.core.data.LayoutMetaDataService;
import org.eclipse.elk.core.data.LayoutOptionData;
import org.eclipse.elk.graph.EMapPropertyHolder;
import org.eclipse.elk.graph.impl.ElkPropertyToValueMapEntryImpl;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.xmi.XMLHelper;
import org.eclipse.emf.ecore.xmi.impl.XMISaveImpl;

/**
 * Specializes serialization such that properties are not serialized if we have no chance of parsing them in again
 * later. See {@link ElkGraphResource} for details.
 * 
 * @see ElkGraphResource
 */
final class ElkGraphXMISave extends XMISaveImpl {

    /**
     * Creates a new instance that uses the given helper.
     */
    ElkGraphXMISave(final XMLHelper helper) {
        super(helper);
    }
    
    
    @Override
    protected void saveElement(final EObject o, final EStructuralFeature f) {
        // We have special handling for property to value map entries
        if (f.getContainerClass().equals(EMapPropertyHolder.class)) {
            if (o instanceof ElkPropertyToValueMapEntryImpl) {
                // Check if the property's data type is deserializable
                IProperty<?> property = ((ElkPropertyToValueMapEntryImpl) o).getKey();
                LayoutOptionData optionData = LayoutMetaDataService.getInstance().getOptionData(property.getId());
                
                // Check if the value we want to serialize here will be parsable later so we can deserialize it
                // again properly; if this is not the case, there is no point serializing it in the first place
                if (optionData != null && !optionData.canParseValue()) {
                    System.out.println("Prevented " + optionData.getId() + " from being serialized.");
                    return;
                }
            }
        }
        
        super.saveElement(o, f);
    }

}

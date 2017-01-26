/**
 *  Copyright (c) 2017 CentraleSupélec & EDF.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  This file is part of the RiseClipse tool
 *  
 *  Contributors:
 *      Computer Science Department, CentraleSupélec : initial implementation
 *  Contacts:
 *      Dominique.Marcadet@centralesupelec.fr
 */
package fr.centralesupelec.edf.riseclipse.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.command.CommandStack;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Factory;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import fr.centralesupelec.edf.riseclipse.util.RiseClipseResourceSet;

public class RiseClipseAdapterFactoryEditingDomain extends AdapterFactoryEditingDomain {

    public RiseClipseAdapterFactoryEditingDomain( AdapterFactory adapterFactory, CommandStack commandStack,
            Map< Resource, Boolean > resourceToReadOnlyMap, HashMap< String, Resource.Factory > factories ) {
        super( adapterFactory, commandStack, resourceToReadOnlyMap );
        // We use RiseClipse specific ResourceSet
        this.resourceSet = new LocalResourceSet( factories );
    }

    public RiseClipseAdapterFactoryEditingDomain( AdapterFactory adapterFactory, CommandStack commandStack,
            ResourceSet resourceSet, HashMap< String, Resource.Factory > factories ) {
        super( adapterFactory, commandStack, resourceSet );
        // TODO: ??
        throw new RuntimeException( "RiseClipseAdapterFactoryEditingDomain got a resourceSet" );
    }

    public RiseClipseAdapterFactoryEditingDomain( AdapterFactory adapterFactory, CommandStack commandStack,
            HashMap< String, Resource.Factory > factories ) {
        super( adapterFactory, commandStack );

        // We use RiseClipse specific ResourceSet
        this.resourceSet = new LocalResourceSet( factories );
    }

    public RiseClipseAdapterFactoryEditingDomain( ComposedAdapterFactory adapterFactory,
            BasicCommandStack commandStack, HashMap< Resource, Boolean > hashMap,
            HashMap< String, Resource.Factory > factories ) {
        super( adapterFactory, commandStack, hashMap );

        // We use RiseClipse specific ResourceSet
        this.resourceSet = new LocalResourceSet( factories );
    }

    // From
    // org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain.AdapterFactoryEditingDomainResourceSet
    protected class LocalResourceSet extends RiseClipseResourceSet implements IEditingDomainProvider {

        LocalResourceSet( HashMap< String, Factory > resourceFactories ) {
            super( resourceFactories );
        }

        public EditingDomain getEditingDomain() {
            return RiseClipseAdapterFactoryEditingDomain.this;
        }

    }

}

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
package fr.centralesupelec.edf.riseclipse.editor.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.ui.handlers.HandlerUtil;

import fr.centralesupelec.edf.riseclipse.ui.RiseClipseEditor;
import fr.centralesupelec.edf.riseclipse.util.AbstractRiseClipseConsole;
import fr.centralesupelec.edf.riseclipse.util.IRiseClipseResource;

public class DisplayStatisticsHandler extends AbstractHandler {

    @Override
    public Object execute( ExecutionEvent event ) throws ExecutionException {
        RiseClipseEditor e = ( RiseClipseEditor ) HandlerUtil.getActiveEditor( event );
        if( e == null ) return null;
        if( e.getEditingDomain() == null ) return null;
        ResourceSet r = e.getEditingDomain().getResourceSet();
        for( int i = 0; i < r.getResources().size(); ++i ) {
            Resource c = r.getResources().get( i );
            if( c instanceof IRiseClipseResource ) {
                (( IRiseClipseResource ) c ).printStatistics( AbstractRiseClipseConsole.getConsole() );
            }
        }
        
        return null;
    }

}

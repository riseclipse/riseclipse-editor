/**
 *  Copyright (c) 2018 CentraleSupélec & EDF.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  This file is part of the RiseClipse tool
 *  
 *  Contributors:
 *      Computer Science Department, CentraleSupélec
 *      EDF R&D
 *  Contacts:
 *      dominique.marcadet@centralesupelec.fr
 *      aurelie.dehouck-neveu@edf.fr
 *  Web site:
 *      http://wdi.supelec.fr/software/RiseClipse/
 */
package fr.centralesupelec.edf.riseclipse.editor.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.CommandContributionItem;

import fr.centralesupelec.edf.riseclipse.ui.RiseClipseEditor;

public class NavigateToHandler extends AbstractHandler {

    public NavigateToHandler() {
    }

    @Override
    public Object execute( ExecutionEvent event ) throws ExecutionException {
        RiseClipseEditor e = ( RiseClipseEditor ) HandlerUtil.getActiveEditor( event );
        Event v = ( Event ) event.getTrigger();
        CommandContributionItem c = ( CommandContributionItem ) v.widget.getData();
        e.navigateTo( c );
        return null;
    }


}

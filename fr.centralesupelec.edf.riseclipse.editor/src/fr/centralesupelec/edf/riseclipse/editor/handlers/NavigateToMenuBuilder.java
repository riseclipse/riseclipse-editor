/*
*************************************************************************
**  Copyright (c) 2019 CentraleSupélec & EDF.
**  All rights reserved. This program and the accompanying materials
**  are made available under the terms of the Eclipse Public License v2.0
**  which accompanies this distribution, and is available at
**  https://www.eclipse.org/legal/epl-v20.html
** 
**  This file is part of the RiseClipse tool
**  
**  Contributors:
**      Computer Science Department, CentraleSupélec
**      EDF R&D
**  Contacts:
**      dominique.marcadet@centralesupelec.fr
**      aurelie.dehouck-neveu@edf.fr
**  Web site:
**      http://wdi.supelec.fr/software/RiseClipse/
*************************************************************************
*/
package fr.centralesupelec.edf.riseclipse.editor.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.menus.IWorkbenchContribution;
import org.eclipse.ui.services.IServiceLocator;

import fr.centralesupelec.edf.riseclipse.ui.RiseClipseEditor;
import fr.centralesupelec.edf.riseclipse.util.RiseClipseMetamodel;

public class NavigateToMenuBuilder extends CompoundContributionItem implements IWorkbenchContribution {

    private IServiceLocator serviceLocator;

    public NavigateToMenuBuilder() {
    }

    // from CompoundContributionItem
    @Override
    protected IContributionItem[] getContributionItems() {

        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if( ! ( window.getActivePage().getActiveEditor() instanceof RiseClipseEditor )) return null;
        RiseClipseEditor editor = ( RiseClipseEditor ) window.getActivePage().getActiveEditor();
        
        ISelection selection = window.getActivePage().getSelection();
        if( ! ( selection instanceof TreeSelection )) return null;
        
        TreeSelection tree = ( TreeSelection ) selection;
        if( ! ( tree.getFirstElement() instanceof EObject )) return null;
        
        IContributionItem[] items = new IContributionItem[1];
        MenuManager navigateTo = new MenuManager( "Navigate to" );
        items[0] = navigateTo;

        EObject o = ( EObject ) tree.getFirstElement();
        Optional< AdapterFactory > adapter = RiseClipseMetamodel.getMetamodel( o.eClass().getEPackage().getNsURI() ).flatMap( a -> a.getAdapterFactory() );
        EList< EReference > refs = o.eClass().getEAllReferences();
        ArrayList< EReference > usedRefs = new ArrayList< EReference >();
        ArrayList< Object > values = new ArrayList< Object >();
        for( EReference r : refs ) {
            Object v = o.eGet( r );
            if( v != null ) {
                usedRefs.add( r );
                values.add( v );
            }
        }
        IContributionItem[] list = new IContributionItem[usedRefs.size()];
        // We need to get back the referenced eObject when the command is executed.
        // As it seems that there is no way to associate an arbitrary object to
        // the command, we build a map and give it to the editor
        Map< CommandContributionItem, Object > map = new HashMap< CommandContributionItem, Object >();
        for( int i = 0; i < list.length; ++i ) {
            MenuManager ref = new MenuManager( usedRefs.get( i ).getName() );
            int numberOfRefs = 1;
            if( values.get( i ) instanceof EList ) {
                numberOfRefs = (( EList< ? > ) values.get( i )).size();
            }
            for( int j = 0; j < numberOfRefs; ++j ) {
                CommandContributionItemParameter contributionParameters = new CommandContributionItemParameter(
                        serviceLocator, null, "fr.centralesupelec.edf.riseclipse.editor.commands.navigateTo",
                        CommandContributionItem.STYLE_PUSH );
                Object val;
                if( values.get( i ) instanceof EList ) {
                    val = (( EList< ? > ) values.get( i )).get( j );
                }
                else {
                    val = values.get( i );
                }
                if( adapter.isPresent() ) {
                    IItemLabelProvider labelProvider = ( IItemLabelProvider ) adapter.get().adapt( val, IItemLabelProvider.class );
                    contributionParameters.label = labelProvider.getText( val );
                }
                CommandContributionItem c = new CommandContributionItem( contributionParameters );
                map.put( c, val );
                ref.add( c );
                
            }
            navigateTo.add( ref );
        }

        editor.setNavigateToMap( map );
        return items;
    }

    // from IWorkbenchContribution
    @Override
    public void initialize( IServiceLocator serviceLocator ) {
        this.serviceLocator = serviceLocator;
    }

}

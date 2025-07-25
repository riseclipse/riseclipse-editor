/*
*************************************************************************
**  Copyright (c) 2016-2025 CentraleSupélec & EDF.
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
**      https://riseclipse.github.io
*************************************************************************
*/
package fr.centralesupelec.edf.riseclipse.ui;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.emf.common.ui.viewer.IViewerProvider;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.emf.edit.ui.action.ControlAction;
import org.eclipse.emf.edit.ui.action.CreateChildAction;
import org.eclipse.emf.edit.ui.action.CreateSiblingAction;
import org.eclipse.emf.edit.ui.action.EditingDomainActionBarContributor;
import org.eclipse.emf.edit.ui.action.LoadResourceAction;
import org.eclipse.emf.edit.ui.action.ValidateAction;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.SubContributionItem;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;

/**
 * This is the action bar contributor for the RiseClipse editor.
 */
public class RiseClipseActionBarContributor extends EditingDomainActionBarContributor implements
        ISelectionChangedListener {
    /**
     * This keeps track of the active editor.
     */
    protected IEditorPart activeEditorPart;

    /**
     * This keeps track of the current selection provider.
     */
    protected ISelectionProvider selectionProvider;

    /**
     * This action opens the Properties view.
     */
    protected IAction showPropertiesViewAction = new Action(
            RiseClipseEditorPlugin.INSTANCE.getString( "_UI_ShowPropertiesView_menu_item" ) ) {
        @Override
        public void run() {
            try {
                getPage().showView( "org.eclipse.ui.views.PropertySheet" );
            }
            catch( PartInitException exception ) {
                RiseClipseEditorPlugin.INSTANCE.log( exception );
            }
        }
    };

    /**
     * This action refreshes the viewer of the current editor if the editor
     * implements {@link org.eclipse.emf.common.ui.viewer.IViewerProvider}.
     */
    protected IAction refreshViewerAction = new Action(
            RiseClipseEditorPlugin.INSTANCE.getString( "_UI_RefreshViewer_menu_item" ) ) {
        @Override
        public boolean isEnabled() {
            return activeEditorPart instanceof IViewerProvider;
        }

        @Override
        public void run() {
            if( activeEditorPart instanceof IViewerProvider ) {
                Viewer viewer = ( ( IViewerProvider ) activeEditorPart ).getViewer();
                if( viewer != null ) {
                    viewer.refresh();
                }
            }
        }
    };

    /**
     * This will contain one
     * {@link org.eclipse.emf.edit.ui.action.CreateChildAction} corresponding to
     * each descriptor generated for the current selection by the item provider.
     */
    protected Collection< IAction > createChildActions;

    /**
     * This is the menu manager into which menu contribution items should be
     * added for CreateChild actions.
     */
    protected IMenuManager createChildMenuManager;

    /**
     * This will contain one
     * {@link org.eclipse.emf.edit.ui.action.CreateSiblingAction} corresponding
     * to each descriptor generated for the current selection by the item
     * provider.
     */
    protected Collection< IAction > createSiblingActions;

    /**
     * This is the menu manager into which menu contribution items should be
     * added for CreateSibling actions.
     */
    protected IMenuManager createSiblingMenuManager;

    /**
     * This creates an instance of the contributor.
     */
    public RiseClipseActionBarContributor() {
        super( ADDITIONS_LAST_STYLE );
        loadResourceAction = new LoadResourceAction();
        validateAction = new ValidateAction();
        controlAction = new ControlAction();
    }

    /**
     * This adds Separators for editor additions to the tool bar.
     */
    @Override
    public void contributeToToolBar( IToolBarManager toolBarManager ) {
        toolBarManager.add( new Separator( "riseclipse-settings" ) );
        toolBarManager.add( new Separator( "riseclipse-additions" ) );
    }

    /**
     * This adds to the menu bar a menu and some separators for editor
     * additions, as well as the sub-menus for object creation items.
     */
    @Override
    public void contributeToMenu( IMenuManager menuManager ) {
        super.contributeToMenu( menuManager );

        // RiseClipseSpecific: we do our own menu
        //
//        IMenuManager submenuManager = new MenuManager(
//                RiseClipseEditorPlugin.INSTANCE.getString( "_UI_RiseClipseEditor_menu" ),
//                "fr.centralesupelec.edf.riseclipse.editor.riseclipseMenuID" );
//        menuManager.insertAfter( "additions", submenuManager );
//        submenuManager.add( new Separator( "settings" ) );
//        submenuManager.add( new Separator( "actions" ) );
//        submenuManager.add( new Separator( "additions" ) );
//        submenuManager.add( new Separator( "additions-end" ) );
//
//        // Prepare for CreateChild item addition or removal.
//        //
//        createChildMenuManager = new MenuManager(
//                RiseClipseEditorPlugin.INSTANCE.getString( "_UI_CreateChild_menu_item" ) );
//        submenuManager.insertBefore( "additions", createChildMenuManager );
//
//        // Prepare for CreateSibling item addition or removal.
//        //
//        createSiblingMenuManager = new MenuManager(
//                RiseClipseEditorPlugin.INSTANCE.getString( "_UI_CreateSibling_menu_item" ) );
//        submenuManager.insertBefore( "additions", createSiblingMenuManager );
//
//        // Force an update because Eclipse hides empty menus now.
//        //
//        submenuManager.addMenuListener( new IMenuListener() {
//            public void menuAboutToShow( IMenuManager menuManager ) {
//                menuManager.updateAll( true );
//            }
//        } );
//
//        addGlobalActions( submenuManager );
    }

    /**
     * When the active editor changes, this remembers the change and registers
     * with it as a selection provider.
     */
    @Override
    public void setActiveEditor( IEditorPart part ) {
        super.setActiveEditor( part );
        activeEditorPart = part;

        // Switch to the new selection provider.
        //
        if( selectionProvider != null ) {
            selectionProvider.removeSelectionChangedListener( this );
        }
        if( part == null ) {
            selectionProvider = null;
        }
        else {
            selectionProvider = part.getSite().getSelectionProvider();
            selectionProvider.addSelectionChangedListener( this );

            // Fake a selection changed event to update the menus.
            //
            if( selectionProvider.getSelection() != null ) {
                selectionChanged( new SelectionChangedEvent( selectionProvider, selectionProvider.getSelection() ) );
            }
        }
    }

    /**
     * This implements
     * {@link org.eclipse.jface.viewers.ISelectionChangedListener}, handling
     * {@link org.eclipse.jface.viewers.SelectionChangedEvent}s by querying for
     * the children and siblings that can be added to the selected object and
     * updating the menus accordingly.
     */
    public void selectionChanged( SelectionChangedEvent event ) {
        // Remove any menu items for old selection.
        //
        if( createChildMenuManager != null ) {
            depopulateManager( createChildMenuManager, createChildActions );
        }
        if( createSiblingMenuManager != null ) {
            depopulateManager( createSiblingMenuManager, createSiblingActions );
        }

        // Query the new selection for appropriate new child/sibling descriptors
        //
        Collection< ? > newChildDescriptors = null;
        Collection< ? > newSiblingDescriptors = null;

        ISelection selection = event.getSelection();
        if( selection instanceof IStructuredSelection && ( ( IStructuredSelection ) selection ).size() == 1 ) {
            Object object = ( ( IStructuredSelection ) selection ).getFirstElement();

            EditingDomain domain = ( ( IEditingDomainProvider ) activeEditorPart ).getEditingDomain();

            newChildDescriptors = domain.getNewChildDescriptors( object, null );
            newSiblingDescriptors = domain.getNewChildDescriptors( null, object );
        }

        // Generate actions for selection; populate and redraw the menus.
        //
        createChildActions = generateCreateChildActions( newChildDescriptors, selection );
        createSiblingActions = generateCreateSiblingActions( newSiblingDescriptors, selection );

        if( createChildMenuManager != null ) {
            populateManager( createChildMenuManager, createChildActions, null );
            createChildMenuManager.update( true );
        }
        if( createSiblingMenuManager != null ) {
            populateManager( createSiblingMenuManager, createSiblingActions, null );
            createSiblingMenuManager.update( true );
        }
    }

    /**
     * This generates a {@link org.eclipse.emf.edit.ui.action.CreateChildAction}
     * for each object in <code>descriptors</code>, and returns the collection
     * of these actions.
     */
    protected Collection< IAction > generateCreateChildActions( Collection< ? > descriptors, ISelection selection ) {
        Collection< IAction > actions = new ArrayList< IAction >();
        if( descriptors != null ) {
            for( Object descriptor : descriptors ) {
                actions.add( new CreateChildAction( activeEditorPart, selection, descriptor ) );
            }
        }
        return actions;
    }

    /**
     * This generates a
     * {@link org.eclipse.emf.edit.ui.action.CreateSiblingAction} for each
     * object in <code>descriptors</code>, and returns the collection of these
     * actions.
     */
    protected Collection< IAction > generateCreateSiblingActions( Collection< ? > descriptors, ISelection selection ) {
        Collection< IAction > actions = new ArrayList< IAction >();
        if( descriptors != null ) {
            for( Object descriptor : descriptors ) {
                actions.add( new CreateSiblingAction( activeEditorPart, selection, descriptor ) );
            }
        }
        return actions;
    }

    /**
     * This populates the specified <code>manager</code> with
     * {@link org.eclipse.jface.action.ActionContributionItem}s based on the
     * {@link org.eclipse.jface.action.IAction}s contained in the
     * <code>actions</code> collection, by inserting them before the specified
     * contribution item <code>contributionID</code>. If
     * <code>contributionID</code> is <code>null</code>, they are simply added.
     */
    protected void populateManager( IContributionManager manager, Collection< ? extends IAction > actions,
            String contributionID ) {
        if( actions != null ) {
            for( IAction action : actions ) {
                if( contributionID != null ) {
                    manager.insertBefore( contributionID, action );
                }
                else {
                    manager.add( action );
                }
            }
        }
    }

    /**
     * This removes from the specified <code>manager</code> all
     * {@link org.eclipse.jface.action.ActionContributionItem}s based on the
     * {@link org.eclipse.jface.action.IAction}s contained in the
     * <code>actions</code> collection.
     */
    protected void depopulateManager( IContributionManager manager, Collection< ? extends IAction > actions ) {
        if( actions != null ) {
            IContributionItem[] items = manager.getItems();
            for( int i = 0; i < items.length; i++ ) {
                // Look into SubContributionItems
                //
                IContributionItem contributionItem = items[i];
                while( contributionItem instanceof SubContributionItem ) {
                    contributionItem = ( ( SubContributionItem ) contributionItem ).getInnerItem();
                }

                // Delete the ActionContributionItems with matching action.
                //
                if( contributionItem instanceof ActionContributionItem ) {
                    IAction action = ( ( ActionContributionItem ) contributionItem ).getAction();
                    if( actions.contains( action ) ) {
                        manager.remove( contributionItem );
                    }
                }
            }
        }
    }

    /**
     * This populates the pop-up menu before it appears.
     */
    @Override
    public void menuAboutToShow( IMenuManager menuManager ) {
        super.menuAboutToShow( menuManager );
        MenuManager submenuManager = null;

        submenuManager = new MenuManager( RiseClipseEditorPlugin.INSTANCE.getString( "_UI_CreateChild_menu_item" ) );
        populateManager( submenuManager, createChildActions, null );
        menuManager.insertBefore( "edit", submenuManager );

        submenuManager = new MenuManager( RiseClipseEditorPlugin.INSTANCE.getString( "_UI_CreateSibling_menu_item" ) );
        populateManager( submenuManager, createSiblingActions, null );
        menuManager.insertBefore( "edit", submenuManager );
    }

    /**
     * This inserts global actions before the "additions-end" separator.
     */
    @Override
    protected void addGlobalActions( IMenuManager menuManager ) {
        menuManager.insertAfter( "additions-end", new Separator( "ui-actions" ) );
        menuManager.insertAfter( "ui-actions", showPropertiesViewAction );

        refreshViewerAction.setEnabled( refreshViewerAction.isEnabled() );
        menuManager.insertAfter( "ui-actions", refreshViewerAction );

        super.addGlobalActions( menuManager );
    }

    /**
     * This ensures that a delete action will clean up all references to deleted
     * objects.
     */
    @Override
    protected boolean removeAllReferencesOnDelete() {
        return true;
    }

}

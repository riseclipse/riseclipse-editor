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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.views.contentoutline.ContentOutline;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.PropertySheet;
import org.eclipse.ui.views.properties.PropertySheetPage;

import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CommandStack;
import org.eclipse.emf.common.command.CommandStackListener;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.ui.MarkerHelper;
import org.eclipse.emf.common.ui.ViewerPane;
import org.eclipse.emf.common.ui.editor.ProblemEditorPart;
import org.eclipse.emf.common.ui.viewer.IViewerProvider;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.ExtensibleURIConverterImpl;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.IllegalValueException;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.emf.edit.provider.AdapterFactoryItemDelegator;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;
import org.eclipse.emf.edit.ui.action.EditingDomainActionBarContributor;
import org.eclipse.emf.edit.ui.celleditor.AdapterFactoryTreeEditor;
import org.eclipse.emf.edit.ui.dnd.EditingDomainViewerDropAdapter;
import org.eclipse.emf.edit.ui.dnd.LocalTransfer;
import org.eclipse.emf.edit.ui.dnd.ViewerDragAdapter;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.emf.edit.ui.provider.UnwrappingSelectionProvider;
import org.eclipse.emf.edit.ui.util.EditUIMarkerHelper;
import org.eclipse.emf.edit.ui.util.EditUIUtil;
import org.eclipse.emf.edit.ui.view.ExtendedPropertySheetPage;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

import fr.centralesupelec.edf.riseclipse.util.IRiseClipseConsole;
import fr.centralesupelec.edf.riseclipse.util.IRiseClipseResourceSet;
import fr.centralesupelec.edf.riseclipse.util.AbstractRiseClipseResourceSet;
import fr.centralesupelec.edf.riseclipse.util.RiseClipseMetamodel;
import fr.centralesupelec.edf.riseclipse.util.Severity;

/**
 * The base is the EMF generated editor
 */
public class RiseClipseEditor extends MultiPageEditorPart implements IEditingDomainProvider, ISelectionProvider,
        IMenuListener, IViewerProvider, IGotoMarker {

    private static final String RISECLIPSE_CONSOLE_NAME = "RiseClipseConsole";

    private static final String EDITOR_CATEGORY = "RiseClipse/Editor";

    /**
     * This keeps track of the editing domain that is used to track all changes
     * to the model.
     */
    protected AdapterFactoryEditingDomain editingDomain;

    /**
     * This is the one adapter ns used for providing views of the model.
      */
    protected ComposedAdapterFactory adapterFactory;

    /**
     * This is the content outline page. 
     */
    protected IContentOutlinePage contentOutlinePage;

    /**
     * This is a kludge... 
     */
    protected IStatusLineManager contentOutlineStatusLineManager;

    /**
     * This is the content outline page's viewer.
     */
    protected TreeViewer contentOutlineViewer;

    /**
     * This is the property sheet page.
     */
    protected List< PropertySheetPage > propertySheetPages = new ArrayList< PropertySheetPage >();

    /**
     * This is the viewer that shadows the selection in the content outline. The
     * parent relation must be correctly defined for this to work.
     */
    protected TreeViewer selectionViewer;

    /**
     * This inverts the roll of parent and child in the content provider and
     * show parents as a tree.
     */
    protected TreeViewer parentViewer;

    /**
     * This shows how a tree view works.
     */
    protected TreeViewer treeViewer;

    /**
     * This shows how a list view works. A list viewer doesn't support icons.
     */
    protected ListViewer listViewer;

    /**
     * This shows how a table view works. A table can be used as a list with
     * icons. 
     */
    protected TableViewer tableViewer;

    /**
     * This shows how a tree view with columns works.
     */
    protected TreeViewer treeViewerWithColumns;

    /**
     * This keeps track of the active viewer pane, in the book.
     */
    protected ViewerPane currentViewerPane;

    /**
     * This keeps track of the active content viewer, which may be either one of
     * the viewers in the pages or the content outline viewer.
     */
    protected Viewer currentViewer;

    /**
     * This listens to which ever viewer is active.
     */
    protected ISelectionChangedListener selectionChangedListener;

    /**
     * This keeps track of all the
     * {@link org.eclipse.jface.viewers.ISelectionChangedListener}s that are
     * listening to this editor.
     */
    protected Collection< ISelectionChangedListener > selectionChangedListeners = new ArrayList< ISelectionChangedListener >();

    /**
     * This keeps track of the selection of the editor as a whole.
     */
    protected ISelection editorSelection = StructuredSelection.EMPTY;

    /**
     * The MarkerHelper is responsible for creating workspace resource markers
     * presented in Eclipse's Problems View.
     */
    protected MarkerHelper markerHelper = new EditUIMarkerHelper();

    /**
     * This listens for when the outline becomes active
     */
    protected IPartListener partListener = new IPartListener() {
        public void partActivated( IWorkbenchPart p ) {
            if( p instanceof ContentOutline ) {
                if( ( ( ContentOutline ) p ).getCurrentPage() == contentOutlinePage ) {
                    getActionBarContributor().setActiveEditor( RiseClipseEditor.this );

                    setCurrentViewer( contentOutlineViewer );
                }
            }
            else if( p instanceof PropertySheet ) {
                if( propertySheetPages.contains( ( ( PropertySheet ) p ).getCurrentPage() ) ) {
                    getActionBarContributor().setActiveEditor( RiseClipseEditor.this );
                    handleActivate();
                }
            }
            else if( p == RiseClipseEditor.this ) {
                handleActivate();
            }
        }

        public void partBroughtToTop( IWorkbenchPart p ) {
            // Ignore.
        }

        public void partClosed( IWorkbenchPart p ) {
            // Ignore.
        }

        public void partDeactivated( IWorkbenchPart p ) {
            // Ignore.
        }

        public void partOpened( IWorkbenchPart p ) {
            // Ignore.
        }
    };

    /**
     * Resources that have been removed since last activation.
     */
    protected Collection< Resource > removedResources = new ArrayList< Resource >();

    /**
     * Resources that have been changed since last activation.
     */
    protected Collection< Resource > changedResources = new ArrayList< Resource >();

    /**
     * Resources that have been saved.
     */
    protected Collection< Resource > savedResources = new ArrayList< Resource >();

    /**
     * Map to store the diagnostic associated with a resource.
     */
    protected Map< Resource, Diagnostic > resourceToDiagnosticMap = new LinkedHashMap< Resource, Diagnostic >();

    /**
     * Controls whether the problem indication should be updated.
     */
    protected boolean updateProblemIndication = true;
    
    
    //===========================
    // RiseClipseSpecific begin

    
//    public AdapterFactory getAdapterFactory( String uri ) {
//        RiseClipseMetaModel mm = knownMetamodels.get( uri );
//        if( mm == null ) mm = knownMetamodels.get( uri + '#' );
//        if( mm == null ) return null;
//        return  mm.getAdapterFactory();
//    }
//    
//    public ViewerFilter getViewerFilter( String uri ) {
//        RiseClipseMetaModel mm = knownMetamodels.get( uri );
//        if( mm == null ) mm = knownMetamodels.get( uri + '#' );
//        if( mm == null ) return null;
//        return  mm.getViewerFilter();
//    }
    
    private Map< CommandContributionItem, Object > navigateToMap = null;
    
    public void setNavigateToMap( Map< CommandContributionItem, Object > map ) {
        navigateToMap = map;
    }
    
    public void navigateTo( CommandContributionItem command ) {
        navigateTo( navigateToMap.get( command ));
    }

    public void navigateTo( Object o ) {
        if( o == null ) return;
        // reveal will work only if the parent is itself revealed.
        // if the parent is o.eContainer(), it's OK.
        // Otherwise, getParent() in the appropriate subclass of ItemProviderAdapter
        // must be correctly defined.
        selectionViewer.reveal( o );
        selectionViewer.setSelection(
                new TreeSelection( new TreePath( new Object[]{ o })),
                true );
    }

    // RiseClipseSpecific end
    //=========================

    /**
     * Adapter used to update the problem indication when resources are demanded
     * loaded.
     */
    protected EContentAdapter problemIndicationAdapter = new EContentAdapter() {
        @Override
        public void notifyChanged( Notification notification ) {
            if( notification.getNotifier() instanceof Resource ) {
                switch( notification.getFeatureID( Resource.class ) ) {
                case Resource.RESOURCE__IS_LOADED:
                case Resource.RESOURCE__ERRORS:
                case Resource.RESOURCE__WARNINGS: {
                    Resource resource = ( Resource ) notification.getNotifier();
                    Diagnostic diagnostic = analyzeResourceProblems( resource, null );
                    if( diagnostic.getSeverity() != Diagnostic.OK ) {
                        resourceToDiagnosticMap.put( resource, diagnostic );
                    }
                    else {
                        resourceToDiagnosticMap.remove( resource );
                    }

                    if( updateProblemIndication ) {
                        getSite().getShell().getDisplay().asyncExec( new Runnable() {
                            public void run() {
                                updateProblemIndication();
                            }
                        } );
                    }
                    break;
                }
                }
            }
            else {
                super.notifyChanged( notification );
            }
        }

        @Override
        protected void setTarget( Resource target ) {
            basicSetTarget( target );
        }

        @Override
        protected void unsetTarget( Resource target ) {
            basicUnsetTarget( target );
            resourceToDiagnosticMap.remove( target );
            if( updateProblemIndication ) {
                getSite().getShell().getDisplay().asyncExec( new Runnable() {
                    public void run() {
                        updateProblemIndication();
                    }
                } );
            }
        }
    };

    /**
     * This listens for workspace changes.
     */
    protected IResourceChangeListener resourceChangeListener = new IResourceChangeListener() {
        public void resourceChanged( IResourceChangeEvent event ) {
            IResourceDelta delta = event.getDelta();
            try {
                class ResourceDeltaVisitor implements IResourceDeltaVisitor {
                    protected ResourceSet resourceSet = editingDomain.getResourceSet();
                    protected Collection< Resource > changedResources = new ArrayList< Resource >();
                    protected Collection< Resource > removedResources = new ArrayList< Resource >();

                    public boolean visit( IResourceDelta delta ) {
                        if( delta.getResource().getType() == IResource.FILE ) {
                            if( delta.getKind() == IResourceDelta.REMOVED || delta.getKind() == IResourceDelta.CHANGED
                                    && delta.getFlags() != IResourceDelta.MARKERS ) {
                                Resource resource = resourceSet.getResource(
                                        URI.createPlatformResourceURI( delta.getFullPath().toString(), true ), false );
                                if( resource != null ) {
                                    if( delta.getKind() == IResourceDelta.REMOVED ) {
                                        removedResources.add( resource );
                                    }
                                    else if( !savedResources.remove( resource ) ) {
                                        changedResources.add( resource );
                                    }
                                }
                            }
                            return false;
                        }

                        return true;
                    }

                    public Collection< Resource > getChangedResources() {
                        return changedResources;
                    }

                    public Collection< Resource > getRemovedResources() {
                        return removedResources;
                    }
                }

                final ResourceDeltaVisitor visitor = new ResourceDeltaVisitor();
                delta.accept( visitor );

                if( !visitor.getRemovedResources().isEmpty() ) {
                    getSite().getShell().getDisplay().asyncExec( new Runnable() {
                        public void run() {
                            removedResources.addAll( visitor.getRemovedResources() );
                            if( !isDirty() ) {
                                getSite().getPage().closeEditor( RiseClipseEditor.this, false );
                            }
                        }
                    } );
                }

                if( !visitor.getChangedResources().isEmpty() ) {
                    getSite().getShell().getDisplay().asyncExec( new Runnable() {
                        public void run() {
                            changedResources.addAll( visitor.getChangedResources() );
                            if( getSite().getPage().getActiveEditor() == RiseClipseEditor.this ) {
                                handleActivate();
                            }
                        }
                    } );
                }
            }
            catch( CoreException exception ) {
                RiseClipseEditorPlugin.INSTANCE.log( exception );
            }
        }
    };

    /**
     * Handles activation of the editor or it's associated views.
     */
    protected void handleActivate() {
        // Recompute the read only state.
        //
        if( editingDomain.getResourceToReadOnlyMap() != null ) {
            editingDomain.getResourceToReadOnlyMap().clear();

            // Refresh any actions that may become enabled or disabled.
            //
            setSelection( getSelection() );
        }

        if( !removedResources.isEmpty() ) {
            if( handleDirtyConflict() ) {
                getSite().getPage().closeEditor( RiseClipseEditor.this, false );
            }
            else {
                removedResources.clear();
                changedResources.clear();
                savedResources.clear();
            }
        }
        else if( !changedResources.isEmpty() ) {
            changedResources.removeAll( savedResources );
            handleChangedResources();
            changedResources.clear();
            savedResources.clear();
        }
    }

    /**
     * Handles what to do with changed resources on activation.
     */
    protected void handleChangedResources() {
        if( !changedResources.isEmpty() && ( !isDirty() || handleDirtyConflict() ) ) {
            if( isDirty() ) {
                changedResources.addAll( editingDomain.getResourceSet().getResources() );
            }
            editingDomain.getCommandStack().flush();

            updateProblemIndication = false;
            for( Resource resource : changedResources ) {
                if( resource.isLoaded() ) {
                    resource.unload();
                    try {
                        resource.load( Collections.EMPTY_MAP );
                    }
                    catch( IOException exception ) {
                        if( !resourceToDiagnosticMap.containsKey( resource ) ) {
                            resourceToDiagnosticMap.put( resource, analyzeResourceProblems( resource, exception ) );
                        }
                    }
                }
            }

            if( AdapterFactoryEditingDomain.isStale( editorSelection ) ) {
                setSelection( StructuredSelection.EMPTY );
            }

            updateProblemIndication = true;
            updateProblemIndication();
        }
    }

    /**
     * Updates the problems indication with the information described in the
     * specified diagnostic.
     */
    protected void updateProblemIndication() {
        if( updateProblemIndication ) {
            BasicDiagnostic diagnostic = new BasicDiagnostic( Diagnostic.OK,
                    "fr.centralesupelec.edf.riseclipse.editor", 0, null,
                    new Object[] { editingDomain.getResourceSet() } );
            for( Diagnostic childDiagnostic : resourceToDiagnosticMap.values() ) {
                if( childDiagnostic.getSeverity() != Diagnostic.OK ) {
                    diagnostic.add( childDiagnostic );
                }
            }

            int lastEditorPage = getPageCount() - 1;
            if( lastEditorPage >= 0 && getEditor( lastEditorPage ) instanceof ProblemEditorPart ) {
                ( ( ProblemEditorPart ) getEditor( lastEditorPage ) ).setDiagnostic( diagnostic );
                if( diagnostic.getSeverity() != Diagnostic.OK ) {
                    setActivePage( lastEditorPage );
                }
            }
            else if( diagnostic.getSeverity() != Diagnostic.OK ) {
                ProblemEditorPart problemEditorPart = new ProblemEditorPart();
                problemEditorPart.setDiagnostic( diagnostic );
                problemEditorPart.setMarkerHelper( markerHelper );
                try {
                    addPage( ++lastEditorPage, problemEditorPart, getEditorInput() );
                    setPageText( lastEditorPage, problemEditorPart.getPartName() );
                    setActivePage( lastEditorPage );
                    showTabs();
                }
                catch( PartInitException exception ) {
                    RiseClipseEditorPlugin.INSTANCE.log( exception );
                }
            }

            if( markerHelper.hasMarkers( editingDomain.getResourceSet() ) ) {
                markerHelper.deleteMarkers( editingDomain.getResourceSet() );
                if( diagnostic.getSeverity() != Diagnostic.OK ) {
                    try {
                        markerHelper.createMarkers( diagnostic );
                    }
                    catch( CoreException exception ) {
                        RiseClipseEditorPlugin.INSTANCE.log( exception );
                    }
                }
            }
        }
    }

    /**
     * Shows a dialog that asks if conflicting changes should be discarded.
     */
    protected boolean handleDirtyConflict() {
        return MessageDialog.openQuestion( getSite().getShell(), getString( "_UI_FileConflict_label" ),
                getString( "_WARN_FileConflict" ) );
    }

    //==========================
    // RiseClipseSpecific begin

    private IRiseClipseConsole console;

    /**
     * This creates a model editor.
     */
    public RiseClipseEditor() {
        super();
        
        this.console = EclipseRiseClipseConsole.useConsole( RISECLIPSE_CONSOLE_NAME );
        IPreferenceStore store = RiseClipseEditorPlugin.getPlugin().getPreferenceStore();
        int choice = store.getInt( IRiseClipseEditorConstants.PRE_SEVERITY_CHOICE );
        if( choice == 0 ) choice = 2;
        switch( choice ) {
        case 1:
            console.setLevel( Severity.INFO );
            break;
        case 2:
            console.setLevel( Severity.NOTICE );
            break;
        case 3:
            console.setLevel( Severity.WARNING );
            break;
        case 4:
            console.setLevel( Severity.ERROR );
            break;
        }
        
        RiseClipseMetamodel.loadKnownMetamodels( console );
        
        // Will be done later, in createModel
        //initializeEditingDomain();
    }
    
    // RiseClipseSpecific end
    //=========================

    /**
     * This sets up the editing domain for the model editor.
     */
    protected void initializeEditingDomain( URI resourceURI ) {
        
        // Create an adapter ns that yields item providers.
        //
        adapterFactory = new ComposedAdapterFactory( ComposedAdapterFactory.Descriptor.Registry.INSTANCE );

        // TODO: do we need it ?
        adapterFactory.addAdapterFactory( new ResourceItemProviderAdapterFactory() );

        
        // TODO: do we need it ?
        adapterFactory.addAdapterFactory( new ReflectiveItemProviderAdapterFactory() );
        
        Optional<String> metamodelName = RiseClipseMetamodel.findMetamodelFor( resourceURI );
        
        Optional< IRiseClipseResourceSet > resourceSet = Optional.empty();
        if( metamodelName.isPresent() ) {
            RiseClipseMetamodel metamodel = RiseClipseMetamodel.getMetamodel( metamodelName.get() ).get();
            if( metamodel.getResourceSetFactory() != null ) {
                // Not strict content for Editor
                resourceSet = metamodel.getResourceSetFactory().map( f -> f.createResourceSet( false ));
            }
            if( metamodel.getAdapterFactory() != null ) {
                adapterFactory.addAdapterFactory( metamodel.getAdapterFactory().get() );
            }
        }

        // Create the command stack that will notify this editor as commands are
        // executed.
        //
        BasicCommandStack commandStack = new BasicCommandStack();

        // Add a listener to set the most recent command's affected objects to
        // be the selection of the viewer with focus.
        //
        commandStack.addCommandStackListener( new CommandStackListener() {
            public void commandStackChanged( final EventObject event ) {
                getContainer().getDisplay().asyncExec( new Runnable() {
                    public void run() {
                        firePropertyChange( IEditorPart.PROP_DIRTY );

                        // Try to select the affected objects.
                        //
                        Command mostRecentCommand = ( ( CommandStack ) event.getSource() ).getMostRecentCommand();
                        if( mostRecentCommand != null ) {
                            setSelectionToViewer( mostRecentCommand.getAffectedObjects() );
                        }
                        for( Iterator< PropertySheetPage > i = propertySheetPages.iterator(); i.hasNext(); ) {
                            PropertySheetPage propertySheetPage = i.next();
                            if( propertySheetPage.getControl().isDisposed() ) {
                                i.remove();
                            }
                            else {
                                propertySheetPage.refresh();
                            }
                        }
                    }
                } );
            }
        } );

        // Create the editing domain with a special command stack.
        //
        //=========================
        // RiseClipseSpecific begin
        
        if( resourceSet.isPresent() ) {
            editingDomain = new AdapterFactoryEditingDomain( adapterFactory, commandStack,
                    resourceSet.get() );
            editingDomain.setResourceToReadOnlyMap( new HashMap< Resource, Boolean >() );
        }
        else {
            editingDomain = new AdapterFactoryEditingDomain( adapterFactory, commandStack,
                    new HashMap< Resource, Boolean >() );
        }

        // RiseClipseSpecific end
        //========================
    }

    /**
     * This is here for the listener to be able to call it.
     */
    @Override
    protected void firePropertyChange( int action ) {
        super.firePropertyChange( action );
    }

    /**
     * This sets the selection into whichever viewer is active.
     */
    public void setSelectionToViewer( Collection< ? > collection ) {
        final Collection< ? > theSelection = collection;
        // Make sure it's okay.
        //
        if( theSelection != null && !theSelection.isEmpty() ) {
            Runnable runnable = new Runnable() {
                public void run() {
                    // Try to select the items in the current content viewer of
                    // the editor.
                    //
                    if( currentViewer != null ) {
                        currentViewer.setSelection( new StructuredSelection( theSelection.toArray() ), true );
                    }
                }
            };
            getSite().getShell().getDisplay().asyncExec( runnable );
        }
    }

    /**
     * This returns the editing domain as required by the
     * {@link IEditingDomainProvider} interface. This is important for
     * implementing the static methods of {@link AdapterFactoryEditingDomain}
     * and for supporting {@link org.eclipse.emf.edit.ui.action.CommandAction}.
     */
    public EditingDomain getEditingDomain() {
        return editingDomain;
    }

    /**
     * 
     */
    public class ReverseAdapterFactoryContentProvider extends AdapterFactoryContentProvider {
        /**
         * 
         */
        public ReverseAdapterFactoryContentProvider( AdapterFactory adapterFactory ) {
            super( adapterFactory );
        }

        /**
         * 
         */
        @Override
        public Object[] getElements( Object object ) {
            Object parent = super.getParent( object );
            return ( parent == null ? Collections.EMPTY_SET : Collections.singleton( parent ) ).toArray();
        }

        /**
         * 
         */
        @Override
        public Object[] getChildren( Object object ) {
            Object parent = super.getParent( object );
            return ( parent == null ? Collections.EMPTY_SET : Collections.singleton( parent ) ).toArray();
        }

        /**
         * 
         */
        @Override
        public boolean hasChildren( Object object ) {
            Object parent = super.getParent( object );
            return parent != null;
        }

        /**
         * 
         */
        @Override
        public Object getParent( Object object ) {
            return null;
        }
    }

    /**
     * 
     */
    public void setCurrentViewerPane( ViewerPane viewerPane ) {
        if( currentViewerPane != viewerPane ) {
            if( currentViewerPane != null ) {
                currentViewerPane.showFocus( false );
            }
            currentViewerPane = viewerPane;
        }
        setCurrentViewer( currentViewerPane.getViewer() );
    }

    /**
     * This makes sure that one content viewer, either for the current page or
     * the outline view, if it has focus, is the current one. 
     */
    public void setCurrentViewer( Viewer viewer ) {
        // If it is changing...
        //
        if( currentViewer != viewer ) {
            if( selectionChangedListener == null ) {
                // Create the listener on demand.
                //
                selectionChangedListener = new ISelectionChangedListener() {
                    // This just notifies those things that are affected by the
                    // section.
                    //
                    public void selectionChanged( SelectionChangedEvent selectionChangedEvent ) {
                        setSelection( selectionChangedEvent.getSelection() );
                    }
                };
            }

            // Stop listening to the old one.
            //
            if( currentViewer != null ) {
                currentViewer.removeSelectionChangedListener( selectionChangedListener );
            }

            // Start listening to the new one.
            //
            if( viewer != null ) {
                viewer.addSelectionChangedListener( selectionChangedListener );
            }

            // Remember it.
            //
            currentViewer = viewer;

            // Set the editors selection based on the current viewer's
            // selection.
            //
            setSelection( currentViewer == null ? StructuredSelection.EMPTY : currentViewer.getSelection() );
        }
    }

    /**
     * This returns the viewer as required by the {@link IViewerProvider}
     * interface.
     */
    public Viewer getViewer() {
        return currentViewer;
    }

    /**
     * This creates a context menu for the viewer and adds a listener as well
     * registering the menu for extension.
     */
    protected void createContextMenuFor( StructuredViewer viewer ) {
        MenuManager contextMenu = new MenuManager( "#PopUp" );
        contextMenu.add( new Separator( "additions" ) );
        contextMenu.setRemoveAllWhenShown( true );
        contextMenu.addMenuListener( this );
        Menu menu = contextMenu.createContextMenu( viewer.getControl() );
        viewer.getControl().setMenu( menu );
        getSite().registerContextMenu( contextMenu, new UnwrappingSelectionProvider( viewer ) );

        int dndOperations = DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK;
        Transfer[] transfers = new Transfer[] { LocalTransfer.getInstance(), LocalSelectionTransfer.getTransfer(),
                FileTransfer.getInstance() };
        viewer.addDragSupport( dndOperations, transfers, new ViewerDragAdapter( viewer ) );
        viewer.addDropSupport( dndOperations, transfers, new EditingDomainViewerDropAdapter( editingDomain, viewer ) );
    }

    
    /**
     * This is the method called to load a resource into the editing domain's
     * resource set based on the editor's input.
     */
    public void createModel() {

        // In the standard generated EMF editor, the EditingDomain (org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain)
        // is created with the editor, the ResourceSet is created by the EditingDomain.
        //
        // We want to use a metamodel specific ResourceSet. To get the right one, we need to get the root element of the XML file.
        // the namespace (URI) of the root element may not be the right one (for example, the root of an CIMXML file belongs to
        // the rdf namespace, not to the CIM one), so we look at xmlns attributes and stop as soon as we got one which is known
        // through RiseClipse metamodel extension point.
        // 
        // But to get the root element, we need to create an InputStream which is done by an URIConverter which is usually obtained
        // through the ResourceSet !
        // We also need to create an InputStream to check for a zip archive.
        //
        // Available options:
        //   1 - create first a standard ResourceSet, then the right one
        //   2 - create an URIConverter
        //   3 - ??
        // 
        // We try with option 2.
        
        URIConverter uriConverter = new ExtensibleURIConverterImpl();

        ArrayList< URI > resourceURIs = new ArrayList< URI >();
        URI uri = EditUIUtil.getURI( getEditorInput() );
        this.console = EclipseRiseClipseConsole.useConsole( uri.lastSegment() );
        resourceURIs.add( uri );
        
        try {
            ZipInputStream in = new ZipInputStream( uriConverter.createInputStream( resourceURIs.get( 0 )));
            ZipEntry entry = in.getNextEntry();
            if( entry != null ) {
                String zipURI = resourceURIs.get( 0 ).toString();
                resourceURIs.clear();
                while( entry != null ) {
                    if( ! entry.isDirectory() ) {
                        // Must use "archive:" and not "zip:" to be recognized by ArchiveURIHandlerImpl
                        resourceURIs.add( URI.createURI( "archive:" + zipURI + "!/" + entry.getName() ));
                    }
                    entry = in.getNextEntry();
                }
            }
            in.close();
        }
        catch( IOException e ) {
            // Will be handled later
        }
        
        for( URI resourceURI : resourceURIs ) {
            // First time
            if( editingDomain == null ) {
                
                // Find the right ResourceSet, create the EditingDomain with it
                initializeEditingDomain( resourceURI );
            }
            
            
            try {
                // Load the resource through the editing domain.
                //
                @SuppressWarnings("unused")
                Resource resource = editingDomain.getResourceSet().getResource( resourceURI, true );
            }
            // This is done by AbstractRiseClipseModelLoader in the command line tool 
            catch( RuntimeException re ) {
                Throwable cause = re.getCause();
                if( cause instanceof IllegalValueException ) {
                    IllegalValueException e = ( IllegalValueException ) cause;
                    console.error( EDITOR_CATEGORY, e.getLine(),
                            "value ", e.getValue(), " is not legal for feature ",
                            e.getFeature().getName(), ", it should be ", e.getFeature().getEType().getInstanceTypeName() );
                }
                else if( cause != null ) {
                    console.error( EDITOR_CATEGORY, 0, "Problem loading ", resourceURI, " : ", cause );
                }
                else {
                    console.error( EDITOR_CATEGORY, 0, "Problem loading ", resourceURI, " : ", re );
                }
            }
            catch( Exception e ) {
                console.error( EDITOR_CATEGORY, 0, "Problem loading ", resourceURI, " : ", e );
            }
            
            // This is the original code generated by EMF: not sure what must be kept
//            catch( Exception e ) {
//                exception = e;
//                resource = editingDomain.getResourceSet().getResource( resourceURI, false );
//            }
//    
//            Diagnostic diagnostic = analyzeResourceProblems( resource, exception );
//            if( diagnostic.getSeverity() != Diagnostic.OK ) {
//                resourceToDiagnosticMap.put( resource, analyzeResourceProblems( resource, exception ) );
//            }
//            editingDomain.getResourceSet().eAdapters().add( problemIndicationAdapter );
        }
        
        // Let each resource do what it needs after all is loaded
        // This is at least needed for CIM with zip files containing several
        // resources and links to be set between objects in different resources
        if( editingDomain.getResourceSet() instanceof AbstractRiseClipseResourceSet ) {
            (( AbstractRiseClipseResourceSet ) editingDomain.getResourceSet() ).finalizeLoad( console );
            (( AbstractRiseClipseResourceSet ) editingDomain.getResourceSet() ).setCallFinalizeLoadAfterGetResource();
        }
    }

    /**
     * Returns a diagnostic describing the errors and warnings listed in the
     * resource and the specified exception (if any).
     */
    public Diagnostic analyzeResourceProblems( Resource resource, Exception exception ) {
        if( !resource.getErrors().isEmpty() || !resource.getWarnings().isEmpty() ) {
            BasicDiagnostic basicDiagnostic = new BasicDiagnostic( Diagnostic.ERROR,
                    "fr.centralesupelec.edf.riseclipse.editor", 0, getString( "_UI_CreateModelError_message",
                            resource.getURI() ), new Object[] { exception == null ? ( Object ) resource : exception } );
            basicDiagnostic.merge( EcoreUtil.computeDiagnostic( resource, true ) );
            return basicDiagnostic;
        }
        else if( exception != null ) {
            return new BasicDiagnostic( Diagnostic.ERROR, "fr.centralesupelec.edf.riseclipse.editor", 0,
                    getString( "_UI_CreateModelError_message", resource.getURI() ), new Object[] { exception } );
        }
        else {
            return Diagnostic.OK_INSTANCE;
        }
    }

    
    /**
     * This is the method used by the framework to install your own controls.
     */
    @Override
    public void createPages() {
        // Creates the model from the editor input
        //
        createModel();

        // RiseClipseSpecific : only the selection tree view.
        
        // Only creates the other pages if there is something that can be edited
        //
        if( !getEditingDomain().getResourceSet().getResources().isEmpty() ) {
            // Create a page for the selection tree view.
            //
            {
                ViewerPane viewerPane = new ViewerPane( getSite().getPage(), RiseClipseEditor.this ) {
                    @Override
                    public Viewer createViewer( Composite composite ) {
                        Tree tree = new Tree( composite, SWT.MULTI );
                        TreeViewer newTreeViewer = new TreeViewer( tree );
                        return newTreeViewer;
                    }

                    @Override
                    public void requestActivation() {
                        super.requestActivation();
                        setCurrentViewerPane( this );
                    }
                };
                viewerPane.createControl( getContainer() );

                selectionViewer = ( TreeViewer ) viewerPane.getViewer();
                selectionViewer.setContentProvider( new AdapterFactoryContentProvider( adapterFactory ) );
                
                // RiseClispeSpecific : for navigateTo speedup
                // Can only enable the hash look up before input has been set
                selectionViewer.setUseHashlookup(  true  );
                // build all levels for navigateTo
                // This takes to much time on big models, see navigateTo() for the new way
                //selectionViewer.setAutoExpandLevel( AbstractTreeViewer.ALL_LEVELS );

                selectionViewer.setLabelProvider( new AdapterFactoryLabelProvider( adapterFactory ) );
                selectionViewer.setInput( editingDomain.getResourceSet() );
                selectionViewer.setSelection( new StructuredSelection( editingDomain.getResourceSet().getResources()
                        .get( 0 ) ), true );
                viewerPane.setTitle( editingDomain.getResourceSet() );
                
                // RiseClispeSpecific : use a filter if given
                // TODO: get it more directly ? 
                String uri = null;
                EList< Resource > resources = editingDomain.getResourceSet().getResources();
                if( resources.size() > 0 ) {
                    EList< EObject > contents = resources.get( 0 ).getContents();
                    // The first element may be the model description in CIM files, it is not the right URI
                    if( contents.size() > 1 ) {
                        uri = contents.get( 1 ).eClass().getEPackage().getNsURI();
                    }
                    else if( contents.size() > 0 ) {
                        uri = contents.get( 0 ).eClass().getEPackage().getNsURI();
                    }
                }
                if( uri != null ) {
                    Optional< ViewerFilter > filter = RiseClipseMetamodel.getMetamodel( uri ).flatMap( mm -> mm.getViewerFilter() );
                    if( filter.isPresent() ) {
                        selectionViewer.addFilter( filter.get() );
                    }
                }

                new AdapterFactoryTreeEditor( selectionViewer.getTree(), adapterFactory );

                createContextMenuFor( selectionViewer );
                int pageIndex = addPage( viewerPane.getControl() );
                setPageText( pageIndex, getString( "_UI_SelectionPage_label" ) );
            }

            getSite().getShell().getDisplay().asyncExec( new Runnable() {
                public void run() {
                    setActivePage( 0 );
                }
            } );
        }

        // Ensures that this editor will only display the page's tab
        // area if there are more than one page
        //
        getContainer().addControlListener( new ControlAdapter() {
            boolean guard = false;

            @Override
            public void controlResized( ControlEvent event ) {
                if( !guard ) {
                    guard = true;
                    hideTabs();
                    guard = false;
                }
            }
        } );

        getSite().getShell().getDisplay().asyncExec( new Runnable() {
            public void run() {
                updateProblemIndication();
            }
        } );
    }

    /**
     * If there is just one page in the multi-page editor part, this hides the
     * single tab at the bottom.
     */
    protected void hideTabs() {
        if( getPageCount() <= 1 ) {
            setPageText( 0, "" );
            if( getContainer() instanceof CTabFolder ) {
                ( ( CTabFolder ) getContainer() ).setTabHeight( 1 );
                Point point = getContainer().getSize();
                getContainer().setSize( point.x, point.y + 6 );
            }
        }
    }

    /**
     * If there is more than one page in the multi-page editor part, this shows
     * the tabs at the bottom.
     */
    protected void showTabs() {
        if( getPageCount() > 1 ) {
            setPageText( 0, getString( "_UI_SelectionPage_label" ) );
            if( getContainer() instanceof CTabFolder ) {
                ( ( CTabFolder ) getContainer() ).setTabHeight( SWT.DEFAULT );
                Point point = getContainer().getSize();
                getContainer().setSize( point.x, point.y - 6 );
            }
        }
    }

    /**
     * This is used to track the active viewer.
     */
    @Override
    protected void pageChange( int pageIndex ) {
        super.pageChange( pageIndex );

        if( contentOutlinePage != null ) {
            handleContentOutlineSelection( contentOutlinePage.getSelection() );
        }
    }

    /**
     * This is how the framework determines which interfaces we implement.
     */
    @SuppressWarnings( { "rawtypes", "unchecked" } )
    @Override
    public Object getAdapter( Class key ) {
        if( key.equals( IContentOutlinePage.class ) ) {
            return showOutlineView() ? getContentOutlinePage() : null;
        }
        else if( key.equals( IPropertySheetPage.class ) ) {
            return getPropertySheetPage();
        }
        else if( key.equals( IGotoMarker.class ) ) {
            return this;
        }
        else {
            return super.getAdapter( key );
        }
    }

    /**
     * This accesses a cached version of the content outliner.
     */
    public IContentOutlinePage getContentOutlinePage() {
        if( contentOutlinePage == null ) {
            // The content outline is just a tree.
            //
            class MyContentOutlinePage extends ContentOutlinePage {
                @Override
                public void createControl( Composite parent ) {
                    super.createControl( parent );
                    contentOutlineViewer = getTreeViewer();
                    contentOutlineViewer.addSelectionChangedListener( this );

                    // Set up the tree viewer.
                    //
                    contentOutlineViewer.setContentProvider( new AdapterFactoryContentProvider( adapterFactory ) );
                    contentOutlineViewer.setLabelProvider( new AdapterFactoryLabelProvider( adapterFactory ) );
                    contentOutlineViewer.setInput( editingDomain.getResourceSet() );

                    // Make sure our popups work.
                    //
                    createContextMenuFor( contentOutlineViewer );

                    if( !editingDomain.getResourceSet().getResources().isEmpty() ) {
                        // Select the root object in the view.
                        //
                        contentOutlineViewer.setSelection( new StructuredSelection( editingDomain.getResourceSet()
                                .getResources().get( 0 ) ), true );
                    }
                }

                @Override
                public void makeContributions( IMenuManager menuManager, IToolBarManager toolBarManager,
                        IStatusLineManager statusLineManager ) {
                    super.makeContributions( menuManager, toolBarManager, statusLineManager );
                    contentOutlineStatusLineManager = statusLineManager;
                }

                @Override
                public void setActionBars( IActionBars actionBars ) {
                    super.setActionBars( actionBars );
                    getActionBarContributor().shareGlobalActions( this, actionBars );
                }
            }

            contentOutlinePage = new MyContentOutlinePage();

            // Listen to selection so that we can handle it is a special way.
            //
            contentOutlinePage.addSelectionChangedListener( new ISelectionChangedListener() {
                // This ensures that we handle selections correctly.
                //
                public void selectionChanged( SelectionChangedEvent event ) {
                    handleContentOutlineSelection( event.getSelection() );
                }
            } );
        }

        return contentOutlinePage;
    }

    /**
     * This accesses a cached version of the property sheet.
     */
    public IPropertySheetPage getPropertySheetPage() {
        PropertySheetPage propertySheetPage = new ExtendedPropertySheetPage( editingDomain ) {
            @Override
            public void setSelectionToViewer( List< ? > selection ) {
                RiseClipseEditor.this.setSelectionToViewer( selection );
                RiseClipseEditor.this.setFocus();
            }

            @Override
            public void setActionBars( IActionBars actionBars ) {
                super.setActionBars( actionBars );
                getActionBarContributor().shareGlobalActions( this, actionBars );
            }
        };
        propertySheetPage.setPropertySourceProvider( new AdapterFactoryContentProvider( adapterFactory ) );
        propertySheetPages.add( propertySheetPage );

        return propertySheetPage;
    }

    /**
     * This deals with how we want selection in the outliner to affect the other
     * views.
     */
    public void handleContentOutlineSelection( ISelection selection ) {
        if( currentViewerPane != null && !selection.isEmpty() && selection instanceof IStructuredSelection ) {
            Iterator< ? > selectedElements = ( ( IStructuredSelection ) selection ).iterator();
            if( selectedElements.hasNext() ) {
                // Get the first selected element.
                //
                Object selectedElement = selectedElements.next();

                // If it's the selection viewer, then we want it to select the
                // same selection as this selection.
                //
                if( currentViewerPane.getViewer() == selectionViewer ) {
                    ArrayList< Object > selectionList = new ArrayList< Object >();
                    selectionList.add( selectedElement );
                    while( selectedElements.hasNext() ) {
                        selectionList.add( selectedElements.next() );
                    }

                    // Set the selection to the widget.
                    //
                    selectionViewer.setSelection( new StructuredSelection( selectionList ) );
                }
                else {
                    // Set the input to the widget.
                    //
                    if( currentViewerPane.getViewer().getInput() != selectedElement ) {
                        currentViewerPane.getViewer().setInput( selectedElement );
                        currentViewerPane.setTitle( selectedElement );
                    }
                }
            }
        }
    }

    /**
     * This is for implementing {@link IEditorPart} and simply tests the command
     * stack.
     */
    @Override
    public boolean isDirty() {
        if( editingDomain == null ) return false;
        return ( ( BasicCommandStack ) editingDomain.getCommandStack() ).isSaveNeeded();
    }

    /**
     * This is for implementing {@link IEditorPart} and simply saves the model
     * file.
     */
    @Override
    public void doSave( IProgressMonitor progressMonitor ) {
        // Save only resources that have actually changed.
        //
        final Map< Object, Object > saveOptions = new HashMap< Object, Object >();
        saveOptions.put( Resource.OPTION_SAVE_ONLY_IF_CHANGED, Resource.OPTION_SAVE_ONLY_IF_CHANGED_MEMORY_BUFFER );
        saveOptions.put( Resource.OPTION_LINE_DELIMITER, Resource.OPTION_LINE_DELIMITER_UNSPECIFIED );

        // Do the work within an operation because this is a long running
        // activity that modifies the workbench.
        //
        WorkspaceModifyOperation operation = new WorkspaceModifyOperation() {
            // This is the method that gets invoked when the operation runs.
            //
            @Override
            public void execute( IProgressMonitor monitor ) {
                // Save the resources to the file system.
                //
                boolean first = true;
                for( Resource resource : editingDomain.getResourceSet().getResources() ) {
                    if( ( first || !resource.getContents().isEmpty() || isPersisted( resource ) )
                            && !editingDomain.isReadOnly( resource ) ) {
                        try {
                            long timeStamp = resource.getTimeStamp();
                            resource.save( saveOptions );
                            if( resource.getTimeStamp() != timeStamp ) {
                                savedResources.add( resource );
                            }
                        }
                        catch( Exception exception ) {
                            resourceToDiagnosticMap.put( resource, analyzeResourceProblems( resource, exception ) );
                        }
                        first = false;
                    }
                }
            }
        };

        updateProblemIndication = false;
        try {
            // This runs the options, and shows progress.
            //
            new ProgressMonitorDialog( getSite().getShell() ).run( true, false, operation );

            // Refresh the necessary state.
            //
            ( ( BasicCommandStack ) editingDomain.getCommandStack() ).saveIsDone();
            firePropertyChange( IEditorPart.PROP_DIRTY );
        }
        catch( Exception exception ) {
            // Something went wrong that shouldn't.
            //
            RiseClipseEditorPlugin.INSTANCE.log( exception );
        }
        updateProblemIndication = true;
        updateProblemIndication();
    }

    /**
     * This returns whether something has been persisted to the URI of the
     * specified resource. The implementation uses the URI converter from the
     * editor's resource set to try to open an input stream.
     */
    protected boolean isPersisted( Resource resource ) {
        boolean result = false;
        try {
            InputStream stream = editingDomain.getResourceSet().getURIConverter().createInputStream( resource.getURI() );
            if( stream != null ) {
                result = true;
                stream.close();
            }
        }
        catch( IOException e ) {
            // Ignore
        }
        return result;
    }

    /**
     * This always returns true because it is not currently supported.
     */
    @Override
    public boolean isSaveAsAllowed() {
        return true;
    }

    /**
     * This also changes the editor's input.
     */
    @Override
    public void doSaveAs() {
        SaveAsDialog saveAsDialog = new SaveAsDialog( getSite().getShell() );
        saveAsDialog.open();
        IPath path = saveAsDialog.getResult();
        if( path != null ) {
            IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile( path );
            if( file != null ) {
                doSaveAs( URI.createPlatformResourceURI( file.getFullPath().toString(), true ), new FileEditorInput(
                        file ) );
            }
        }
    }

    /**
     */
    protected void doSaveAs( URI uri, IEditorInput editorInput ) {
        ( editingDomain.getResourceSet().getResources().get( 0 ) ).setURI( uri );
        setInputWithNotify( editorInput );
        setPartName( editorInput.getName() );
        IProgressMonitor progressMonitor = getActionBars().getStatusLineManager() != null ? getActionBars()
                .getStatusLineManager().getProgressMonitor() : new NullProgressMonitor();
        doSave( progressMonitor );
    }

    /**
     */
    public void gotoMarker( IMarker marker ) {
        List< ? > targetObjects = markerHelper.getTargetObjects( editingDomain, marker );
        if( !targetObjects.isEmpty() ) {
            setSelectionToViewer( targetObjects );
        }
    }

    /**
     * This is called during startup.
     */
    @Override
    public void init( IEditorSite site, IEditorInput editorInput ) {
        setSite( site );
        setInputWithNotify( editorInput );
        setPartName( editorInput.getName() );
        site.setSelectionProvider( this );
        site.getPage().addPartListener( partListener );
        ResourcesPlugin.getWorkspace().addResourceChangeListener( resourceChangeListener,
                IResourceChangeEvent.POST_CHANGE );
    }

    /**
     */
    @Override
    public void setFocus() {
        if( currentViewerPane != null ) {
            currentViewerPane.setFocus();
        }
        else {
            getControl( getActivePage() ).setFocus();
        }
    }

    /**
     * This implements {@link org.eclipse.jface.viewers.ISelectionProvider}.
     */
    public void addSelectionChangedListener( ISelectionChangedListener listener ) {
        selectionChangedListeners.add( listener );
    }

    /**
     * This implements {@link org.eclipse.jface.viewers.ISelectionProvider}.
     */
    public void removeSelectionChangedListener( ISelectionChangedListener listener ) {
        selectionChangedListeners.remove( listener );
    }

    /**
     * This implements {@link org.eclipse.jface.viewers.ISelectionProvider} to
     * return this editor's overall selection.
     */
    public ISelection getSelection() {
        return editorSelection;
    }

    /**
     * This implements {@link org.eclipse.jface.viewers.ISelectionProvider} to
     * set this editor's overall selection. Calling this result will notify the
     * listeners.
     */
    public void setSelection( ISelection selection ) {
        editorSelection = selection;

        for( ISelectionChangedListener listener : selectionChangedListeners ) {
            listener.selectionChanged( new SelectionChangedEvent( this, selection ) );
        }
        setStatusLineManager( selection );
    }

    /**
     */
    public void setStatusLineManager( ISelection selection ) {
        IStatusLineManager statusLineManager = currentViewer != null && currentViewer == contentOutlineViewer ? contentOutlineStatusLineManager
                : getActionBars().getStatusLineManager();

        if( statusLineManager != null ) {
            if( selection instanceof IStructuredSelection ) {
                Collection< ? > collection = ( ( IStructuredSelection ) selection ).toList();
                switch( collection.size() ) {
                case 0: {
                    statusLineManager.setMessage( getString( "_UI_NoObjectSelected" ) );
                    break;
                }
                case 1: {
                    String text = new AdapterFactoryItemDelegator( adapterFactory ).getText( collection.iterator()
                            .next() );
                    statusLineManager.setMessage( getString( "_UI_SingleObjectSelected", text ) );
                    break;
                }
                default: {
                    statusLineManager.setMessage( getString( "_UI_MultiObjectSelected",
                            Integer.toString( collection.size() ) ) );
                    break;
                }
                }
            }
            else {
                statusLineManager.setMessage( "" );
            }
        }
    }

    /**
     * This looks up a string in the plugin's plugin.properties file.
     */
    private static String getString( String key ) {
        return RiseClipseEditorPlugin.INSTANCE.getString( key );
    }

    /**
     * This looks up a string in plugin.properties, making a substitution.
     */
    private static String getString( String key, Object s1 ) {
        return RiseClipseEditorPlugin.INSTANCE.getString( key, new Object[] { s1 } );
    }

    /**
     * This implements {@link org.eclipse.jface.action.IMenuListener} to help
     * fill the context menus with contributions from the Edit menu.
     */
    public void menuAboutToShow( IMenuManager menuManager ) {
        ( ( IMenuListener ) getEditorSite().getActionBarContributor() ).menuAboutToShow( menuManager );
    }

    /**
     */
    public EditingDomainActionBarContributor getActionBarContributor() {
        return ( EditingDomainActionBarContributor ) getEditorSite().getActionBarContributor();
    }

    /**
     */
    public IActionBars getActionBars() {
        return getActionBarContributor().getActionBars();
    }

    /**
     */
    public AdapterFactory getAdapterFactory() {
        return adapterFactory;
    }

    /**
     */
    @Override
    public void dispose() {
        // Remove console (see editingDomain)
        EclipseRiseClipseConsole.disposeConsole( getPartName() );
        this.console = EclipseRiseClipseConsole.useConsole( RISECLIPSE_CONSOLE_NAME );
        
        // Loaded resource still referenced by editing domain and other attributes
        // 31 January 2025: fail to find the root cause of this!
        // For example, ValidateAction keeps a link on the resource even if the corresponding window is closed
        // And we may need to change EMF to, give a solution to this.
        // So we will live with these memory leaks. 
        editingDomain = null;
        selectionViewer = null;
        currentViewer = null;
        currentViewerPane = null;
        contentOutlineViewer = null;
        editorSelection = StructuredSelection.EMPTY;
        
        updateProblemIndication = false;

        ResourcesPlugin.getWorkspace().removeResourceChangeListener( resourceChangeListener );

        getSite().getPage().removePartListener( partListener );

        adapterFactory.dispose();

        if( getActionBarContributor().getActiveEditor() == this ) {
            getActionBarContributor().setActiveEditor( null );
        }

        for( PropertySheetPage propertySheetPage : propertySheetPages ) {
            propertySheetPage.dispose();
        }

        if( contentOutlinePage != null ) {
            contentOutlinePage.dispose();
        }

        super.dispose();
    }

    /**
     * Returns whether the outline view should be presented to the user.
     */
    protected boolean showOutlineView() {
        return true;
    }
}

/*
*************************************************************************
**  Copyright (c) 2025 CentraleSupélec & EDF.
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
package fr.centralesupelec.edf.riseclipse.editor.preferences;

import org.eclipse.jface.preference.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.IWorkbench;
import fr.centralesupelec.edf.riseclipse.ui.IRiseClipseEditorConstants;
import fr.centralesupelec.edf.riseclipse.ui.RiseClipseEditorPlugin;

/*
 * Mainly from org.eclipse.ui.examples.readmetool
 */

public class RiseClipsePreferencePage
        extends PreferencePage
        implements IWorkbenchPreferencePage, SelectionListener, ModifyListener {
    
    private Button    infoButton;
    private Button  noticeButton;
    private Button warningButton;
    private Button   errorButton;

    /**
     * (non-Javadoc) Method declared on PreferencePage
     */
    @Override
    protected Control createContents(Composite parent) {
        PlatformUI.getWorkbench().getHelpSystem().setHelp( parent, IRiseClipseEditorConstants.PREFERENCE_PAGE_CONTEXT );
    
        Composite severity_tab = createComposite( parent, 2 );
        createLabel( severity_tab, "Severity" ); //$NON-NLS-1$
        tabForward( severity_tab );
        Composite severity_radioButton = createComposite( severity_tab, 1 );
           infoButton = createRadioButton( severity_radioButton, "Info"    ); //$NON-NLS-1$
         noticeButton = createRadioButton( severity_radioButton, "Notice"  ); //$NON-NLS-1$
        warningButton = createRadioButton( severity_radioButton, "Warning" ); //$NON-NLS-1$
          errorButton = createRadioButton( severity_radioButton, "Error"   ); //$NON-NLS-1$
        
        initializeValues();

        // font = null;
        return new Composite( parent, SWT.NULL );
    }
    
    /**
     * Creates composite control and sets the default layout data.
     *
     * @param parent     the parent of the new composite
     * @param numColumns the number of columns for the new composite
     * @return the newly-created composite
     */
    private Composite createComposite( Composite parent, int numColumns ) {
        Composite composite = new Composite( parent, SWT.NULL );

        // GridLayout
        GridLayout layout = new GridLayout();
        layout.numColumns = numColumns;
        composite.setLayout( layout );

        // GridData
        GridData data = new GridData();
        data.verticalAlignment = GridData.FILL;
        data.horizontalAlignment = GridData.FILL;
        composite.setLayoutData( data );
        return composite;
    }

    /**
     * Utility method that creates a label instance and sets the default layout
     * data.
     *
     * @param parent the parent for the new label
     * @param text   the text for the new label
     * @return the new label
     */
    private Label createLabel( Composite parent, String text ) {
        Label label = new Label( parent, SWT.LEFT );
        label.setText( text );
        GridData data = new GridData();
        data.horizontalSpan = 2;
        data.horizontalAlignment = GridData.FILL;
        label.setLayoutData( data );
        return label;
    }

    /**
     * Utility method that creates a radio button instance and sets the default
     * layout data.
     *
     * @param parent the parent for the new button
     * @param label  the label for the new button
     * @return the newly-created button
     */
    private Button createRadioButton( Composite parent, String label ) {
        Button button = new Button( parent, SWT.RADIO | SWT.LEFT );
        button.setText( label );
        button.addSelectionListener( this );
        GridData data = new GridData();
        button.setLayoutData( data );
        return button;
    }

    /**
     * Creates a tab of one horizontal spans.
     *
     * @param parent the parent in which the tab should be created
     */
    private void tabForward( Composite parent ) {
        Label vfiller = new Label( parent, SWT.LEFT );
        GridData gridData = new GridData();
        gridData = new GridData();
        gridData.horizontalAlignment = GridData.BEGINNING;
        gridData.grabExcessHorizontalSpace = false;
        gridData.verticalAlignment = GridData.CENTER;
        gridData.grabExcessVerticalSpace = false;
        vfiller.setLayoutData( gridData );
    }

    @Override
    protected void performDefaults() {
        super.performDefaults();
        initializeDefaults();
    }

    @Override
    public boolean performOk() {
        storeValues();
        RiseClipseEditorPlugin.getPlugin().savePluginPreferences();
        return true;
    }

    /**
     * Stores the values of the controls back to the preference store.
     */
    private void storeValues() {
        IPreferenceStore store = getPreferenceStore();

        int choice = 1;
        if( noticeButton.getSelection() )
            choice = 2;
        else if( warningButton.getSelection() )
            choice = 3;
        else if( errorButton.getSelection() ) choice = 3;

        store.setValue( IRiseClipseEditorConstants.PRE_SEVERITY_CHOICE, choice );
    }
    
    /**
     * The <code>ReadmePreferencePage</code> implementation of this
     * <code>PreferencePage</code> method returns preference store that belongs to
     * the our plugin. This is important because we want to store our preferences
     * separately from the workbench.
     */
    @Override
    protected IPreferenceStore doGetPreferenceStore() {
        return RiseClipseEditorPlugin.getPlugin().getPreferenceStore();
    }

    @Override
    public void init( IWorkbench workbench ) {
        // do nothing        
    }

    /**
     * Initializes states of the controls using default values in the preference
     * store.
     */
    private void initializeDefaults() {
        infoButton.setSelection( false );
        noticeButton.setSelection( true );
        warningButton.setSelection( false );
        errorButton.setSelection( false );
    }

    /**
     * Initializes states of the controls from the preference store.
     */
    private void initializeValues() {
        IPreferenceStore store = getPreferenceStore();
        int choice = store.getInt( IRiseClipseEditorConstants.PRE_SEVERITY_CHOICE );
        if( choice == 0 ) choice = 2;
        switch( choice ) {
        case 1:
            infoButton.setSelection( true );
            break;
        case 2:
            noticeButton.setSelection( true );
            break;
        case 3:
            warningButton.setSelection( true );
            break;
        case 4:
            errorButton.setSelection( true );
            break;
        }
    }

    @Override
    public void modifyText( ModifyEvent e ) {
        // Do nothing on a modification        
    }

    @Override
    public void widgetSelected( SelectionEvent e ) {
        // Do nothing on selection
    }

    @Override
    public void widgetDefaultSelected( SelectionEvent e ) {
        // Handle a default selection. Do nothing        
    }

    
}
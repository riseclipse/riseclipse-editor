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

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import fr.centralesupelec.edf.riseclipse.ui.RiseClipseEditorPlugin;


/**
 * Class used to initialize default preference values.
 */
public class RiseClipsePreferenceInitializer extends AbstractPreferenceInitializer {
    
    public RiseClipsePreferenceInitializer() {
        super();
    }

    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore store = RiseClipseEditorPlugin.getPlugin().getPreferenceStore();
        
        store.setDefault( PreferenceConstants.P_SEVERITY, 2 );
    }

}

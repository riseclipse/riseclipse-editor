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
package fr.centralesupelec.edf.riseclipse.ui;

/**
 * This interface contains constants for use within the RiseClipseEditor.
 */
public interface IRiseClipseEditorConstants {

    String PLUGIN_ID = "fr.centralesupelec.edf.riseclipse.editor"; //$NON-NLS-1$

    String PREFIX = PLUGIN_ID + "."; //$NON-NLS-1$

    String PREFERENCE_PAGE_CONTEXT = PREFIX + "preference_page_context"; //$NON-NLS-1$

    String PRE_SEVERITY_CHOICE = PREFIX + "severity_choice"; //$NON-NLS-1$

}

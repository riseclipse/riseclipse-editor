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
package fr.centralesupelec.edf.riseclipse.ui;

import java.io.IOException;
import java.io.PrintStream;

import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import fr.centralesupelec.edf.riseclipse.util.AbstractRiseClipseConsole;
import fr.centralesupelec.edf.riseclipse.util.IRiseClipseConsole;

public class EclipseRiseClipseConsole extends AbstractRiseClipseConsole {

    public static final IRiseClipseConsole INSTANCE = new EclipseRiseClipseConsole();

    private static final String ConsoleName = "RiseClipseConsole";

    private MessageConsole findConsole() {
        if( !Platform.isRunning() ) return null;

        ConsolePlugin plugin = ConsolePlugin.getDefault();
        // Got null if running as Java instead of running as Eclipse
        if( plugin == null ) return null;
        IConsoleManager conMan = plugin.getConsoleManager();
        IConsole[] existing = conMan.getConsoles();
        for( int i = 0; i < existing.length; i++ ) {
            if( ConsoleName.equals( existing[i].getName() )) {
                return ( MessageConsole ) existing[i];
            }
        }
        // no console found, so create a new one
        MessageConsole myConsole = new MessageConsole( ConsoleName, null );
        conMan.addConsoles( new IConsole[] { myConsole } );
        return myConsole;
    }

    private void out( String message, PrintStream std  ) {
        MessageConsole myConsole = findConsole();
        if( myConsole == null ) {
            std.println( message );
            std.flush();
            return;
        }
        MessageConsoleStream stream = myConsole.newMessageStream();
        stream.println( message );
        try {
            stream.flush();
        }
        catch( IOException e ) {
        }
    }

    @Override
    protected void doOutputMessage( String m ) {
       out( m, System.out );        
    }
}

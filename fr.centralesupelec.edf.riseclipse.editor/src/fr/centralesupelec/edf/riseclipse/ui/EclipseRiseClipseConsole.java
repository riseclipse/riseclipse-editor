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
import java.io.PrintStream;

import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import fr.centralesupelec.edf.riseclipse.util.AbstractRiseClipseConsole;
import fr.centralesupelec.edf.riseclipse.util.IRiseClipseConsole;

/**
 * The initial design of EclipseRiseClipseConsole was a singleton and only one IConsole.
 * 
 * When using only one console in Eclipse, messages are accumulated in org.eclipse.ui.console.MessageConsoleStream
 * and are never destroyed, even if the opened resource that produces these messages is closed.
 * There are a lot of these messages, they use a lot of memory, and the RCP version of RiseClipse runs out of memory.
 * To avoid this problem, a resource should use its specific named console, and dispose it when it is closed.
 * EclipseRiseClipseConsole is still a singleton, but changes the IConsole used.
 */

public class EclipseRiseClipseConsole extends AbstractRiseClipseConsole {
    
    public static final IRiseClipseConsole INSTANCE = new EclipseRiseClipseConsole();
    private static MessageConsole currentConsole = null;

    private static void manageConsole( String name, boolean use ) {
        if( !Platform.isRunning() ) return;

        ConsolePlugin plugin = ConsolePlugin.getDefault();
        // Got null if running as Java instead of running as Eclipse
        if( plugin == null ) return;
        IConsoleManager conMan = plugin.getConsoleManager();
        IConsole[] existing = conMan.getConsoles();
        for( int i = 0; i < existing.length; i++ ) {
            if( name.equals( existing[i].getName() )) {
                if( use ) {
                    currentConsole = ( MessageConsole ) existing[i];
                }
                else {
                    if( currentConsole == ( MessageConsole ) existing[i] ) {
                        currentConsole = null;
                    }
                    conMan.removeConsoles( new IConsole[]{ existing[i] });
                }
                return;
            }
        }
        if( ! use ) return;
        // no console found, so create a new one
        MessageConsole newConsole = new MessageConsole( name, null );
        conMan.addConsoles( new IConsole[] { newConsole } );
        currentConsole = newConsole;
    }
    
    public static IRiseClipseConsole useConsole( String name ) {
        manageConsole( name, true );
        return INSTANCE;
    }
    
    public static void disposeConsole( String name ) {
        manageConsole( name, false );
    }
    
    private EclipseRiseClipseConsole() {}

    private void out( String message, PrintStream std  ) {
        if( currentConsole == null ) {
            std.println( message );
            std.flush();
            return;
        }
        MessageConsoleStream stream = currentConsole.newMessageStream();
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

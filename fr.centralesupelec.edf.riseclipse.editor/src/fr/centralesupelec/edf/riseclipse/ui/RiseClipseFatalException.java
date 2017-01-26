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
package fr.centralesupelec.edf.riseclipse.ui;

@SuppressWarnings( "serial" )
public class RiseClipseFatalException extends RuntimeException {

    public RiseClipseFatalException() {
        super();
    }

    public RiseClipseFatalException( String message ) {
        super( message );
    }

    public RiseClipseFatalException( Throwable cause ) {
        super( cause );
    }

    public RiseClipseFatalException( String message, Throwable cause ) {
        super( message, cause );
    }

    // Not in Java6
//    public RiseClipseFatalException( String message, Throwable cause, boolean enableSuppression,
//            boolean writableStackTrace ) {
//        super( message, cause, enableSuppression, writableStackTrace );
//    }

}

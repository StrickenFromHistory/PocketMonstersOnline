package org.pokenet.utils;

import java.util.StringTokenizer;

public class Snippet {
	public static String capitalizeFirstLettersTokenizer ( String s ) {
	        
	    final StringTokenizer st = new StringTokenizer( s, " ", true );
	    final StringBuilder sb = new StringBuilder();
	     
	    while ( st.hasMoreTokens() ) {
	        String token = st.nextToken();
	        token = String.format( "%s%s",
	                                Character.toUpperCase(token.charAt(0)),
	                                token.substring(1) );
	        sb.append( token );
	    }
	        
	    return sb.toString();
	                
	}
}


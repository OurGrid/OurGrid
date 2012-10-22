/*
 * Copyright (C) 2008 Universidade Federal de Campina Grande
 *  
 * This file is part of OurGrid. 
 *
 * OurGrid is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version. 
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details. 
 * 
 * You should have received a copy of the GNU Lesser General Public License 
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package org.ourgrid.common.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;

import org.ourgrid.discoveryservice.DiscoveryServiceConstants;

import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLoggerFactory;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

/**
 * This is a auxiliar class that incloses many util functionalities related to
 * strings.
 */
public class StringUtil {
	
	public static final int VARCHAR_MAX_LENGTH = 255;
	
	private static final String USER_SERVER_SEPARATOR = "@";

	private static final String COMMUNE_ADDRESS_SEPARATOR = ";";

	static CommuneLogger logger = CommuneLoggerFactory.getInstance().gimmeALogger(StringUtil.class);

	/**
	 * Writes the given string into the given file.
	 * 
	 * @param file The file where the string have to be written (appended).
	 * @param s the string to be written into the file
	 * @throws IOException If the file could not be used (created) or any other
	 *         I/O problem at the open, writte or close moment.
	 */
	public static void writeStringOnFile( String file, String s ) throws IOException {

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream( file, true );
			fos.write( s.getBytes() );
		} finally {
			if ( fos != null ) {
				fos.close();
			}
		}
	}


	/**
	 * Replaces at the a String a patter to another.
	 * 
	 * @param str The String where will be made the changes.
	 * @param subOld The pattern that will be removed.
	 * @param subNew The pattern that will be inserted.
	 * @return the new string with the changes made if any was necessary, or the
	 *         same string if any changes was needed.
	 */
	public static String replace( String str, String subOld, String subNew ) {

		int i = str.indexOf( subOld );

		if ( i == -1 ) {
			return str;
		}
		StringBuffer stb = new StringBuffer( str );
		stb.replace( i, i + subOld.length(), subNew );
		return replace( stb.toString(), subOld, subNew );
	}


	/**
	 * Receives an array of String and returns a simple string of all separeted
	 * by a ","
	 */
	public static String passToString( String[ ] orig ) {

		String attrib = new String();

		if ( orig.length != 0 ) {
			attrib = orig[0];
			for ( int i = 1; i < orig.length; i++ ) {
				attrib += ", " + orig[i];
			}
		} else
			attrib = "";

		return attrib;
	}
	
	/**
	 * 
	 * @param file
	 * @return
	 * @throws IOException 
	 */
	public static StringBuffer readFile(File file) throws IOException {
		
		BufferedReader reader = null;
		
		StringWriter result = new StringWriter();
		PrintWriter writer = new PrintWriter(result);
		
		try {
			reader = new BufferedReader(new FileReader(file));
			
			String line;
			while ((line = reader.readLine()) != null) {
				writer.println(line);
			}
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					throw e;
				}
			}
		}
		
		return result.getBuffer();
	}


	/**
	 * Receives a simple String and returns a array containing all words of the
	 * String supressed the "," - it is the delimiter
	 * 
	 * @param strToArray string that must be transformed to an array.
	 * @return an array of strings based on the string parameter.
	 */
	public static String[ ] passToArrayStr( String strToArray ) {

		String[ ] returnArray = {};

		if ( strToArray == null ) {
			return returnArray;
		}

		StringTokenizer noComma = new StringTokenizer( strToArray, "," );
		returnArray = new String[ noComma.countTokens() ];

		for ( int i = 0; noComma.hasMoreTokens(); returnArray[i++] = noComma.nextToken().trim() ) {
			// Trim over all tokens
		}
		return returnArray;
	}


	/**
	 * Inserts a white space at a specific point.
	 * 
	 * @param expr the string where the space have to be inserted.
	 * @param i the specific point where the space have to be inserted
	 * @return the changed string if the position is valid or the same string
	 *         otherwise.
	 */
	public static String insertSpace( String expr, int i ) {

		StringBuffer str = new StringBuffer( expr );
		try {
			str.insert( i, " " );
		} catch ( StringIndexOutOfBoundsException sobex ) {
			// do nothing, returns the string like it was received.
		}
		return str.toString();
	}


	/**
	 * This method fills value with the characters at right.
	 * 
	 * @param value - The string to be filled with white space caracters
	 * @param character - The character to be used in the filling of the string
	 * @param numChars - The number of characters to insert in the string.
	 */
	public static String fillWithChars( String value, char character, int numChars ) {

		StringBuffer formatted = new StringBuffer();

		formatted.append( value );

		for ( int i = 0; i < numChars; i++ ) {
			formatted.append( character );
		}

		return formatted.toString();
	}


	/**
	 * Insert spaces in the sentence expr. This is necessary in order to
	 * StringTokenizer can function properlly when matching sentence of the
	 * task.
	 * 
	 * @param expr The sentence that spaces will be inserted
	 * @return The modified sentence (with spaces)
	 */
	public static String insertSpaces( String expr ) {

		if ( expr != null ) {

			for ( int i = 0; i < expr.length(); i++ ) {
				if ((expr.charAt(i) == '(')) {
					if ((i != (expr.length() - 1)) && (expr.charAt(i + 1) != ' ')) {
						expr = insertSpace(expr, i + 1);
					}
					if ((i >= 1) && (expr.charAt(i - 1) != ' ')) {
						expr = insertSpace(expr, i);
					}
					i++;
				} else if ( (expr.charAt( i ) == ')') && (i != 0) && (expr.charAt( i - 1 ) != ' ') ) {
					expr = insertSpace( expr, i );
					i++;
				} else if ( (expr.charAt( i ) == '&') || (expr.charAt( i ) == '|') ) { // Symbol
					// can
					// be
					// &&
					// or
					// ||
					int skip = 1;
					boolean changedAhead = false;
					if ( (i != (expr.length() - 2)) && (expr.charAt( i + 2 ) != ' ') ) {
						expr = insertSpace( expr, i + 2 );
						skip++;
						changedAhead = true;
					}
					if ( (i != 0) && (expr.charAt( i - 1 ) != ' ') && changedAhead ) {
						expr = insertSpace( expr, i );
						skip++;
					} else if ( (i != 0) && (expr.charAt( i - 1 ) != ' ') ) {
						expr = insertSpace( expr, i );
						skip++;
					}
					i += skip;
				} else if ( (expr.charAt( i ) == '=') || (expr.charAt( i ) == '<') || (expr.charAt( i ) == '>')
						|| (expr.charAt( i ) == '!') ) {
					int skip = 1;
					if ( expr.charAt( i + 1 ) == '=' ) { // Symbol can be ==
															// or
						// >=
						// or <=
						if ( (i != (expr.length() - 2)) && (expr.charAt( i + 2 ) != ' ') ) {
							expr = insertSpace( expr, i + 2 );
							skip++;
						}
						if ( (i != 0) && (expr.charAt( i - 1 ) != ' ') ) {
							expr = insertSpace( expr, i );
							skip++;
						}
					} else { // Symbol can be only < or >
						if ( i != (expr.length() - 1) && (expr.charAt( i + 1 ) != ' ') ) {
							expr = insertSpace( expr, i + 1 );
						}
						if ( (i != 0) && (expr.charAt( i - 1 ) != ' ') ) {
							expr = insertSpace( expr, i );
							skip++;
						}
					}
					i += skip;
				}
			}
		}
		return expr;
	}


	/**
	 * This method does a job that is expected to be done by the shell: it
	 * converts variables to String literals.
	 * 
	 * @param stringWithVariables A string with 0 or more variables.
	 * @return A String without any variables.
	 */
	public static String replaceVariables( String stringWithVariables, Map<String, String> envVars ) {

		String stringWithoutVariables = stringWithVariables;

		if ( envVars != null ) {

			Set<Entry<String, String>> entries = envVars.entrySet();
			
			for (Entry<String, String> entry : entries) {
				String key = entry.getKey();
				String value = entry.getValue();
				stringWithoutVariables = StringUtil.replace( stringWithoutVariables, "$" + key, value);
			}
		}

		return stringWithoutVariables;

	}


	/**
	 * This method parse a permissions file from stringo to int
	 * 
	 * @param permStr The file permission as drwxrwxrwx
	 * @return int
	 */
	public static int parsePermissionString( String permStr ) {

		int perm_o = 0;
		int perm_g = 0;
		int perm_u = 0;

		if ( permStr.charAt( 0 ) == 'r' )
			perm_o = perm_o + 4;
		if ( permStr.charAt( 1 ) == 'w' )
			perm_o = perm_o + 2;
		if ( permStr.charAt( 2 ) == 'x' )
			perm_o = perm_o + 1;

		if ( permStr.charAt( 3 ) == 'r' )
			perm_g = perm_g + 4;
		if ( permStr.charAt( 4 ) == 'w' )
			perm_g = perm_g + 2;
		if ( permStr.charAt( 5 ) == 'x' )
			perm_g = perm_g + 1;

		if ( permStr.charAt( 6 ) == 'r' )
			perm_u = perm_u + 4;
		if ( permStr.charAt( 7 ) == 'w' )
			perm_u = perm_u + 2;
		if ( permStr.charAt( 8 ) == 'x' )
			perm_u = perm_u + 1;

		return (perm_o * 100) + (perm_g * 10) + (perm_u);
	}


	/**
	 * Gets a array of string and return a string that contains the strings
	 * separated by commas
	 * 
	 * @param strings The array of strings
	 * @return A string with strings separated by commas
	 */
	public static String getStringSeparatedByCommas( String[ ] strings ) {

		StringBuffer strAttributes = new StringBuffer();
		if ( strings == null ) {
			return "";
		}
		for ( int indexStrings = 0; indexStrings < strings.length; indexStrings++ ) {
			strAttributes.append( strings[indexStrings] );
			strAttributes.append( "," );
		}
		if ( strAttributes.length() == 0 ) {
			return "";
		}
		return strAttributes.substring( 0, strAttributes.length() - 1 );
	}


	/**
	 * Gets a array of strings after broken with the given delimitator
	 * 
	 * @param string A string that will broken in an array of strings
	 * @param delimitator The delimitator between strings
	 * @return An array with the strings of "string" parameter
	 */
	public static String[ ] getArrayOfStrings( String string, String delimitator ) {

		StringTokenizer strToken = new StringTokenizer( string, delimitator );
		String[ ] strings = new String[ strToken.countTokens() ];
		int indexStrings = 0;
		while ( strToken.hasMoreTokens() ) {
			strings[indexStrings] = (String) strToken.nextElement();
			indexStrings++;
		}
		return strings;
	}

	private static final String SECONDS = "s";

	private static final String MINUTES = "m";

	private static final String HOURS = "h";

	private static final String DAYS = "d";


	/**
	 * Converts an amount of time (in seconds) in an user-friendly text.
	 * 
	 * @param timeInSeconds The amount of time (in seconds).
	 * @return A <code>String</code> with the amount of time in ( <i>ww</i> d
	 *         <i>xx</i> h <i>yy</i> m <i>zz</i> s)
	 */
	public static String getTimeAsText( long timeInSeconds ) {

		if ( timeInSeconds == 0 )
			return timeInSeconds + SECONDS;

		long seconds = timeInSeconds;
		long minutes = 0;
		long hours = 0;
		long days = 0;

		if ( seconds >= 60 ) {
			minutes = seconds / 60;
			seconds %= 60;

			if ( minutes >= 60 ) {
				hours = minutes / 60;
				minutes %= 60;

				if ( hours >= 24 ) {
					days = hours / 24;
					hours %= 24;
				}
			}
		}

		String duration = "" + (days > 0 ? days + DAYS + " " : "") + (hours > 0 ? hours + HOURS + " " : "")
				+ (minutes > 0 ? minutes + MINUTES + " " : "") + (seconds > 0 ? seconds + SECONDS + " " : "");

		return duration.trim();
	}
	
	/**
     * Returns a random String of numbers and letters (lower and upper case)
     * of the specified length. The method uses the Random class that is
     * built-in to Java which is suitable for low to medium grade security uses.
     * This means that the output is only pseudo random, i.e., each number is
     * mathematically generated so is not truly random.<p>
     * <p/>
     * The specified length must be at least one. If not, the method will return
     * null.
     *
     * @param length the desired length of the random String to return.
     * @return a random String of numbers and letters of the specified length.
     */
    public static String randomString(int length) {
        if (length < 1) {
            return null;
        }
        // Create a char buffer to put random letters and numbers in.
        char[] randBuffer = new char[length];
        for (int i = 0; i < randBuffer.length; i++) {
            randBuffer[i] = numbersAndLetters[randGen.nextInt(71)];
        }
        return new String(randBuffer);
    }
    
    /**
     * Pseudo-random number generator object for use with randomString().
     * The Random class is not considered to be cryptographically secure, so
     * only use these random Strings for low to medium security applications.
     */
    private static Random randGen = new Random();
    
    /** Array of numbers and letters of mixed case. Numbers appear in the list
    * twice so that there is a more equal chance that a number will be picked.
    * We can use the array to get a random number or letter by picking a random
    * array index.
    */
    private static char[] numbersAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyz" +
    "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();

    public static String concatAddresses(
    		Set<String> addresses) {
    	StringBuilder sb = new StringBuilder();
    	for (String address : addresses) {
    		sb.append(address).append(';');
    	}
    	sb.deleteCharAt(sb.length() - 1);
    	return sb.toString();
    }
    
	/**
	 * Returns a {@link ServiceID} list of DiscoveryService identifications. 
	 * It parses an {@link String} in the following pattern: 
	 * dsuser1@dsserver1;dsuser2@dsserver2
	 * 
	 * @param networkString
	 * @return
	 */
	public static List<ServiceID> splitDiscoveryServiceAddresses(String networkString) {
		List<ServiceID> dsIDs = new LinkedList<ServiceID>();
		String[] addresses = networkString.split(COMMUNE_ADDRESS_SEPARATOR);
		
		for (String address : addresses) {
			String[] splitAddress = splitAddress(address);
			if (splitAddress.length != 2) {
				logger.warn("Parsing Discovery Service addresses: [" + address
						+ "] is not a valid commune address.");
				continue;
			}
			
			ServiceID dsServiceID = new ServiceID(splitAddress[0], splitAddress[1], 
					DiscoveryServiceConstants.MODULE_NAME, DiscoveryServiceConstants.DS_OBJECT_NAME );
			
			dsIDs.add(dsServiceID);
		}
		return dsIDs;
	}
	
	public static ServiceID userAtServerToServiceID(String userAtServer){
		if(userAtServer == null){
			return null;
		}
			
		String[] splitAddress = splitAddress(userAtServer);
		
		ServiceID dsServiceID = new ServiceID(splitAddress[0], splitAddress[1], 
				DiscoveryServiceConstants.MODULE_NAME, DiscoveryServiceConstants.DS_OBJECT_NAME );
		
		return dsServiceID;
	}
	
	public static String[] splitAddress(String address) {
		return address.split(USER_SERVER_SEPARATOR);
	}

	public static String addressToUserAtServer(String serviceId){
		return serviceId.split("/")[0];
	}
	
	public static String addressToContainerID(String address) {
		String[] split = address.split("/");
		
		return split[0] + "/" + split[1];
	}


	public static String userAtServerToAddress(String userAtServer,
			String moduleName, String objectName) {
		return userAtServer + "/" + moduleName + "/" + objectName;
	}


	public static String deploymentIDToContainerID(String deploymentID) {
		return addressToContainerID(deploymentID);
	}

	public static String deploymentIDToAddress(String workerDeploymentID) {
		return workerDeploymentID == null ? null : workerDeploymentID.substring(0, workerDeploymentID.lastIndexOf('/'));
	}


	public static String deploymentIDToUserAtServer(String workerID) {
		return addressToUserAtServer(deploymentIDToAddress(workerID));
	}
	
	public static String joinStrings(String delim, Collection<String> info) {
	    StringBuilder sb = new StringBuilder();
	    Iterator<String> iterator = info.iterator();
	    if (iterator.hasNext()) {
	      sb.append(iterator.next());
	      while (iterator.hasNext()){
	    	  sb.append(delim);
	    	  sb.append(iterator.next());
	      }
	    }
	    return sb.toString();
	}
	
	public static String shrink(String varchar) {
		if (varchar == null) {
			return null;
		}
		return varchar.substring(0, varchar.length() > StringUtil.VARCHAR_MAX_LENGTH ? 
				StringUtil.VARCHAR_MAX_LENGTH : varchar.length());
	}
}

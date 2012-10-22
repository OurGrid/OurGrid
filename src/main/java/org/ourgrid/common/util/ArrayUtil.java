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

/**
 * This is a auxiliar class that incloses many util functionalities related to
 * the Array data structure.
 */
public class ArrayUtil {

	/**
	 * This method removes the fisrt element of an array, if the array contains
	 * only one element, it returns null.
	 * 
	 * @param objArray The source array. Their first element will be removed.
	 * @param resultArray Is the source array without the first element, or null
	 *        if there is no more elements in the array.
	 */
	public static void removeFirst( Object[ ] objArray, Object[ ] resultArray ) {

		if ( resultArray.length >= objArray.length - 1 ) {
			if ( objArray.length > 1 ) {
				for ( int j = 1; j < objArray.length; j++ ) {
					resultArray[j - 1] = objArray[j];
				}
			}
		}
	}


	/**
	 * This method inverts the order of the elements in <code>objArray</code>.
	 * 
	 * @param objArray the array that will have its elements inverted.
	 */
	public static void invert( Object[ ] objArray ) {

		if ( objArray != null ) {
			int changes = objArray.length / 2;
			for ( int i = 0; i < changes; i++ ) {
				Object temp = objArray[i];
				objArray[i] = objArray[objArray.length - i - 1];
				objArray[objArray.length - i - 1] = temp;
			}
		}
	}


	/**
	 * Puts all the elements from two source arrays at another destiny one. It
	 * will put first in sequence all the elements from the first array then the
	 * sequence of the other one. It will only happens if the destiny array has
	 * more or equals then the first source array Length plus the length from
	 * the second one.
	 * 
	 * @param array1 the first array source.
	 * @param array2 the second array source.
	 * @param mergedArray the destiny array.
	 */
	public static void concat( Object[ ] array1, Object[ ] array2, Object[ ] mergedArray ) {

		if ( mergedArray.length >= array1.length + array2.length ) {
			System.arraycopy( array1, 0, mergedArray, 0, array1.length );
			System.arraycopy( array2, 0, mergedArray, array1.length, array2.length );
		}
	}


	/**
	 * Search for a object element into a array. It will search for the element
	 * using the equals method of it.
	 * 
	 * @param element the element to search
	 * @param array1 the array where to search.
	 * @return the index of the element if it was found and -1 otherwise.
	 */
	public static int find( Object element, Object[ ] array1 ) {

		int index = -1;
		for ( int count = 0; count < array1.length; count++ ) {
			if ( array1[count].equals( element ) ) {
				index = count;
				break;
			}
		}
		return index;
	}
}

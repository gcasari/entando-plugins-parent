/*
*
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
* This file is part of Entando Enterprise Edition software.
* You can redistribute it and/or modify it
* under the terms of the Entando's EULA
* 
* See the file License for the specific language governing permissions   
* and limitations under the License
* 
* 
* 
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
*/
package com.agiletec.plugins.jpwtt.aps.util;

import java.util.GregorianCalendar;

/**
 * @author Sara Didaci
 */
public class KeyGenerator {
	
    /**
	 * Genera un codice univoco che rappresenta il  numero di ticket. Si compone delle prime due cifre che 
	 * rappresentano l'anno in corso e di un progressivo univoco.
	 * @param stringData 
	 * @param pattern
	 * @return Date
	 */
	public static String generateTicketKey(String simpleKey) {
		String newKey = null;
	    int keyLenght=8;
	    int numberOfZero =  keyLenght - 2 - simpleKey.length();
	    GregorianCalendar calen =  new GregorianCalendar();
	    String year = String.valueOf(calen.get(GregorianCalendar.YEAR ));
	    newKey = ""+year.substring(2) ;
	    for (int i = 0; i < numberOfZero; i++) {
	        newKey=newKey+"0";
        }
	    newKey=newKey+simpleKey;
		return newKey;
	}
	
}
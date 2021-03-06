/*
*
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
* This file is part of Entando software. 
* Entando is a free software; 
* You can redistribute it and/or modify it
* under the terms of the GNU General Public License (GPL) as published by the Free Software Foundation; version 2.
* 
* See the file License for the specific language governing permissions   
* and limitations under the License
* 
* 
* 
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
*/
package com.agiletec.plugins.jpmassiveresourceloader.apsadmin.resource;

/**
 * @author E.Santoboni
 */
public interface IMassiveResourceLoaderAction {
	
	public String save();
	
	/**
	 * Esegue l'azione di associazione di una 
	 * categoria alla risorsa in fase di edit.
	 * @return Il codice del risultato dell'azione.
	 */
	public String joinCategory();
	
	/**
	 * Esegue l'azione di rimozione di una 
	 * categoria da una risorsa in fase di edit.
	 * @return Il codice del risultato dell'azione.
	 */
	public String removeCategory();
	
}
/*
*
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
* This file is part of Entando software.
* Entando is a free software; 
* you can redistribute it and/or modify it
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
package com.agiletec.plugins.jpactionlogger.apsadmin.actionlogger;

import java.util.List;

import com.agiletec.plugins.jpactionlogger.aps.system.services.actionlogger.model.ActionRecord;
/**
 * Interface for the backoffice actions on the {@link ActionRecord} objects
 */
public interface IActionLoggerAction {
	
	public List<Integer> getActionRecords();

	public String delete();

}
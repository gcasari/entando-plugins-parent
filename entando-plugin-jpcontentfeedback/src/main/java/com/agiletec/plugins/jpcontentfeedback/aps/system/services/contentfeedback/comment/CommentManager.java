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
package com.agiletec.plugins.jpcontentfeedback.aps.system.services.contentfeedback.comment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.common.AbstractService;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.keygenerator.IKeyGeneratorManager;
import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;
import com.agiletec.plugins.jpcontentfeedback.aps.system.services.contentfeedback.comment.model.CommentSearchBean;
import com.agiletec.plugins.jpcontentfeedback.aps.system.services.contentfeedback.comment.model.IComment;
import com.agiletec.plugins.jpcontentfeedback.aps.system.services.contentfeedback.comment.model.ICommentSearchBean;
import com.agiletec.plugins.jpcontentfeedback.aps.system.services.contentfeedback.event.ContentFeedbackChangedEvent;

@Aspect
public class CommentManager extends AbstractService implements ICommentManager {

	@Override
	public void init() throws Exception {
		ApsSystemUtils.getLogger().debug(this.getName() + ": initialized");
	}
	
	@Override
	public void addComment(IComment comment) throws ApsSystemException {
		try {
			int key = this.getKeyGeneratorManager().getUniqueKeyCurrentValue();
			comment.setId(key);
			comment.setCreationDate(new Date());
			this.getCommentDAO().addComment(comment);
            this.notifyEvent(comment.getContentId(), -1, ContentFeedbackChangedEvent.CONTENT_COMMENT, ContentFeedbackChangedEvent.INSERT_OPERATION_CODE);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "addComment");
			throw new ApsSystemException("Error add comment", t);
		}
	}
	
	@AfterReturning(
			pointcut="execution(* com.agiletec.plugins.jacms.aps.system.services.content.IContentManager.deleteContent(..)) && args(content)")
	public void deleteAllComments(Content content) throws ApsSystemException{
		try {
			String contentId = content.getId();
			CommentSearchBean searcherBean = new CommentSearchBean();
			searcherBean.setContentId(contentId);
			List<String> ids = this.searchCommentIds(searcherBean);
			for (int i = 0; i< ids.size();i++){
				this.deleteComment(Integer.parseInt(ids.get(i)));
			}
            this.notifyEvent(contentId, -1, ContentFeedbackChangedEvent.CONTENT_COMMENT, ContentFeedbackChangedEvent.REMOVE_OPERATION_CODE);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "deleteAllComments");
			throw new ApsSystemException("Error while remove all comment", t);
		}
	}
	
	@Override
	public void deleteComment(int id) throws ApsSystemException {
		try {
			this.getCommentDAO().deleteComment(id);
            this.notifyEvent(null, id, ContentFeedbackChangedEvent.CONTENT_COMMENT, ContentFeedbackChangedEvent.REMOVE_OPERATION_CODE);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "deleteComment");
			throw new ApsSystemException("Error while remove comment", t);
		}
	}
	
	@Override
	public IComment getComment(int id) throws ApsSystemException {
		IComment comment = null;
		try {
			comment = this.getCommentDAO().getComment(id);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getComment");
			throw new ApsSystemException("Error while extract comment", t);
		}
		return comment;
	}

	@Override
	public List<String> searchCommentIds(ICommentSearchBean searchBean) throws ApsSystemException {
		List<String> commentIds = new ArrayList<String>();
		try {
			commentIds = this.getCommentDAO().searchCommentsId(searchBean);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "searchCommentIds");
			throw new ApsSystemException("Error while search comment", t);
		}
		return commentIds;
	}
	
	@Override
	public void updateCommentStatus(int id, int status) throws ApsSystemException {
		try {
			this.getCommentDAO().updateStatus(id, status);
            this.notifyEvent(null, id, ContentFeedbackChangedEvent.CONTENT_COMMENT, ContentFeedbackChangedEvent.UPDATE_OPERATION_CODE);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "updateCommentStatus");
			throw new ApsSystemException("Error while update comment status", t);
		}
	}
	
	private void notifyEvent(String contentId, int commentId, int objectCode, int operationCode) throws ApsSystemException {
		ContentFeedbackChangedEvent event = new ContentFeedbackChangedEvent();
		event.setContentId(contentId);
		event.setCommentId(commentId);
		event.setObjectCode(objectCode);
		event.setOperationCode(operationCode);
		this.notifyEvent(event);
	}
    
	protected ICommentDAO getCommentDAO() {
		return _commentDAO;
	}
	public void setCommentDAO(ICommentDAO commentDAO) {
		this._commentDAO = commentDAO;
	}
	
	protected IKeyGeneratorManager getKeyGeneratorManager() {
		return _keyGeneratorManager;
	}
	public void setKeyGeneratorManager(IKeyGeneratorManager keyGeneratorManager) {
		this._keyGeneratorManager = keyGeneratorManager;
	}
	
	private ICommentDAO _commentDAO;
	private IKeyGeneratorManager _keyGeneratorManager;
	
}
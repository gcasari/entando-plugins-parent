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
package com.agiletec.plugins.jpmyportalplus.aps.system.services.pagemodel;

import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

import org.entando.entando.aps.system.services.widgettype.IWidgetTypeManager;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.page.Widget;
import com.agiletec.aps.util.ApsProperties;

/**
 * This support class parses the XML representing the configuration of a page model
 * @author E.Santoboni
 *
 *
<frames>
	<frame pos="0" locked="false" column="3">
		<descr>Search in this site</descr>
		<defaultWidget code="searchForm"/>
	</frame>
	....
	....
</frames>
 *
 */
public class MyPortalPageModelDOM {

	/**
	 * Class constructor
	 * @param xmlText The XML string to parse
	 * @param widgetTypeManager The manager of the showlet type
	 * @throws ApsSystemException In case of error
	 */
	public MyPortalPageModelDOM(String xmlText, IWidgetTypeManager widgetTypeManager) throws ApsSystemException {
		this.decodeDOM(xmlText);
		this.buildFrames(widgetTypeManager);
	}

	private void decodeDOM(String xmlText) throws ApsSystemException {
		SAXBuilder builder = new SAXBuilder();
		builder.setValidation(false);
		StringReader reader = new StringReader(xmlText);
		try {
			_doc = builder.build(reader);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "decodeDOM");
			throw new ApsSystemException("Error detected parsing the XML", t);
		}
	}

	private ApsProperties buildProperties(Element propertiesElement) {
		ApsProperties prop = new ApsProperties();
		List<Element> propertyElements = propertiesElement.getChildren(TAB_PROPERTY);
		Iterator<Element> propertyElementsIter = propertyElements.iterator();
		while (propertyElementsIter.hasNext()) {
			Element property = (Element) propertyElementsIter.next();
			prop.put(property.getAttributeValue(ATTRIBUTE_KEY), property.getText().trim());
		}
		return prop;
	}

	private void buildFrames(IWidgetTypeManager widgetTypeManager) throws ApsSystemException {
		List<Element> frameElements = _doc.getRootElement().getChildren(TAB_FRAME);
		if (null != frameElements && frameElements.size() > 0) {
			int framesNumber = frameElements.size();
			_frames = new String[framesNumber];
			_frameConfigs = new Frame[framesNumber];
			_defaultShowlet = new Widget[framesNumber];
			_existMainFrame = false;
			Iterator<Element> frameElementsIter = frameElements.iterator();
			while (frameElementsIter.hasNext()) {
				Element frameElement = frameElementsIter.next();
				this.buildFrame(widgetTypeManager, framesNumber, frameElement);
			}
		} else {
			_frames = new String[0];
			_defaultShowlet = new Widget[0];
		}
	}

	private void buildFrame(IWidgetTypeManager showletTypeManager,
			int framesNumber, Element frameElement) throws ApsSystemException {
		int pos = Integer.parseInt(frameElement.getAttributeValue(ATTRIBUTE_POS));
		if(pos >= framesNumber) {
			throw new ApsSystemException("The 'pos' attribute exceeds the available frames in the page model");
		}
		Frame frame = new Frame();
		frame.setPos(pos);
		String main = frameElement.getAttributeValue(ATTRIBUTE_MAIN);
		if (null != main && main.equals("true")) {
			_existMainFrame = true;
			_mainFrame = pos;
			frame.setMainFrame(true);
		}
		String fixed = frameElement.getAttributeValue("locked");
		if (null == fixed || fixed.equals("true")) {
			frame.setLocked(true);
		}
		String column = frameElement.getAttributeValue("column");
		if (null != column) {
			try {
				frame.setColumn(Integer.parseInt(column));
			} catch (Throwable e) {
				//nothing to do
			}
		}
		Element frameDescrElement = frameElement.getChild(TAB_DESCR);
		if (null != frameDescrElement) {
			String descr = frameDescrElement.getText();
			_frames[pos] = descr;
			frame.setDescr(descr);
		}
		Element defaultShowletElement = frameElement.getChild(TAB_DEFAULT_WIDGET);
		if (null == defaultShowletElement) {
			defaultShowletElement = frameElement.getChild("defaultShowlet");
		}
		if (null != defaultShowletElement) {
			Widget defaultShowlet = this.buildDefaultShowlet(defaultShowletElement, pos, showletTypeManager);
			frame.setDefaultWidget(defaultShowlet);
			this.getDefaultShowlet()[pos] = defaultShowlet;
		}
		_frameConfigs[pos] = frame;
	}

	private Widget buildDefaultShowlet(Element defaultShowletElement, int pos, IWidgetTypeManager showletTypeManager) {
		Widget showlet = new Widget();
		String showletCode = defaultShowletElement.getAttributeValue(ATTRIBUTE_CODE);
		showlet.setType(showletTypeManager.getWidgetType(showletCode));
		Element propertiesElement = defaultShowletElement.getChild(TAB_PROPERTIES);
		if (null != propertiesElement) {
			ApsProperties prop = this.buildProperties(propertiesElement);
			showlet.setConfig(prop);
		} else {
			showlet.setConfig(new ApsProperties());
		}
		_defaultShowlet[pos] = showlet;
		return showlet;
	}

	/**
	 * Return the sorted descriptions by frames of the page models.
	 * @return An array with the frames descriptions.
	 */
	public String[] getFrames() {
		return this._frames;
	}

	public Frame[] getFrameConfigs() {
		return this._frameConfigs;
	}

	/**
	 * Return the position of the main frame when available, otherwise return -1.
	 * @return The position of the main frame or -1 when it's not available.
	 */
	public int getMainFrame() {
		if (_existMainFrame) {
			return this._mainFrame;
		} else {
			return -1;
		}
	}

	/**
	 * Return the configuration of the default showlets
	 * @return The default showlets
	 */
	public Widget[] getDefaultShowlet() {
		return this._defaultShowlet;
	}

	private Document _doc;
	private final String TAB_FRAME = "frame";
	private final String ATTRIBUTE_POS = "pos";
	private final String ATTRIBUTE_MAIN = "main";
	private final String TAB_DESCR = "descr";
	private final String TAB_DEFAULT_WIDGET = "defaultWidget";
	private final String ATTRIBUTE_CODE = "code";
	private final String TAB_PROPERTIES = "properties";
	private final String TAB_PROPERTY = "property";
	private final String ATTRIBUTE_KEY = "key";
	private boolean _existMainFrame;
	private int _mainFrame;
	private String[] _frames;
	private Widget[] _defaultShowlet;

	private Frame[] _frameConfigs;

}
package com.mmt.wiremock.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.helpers.DefaultHandler;

public class XMLParser extends DefaultHandler {

	protected Map<String, String> xmlValues = new HashMap<String, String>();

	public Map<String, String> getXmlValues() {
		return xmlValues;
	}

	public void setXmlValues(Map<String, String> xmlValues) {
		this.xmlValues = xmlValues;
	}

	protected List<String> responseVariableList = new ArrayList<String>();

	public List<String> getResponseVariableList() {
		return responseVariableList;
	}

	public void setResponseVariableList(List<String> responseVariableList) {
		this.responseVariableList = responseVariableList;
	}

}
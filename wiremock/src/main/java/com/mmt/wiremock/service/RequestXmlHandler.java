package com.mmt.wiremock.service;

import java.util.*;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class RequestXmlHandler extends XMLParser {

	boolean flag = false;
	String mapKey;

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		// System.out.println("Uri is "+uri+"--localName is"+localName+"--qname
		// is"+qName+"Attribute value is" );
		if (responseVariableList.contains(localName)) {
			flag = true;
			mapKey = localName;
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		mapKey = null;
		flag = false;
	}

	@Override
	public void characters(char ch[], int start, int length) throws SAXException {
		if (flag) {
			super.getXmlValues().put(mapKey, new String(ch, start, length));
		}
	}

	public void setResponseVariableList(List<String> responseVariableListFromResponse) {
		super.setResponseVariableList(responseVariableListFromResponse);
	}
}
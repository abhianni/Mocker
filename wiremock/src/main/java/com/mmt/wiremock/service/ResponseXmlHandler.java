package com.mmt.wiremock.service;

import java.util.regex.Pattern;

import org.xml.sax.SAXException;

public class ResponseXmlHandler extends XMLParser {
	private String responseString;
	private static final Pattern interpolationPattern = Pattern.compile("\\$\\(.*?\\)");

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (responseString.contains("$")) {
			String requiredString = responseString.substring(responseString.indexOf("(") + 1,
					responseString.indexOf("."));
			super.getResponseVariableList().add(requiredString);
		}
	}

	@Override
	public void characters(char ch[], int start, int length) throws SAXException {
		responseString = new String(ch, start, length);
	}
}
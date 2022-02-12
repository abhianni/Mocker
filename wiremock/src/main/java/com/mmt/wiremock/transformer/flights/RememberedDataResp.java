/**
 * 
 */
package com.mmt.wiremock.transformer.flights;

import java.util.Map;

/**
 * @author Harpal
 *
 */
public class RememberedDataResp {
	
	private Map<String,String> reqHeaders;
	private String requestBody;
	private String requestUrl;
	private String errorMsg;
	private Map<String,String> respHeaders;
	private String responseBody;
	private int httpResponseCode;
	
	
	
	public RememberedDataResp(Map<String,String> headerParam,String requestBody, String requestUrl,
			Map<String,String> respHeadersParam,String responseBodyParam,int httpResponseCodeParam)
	{
		this.reqHeaders=headerParam;
		this.requestBody= requestBody;
		this.requestUrl = requestUrl;
		this.respHeaders= respHeadersParam;
		this.responseBody= responseBodyParam;
		this.httpResponseCode= httpResponseCodeParam;
		this.errorMsg=null;
	}
	
	
	public RememberedDataResp(String errorMsg)
	{
		this.errorMsg=errorMsg;
	}
	
	/**
	 * @return the requestBody
	 */
	public String getRequestBody() {
		return requestBody;
	}
	/**
	 * @param requestBody the requestBody to set
	 */
	public void setRequestBody(String requestBody) {
		this.requestBody = requestBody;
	}
	/**
	 * @return the requestUrl
	 */
	public String getRequestUrl() {
		return requestUrl;
	}
	/**
	 * @param requestUrl the requestUrl to set
	 */
	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}
	/**
	 * @return the reqHeaders
	 */
	public Map<String,String> getReqHeaders() {
		return reqHeaders;
	}
	/**
	 * @param reqHeaders the reqHeaders to set
	 */
	
	/**
	 * @return the errorMsg
	 */
	public String getErrorMsg() {
		return errorMsg;
	}
	/**
	 * @param errorMsg the errorMsg to set
	 */
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	public Map<String, String> getRespHeaders() {
		return respHeaders;
	}
	public void setRespHeaders(Map<String, String> respHeaders) {
		this.respHeaders = respHeaders;
	}
	public String getResponseBody() {
		return responseBody;
	}
	public void setResponseBody(String responseBody) {
		this.responseBody = responseBody;
	}
	public int getHttpResponseCode() {
		return httpResponseCode;
	}
	public void setHttpResponseCode(int httpResponseCode) {
		this.httpResponseCode = httpResponseCode;
	}
	public void setReqHeaders(Map<String, String> reqHeaders) {
		this.reqHeaders = reqHeaders;
	}

}

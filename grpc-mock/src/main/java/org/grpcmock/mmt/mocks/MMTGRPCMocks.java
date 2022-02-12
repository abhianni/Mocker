/**
 * 
 */
package org.grpcmock.mmt.mocks;

import org.grpcmock.definitions.stub.ServerStreamingMethodStubBuilderImpl;

import com.jayway.restassured.response.Response;

/**
 * @author Harpal
 *
 */
public interface MMTGRPCMocks {
	
	public String host="localhost";
	public String port="8081";
	
	public String baseUrl="http://"+host+":"+port+"/";
	 String url=null;
	
	public void setup() ;
	public String getWireMockEndPoint();
	public ServerStreamingMethodStubBuilderImpl getStubBuilderImpl();
	public Object getResponseObjectProto();
	public void setWireMockResponse(Response wireMokcResposeParam);
	public void setWireMockRequest(String request);


	
}

/**
 * 
 */
package org.grpcmock.mmt.mocks.impl.flights;

import org.grpcmock.definitions.stub.ServerStreamingMethodStubBuilderImpl;
import org.grpcmock.mmt.mocks.MMTGRPCMocks;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.jayway.restassured.response.Response;
import com.mmt.flights.service.rpc.search.SearchResponse;
import com.mmt.flights.service.rpc.search.SearchServiceGrpc;
import com.mmt.flights.service.search.v4.response.SearchResponseDTO;

/**
 * @author Harpal
 *
 */
public class SearchServiceGRPC implements MMTGRPCMocks {

	Response wireMokcRespose;
	ServerStreamingMethodStubBuilderImpl serverStreamMethodBuilder =null;
	String request;
	
	@Override
	public void setup() {
		
		System.out.println("in SearchServiceGRPC baseurl:"+baseUrl);
		
		serverStreamMethodBuilder = new ServerStreamingMethodStubBuilderImpl(
				SearchServiceGrpc.METHOD_SEARCH_STREAM);

	}
	@Override
	public String getWireMockEndPoint()
	{
		return "flights-grpc/searchstream";
	}

	@Override
	public void setWireMockResponse(Response wireMokcResposeParam)
	{
		 this.wireMokcRespose=wireMokcResposeParam;
	}
	@Override
	public ServerStreamingMethodStubBuilderImpl getStubBuilderImpl()
	{
		return serverStreamMethodBuilder;
	}
	
	@Override
	public Object getResponseObjectProto()
	{
		SearchResponseDTO.Builder searchResponse = SearchResponseDTO.newBuilder();
		

		try {

			String str=wireMokcRespose.asString();
			//System.out.println("wiremockreeso:\n"+str);
			JsonFormat.parser().ignoringUnknownFields().merge(str, searchResponse);

		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}

		SearchResponse searchResponseExpected = SearchResponse.newBuilder().setSearchResponse(searchResponse)
				.build();

		return searchResponseExpected;
		
	}
	
	@Override
	public void setWireMockRequest(String request) {

		this.request=request;
	}

		
	
}

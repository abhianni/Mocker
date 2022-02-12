/**
 * 
 */
package org.grpcmock.mmt.mocks.impl.flights;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.grpcmock.definitions.stub.ServerStreamingMethodStubBuilderImpl;
import org.grpcmock.definitions.stub.UnaryMethodStubBuilderImpl;
import org.grpcmock.mmt.mocks.MMTGRPCMocks;
import org.json.JSONObject;

import com.goibibo.manthan.pkg.api.grpc.manthanGrpc;
import com.goibibo.manthan.pkg.api.grpc.discount.ViewAllOffers;
import com.goibibo.manthan.pkg.api.grpc.discount.ViewAllOffers.ViewAllOffersResponse;
import com.goibibo.manthan.pkg.api.grpc.discount.ViewAllOffers.ViewAllOffersResponse.AllPromos;
import com.goibibo.manthan.pkg.api.grpc.discount.ViewAllOffers.ViewAllOffersResponse.PromoData;
import com.goibibo.manthan.pkg.api.grpc.earn_and_burn.EarnAndBurn;
import com.goibibo.manthan.pkg.api.grpc.earn_and_burn.EarnAndBurn.BurnData;
import com.goibibo.manthan.pkg.api.grpc.earn_and_burn.EarnAndBurn.CommonData;
import com.goibibo.manthan.pkg.api.grpc.earn_and_burn.EarnAndBurn.EarnData;
import com.goibibo.manthan.pkg.api.grpc.earn_and_burn.EarnAndBurn.Response.Builder;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import com.google.protobuf.TextFormat.ParseException;
import com.google.protobuf.util.JsonFormat;
import com.jayway.restassured.response.Response;
import com.mmt.flights.service.rpc.search.SearchResponse;
import com.mmt.flights.service.search.v4.response.SearchResponseDTO;

public class ManthanViewAllOffersGRPC implements MMTGRPCMocks {

	Response wireMokcRespose;
	ServerStreamingMethodStubBuilderImpl serverStreamMethodBuilder =null;
	String request;
	
	@Override
	public void setup() {
		
		System.out.println("in ManthanViewAllOffersGRPC baseurl:"+baseUrl);
		
		serverStreamMethodBuilder = new ServerStreamingMethodStubBuilderImpl(
				manthanGrpc.METHOD_VIEW_ALL_OFFERS);

	}
	@Override
	public String getWireMockEndPoint()
	{
		return "manthan-grpc/view-all-offers";
	}

	@Override
	public void setWireMockResponse(Response wireMokcResposeParam)
	{
		 this.wireMokcRespose=wireMokcResposeParam;
	}
	
	@Override
	public Object getResponseObjectProto()
	{
		
		ViewAllOffers.ViewAllOffersResponse.Builder promoData = ViewAllOffers.ViewAllOffersResponse.newBuilder();
		
		try {
			JsonFormat.parser().merge(wireMokcRespose.asString(), promoData);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return promoData.build();
		
	}
	
	@Override
	public ServerStreamingMethodStubBuilderImpl getStubBuilderImpl() {
		// TODO Auto-generated method stub
		return serverStreamMethodBuilder;
	}
	@Override
	public void setWireMockRequest(String request) {

		this.request=request;
	}
	

		
	
}

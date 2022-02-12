package org.grpcmock.mmt.mocks.impl.flights;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.grpcmock.definitions.stub.ServerStreamingMethodStubBuilderImpl;
import org.grpcmock.mmt.mocks.MMTGRPCMocks;

import com.goibibo.manthan.pkg.api.grpc.manthanGrpc;
import com.goibibo.manthan.pkg.api.grpc.earn_and_burn.EarnAndBurn;
import com.google.protobuf.util.JsonFormat;
import com.jayway.jsonpath.JsonPath;
import com.jayway.restassured.response.Response;

public class ManthanEarnandBurnGRPC implements MMTGRPCMocks {

	Response wireMokcRespose;
	ServerStreamingMethodStubBuilderImpl serverStreamMethodBuilder =null;
	String request;
	
	@Override
	public void setup() {
		
		System.out.println("in ManthanEarnandBurnGRPC baseurl:"+baseUrl);
		
		serverStreamMethodBuilder = new ServerStreamingMethodStubBuilderImpl(
				manthanGrpc.METHOD_EARN_AND_BURN);

	}
	@Override
	public String getWireMockEndPoint() {
		String endPoint = "";
		String requestType = JsonPath.read(request, "$.requestType");
		if (requestType.equals(EarnAndBurn.RequestType.BURN.name())) {
			String promocode = JsonPath.read(request, "$.promoCode");
			endPoint = "manthan-grpc/apply-coupon/" + promocode;
		}

		if (requestType.equals(EarnAndBurn.RequestType.EARN.name())) {
			LinkedHashMap paramsMap = JsonPath.read(request, "$.lobQuery.flightParams.paramsMap");
			List<String> itIdList = new ArrayList<>(paramsMap.keySet());
			endPoint = "manthan-grpc/convfee-v2/" + itIdList.get(0);
		}
		return endPoint;
	}

	@Override
	public void setWireMockResponse(Response wireMokcResposeParam)
	{
		 this.wireMokcRespose=wireMokcResposeParam;
	}
	
	@Override
	public Object getResponseObjectProto()
	{
		
		EarnAndBurn.Response.Builder applyCoupon = EarnAndBurn.Response.newBuilder();
		
		try {
			JsonFormat.parser().merge(wireMokcRespose.asString(), applyCoupon);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return applyCoupon.build();
		
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

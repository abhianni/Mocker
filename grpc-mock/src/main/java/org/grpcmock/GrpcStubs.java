package org.grpcmock;

import com.goibibo.manthan.pkg.api.grpc.manthanGrpc;
import com.mmt.flights.service.rpc.search.SearchServiceGrpc;
import static org.grpcmock.GrpcMock.stubFor;
import static org.grpcmock.GrpcMock.*;

public class GrpcStubs {

	/**
	 * 
	 * Register Method Name from proto definition
	 */
	public static void registerGrpcStubs() {
		
        stubFor(serverStreamingMethod(SearchServiceGrpc.METHOD_SEARCH_STREAM));
        stubFor(serverStreamingMethod(manthanGrpc.METHOD_VIEW_ALL_OFFERS));
        stubFor(serverStreamingMethod(manthanGrpc.METHOD_EARN_AND_BURN));


	}
}

/**
 * 
 */
package org.grpcmock.mmt.mocks;

import java.util.HashMap;
import java.util.Map;

import org.grpcmock.mmt.mocks.impl.flights.ManthanEarnandBurnGRPC;
import org.grpcmock.mmt.mocks.impl.flights.ManthanViewAllOffersGRPC;
import org.grpcmock.mmt.mocks.impl.flights.SearchServiceGRPC;

/**
 * @author Harpal
 *
 */
public class MMTGRPCMockImpls {
	private Map<String, Class<? extends MMTGRPCMocks>> mmtgrpcImplsClsses = new HashMap<String, Class<? extends MMTGRPCMocks>>();

	public MMTGRPCMockImpls()
	{
		this.registerGRPCImpls();
	}
	
	
	public Class<? extends MMTGRPCMocks> getClassByfullMethodName(String methodName)
	{
		return mmtgrpcImplsClsses.get(methodName);
	}
	
	private void registerGRPCImpls() {
		mmtgrpcImplsClsses.put("com.mmt.flights.service.rpc.search.SearchService/SearchStream", SearchServiceGRPC.class); // copy paste and edit this line with your method name and class
		mmtgrpcImplsClsses.put("grpc.manthan/ViewAllOffers", ManthanViewAllOffersGRPC.class);
		mmtgrpcImplsClsses.put("grpc.manthan/EarnAndBurn", ManthanEarnandBurnGRPC.class);



	}
}

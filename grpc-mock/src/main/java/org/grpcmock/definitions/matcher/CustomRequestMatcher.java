package org.grpcmock.definitions.matcher;

public interface CustomRequestMatcher<ReqT> {

	boolean compareString(String request);
	
	boolean compareObject(Object request);


}

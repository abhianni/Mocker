package org.grpcmock.definitions.stub;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.annotation.Nonnull;

import org.grpcmock.GrpcMock;
import org.grpcmock.definitions.verification.CapturedRequest;
import org.grpcmock.definitions.verification.RequestPattern;
import org.grpcmock.exception.GrpcMockException;
import org.grpcmock.interceptors.HeadersInterceptor;
import org.grpcmock.mmt.mocks.MMTGRPCMockImpls;
import org.grpcmock.mmt.mocks.MMTGRPCMocks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.util.JsonFormat;
import com.jayway.jsonpath.JsonPath;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import com.mmt.flights.service.rpc.search.SearchResponse;
import com.mmt.flights.service.rpc.search.SearchServiceGrpc;
import com.mmt.flights.service.search.v4.response.SearchResponseDTO;

import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.ServerCallHandler;
import io.grpc.ServerMethodDefinition;
import io.grpc.stub.ServerCalls;
import io.grpc.stub.StreamObserver;

/**
 * @author Fadelis
 */
public class MethodStub<ReqT, RespT> {

	private static final Logger log = LoggerFactory.getLogger(GrpcMock.class);
	private static final String SEPARATOR = "----------------------------------------";

	private final MethodDescriptor<ReqT, RespT> method;
	private final List<StubScenario<ReqT, RespT>> stubScenarios;
	private final Queue<CapturedRequest<ReqT>> capturedRequests = new ConcurrentLinkedQueue<>();

	MethodStub(@Nonnull MethodDescriptor<ReqT, RespT> method, @Nonnull List<StubScenario<ReqT, RespT>> stubScenarios) {
		Objects.requireNonNull(method);
		Objects.requireNonNull(stubScenarios);
		Objects.requireNonNull(method.getServiceName());
		this.method = method;
		this.stubScenarios = new ArrayList<>(stubScenarios);
	}

	public String serviceName() {
		return this.method.getServiceName();
	}

	public String fullMethodName() {
		return this.method.getFullMethodName();
	}

	ServerMethodDefinition<ReqT, RespT> serverMethodDefinition() {
		return ServerMethodDefinition.create(method, serverCallHandler());
	}

	int callCountFor(@Nonnull RequestPattern<ReqT> requestPattern) {
		Objects.requireNonNull(requestPattern);
		if (!fullMethodName().equals(requestPattern.fullMethodName())) {
			throw new GrpcMockException("Cannot get call count for a different method");
		}
		return Math.toIntExact(capturedRequests.stream().filter(requestPattern::matches).count());
	}

	MethodStub registerScenarios(@Nonnull MethodStub<ReqT, RespT> methodStub) {
		Objects.requireNonNull(methodStub);
		if (!method.getFullMethodName().equals(methodStub.fullMethodName())) {
			throw new GrpcMockException("Cannot register stub scenarios for a different method");
		}
		this.stubScenarios.addAll(methodStub.stubScenarios);
		return this;
	}

	private ServerCallHandler<ReqT, RespT> serverCallHandler() {
		switch (method.getType()) {
		case UNARY:
			return ServerCalls.asyncUnaryCall(this::singleRequestCall);
		case SERVER_STREAMING:
			return ServerCalls.asyncServerStreamingCall(this::singleRequestCall);
		case CLIENT_STREAMING:
			return ServerCalls.asyncClientStreamingCall(
					responseObserver -> ServerCalls.asyncUnimplementedStreamingCall(method, responseObserver));
		case BIDI_STREAMING:
			return ServerCalls.asyncBidiStreamingCall(
					responseObserver -> ServerCalls.asyncUnimplementedStreamingCall(method, responseObserver));
		default:
			throw new GrpcMockException("Unsupported method type: " + method.getType());
		}

	}

	private void singleRequestCall(ReqT request, StreamObserver<RespT> streamObserver) {

		Metadata headers = HeadersInterceptor.INTERCEPTED_HEADERS.get();
		method.getFullMethodName();
		method.getServiceName();
		method.getSchemaDescriptor();
		MMTGRPCMockImpls mmtGRPCMockImpls = new MMTGRPCMockImpls();
		Class<? extends MMTGRPCMocks> clazz = mmtGRPCMockImpls.getClassByfullMethodName(method.getFullMethodName());
		MMTGRPCMocks mmtgrpcMock = null;
		if (clazz != null) {
			try {
				mmtgrpcMock = clazz.newInstance();
				mmtgrpcMock.setup();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		String jsonFormat = "";
		MessageOrBuilder requestBuilder = (MessageOrBuilder) request;
		try {

			jsonFormat = JsonFormat.printer().includingDefaultValueFields().print(requestBuilder);
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
		
		mmtgrpcMock.setWireMockRequest(jsonFormat);
		RequestSpecification requestSpecfication = RestAssured.given().when().with().urlEncodingEnabled(false)
				.contentType("application/json");

		requestSpecfication.body(jsonFormat);
		Response response = requestSpecfication.post(MMTGRPCMocks.baseUrl + mmtgrpcMock.getWireMockEndPoint());
		mmtgrpcMock.setWireMockResponse(response);
		mmtgrpcMock.getStubBuilderImpl().willReturn(mmtgrpcMock.getResponseObjectProto());
		mmtgrpcMock.getStubBuilderImpl().call(request, streamObserver);

	}

	private void captureRequest(CapturedRequest<ReqT> capturedRequest) {
		if (!capturedRequests.offer(capturedRequest)) {
			log.warn("Failed to capture request in the queue");
		} else {
			log.info("\n{}\nReceived request:\n{}\n{}", SEPARATOR, capturedRequest, SEPARATOR);
		}
	}
}

package com.mmt.wiremock.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.cloud.contract.stubrunner.HttpServerStub;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import com.mmt.wiremock.transformer.BaseTransformer;
import com.mmt.wiremock.transformer.flights.RememberedDataResp;
import com.mmt.wiremock.transformer.flights.soa.FlightSOAResponseTransformer;
import com.mmt.wiremock.transformer.flights.soa.FlightsGoCacheTransformer;

@Service("mockingService")
public class MockingService implements HttpServerStub {

	
	public static String runningMappings;
	public static ConcurrentHashMap<String,RememberedDataResp> tempRembData=new ConcurrentHashMap<String,RememberedDataResp>();
	private static final int wireMockPort = 8081;
	private static final int grpcPort = 8886;


	private WireMockServer wireMockServer;
	
	private StubMapping getMapping(File file) {
		try (InputStream stream = Files.newInputStream(file.toPath())) {
			return StubMapping.buildFrom(StreamUtils.copyToString(stream, Charset.forName("UTF-8")));
		} catch (IOException e) {
			throw new IllegalStateException("Cannot read file", e);
		}
	}

	@Override
	public boolean isAccepted(File arg0) {
		return false;
	}

	@Override
	public boolean isRunning() {
		return this.wireMockServer != null && this.wireMockServer.isRunning();
	}

	private String jsonArrayOfMappings(Collection<String> mappings) {
		return "[" + StringUtils.collectionToDelimitedString(mappings, ",\n") + "]";
	}

	@Override
	public int port() {
		return wireMockPort;
	}

	@Override
	public String registeredMappings() {
		Collection<String> mappings = new ArrayList<>();
		for (StubMapping stubMapping : this.wireMockServer.getStubMappings()) {
			mappings.add(stubMapping.toString());
		}
		return jsonArrayOfMappings(mappings);
	}

	@Override
	public HttpServerStub registerMappings(Collection<File> stubFiles) {
		if (!isRunning()) {
			throw new IllegalStateException("Server not started!");
		}
		registerStubMappings(stubFiles);
		return this;
	}

	private void registerStubMappings(Collection<File> stubFiles) {
		WireMock wireMock = new WireMock("localhost", port(), "");
		registerStubs(stubFiles, wireMock);
	}

	private void registerStubs(Collection<File> sortedMappings, WireMock wireMock) {
		for (File mappingDescriptor : sortedMappings) {
			try {
				wireMock.register(getMapping(mappingDescriptor));
				
			} catch (Exception e) {
			}
		}
	}

	@Override
	public HttpServerStub start() {
		if (isRunning()) {	
			return this;
		}
		return start(wireMockPort);
	}

	@Override
	public HttpServerStub start(int port) {
		WireMockConfiguration wireMockConfig = new WireMockConfiguration();
		wireMockConfig.extensions(new BaseTransformer(),new FlightSOAResponseTransformer());
		
		
		wireMockConfig.port(port);
		//this.wireMockServer = new WireMockServer(wireMockConfig);
		this.wireMockServer = new WireMockServer(wireMockConfig.httpsPort(8080));
		this.wireMockServer.start();
		return this;
	}

	@Override
	public HttpServerStub stop() {
		if (!isRunning()) {
			return this;
		}
		this.wireMockServer.stop();
		return this;
	}
	
	public int grpcPort() {
		return grpcPort;
	}

}
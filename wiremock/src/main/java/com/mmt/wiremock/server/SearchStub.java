package com.mmt.wiremock.server;

import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.cloud.contract.spec.Contract;
import org.springframework.cloud.contract.stubrunner.RunningStubs;
import org.springframework.cloud.contract.stubrunner.StubConfiguration;
import org.springframework.cloud.contract.stubrunner.StubFinder;
import org.springframework.cloud.contract.stubrunner.StubNotFoundException;

import com.github.tomakehurst.wiremock.client.CountMatchingStrategy;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.junit.Stubbing;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.github.tomakehurst.wiremock.verification.NearMiss;

public class SearchStub implements Stubbing, StubFinder {

	@Override
	public StubMapping givenThat(MappingBuilder mappingBuilder) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StubMapping stubFor(MappingBuilder mappingBuilder) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void editStub(MappingBuilder mappingBuilder) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeStub(MappingBuilder mappingBuilder) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeStub(StubMapping mappingBuilder) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<StubMapping> getStubMappings() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StubMapping getSingleStubMapping(UUID id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void verify(RequestPatternBuilder requestPatternBuilder) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void verify(int count, RequestPatternBuilder requestPatternBuilder) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<LoggedRequest> findAll(RequestPatternBuilder requestPatternBuilder) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ServeEvent> getAllServeEvents() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setGlobalFixedDelay(int milliseconds) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<LoggedRequest> findAllUnmatchedRequests() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<NearMiss> findNearMissesForAllUnmatchedRequests() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<NearMiss> findNearMissesFor(LoggedRequest loggedRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<NearMiss> findAllNearMissesFor(RequestPatternBuilder requestPatternBuilder) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean trigger(String ivyNotation, String labelName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean trigger(String labelName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean trigger() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Map<String, Collection<String>> labels() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URL findStubUrl(String groupId, String artifactId) throws StubNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URL findStubUrl(String ivyNotation) throws StubNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RunningStubs findAllRunningStubs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<StubConfiguration, Collection<Contract>> getContracts() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<StubMapping> findStubMappingsByMetadata(StringValuePattern pattern) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeStubMappingsByMetadata(StringValuePattern pattern) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void verify(CountMatchingStrategy countMatchingStrategy, RequestPatternBuilder requestPatternBuilder) {
		// TODO Auto-generated method stub
		
	}

}

package com.mmt.wiremock.controller;

import java.io.File;
import java.util.List;

import javax.annotation.Resource;

import com.mmt.wiremock.service.PaymentMockedService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.mmt.wiremock.service.MockingService;
import com.mmt.wiremock.transformer.flights.RememberedDataResp;

@RestController
public class MockingServiceController {

	@Autowired
	private
	PaymentMockedService paymentMockedService;

	@Resource(name = "mockingService")
	@Autowired
	private MockingService mockingService;
	@GetMapping(path = "/wiremock/restartWireMock")
	public String stopMockingService() throws MissingServletRequestParameterException {
		MockingService.runningMappings="mappings";
		mockingService.stop();
		mockingService.start().registerMappings(getMappingFileList());
		return "WireMock is Restarted And I am up and Running";
	}


	//@PostMapping(path = "payments-webservices-5-2/rest/paymentService/paymentHistory")
	public String getPaymentMockedResponse() throws MissingServletRequestParameterException {
//		MockingService.runningMappings="mappings";
//		mockingService.stop();
//		mockingService.start().registerMappings(getMappingFileList());
//		return "WireMock is Restarted And I am up and Running";
		return paymentMockedService.getPaymentHistoryResponse();
	}

	@GetMapping(path = "/wiremock/setServiceName/{service}")
	public String setMappLocation(@PathVariable("service") String service) throws MissingServletRequestParameterException {
		mockingService.isRunning();
		mockingService.stop();

		String serviceMappingDir="mappings";
		if(service!=null&&service.startsWith("mappings_"))
		{
			serviceMappingDir=service;
		}

		mockingService.start().registerMappings(getMappingFileList(serviceMappingDir));
		MockingService.runningMappings=serviceMappingDir;
		return "Mocking Service is Restarted with mappingDir:"+serviceMappingDir+" <br>Now I am up and Running";
	}

	@GetMapping(path = "/wiremock/setServiceName/{service}/isHealthCheck={hc}")
	public String setMappLocation(@PathVariable("service") String service,@PathVariable("hc") String hc) throws MissingServletRequestParameterException {
		if(MockingService.runningMappings==null||!MockingService.runningMappings.equals(service)){
		mockingService.isRunning();
		mockingService.stop();
		String serviceMappingDir="mappings";
		if(service!=null&&service.startsWith("mappings_"))
		{
			serviceMappingDir=service;
		}

		mockingService.start().registerMappings(getMappingFileList(serviceMappingDir));
		MockingService.runningMappings=serviceMappingDir;
		return "Mocking Service is Restarted with mappingDir:"+serviceMappingDir+" <br>Now I am up and Running";
		}
		else
		{
			return "WireMock is Already running with"+service
					+ "<br>"
					+ "still wont to reload please use <br>http://IP/wiremock/setServiceName/{mappingDirName}"
					+"<br>OR please use  <br>http://IP/wiremock/help";

		}
	}

	@GetMapping(path = "/wiremock/help")
	public String getInfo(String st)throws MissingServletRequestParameterException {

		return " mappings directory should be like below examples"
				+ "<br> 1. mappings_flights-search"
				+ "<br> 2. mappings_flights-mmt-svcs"
				+ "<br> 3. mappings_client_backend"
				+ "<br> 4. mappings_booking"
				+ "<br> 5. mappings_fare_rules"
				+ "<br> 6. mappings_All"
				+ "<br> 7. mappings_flights-booking"
				+ "<br> 8. mappings_flights-rules"
				+ "<br> 9. mappings_flights-corp"
				+"<br> please activate specific dir level mockings as below Example."
				+ "<br>IP:9090/wiremock/setServiceName/mappings_flights_search";
	}

	@GetMapping(path = "/wiremock/getTempRememberedData/{id}")
	public RememberedDataResp getTempRemberedData(@PathVariable("id") String remKey)throws MissingServletRequestParameterException {


		return getTempRemberedDataAndUpdateMap(remKey);


	}

	private RememberedDataResp getTempRemberedDataAndUpdateMap(String remKey)
	{
		if(null==remKey||remKey.length()<1)
		{
			RememberedDataResp localData= new RememberedDataResp("is Sent in request is Null or of lenght Zero.");
			return localData;
		}
		RememberedDataResp tempValue=MockingService.tempRembData.get(remKey);
		MockingService.tempRembData.remove(remKey);
		if(null==tempValue)
		{
			RememberedDataResp localData= new RememberedDataResp("No Data Found");
			return localData;
		}else
		{
			return tempValue;
		}
	}

	@GetMapping(path = "/wiremock/addRememberedData/{id}/{dataValue}")
	public String addTempRemberedData(@PathVariable("id") String remKey,@PathVariable("dataValue") String remValue)throws MissingServletRequestParameterException {

		RememberedDataResp localData= new RememberedDataResp(null,remValue,"",null,null,200);
		MockingService.tempRembData.put(remKey, localData);

		return "{\"status\":\"success\"}";


	}

	private List<File> getMappingFileList() {
		File rootDirPath = new File("src/test/resources/mappings/"); // src/main/resources/mappings/
		String[] extensions = { "json" };
		boolean recursive = true;
		return ((List<File>) FileUtils.listFiles(rootDirPath, extensions, recursive));
	}

	private List<File> getMappingFileList(String service) {

		File rootDirPath = new File("src/test/resources/"+service+"/"); // src/main/resources/mappings/
		String[] extensions = { "json" };
		boolean recursive = true;
		return ((List<File>) FileUtils.listFiles(rootDirPath, extensions, recursive));
	}
}
package com.mmt.wiremock;

import java.io.File;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.grpcmock.GrpcMock;
import org.grpcmock.GrpcStubs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import static org.grpcmock.GrpcMock.grpcMock;

import com.mmt.wiremock.service.MockingService;


@SpringBootApplication
public class MockingServiceApplication implements CommandLineRunner {

	@Resource(name = "mockingService")
	@Autowired
	private MockingService mockingService ;
	
	
	public static void main(String[] args) {
		SpringApplication.run(MockingServiceApplication.class, args);
	}
	
	void startMockingService()
	{
		mockingService.start().registerMappings(getMappingFileList());
	    GrpcMock.configureFor(grpcMock(mockingService.grpcPort()).build().start());
	    GrpcStubs.registerGrpcStubs();
	}
	private List<File> getMappingFileList() {
		File rootDirPath = new File("src/test/resources/mappings/"); // src/main/resources/mappings/
		String[] extensions = { "json" };
		boolean recursive = true;
		return ((List<File>) FileUtils.listFiles(rootDirPath, extensions, recursive));
	}

	@Override
	public void run(String... arg0) throws Exception {
		startMockingService();
	}
}
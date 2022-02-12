/**
 * 
 */
package com.mmt.wiremock.transformer.flights.soa;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.ResponseTransformer;
import com.github.tomakehurst.wiremock.http.HttpHeader;
import com.github.tomakehurst.wiremock.http.HttpHeaders;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.Response;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.jayway.jsonpath.JsonPath;
import com.mmt.flights.service.rpc.search.SearchResponse;
import com.mmt.flights.service.search.v4.response.SearchResponseDTO;
import com.mmt.wiremock.service.MockingService;
import com.mmt.wiremock.transformer.flights.RememberedDataResp;

/**
 * @author Harpal
 *
 */
public class FlightsGoCacheTransformer extends ResponseTransformer {

	String onwardDate = null;
	String returnDate = null;
	int initialDelay = 0;
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.tomakehurst.wiremock.extension.Extension#getName()
	 */
	@Override
	public String getName() {
		//
		System.out.println("in getNAme FlightsGoCacheTransformer");
		return "FlightsGoCacheTransformer";
	}

	@Override
	public Response transform(Request request, Response response, FileSource files, Parameters parameters) {

		Response responseLocal = null;
		long timestart = System.currentTimeMillis();
		if (request.getUrl().contains("/flights-search-go-cache/v1/search")) {

			String responseBody = response.getBodyAsString();
			// responseBody =
			// processSearchGoCacheRespNonGRPC(request, response, files, parameters,
			// responseBody);

			SearchResponseDTO.Builder searchResponse = SearchResponseDTO.newBuilder();

			try {

				JsonFormat.parser().ignoringUnknownFields().merge(responseBody, searchResponse);
				
			} catch (InvalidProtocolBufferException e) {
				e.printStackTrace();
			}

			responseLocal = new Response(response.getStatus(), "success", searchResponse.build().toByteArray(),
					response.getHeaders(), true, null, initialDelay, null, false);


			return responseLocal;
		}

		System.out.println("TimeTaken:" + (System.currentTimeMillis() - timestart));
		return response;

	}


	String processSearchGoCacheRespNonGRPC(Request request, Response response, FileSource files, Parameters parameters,
			String responseBody) {
		/// do processing

		return "";
	}


}

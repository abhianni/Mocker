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
import java.util.LinkedHashMap;
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
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.http.Response;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.jayway.jsonpath.JsonPath;
import com.jayway.restassured.path.xml.XmlPath;
import com.mmt.flights.service.search.v4.request.SearchRequestDTO;
import com.mmt.flights.service.search.v4.response.SearchResponseDTO;
import com.mmt.wiremock.service.MockingService;
import com.mmt.wiremock.transformer.flights.RememberedDataResp;

import net.minidev.json.JSONArray;

/**
 * @author Harpal
 *
 */
public class FlightSOAResponseTransformer extends ResponseTransformer {

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
		// TODO Auto-generated method stub
		System.out.println("in getNAme FklightSOA");
		return "FlightSOAResponseTransformer";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.tomakehurst.wiremock.extension.ResponseTransformer#transform(
	 * com. github.tomakehurst.wiremock.http.Request,
	 * com.github.tomakehurst.wiremock.http.Response,
	 * com.github.tomakehurst.wiremock.common.FileSource,
	 * com.github.tomakehurst.wiremock.extension.Parameters)
	 */
	@Override
	public Response transform(Request request, Response response, FileSource files, Parameters parameters) {
		

		
		Response responseLocal = null;
		long timestart = System.currentTimeMillis();

		if (request.getUrl().contains("/flights-search-go-cache/v1/search")) {
			String respondeBody = response.getBodyAsString();
			SearchResponseDTO.Builder searchResponse = SearchResponseDTO.newBuilder();
			try {
			JsonFormat.parser().ignoringUnknownFields().merge(respondeBody, searchResponse);

			} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			}
			try {
				SearchRequestDTO.Builder searchReq =SearchRequestDTO.newBuilder().mergeFrom(request.getBody());
				searchReq.build().toByteString();
				RememberedDataResp rememberedDataResp = new RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
						request.getBodyAsString(), request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
						respondeBody, response.getStatus());
				MockingService.tempRembData.put(
						"FTS_"+searchReq.getRequestConfig().getCorrelationId() + "_" + request.getMethod() +  "_flights-search-go-cache_v1_search" + "_" ,
						rememberedDataResp);
				
			} catch (InvalidProtocolBufferException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			/*
			 * SearchRequestDTO.Builder searchReq = SearchRequestDTO.newBuilder(); try {
			 * JsonFormat.parser().ignoringUnknownFields().merge(request.getBodyAsBase64(),
			 * searchReq);
			 * 
			 * } catch (InvalidProtocolBufferException e) { e.printStackTrace(); }
			 */
			
			responseLocal = new Response(response.getStatus(), "success", searchResponse.build().toByteArray(),
			response.getHeaders(), true, null, initialDelay, null, false);
			return responseLocal;
			}
		if (request.getUrl().contains("/flightSOA-web/search")) {

			String respondeBody = getProccessSOAResp(request, response, files, parameters);
			responseLocal = new Response(response.getStatus(), "success", respondeBody.getBytes(),
					response.getHeaders(), true, null, initialDelay, null, false);
			return responseLocal;
		} else if (request.getUrl().contains("/api/fareCalendar")) {
			String respondeBody = response.getBodyAsString();
			respondeBody = transformFareCalendarData(respondeBody);
			responseLocal = new Response(response.getStatus(), "success", respondeBody.getBytes(),
					response.getHeaders(), true, null,initialDelay, null, false);
			return responseLocal;
		} else if (request.getUrl().contains("/flightSOA-web/v1/review")) {
			String responseBody = getProcessSOAReivewRes(request, response, files, parameters);
			responseLocal = new Response(response.getStatus(), "success", responseBody.getBytes(),
					response.getHeaders(), true, null, initialDelay, null, false);
			return responseLocal;
		} else if (request.getUrl().contains("/flights-mmt-svcs/")) {
			return proccessMMTSVCSResp(request, response, files, parameters);
		} else if (request.getUrl().contains("/flightSOA-web/fareCal")) {
			String respondeBody = response.getBodyAsString();
			respondeBody = getProcessSOAFareCalRes(respondeBody);
			responseLocal = new Response(response.getStatus(), "success", respondeBody.getBytes(),
					response.getHeaders(), true, null, initialDelay, null, false);
			return responseLocal;
		} else if (request.getUrl().contains("/flights-itinerary")) {
			String responseBody = response.getBodyAsString();
			responseBody = processItineraryResp(request, response, files, parameters, responseBody);
			responseLocal = new Response(response.getStatus(), "success", responseBody.getBytes(),
					response.getHeaders(), true, null,initialDelay, null, false);
			return responseLocal;
		} else if (request.getUrl().contains("/flightBooking/")) {
			String responseBody = response.getBodyAsString();
			responseBody = processFlightBookingSOAReq(request, response, files, parameters, responseBody);
			responseLocal = new Response(response.getStatus(), "success", responseBody.getBytes(),
					response.getHeaders(), true, null, initialDelay, null, false);
			return responseLocal;
		} else if (request.getUrl().contains("/flights-ancillary-api/ancillary/stateFul/v2")) {
			String responseBody = response.getBodyAsString();
			responseBody = processFlightAncillarySOAReq(request, response, files, parameters, responseBody);
			responseLocal = new Response(response.getStatus(), "success", responseBody.getBytes(),
					response.getHeaders(), true, null, initialDelay, null, false);
			return responseLocal;
		} else if (request.getUrl().contains("/addons/airline")) {
			String responseBody = response.getBodyAsString();
			responseBody = processAncillaryAddonReq(request, response, files, parameters, responseBody);
			responseLocal = new Response(response.getStatus(), "success", responseBody.getBytes(),
					response.getHeaders(), true, null, initialDelay, null, false);
			return responseLocal;
		} else if (request.getUrl().contains("/flights-ancillary-api/ancillary/stateless")) {
			String responseBody = response.getBodyAsString();
			responseBody = processFlightAncillarySOAReqStateless(request, response, files, parameters, responseBody);
			responseLocal = new Response(response.getStatus(), "success", responseBody.getBytes(),
					response.getHeaders(), true, null, initialDelay, null, false);
			return responseLocal;
		} else if (request.getUrl().contains("/crosssell/flight/")) {
			String responseBody = response.getBodyAsString();
			responseBody = processCrosssellCABReq(request, response, files, parameters, responseBody);
			responseLocal = new Response(response.getStatus(), "success", responseBody.getBytes(),
					response.getHeaders(), true, null, initialDelay, null, false);
			return responseLocal;
		} else if (request.getUrl().contains("/flight/holdbooking/")) {
			String responseBody = response.getBodyAsString();
			responseBody = processholdBookingCABReq(request, response, files, parameters, responseBody);
			responseLocal = new Response(response.getStatus(), "success", responseBody.getBytes(),
					response.getHeaders(), true, null, initialDelay, null, false);
			return responseLocal;
		} else if (request.getUrl().contains("/flight/confirmbooking/")) {
			String responseBody = response.getBodyAsString();
			responseBody = processconfirmBookingCABReq(request, response, files, parameters, responseBody);
			responseLocal = new Response(response.getStatus(), "success", responseBody.getBytes(),
					response.getHeaders(), true, null, initialDelay, null, false);
			return responseLocal;
		}

		else if (request.getUrl().contains("flights-book-service")) {
			String responseBody = response.getBodyAsString();
			responseBody = processBookServiceReq(request, response, files, parameters, responseBody);
			responseLocal = new Response(response.getStatus(), "success", responseBody.getBytes(),
					response.getHeaders(), true, null, initialDelay, null, false);
			return responseLocal;
		}

		else if (request.getUrl().contains("/flights-search")) {
			String responseBody = response.getBodyAsString();
			responseBody = processFlightsCorpReq(request, response, files, parameters, responseBody);
			responseLocal = new Response(response.getStatus(), "success", responseBody.getBytes(),
					response.getHeaders(), true, null, initialDelay, null, false);
			return responseLocal;
		} else if (request.getUrl().contains("/flights-pnr-service")) {
			String responseBody = response.getBodyAsString();
			responseBody = processFlightsPnrServiceReq(request, response, files, parameters, responseBody);
			responseLocal = new Response(response.getStatus(), "success", responseBody.getBytes(),
					response.getHeaders(), true, null, initialDelay, null, false);
			return responseLocal;
		} else if (request.getUrl().contains("/flights-mp-rules")) {
			String responseBody = response.getBodyAsString();
			responseBody = processFlightsMPRuleReq(request, response, files, parameters, responseBody);
			responseLocal = new Response(response.getStatus(), "success", responseBody.getBytes(),
					response.getHeaders(), true, null, initialDelay, null, false);
			return responseLocal;
		} else if (request.getUrl().contains("/Nav-PostSales")) {
			String responseBody = response.getBodyAsString();
			responseBody = processNavPostSalesReq(request, response, files, parameters, responseBody);
			responseLocal = new Response(response.getStatus(), "success", responseBody.getBytes(),
					response.getHeaders(), true, null, initialDelay, null, false);
			return responseLocal;
		} else if (request.getUrl().contains("/flights-lcc-misc-rules")) {
			String responseBody = response.getBodyAsString();
			responseBody = processFlightsLccMiscRulesReq(request, response, files, parameters, responseBody);
			responseLocal = new Response(response.getStatus(), "success", responseBody.getBytes(),
					response.getHeaders(), true, null, initialDelay, null, false);
			return responseLocal;
		} else if (request.getUrl().contains("/flights/flightsUnification/")) {
			String responseBody = response.getBodyAsString();
			responseBody = processUserDetailReq(request, response, files, parameters, responseBody);
			responseLocal = new Response(response.getStatus(), "success", responseBody.getBytes(),
					response.getHeaders(), true, null, initialDelay, null, false);
			return responseLocal;
		} else if (request.getUrl().contains("/flights-tp-rules")) {
			String responseBody = response.getBodyAsString();
			responseBody = processFlightsTPRuleReq(request, response, files, parameters, responseBody);
			responseLocal = new Response(response.getStatus(), "success", responseBody.getBytes(),
					response.getHeaders(), true, null, initialDelay, null, false);
			return responseLocal;
		} else if (request.getUrl().contains("/flights-tp-svcs/v1/loyalty")) {
			String responseBody = response.getBodyAsString();
			responseBody = processloyaldata(request, response, files, parameters, responseBody);
			responseLocal = new Response(response.getStatus(), "success", responseBody.getBytes(),
					response.getHeaders(), true, null, initialDelay, null, false);
			return responseLocal;
		} else if (request.getUrl().contains("/flights-ancillaries")) {
			String responseBody = response.getBodyAsString();
			responseBody = processFlightsAncillariesReq(request, response, files, parameters, responseBody);
			responseLocal = new Response(response.getStatus(), "success", responseBody.getBytes(),
					response.getHeaders(), true, null, initialDelay, null, false);
			return responseLocal;
		} else if (request.getUrl().contains("/v1/getCustomServices")) {
			String responseBody = response.getBodyAsString();
			responseBody = processDptReq(request, response, files, parameters, responseBody);
			responseLocal = new Response(response.getStatus(), "success", responseBody.getBytes(),
					response.getHeaders(), true, null, initialDelay, null, false);
			return responseLocal;
		} else if (request.getUrl().contains("/cms-service")) {
			String responseBody = response.getBodyAsString();
			responseBody = processCmsServiceReq(request, response, files, parameters, responseBody);
			responseLocal = new Response(response.getStatus(), "success", responseBody.getBytes(),
					response.getHeaders(), true, null, initialDelay, null, false);
			return responseLocal;
		} else if (request.getUrl().contains("/internal/employee")) {
			String responseBody = response.getBodyAsString();
			responseBody = processEmployeeReq(request, response, files, parameters, responseBody);
			responseLocal = new Response(response.getStatus(), "success", responseBody.getBytes(),
					response.getHeaders(), true, null, initialDelay, null, false);
			return responseLocal;
		}

		else if (request.getUrl().contains("internal/policy/flights")) {
			String responseBody = response.getBodyAsString();
			responseBody = processPolicyCorpReq(request, response, files, parameters, responseBody);
			responseLocal = new Response(response.getStatus(), "success", responseBody.getBytes(),
					response.getHeaders(), true, null, initialDelay, null, false);
			return responseLocal;
		}

		else if (request.getUrl().contains("/approvals")) {
			String responseBody = response.getBodyAsString();
			responseBody = processApprovalCorpReq(request, response, files, parameters, responseBody);
			responseLocal = new Response(response.getStatus(), "success", responseBody.getBytes(),
					response.getHeaders(), true, null, initialDelay, null, false);
			return responseLocal;
		} else if (request.getUrl().contains("/common-payment-web-iframe")) {
			String responseBody = response.getBodyAsString();
			responseBody = processPaymentReq(request, response, files, parameters, responseBody);
			responseLocal = new Response(response.getStatus(), "success", responseBody.getBytes(),
					response.getHeaders(), true, null, initialDelay, null, false);
			return responseLocal;
		
	} else if (request.getUrl().contains("flights-grpc/searchstream")) {
		String responseBody = response.getBodyAsString();
		responseBody = processSearchGoCacheResp(request, response, files, parameters, responseBody);
		responseLocal = new Response(response.getStatus(), "success", responseBody.getBytes(),
				response.getHeaders(), true, null, initialDelay, null, false);
		return responseLocal;
	}
		
	else if (request.getUrl().contains("/manthan-grpc/view-all-offers")) {
		String responseBody = response.getBodyAsString();
		responseBody = processManthanViewAllOffersResp(request, response, files, parameters, responseBody);
		responseLocal = new Response(response.getStatus(), "success", responseBody.getBytes(),
				response.getHeaders(), true, null, initialDelay, null, false);
		return responseLocal;
	}
		
	else if (request.getUrl().contains("/manthan-grpc/apply-coupon/")) {
		String responseBody = response.getBodyAsString();
		responseBody = processManthanApplyCouponResp(request, response, files, parameters, responseBody);
		responseLocal = new Response(response.getStatus(), "success", responseBody.getBytes(),
				response.getHeaders(), true, null, initialDelay, null, false);
		return responseLocal;
	}
	
	else if (request.getUrl().contains("/manthan-grpc/convfee-v2/")) {
		String responseBody = response.getBodyAsString();
		responseBody = processManthanConvFeeResp(request, response, files, parameters, responseBody);
		responseLocal = new Response(response.getStatus(), "success", responseBody.getBytes(),
				response.getHeaders(), true, null, initialDelay, null, false);
		return responseLocal;
	}


		if (request.getUrl().contains("/bookingmanager.svc") || request.getUrl().contains("/BookingManager.svc")) {

			String respondeBody = null;
			try {
				respondeBody = getBookingManagerResponse(request, response, files, parameters);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			responseLocal = new Response(response.getStatus(), "success", respondeBody.getBytes(),
					response.getHeaders(), true, null, initialDelay, null, false);

			return responseLocal;
		}

		System.out.println("TimeTaken:" + (System.currentTimeMillis() - timestart));
		return response;

	}

	private String getBookingManagerResponse(Request request, Response response, FileSource files,
			Parameters parameters) throws ParseException {

		XmlPath xmlPath = new XmlPath(request.getBodyAsString()).setRoot("Envelope");
	
		String respondeBody = response.getBodyAsString();
		if (request.getBodyAsString().contains("GetAvailabilityRequest")) {

			int items = xmlPath.get(
					"Body.GetAvailabilityRequest.TripAvailabilityRequest.AvailabilityRequests.AvailabilityRequest.size()");
			if (items > 1) {
				String owDate = xmlPath.get(
						"Body.GetAvailabilityRequest.TripAvailabilityRequest.AvailabilityRequests.AvailabilityRequest[0].BeginDate");
				String rtDate = xmlPath.get(
						"Body.GetAvailabilityRequest.TripAvailabilityRequest.AvailabilityRequests.AvailabilityRequest[1].BeginDate");
				respondeBody = respondeBody.replaceAll("<DEPARTUREDATE_1>", owDate.split("T")[0]);
				respondeBody = respondeBody.replaceAll("<DEPARTUREDATE_2>", rtDate.split("T")[0]);
				onwardDate = changeDateFormat(owDate.split("T")[0]);
				returnDate = changeDateFormat(rtDate.split("T")[0]);
			} else {
				String owDate = xmlPath.get(
						"Body.GetAvailabilityRequest.TripAvailabilityRequest.AvailabilityRequests.AvailabilityRequest[0].BeginDate");
				respondeBody = respondeBody.replaceAll("<DEPARTUREDATE_1>", owDate.split("T")[0]);
				onwardDate = changeDateFormat(owDate.split("T")[0]);
			}

		//	System.out.println(onwardDate);

			respondeBody = respondeBody.replaceAll("<ONWARDDATE>", onwardDate);
			respondeBody = respondeBody.replaceAll("<RETURNDATE>", returnDate);
			System.out.println(respondeBody);
			
		} else if (request.getBodyAsString().contains("PriceItineraryRequest")) {
		
		respondeBody = respondeBody.replaceAll("<ONWARDDATE>", onwardDate);
		respondeBody = respondeBody.replaceAll("<RETURNDATE>", returnDate);
		/*System.out.println("==========================================PriceItineraryResponse=============================================");
		System.out.println(respondeBody);*/
		}
		return respondeBody;
	}

	String changeDateFormat(String inputdate) throws ParseException {

		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("MM/dd/yyyy");
		String ds2 = sdf2.format(sdf1.parse(inputdate));

		return ds2;
	}

	HttpHeaders getHttpHeaders() {
		HttpHeader additionalHeaders = new HttpHeader("Content-Type", "application/json");
		HttpHeader encoding = new HttpHeader("Content-Encoding", "gzip");
		List<HttpHeader> headerList = new ArrayList<>();
		headerList.add(additionalHeaders);
		headerList.add(encoding);
		HttpHeaders headers1 = new HttpHeaders(headerList);
		return headers1;
	}

	String getProccessSOAResp(Request request, Response response, FileSource files, Parameters parameters) {
		List<String> FromCities = JsonPath.read(request.getBodyAsString(), "$.requestCore.itineraryList[*].from");
		List<String> ToCities = JsonPath.read(request.getBodyAsString(), "$.requestCore.itineraryList[*].to");
		List<Object> deptDates = JsonPath.read(request.getBodyAsString(), "$.requestCore.itineraryList[*].depDate");
		String tripType = JsonPath.read(request.getBodyAsString(), "$.requestCore.tripType");
		String cabinClass = JsonPath.read(request.getBodyAsString(), "$.requestCore.cabinClass");
		String searchKey = JsonPath.read(request.getBodyAsString(), "$.requestCore.sequenceId");
		String respondeBody = response.getBodyAsString();
		for (int i = 0; i < FromCities.size(); i++) {
			respondeBody = transformRTData(respondeBody, i, FromCities, ToCities, deptDates, tripType, cabinClass);
		}
		respondeBody = updateEpochTimesForSOASearch(respondeBody, getDate(deptDates.get(0), 0, "ddMMyy"));

		RememberedDataResp rememberedDataResp = new RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
				request.getBodyAsString(), request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
				respondeBody, response.getStatus());
		MockingService.tempRembData.put(
				searchKey + "_" + request.getMethod() + "_" + request.getUrl().replaceAll("/", "_"),
				rememberedDataResp);

		return respondeBody;

	}

	Response proccessMMTSVCSResp(Request request, Response response, FileSource files, Parameters parameters) {
		if (request.getUrl().contains("/flights-mmt-svcs/v1/tp-aggregator/pre-applied-discount")) {
			Response responselocal = null;
			String responseBody = getProccessedPreAppliedINTLOWResponse(request, response, files, parameters);
			responselocal = new Response(response.getStatus(), "success", responseBody.getBytes(),
					response.getHeaders(), true, null, initialDelay, null, false);
			return responselocal;
		} else if (request.getUrl().contains("/flights-mmt-svcs/v1/tp-aggregator/recommended-discount")) {
			String urlComps[] = request.getAbsoluteUrl().split("\\/")[6].split("&");
			String itId = "";
			for (String queryPram : urlComps) {
				if (queryPram.contains("itId")) {
					itId = queryPram.split("=")[1];
					break;
				}
			}
			RememberedDataResp rememberedDataResp = new RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
					request.getBodyAsString(), request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
					response.getBodyAsString(), response.getStatus());
			MockingService.tempRembData.put(
					itId + "_" + request.getMethod() + "_" + "flights-mmt-svcs_v1_tp-aggregator_recommended-discount",
					rememberedDataResp);
			return response;
		} else if (request.getUrl().contains("/flights-mmt-svcs/v1/apply-coupon")
				|| request.getUrl().contains("/flights-mmt-svcs/v2/apply-coupon")) {
			String reqBody = request.getBodyAsString();
			String itId = JsonPath.read(reqBody, "$.itineraryId");
			RememberedDataResp rememberedDataResp = new RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
					request.getBodyAsString(), request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
					response.getBodyAsString(), response.getStatus());
			MockingService.tempRembData.put(itId + "_" + request.getMethod() + "_" + "flights-mmt-svcs_v1_apply-coupon",
					rememberedDataResp);
			return response;
		} else if (request.getUrl().contains("flights-mmt-svcs/v1/insurance-plan")
				|| request.getUrl().contains("flights-mmt-svcs/v2/insurance-plan")) {
			String urlComps[] = request.getAbsoluteUrl().split("\\/")[5].split("&");
			String itId = "";
			for (String queryPram : urlComps) {
				if (queryPram.contains("itId")) {
					itId = queryPram.split("=")[1];
					break;
				}
			}
			RememberedDataResp rememberedDataResp = new RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
					request.getBodyAsString(), request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
					response.getBodyAsString(), response.getStatus());
			MockingService.tempRembData.put(
					itId + "_" + request.getMethod() + "_" + "flights-mmt-svcs_v1_insurance-plan", rememberedDataResp);
			return response;
		} else if (request.getUrl().contains("flights-mmt-svcs/v1/tp-aggregator/loyalty-benefit-data")) {
			String urlComps[] = request.getAbsoluteUrl().split("\\/")[6].split("&");
			String itId = getQueryParamValue(urlComps, "ItId");
			if (itId != null) {
				RememberedDataResp rememberedDataResp = new RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
						request.getBodyAsString(), request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
						response.getBodyAsString(), response.getStatus());
				MockingService.tempRembData.put(itId + "_" + request.getMethod() + "_"
						+ "flights-mmt-svcs_v1_tp-aggregator_loyalty-benefit-data", rememberedDataResp);
			}
			return response;
		} else if (request.getUrl().contains("flights-mmt-svcs/v1/lending-data")) {
			String urlComps[] = request.getAbsoluteUrl().split("&");
			String itId = getQueryParamValue(urlComps, "ItId");
			if (itId != null) {
				RememberedDataResp rememberedDataResp = new RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
						request.getBodyAsString(), request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
						response.getBodyAsString(), response.getStatus());
				MockingService.tempRembData.put(
						itId + "_" + request.getMethod() + "_" + "flights-mmt-svcs_v1_lending-data",
						rememberedDataResp);
			}
			return response;
		} else if (request.getUrl().contains("flights-mmt-svcs/v1/user-details")) {
			String urlComps[] = request.getAbsoluteUrl().split("&");
			String itId = getQueryParamValue(urlComps, "idVal");
			if (itId != null) {
				itId = itId.replaceAll("\\%40", "AT");
				itId = itId.replaceAll(".com", "DOTcom");

				if (itId != null) {
					RememberedDataResp rememberedDataResp = new RememberedDataResp(
							getAllHeadersInMap(request.getHeaders()), request.getBodyAsString(),
							request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
							response.getBodyAsString(), response.getStatus());
					MockingService.tempRembData.put(
							itId + "_" + request.getMethod() + "_" + "flights-mmt-svcs_v1_user-details",
							rememberedDataResp);
				}
			}
			return response;
		} else if (request.getUrl().contains("/flights-mmt-svcs/v1/cabs")) {
			String urlComps[] = request.getAbsoluteUrl().split("&");
			String itId = getQueryParamValue(urlComps, "itId");

			if (itId != null) {
				RememberedDataResp rememberedDataResp = new RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
						request.getBodyAsString(), request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
						response.getBodyAsString(), response.getStatus());
				MockingService.tempRembData.put(itId + "_" + request.getMethod() + "_" + "flights-mmt-svcs_v1_cabs",
						rememberedDataResp);

			}
			return response;
		} else if (request.getUrl().contains("flights-mmt-svcs/v2/user-details")) {
			String urlComps[] = request.getAbsoluteUrl().split("&");
			String itId = getQueryParamValue(urlComps, "idVal");
			if (itId != null) {
				itId = itId.replaceAll("\\%40", "AT");
				itId = itId.replaceAll(".com", "DOTcom");

				if (itId != null) {
					RememberedDataResp rememberedDataResp = new RememberedDataResp(
							getAllHeadersInMap(request.getHeaders()), request.getBodyAsString(),
							request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
							response.getBodyAsString(), response.getStatus());
					MockingService.tempRembData.put(
							itId + "_" + request.getMethod() + "_" + "flights-mmt-svcs_v2_user-details",
							rememberedDataResp);
				}
			}
			return response;

		} else if (request.getUrl().contains("/flights-mmt-svcs/v1/wallet-info")) {
			String urlComps[] = request.getAbsoluteUrl().split("&");
			String coRel_Id = getQueryParamValue(urlComps, "crId");
			if (coRel_Id != null) {
				RememberedDataResp rememberedDataResp = new RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
						request.getBodyAsString(), request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
						response.getBodyAsString(), response.getStatus());
				MockingService.tempRembData.put(
						coRel_Id + "_" + request.getMethod() + "_" + "_flights-mmt-svcs_v1_wallet-info",
						rememberedDataResp);

			}
			return response;
		}

		else if (request.getUrl().contains("/flights-mmt-svcs/v1/tp-aggregator/loyalty-benefit-data")) {
			String urlComps[] = request.getAbsoluteUrl().split("&");
			String coRel_Id = getQueryParamValue(urlComps, "crId");
			if (coRel_Id != null) {
				RememberedDataResp rememberedDataResp = new RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
						request.getBodyAsString(), request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
						response.getBodyAsString(), response.getStatus());
				MockingService.tempRembData.put(coRel_Id + "_" + request.getMethod() + "_"
						+ "_flights-mmt-svcs_v1_tp-aggregator_loyalty-benefit-data", rememberedDataResp);

			}
			return response;
		}

		else if (request.getUrl().contains("flights-mmt-svcs/v2/hydra-segment")) {
			String urlComps[] = request.getAbsoluteUrl().split("&");
			String coRel_Id = getQueryParamValue(urlComps, "crId");
			if (coRel_Id != null) {
				RememberedDataResp rememberedDataResp = new RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
						request.getBodyAsString(), request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
						response.getBodyAsString(), response.getStatus());
				MockingService.tempRembData.put(
						coRel_Id + "_" + request.getMethod() + "_" + "_flights-mmt-svcs_v2_hydra-segment",
						rememberedDataResp);

			}
			return response;
		}

		else if (request.getUrl().contains("flights-tp-svcs/v1/route-happy-amenities")) {
			String urlComps[] = request.getAbsoluteUrl().split("&");
			String coRel_Id = getQueryParamValue(urlComps, "crId");
			if (coRel_Id != null) {
				RememberedDataResp rememberedDataResp = new RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
						request.getBodyAsString(), request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
						response.getBodyAsString(), response.getStatus());
				MockingService.tempRembData.put(
						coRel_Id + "_" + request.getMethod() + "_" + "_flights-tp-svcs_v1_route-happy-amenities",
						rememberedDataResp);

			}
			return response;
		}

		else if (request.getUrl().contains("/flights-mmt-svcs/v2/persuasion/peitho")) {
			String urlComps[] = request.getAbsoluteUrl().split("&");
			String coRel_Id = getQueryParamValue(urlComps, "itId");
			RememberedDataResp rememberedDataResp = MockingService.tempRembData.get(coRel_Id);
			String responseBody = response.getBodyAsString();

			if (rememberedDataResp != null) {
				String rkeys[] = rememberedDataResp.getRequestBody().split("\\$");
				int i = 1;
				for (String rkey : rkeys) {
					responseBody = responseBody.replaceAll("RKEY_" + i, rkey);
					i++;
				}

			}
			Response responseLocal = new Response(response.getStatus(), "success", responseBody.getBytes(),
					response.getHeaders(), true, null, initialDelay, null, false);
			if (coRel_Id != null) {
				RememberedDataResp rememberedDataResp1 = new RememberedDataResp(
						getAllHeadersInMap(request.getHeaders()), request.getBodyAsString(), request.getAbsoluteUrl(),
						getAllHeadersInMap(responseLocal.getHeaders()), responseLocal.getBodyAsString(),
						responseLocal.getStatus());
				MockingService.tempRembData.put(
						coRel_Id + "_" + request.getMethod() + "_" + "_flights-mmt-svcs_v2_persuasion_peitho",
						rememberedDataResp1);

			}

			return responseLocal;
		} else if (request.getUrl().contains("flights-mmt-svcs/v1/tp-aggregator/insurance")) {
			String responseBody = response.getBodyAsString();
			Response responseLocal = new Response(response.getStatus(), "success", responseBody.getBytes(),
					response.getHeaders(), true, null, initialDelay, null, false);
			String urlComps[] = request.getAbsoluteUrl().split("\\/")[6].split("&");
			String itId = getQueryParamValue(urlComps, "ItId");
			if (itId != null) {
				RememberedDataResp rememberedDataResp = new RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
						request.getBodyAsString(), request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
						response.getBodyAsString(), response.getStatus());
				MockingService.tempRembData.put(
						itId + "_" + request.getMethod() + "_" + "flights-mmt-svcs_v1_tp-aggregator_insurance",
						rememberedDataResp);
			}
			return responseLocal;
		}
		return response;
	}

	String getQueryParamValue(String urlComps[], String param) {
		try {
			for (String queryPram : urlComps) {
				if (queryPram.toUpperCase().contains(param.toUpperCase() + "=")) {
					return queryPram.split("=")[1];

				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		return null;
	}

	String processItineraryResp(Request request, Response response, FileSource files, Parameters parameters,
			String responseBody) {

		if (request.getUrl().endsWith("/CAN_PEN")) {
			String identifier = null;
			String responseLocal = response.getBodyAsString();
			if (request.getUrl().contains("/v2/")) {
				identifier = request.getUrl().split("booking")[1].split("/")[1];
			} else if (request.getUrl().contains("/v1/")) {
				identifier = request.getUrl().split("itinerary")[1].split("/")[1];
			}
//			System.out.println("idetifier :" + identifier);
			RememberedDataResp rememberedDataResp = new RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
					request.getBodyAsString(), request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
					responseBody, response.getStatus());
			MockingService.tempRembData.put("flights-itinerary_v2_itinerary_booking_CAN_PEN" + "_" + identifier,
					rememberedDataResp);

		} else if (request.getUrl().endsWith("/REVIEW") || request.getUrl().endsWith("/W_REVIEW")) {
			String urlComps[] = request.getAbsoluteUrl().split("\\/");
			RememberedDataResp rememberedDataResp = new RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
					request.getBodyAsString(), request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
					responseBody, response.getStatus());
			MockingService.tempRembData.put(urlComps[6] + "_" + request.getMethod() + "_" + urlComps[7],
					rememberedDataResp);
		} else if (request.getUrl().endsWith("/R_KEY")) {
			String urlComps[] = request.getAbsoluteUrl().split("\\/");
			RememberedDataResp rememberedDataResp = new RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
					request.getBodyAsString(), request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
					responseBody, response.getStatus());
			MockingService.tempRembData.put(urlComps[6] + "_" + request.getMethod() + "_" + "R_KEY" + "_" + urlComps[7],
					rememberedDataResp);
		} else if (request.getUrl().endsWith("/PRE_REVIEW")) {

			if (request.getMethod().equals(RequestMethod.GET)) {
				String urlComps[] = request.getAbsoluteUrl().split("\\/");
				RememberedDataResp rememberedDataResp = MockingService.tempRembData.get(urlComps[6]);
				if (rememberedDataResp != null) {

					if (responseBody.contains("RKEY_TOBEREPLACED")) {
						responseBody = responseBody.replaceAll("RKEY_TOBEREPLACED",
								rememberedDataResp.getRequestBody());
					} else {
						String rkeys[] = rememberedDataResp.getRequestBody().split("\\$");
						int i = 1;
						for (String rkey : rkeys) {
							responseBody = responseBody.replaceAll("RKEY_" + i, rkey);
							i++;
						}
					}

				}
				RememberedDataResp rememberedDataResp1 = new RememberedDataResp(
						getAllHeadersInMap(request.getHeaders()), responseBody, request.getAbsoluteUrl(),
						getAllHeadersInMap(response.getHeaders()), responseBody, response.getStatus());
				MockingService.tempRembData.put(urlComps[6] + "_" + request.getMethod() + "_" + urlComps[7],
						rememberedDataResp1);

				// MockingService.tempRembData.remove(urlComps[6]);

			} else if (request.getMethod().equals(RequestMethod.POST)) {
				Response responseLocal = null;
				String respondeBody = getUniqueItineraryIds(request, response, files, parameters);
				responseLocal = new Response(200, "success", respondeBody.getBytes(), response.getHeaders(), true, null,
						initialDelay, null, false);
				return respondeBody;
			}
		}

		else if (request.getUrl().contains("/flights-itinerary/v1/itinerary")
				&& request.getUrl().contains("TRAVELLER_DETAILS")) {
			Response responseLocal = null;
			String urlString[] = request.getAbsoluteUrl().split("/");
			String itineraryId = urlString[urlString.length - 2];
			RememberedDataResp rememberedDataResp = new RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
					request.getBodyAsString(), request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
					responseBody, response.getStatus());
			MockingService.tempRembData.put(itineraryId, rememberedDataResp);

		} else if (request.getUrl().endsWith("/LOB")) {
			String urlComps[] = request.getAbsoluteUrl().split("\\/");
			RememberedDataResp rememberedDataResp = new RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
					request.getBodyAsString(), request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
					responseBody, response.getStatus());
			MockingService.tempRembData.put(urlComps[6] + "_" + request.getMethod() + "_" + "LOB" + "_" + urlComps[7],
					rememberedDataResp);
//			System.out.println(MockingService.tempRembData.toString());
		} else if (request.getUrl().endsWith("/SRC")) {
			String urlComps[] = request.getAbsoluteUrl().split("\\/");
			RememberedDataResp rememberedDataResp = new RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
					request.getBodyAsString(), request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
					responseBody, response.getStatus());
			MockingService.tempRembData.put(urlComps[6] + "_" + request.getMethod() + "_" + "SRC" + "_" + urlComps[7],
					rememberedDataResp);
//			System.out.println(MockingService.tempRembData.toString());
		} else if (request.getUrl().contains("flights-itinerary/v2/itinerary")
				&& request.getUrl().contains("PREBOOK_INFO")) {
			responseBody = responseBody.replaceAll("replacableTime", String.valueOf(System.currentTimeMillis()));
			System.out.println(responseBody);
		} else if (request.getUrl().contains("flights-itinerary/v2/itinerary")) {
			Response responseLocal = null;
			String urlString[] = request.getAbsoluteUrl().split("/");
			String itineraryId = urlString[urlString.length - 2];
			RememberedDataResp rememberedDataResp = new RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
					request.getBodyAsString(), request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
					responseBody, response.getStatus());

			if (request.getUrl().contains(
					"W_REVIEW,TRAVELLER_DETAILS,APPLY_COUPON,WPM_RECOMMENDATION,RECOMMENDED_DISCOUNT,PAYMENT_REQUEST_DATA,LOYALTY_BENEFIT_DATA,MMT_ZC_INSURANCE,DIGIT_GENERAL_INSURANCE,CDF_APPLY_COUPON_V1,CDF_RECOMMENDED_COUPON_V1")
					|| request.getUrl().contains(
							"W_REVIEW,TRAVELLER_DETAILS,APPLY_COUPON,WPM_RECOMMENDATION,RECOMMENDED_DISCOUNT,PAYMENT_REQUEST_DATA,LOYALTY_BENEFIT_DATA,MMT_ZC_INSURANCE,ANCILLARIES_INFO,DIGIT_GENERAL_INSURANCE,CDF_APPLY_COUPON_V1,CDF_RECOMMENDED_COUPON_V1")
					|| request.getUrl().contains(
							"W_REVIEW,TRAVELLER_DETAILS,POST_REVIEW,FARE_RULE_PENALTY_BAGGAGE,CREDIT_SHELL_PNR_INFO")) {
				MockingService.tempRembData.put(itineraryId + "_GET_flights-itinerary_v2_itinerary",
						rememberedDataResp);
			} else if (request.getUrl().contains("LOYALTY_ACTIVATE_USER")) {
				MockingService.tempRembData.put(itineraryId + "_" + request.getMethod() + "_" + "postThankYou",
						rememberedDataResp);
			} else if (request.getUrl().contains("DIGIT_GENERAL_INSURANCE")) {
				MockingService.tempRembData.put(itineraryId + "_" + request.getMethod() + "_" + "digitTravelProtect",
						rememberedDataResp);
			}
			else {
				MockingService.tempRembData.put(itineraryId + "_" + request.getMethod() + "_" + "flights-itinerary_v2_itinerary",
						rememberedDataResp);
			}

		} else if (request.getUrl().contains("/flights-itinerary/v1/itinerary")
				&& request.getUrl().contains("DIGIT_GENERAL_INSURANCE")) {
			Response responseLocal = null;
			String urlString[] = request.getAbsoluteUrl().split("/");
			String itineraryId = urlString[urlString.length - 2];

			RememberedDataResp rememberedDataResp = new RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
					request.getBodyAsString(), request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
					responseBody, response.getStatus());
			MockingService.tempRembData.put(itineraryId + "_" + request.getMethod() + "_" + "digitTravelProtectPUT",
					rememberedDataResp);
		} else if (request.getUrl().contains("flights-itinerary/v1/itinerary/commit")) {
			Response responseLocal = null;
			String urlString[] = request.getAbsoluteUrl().split("/");
			String itineraryId = urlString[urlString.length - 1];
			RememberedDataResp rememberedDataResp = new RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
					request.getBodyAsString(), request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
					responseBody, response.getStatus());
			MockingService.tempRembData.put(itineraryId + "_PUT_flights-itinerary_v1_itinerary_commit",
					rememberedDataResp);

		} else if (request.getUrl().contains("IMPORTANT_INFO")) {
			Response responseLocal = null;
			String urlString[] = request.getAbsoluteUrl().split("/");
			String itineraryId = urlString[urlString.length - 2];
			RememberedDataResp rememberedDataResp = new RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
					request.getBodyAsString(), request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
					responseBody, response.getStatus());
			MockingService.tempRembData.put(itineraryId + "_PUT_flights-itinerary_v1_itinerary_IMPORTANT_INFO",
					rememberedDataResp);

		}

		return responseBody;
	}

	String getUniqueItineraryIds(Request request, Response response, FileSource fileSource, Parameters parameters) {
		String responseBody = "";
		String requestParam = JsonPath.read(request.getBodyAsString(), "$.deviceDetails.deviceId");
		requestParam = requestParam + System.currentTimeMillis();
		responseBody = response.getBodyAsString();
		responseBody = responseBody.replaceAll("itineraryID", requestParam);

		return responseBody;
	}

	String processFlightBookingSOAReq(Request request, Response response, FileSource files, Parameters parameters,
			String responseBody) {
		String responseBodyLocal = response.getBodyAsString();
		if (request.getUrl().endsWith("/flightBooking/prePayment")) {
			String FromCities = JsonPath.read(request.getBodyAsString(), "$.searchKey");
			RememberedDataResp rememberedDataResp = new RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
					request.getBodyAsString(), request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
					responseBodyLocal, response.getStatus());
			MockingService.tempRembData.put(FromCities + "_" + request.getMethod() + "_" + "prePayment",
					rememberedDataResp);
		} else if (request.getUrl().contains("/flightBooking/postPayment")) {
			String urlComps[] = request.getAbsoluteUrl().split("&");
			String itID = getQueryParamValue(urlComps, "searchKey");
			RememberedDataResp rememberedDataResp = new RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
					request.getBodyAsString(), request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
					responseBodyLocal, response.getStatus());
			MockingService.tempRembData.put(itID + "_" + request.getMethod() + "_" + "postPayment", rememberedDataResp);
		} else if (request.getUrl().endsWith("/flightBooking/v1/traveller-name-change")) {
			String correlationId = JsonPath.read(request.getBodyAsString(), "$.requestParams.correlationId");
			RememberedDataResp rememberedDataResp = new RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
					request.getBodyAsString(), request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
					responseBodyLocal, response.getStatus());
			MockingService.tempRembData.put(correlationId + "_" + request.getMethod() + "_" + "travellerNameChange",
					rememberedDataResp);
		} else if (request.getUrl().contains("/flightBooking/v1/booking-details")) {
			String urlComps[] = request.getAbsoluteUrl().split("//?");
			String mmt_Id = getQueryParamValue(urlComps, "mmtId");
			RememberedDataResp rememberedDataResp = new RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
					request.getBodyAsString(), request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
					responseBodyLocal, response.getStatus());
			MockingService.tempRembData.put(mmt_Id + "_" + request.getMethod() + "_" + "v1_booking-details",
					rememberedDataResp);
		} else if (request.getUrl().contains("/flightBooking/v1/fbk-pnr-details")) {
			String urlComps[] = request.getAbsoluteUrl().split("//?");
			String mmt_Id = getQueryParamValue(urlComps, "mmtId");
			RememberedDataResp rememberedDataResp = new RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
					request.getBodyAsString(), request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
					responseBodyLocal, response.getStatus());
			MockingService.tempRembData.put(mmt_Id + "_" + request.getMethod() + "_" + "v1_fbk-pnr-details",
					rememberedDataResp);
		}
		return responseBodyLocal;
	}

	String processFlightsPnrServiceReq(Request request, Response response, FileSource files, Parameters parameters,
			String responseBody) {
		String responseBodyLocal = response.getBodyAsString();
		if (request.getUrl().contains("flights-pnr-service/indexItems")) {
			String urlComps[] = request.getAbsoluteUrl().split("//?");
			String mmt_Id = getQueryParamValue(urlComps, "mmt_id");
			RememberedDataResp rememberedDataResp = new RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
					request.getBodyAsString(), request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
					responseBodyLocal, response.getStatus());
			MockingService.tempRembData.put(mmt_Id + "_" + request.getMethod() + "_" + "indexItems",
					rememberedDataResp);
		}
		return responseBodyLocal;
	}

	String processloyaldata(Request request, Response response, FileSource files, Parameters parameters,
			String responseBody) {
		String responseBodyLocal = response.getBodyAsString();
		if (request.getUrl().contains("/flights-tp-svcs/v1/loyalty")) {

			String mmt_Id = getValueFromPostRequest(request, "bookingId");
			RememberedDataResp rememberedDataResp = new RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
					request.getBodyAsString(), request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
					responseBodyLocal, response.getStatus());
			MockingService.tempRembData.put(mmt_Id + "_" + request.getMethod() + "_" + "flights_tp_svcs_loyalty",
					rememberedDataResp);
		}
		return responseBodyLocal;
	}

	String getValueFromPostRequest(Request request, String param) {
		String[] requestbody = request.getBodyAsString().split(",");

		for (int i = 0; i < requestbody.length; i++) {
			if (requestbody[i].contains(param)) {
				String str = requestbody[i].split(":")[1].replaceAll("^\"|\"$", "");
				return str;
			}
		}

		return null;
	}

	String processFlightsMPRuleReq(Request request, Response response, FileSource files, Parameters parameters,
			String responseBody) {
		String responseBodyLocal = response.getBodyAsString();
		String supplierCredential = response.getBodyAsString().split("supplierCredential")[1].split("[\\\"]+")[2]
				.replace("\\", "");
		String supplierName = response.getBodyAsString().split("supplierName")[1].split("[\\\"]+")[2].replace("\\", "");
		String jrnyKey = response.getBodyAsString().split("jrnyKey")[1].split(",")[0].split("\"")[2].replace("\\", "");
		if (request.getUrl().endsWith("/v1/postSaleRules")) {
			RememberedDataResp rememberedDataResp = new RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
					request.getBodyAsString(), request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
					responseBodyLocal, response.getStatus());

			MockingService.tempRembData.put(request.getMethod() + "_" + "flights-mp-rules_v1_postSaleRules_" + jrnyKey
					+ "_" + supplierName + "_" + supplierCredential, rememberedDataResp);
		} else if (request.getUrl().endsWith("/v1/preSaleRules")) {
			RememberedDataResp rememberedDataResp = new RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
					request.getBodyAsString(), request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
					responseBodyLocal, response.getStatus());

			MockingService.tempRembData.put(request.getMethod() + "_" + "flights-mp-rules_v1_preSaleRules_" + jrnyKey
					+ "_" + supplierName + "_" + supplierCredential, rememberedDataResp);
		}
//					System.out.println(MockingService.tempRembData);
		return responseBodyLocal;
	}

	String processCmsServiceReq(Request request, Response response, FileSource files, Parameters parameters,
			String responseBody) {
		String responseBodyLocal = response.getBodyAsString();
		String identifier = request.getBodyAsString().toString().split("OfficeID")[1].split("\"")[2];
//		System.out.println("OfficeID : " + identifier);
		if (request.getUrl().endsWith("credentialDetail")) {
			RememberedDataResp rememberedDataResp = new RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
					request.getBodyAsString(), request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
					responseBodyLocal, response.getStatus());
			MockingService.tempRembData.put(request.getMethod() + "_" + "cms_credentialDetail_" + identifier,
					rememberedDataResp);
		}
		return responseBodyLocal;
	}

	private String processEmployeeReq(Request request, Response response, FileSource files, Parameters parameters,
			String responseBody) {

		String responseBodyLocal = response.getBodyAsString();
		String urlComps[] = request.getAbsoluteUrl().split("&");
		String mmtUuid = "";
		if (getQueryParamValue(urlComps, "mmtUuid") == null) {
			mmtUuid = getQueryParamValue(urlComps, "emailCommId");
		} else {
			mmtUuid = getQueryParamValue(urlComps, "mmtUuid");
		}
		RememberedDataResp rememberedDataResp = new RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
				request.getBodyAsString(), request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
				responseBodyLocal, response.getStatus());
		MockingService.tempRembData.put(mmtUuid + "_" + request.getMethod() + "_" + "employee", rememberedDataResp);
		return responseBodyLocal;
	}

	private String processPolicyCorpReq(Request request, Response response, FileSource files, Parameters parameters,
			String responseBody) {

		String responseBodyLocal = response.getBodyAsString();
		String policyId = request.getUrl().substring(request.getUrl().lastIndexOf("/") + 1);
		RememberedDataResp rememberedDataResp = new RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
				request.getBodyAsString(), request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
				responseBodyLocal, response.getStatus());
		MockingService.tempRembData.put(policyId + "_" + request.getMethod() + "_" + "policy", rememberedDataResp);
		return responseBodyLocal;
	}

	private String processAncillaryAddonReq(Request request, Response response, FileSource files, Parameters parameters,
			String responseBody) {

		String responseBodyLocal = response.getBodyAsString();
		String itId = request.getUrl().split("itId")[1].split("&")[0].replaceAll("=", "");
		System.out.println("IT ID is " + itId);
		RememberedDataResp rememberedDataResp = new RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
				request.getBodyAsString(), request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
				responseBodyLocal, response.getStatus());
		MockingService.tempRembData.put(itId + "_" + request.getMethod() + "_" + "ancillaryAddOn", rememberedDataResp);
		return responseBodyLocal;
	}

	private String processApprovalCorpReq(Request request, Response response, FileSource files, Parameters parameters,
			String responseBody) {
		String reqBody = request.getBodyAsString();
		String responseBodyLocal = response.getBodyAsString();
		if (request.getUrl().endsWith("/approvals")) {
			String itId = JsonPath.read(reqBody, "$.idempotencyKey");
			RememberedDataResp rememberedDataResp = new RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
					request.getBodyAsString(), request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
					responseBodyLocal, response.getStatus());
			MockingService.tempRembData.put(itId + "_" + request.getMethod() + "_" + "approvals", rememberedDataResp);
		} else if (request.getUrl().contains("/approvals")) {
			String itId = request.getUrl().substring(request.getUrl().lastIndexOf("/") + 1);
			RememberedDataResp rememberedDataResp = new RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
					request.getBodyAsString(), request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
					responseBodyLocal, response.getStatus());
			MockingService.tempRembData.put(itId + "_" + request.getMethod() + "_" + "updateapprovals",
					rememberedDataResp);
		}
		return responseBodyLocal;
	}

	String processFlightsTPRuleReq(Request request, Response response, FileSource files, Parameters parameters,
			String responseBody) {
		String responseBodyLocal = response.getBodyAsString();
//		System.out.println("getBodyAsString : " +  response.getBodyAsString());
		String supplierCredentialArray[] = response.getBodyAsString().split("supplierCredential");
		String supplierCredential = supplierCredentialArray[supplierCredentialArray.length - 1].split(":")[1]
				.split("\"")[1];

		String supplierNameArray[] = response.getBodyAsString().split("supplierName");
		String supplierName = supplierNameArray[supplierNameArray.length - 1].split(":")[1].split("\"")[1];

		String jrnyKey = response.getBodyAsString().split("jrnyKey")[1].split(",")[0];
//		.split("\"")[2].replace("\\", "")
		if (jrnyKey.contains("\"")) {
			jrnyKey = jrnyKey.split("\"")[2].replace("\\", "");
		} else {
			jrnyKey = jrnyKey.replaceAll("[<>/]", "");
		}

		if (request.getUrl().endsWith("/v1/postSaleRules")) {
			RememberedDataResp rememberedDataResp = new RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
					request.getBodyAsString(), request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
					responseBodyLocal, response.getStatus());

			MockingService.tempRembData.put(request.getMethod() + "_" + "flights-tp-rules_v1_postSaleRules_" + jrnyKey
					+ "_" + supplierName + "_" + supplierCredential, rememberedDataResp);
		} else if (request.getUrl().endsWith("/v1/preSaleRules")) {
			RememberedDataResp rememberedDataResp = new RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
					request.getBodyAsString(), request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
					responseBodyLocal, response.getStatus());
			MockingService.tempRembData.put(request.getMethod() + "_" + "flights-tp-rules_v1_preSaleRules_" + jrnyKey
					+ "_" + supplierName + "_" + supplierCredential, rememberedDataResp);
		}
		System.out.println(MockingService.tempRembData.toString());
		return responseBodyLocal;
	}

	String processFlightsLccMiscRulesReq(Request request, Response response, FileSource files, Parameters parameters,
			String responseBody) {
		String responseBodyLocal = response.getBodyAsString();
		String supplierCredential = response.getBodyAsString().split("supplierCredential")[1].split("[\\\"]+")[2]
				.replace("\\", "");
		String supplierName = response.getBodyAsString().split("supplierName")[1].split("[\\\"]+")[2].replace("\\", "");
		String jrnyKey = response.getBodyAsString().split("jrnyKey")[1].split(",")[0].split("\"")[2].replace("\\", "");
		if (request.getUrl().endsWith("/v1/postSaleRules")) {
			RememberedDataResp rememberedDataResp = new RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
					request.getBodyAsString(), request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
					responseBodyLocal, response.getStatus());
			MockingService.tempRembData.put(request.getMethod() + "_" + "flights-lcc-misc-rules_v1_postSaleRules_"
					+ jrnyKey + "_" + supplierName + "_" + supplierCredential, rememberedDataResp);
		} else if (request.getUrl().endsWith("/v1/preSaleRules")) {
			RememberedDataResp rememberedDataResp = new RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
					request.getBodyAsString(), request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
					responseBodyLocal, response.getStatus());
			MockingService.tempRembData.put(request.getMethod() + "_" + "flights-lcc-misc-rules_v1_preSaleRules_"
					+ jrnyKey + "_" + supplierName + "_" + supplierCredential, rememberedDataResp);
		}
		System.out.println(MockingService.tempRembData.toString());
		return responseBodyLocal;
	}

	String processNavPostSalesReq(Request request, Response response, FileSource files, Parameters parameters,
			String responseBody) {
		String responseBodyLocal = response.getBodyAsString();
//		String supplierName = request.getBodyAsString().split("supplierCode")[1].split("[\":\"]+")[1].replaceAll(" ", "");
//		String supplierCredential = request.getBodyAsString().split("cn")[1].split("[\":\"]+")[1].replaceAll(" ", "");
//		String jrnyKey =response.getBodyAsString().split("jrnyKey")[1].split(",")[0].split("\"")[2].replace("\\","");
		String supplierName = request.getBodyAsString().split("supplierCode")[1].split("[\":\"]+")[1].replaceAll(" ",
				"");
		String supplierCredential = request.getBodyAsString().split("cn")[1].split("[\":\"]+")[1].replaceAll(" ", "");
//		pnrURLMaking(responseBodyLocal,supplierName,supplierCredential);
		if (request.getUrl().endsWith("/getpnr/getpnrrequest")) {
			RememberedDataResp rememberedDataResp = new RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
					request.getBodyAsString(), request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
					responseBodyLocal, response.getStatus());
//			MockingService.tempRembData.put(request.getMethod() + "_" + "Nav-PostSales_getpnr_getpnrrequest_"
//					+ supplierName + "_" + supplierCredential, rememberedDataResp);
			MockingService.tempRembData.put(request.getMethod() + "_" + "Nav-PostSales_getpnr_getpnrrequest_"
					+ pnrURLMaking(responseBodyLocal, supplierName, supplierCredential), rememberedDataResp);
		}
		return responseBodyLocal;
	}

	String pnrURLMaking(String ResponseBody, String supplierName, String supplierCredential) {
		StringBuilder stringBuilder = new StringBuilder();
		String from[] = ResponseBody.split("fc");
		stringBuilder.append(from[2].split(":")[1].split("\"")[1]).append("-");
		String to[] = from[2].split("tc");
		if (from.length > 3) {
			stringBuilder.append(from[3].split("tc")[1].split(":")[1].split("\"")[1]);
			stringBuilder.append(" : ");
			stringBuilder.append(from[2].split("oa")[1].split(":")[1].split("\"")[1]).append("-");
			stringBuilder.append(from[2].split("fn")[1].split(":")[1].split("\"")[1]).append(" | ");
			stringBuilder.append(from[3].split("oa")[1].split(":")[1].split("\"")[1]).append("-");
			stringBuilder.append(from[3].split("fn")[1].split(":")[1].split("\"")[1]);
		} else {
			stringBuilder.append(from[2].split("tc")[1].split(":")[1].split("\"")[1]);
			stringBuilder.append(" : ");
			stringBuilder.append(from[2].split("oa")[1].split(":")[1].split("\"")[1]).append("-");
			stringBuilder.append(from[2].split("fn")[1].split(":")[1].split("\"")[1]);
		}
		stringBuilder.append("_").append(supplierName).append("_").append(supplierCredential);
		return stringBuilder.toString();
	}

	String processFlightAncillarySOAReq(Request request, Response response, FileSource files, Parameters parameters,
			String responseBody) {
		String responseBodyLocal = response.getBodyAsString();
		if (request.getUrl().endsWith("/flights-ancillary-api/ancillary/stateFul/v2")) {
			String ItinararyId = JsonPath.read(request.getBodyAsString(), "$.searchKey");
			RememberedDataResp rememberedDataResp = new RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
					request.getBodyAsString(), request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
					responseBodyLocal, response.getStatus());
			MockingService.tempRembData.put(ItinararyId + "_" + request.getMethod() + "_" + "stateFulV2",
					rememberedDataResp);
		}
		return responseBodyLocal;
	}

	String processFlightAncillarySOAReqStateless(Request request, Response response, FileSource files,
			Parameters parameters, String responseBody) {
		String responseBodyLocal = response.getBodyAsString();
		if (request.getUrl().endsWith("/flights-ancillary-api/ancillary/stateless")) {
			String ItinararyId = JsonPath.read(request.getBodyAsString(), "$.searchKey");
			RememberedDataResp rememberedDataResp = new RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
					request.getBodyAsString(), request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
					responseBodyLocal, response.getStatus());
			MockingService.tempRembData.put(ItinararyId + "_" + request.getMethod() + "_" + "stateless",
					rememberedDataResp);
		}
		return responseBodyLocal;
	}

	String processCrosssellCABReq(Request request, Response response, FileSource files, Parameters parameters,
			String responseBody) {
		String responseBodyLocal = response.getBodyAsString();
		if (request.getUrl().endsWith("/crosssell/flight/pricing")) {
			RememberedDataResp rememberedDataResp = new RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
					request.getBodyAsString(), request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
					responseBodyLocal, response.getStatus());
			MockingService.tempRembData.put("_PUT_crosssell_flight_pricing", rememberedDataResp);
		}
		return responseBodyLocal;
	}

	String processholdBookingCABReq(Request request, Response response, FileSource files, Parameters parameters,
			String responseBody) {
		String responseBodyLocal = response.getBodyAsString();
		if (request.getUrl().endsWith("/flight/holdbooking/")) {
			RememberedDataResp rememberedDataResp = new RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
					request.getBodyAsString(), request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
					responseBodyLocal, response.getStatus());
			MockingService.tempRembData.put("_POST_cabs_holdBooking", rememberedDataResp);
		}
		return responseBodyLocal;
	}

	String processconfirmBookingCABReq(Request request, Response response, FileSource files, Parameters parameters,
			String responseBody) {
		String responseBodyLocal = response.getBodyAsString();
		if (request.getUrl().endsWith("/flight/confirmbooking/")) {
			RememberedDataResp rememberedDataResp = new RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
					request.getBodyAsString(), request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
					responseBodyLocal, response.getStatus());
			MockingService.tempRembData.put("_POST_cabs_confirmBooking", rememberedDataResp);
		}
		return responseBodyLocal;
	}

	String processBookServiceReq(Request request, Response response, FileSource files, Parameters parameters,
			String responseBody) {
		String itId = JsonPath.read(request.getBodyAsString(), "$.itineraryId");
		String responseBodyLocal = response.getBodyAsString();
		if (request.getUrl().endsWith("/v1/travellerFormInfo")) {
			RememberedDataResp rememberedDataResp = new RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
					request.getBodyAsString(), request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
					responseBodyLocal, response.getStatus());
			MockingService.tempRembData.put(itId + "_" + request.getMethod() + "_" + "traveller_form_details",
					rememberedDataResp);
		} else if (request.getUrl().endsWith("/v1/validateTravellerInfo")) {
			RememberedDataResp rememberedDataResp = new RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
					request.getBodyAsString(), request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
					responseBodyLocal, response.getStatus());
			MockingService.tempRembData.put(itId + "_" + request.getMethod() + "_" + "validate_traveler_info",
					rememberedDataResp);
		} else if (request.getUrl().endsWith("/v1/validateTravellersInfo")) {
			RememberedDataResp rememberedDataResp = new RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
					request.getBodyAsString(), request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
					responseBodyLocal, response.getStatus());
			MockingService.tempRembData.put(itId + "_" + request.getMethod() + "_" + "validate_traveler_form",
					rememberedDataResp);
		}
		return responseBodyLocal;
	}

	String processPaymentReq(Request request, Response response, FileSource files, Parameters parameters,
			String responseBody) {
		String itId = JsonPath.read(request.getBodyAsString(), "$.searchKey");
		String responseBodyLocal = response.getBodyAsString();
		if (request.getUrl().endsWith("/internal/submitPrepaidAdjustment.pymt")) {
			RememberedDataResp rememberedDataResp = new RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
					request.getBodyAsString(), request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
					responseBodyLocal, response.getStatus());
			MockingService.tempRembData.put(itId + "_" + request.getMethod() + "_" + "submitPrepaidAdjustment",
					rememberedDataResp);
		}
		return responseBodyLocal;
	}

	private String processFlightsAncillariesReq(Request request, Response response, FileSource files,
			Parameters parameters, String responseBody) {

		String responseBodyLocal = response.getBodyAsString();
		if (request.getUrl().contains("b2c/meals-and-baggage/") || request.getUrl().contains("b2c/seats/")) {
			String itId = request.getUrl().substring(request.getUrl().lastIndexOf("/") + 1);
			RememberedDataResp rememberedDataResp = new RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
					request.getBodyAsString(), request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
					responseBodyLocal, response.getStatus());
			MockingService.tempRembData.put(itId, rememberedDataResp);
		}
		return responseBodyLocal;
	}

	String processDptReq(Request request, Response response, FileSource files, Parameters parameters,
			String responseBody) {
		String serviceName = JsonPath.read(request.getBodyAsString(), "$.service");
		String responseBodyLocal = response.getBodyAsString();
		if (serviceName.equalsIgnoreCase("flights-zc-user-agg")) {
			RememberedDataResp rememberedDataResp = new RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
					request.getBodyAsString(), request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
					responseBodyLocal, response.getStatus());
			MockingService.tempRembData.put("UFULKJ5CVMK" + "_" + request.getMethod() + "_" + "flights_zc_user_agg",
					rememberedDataResp);
		}
		return responseBodyLocal;
	}

	String processFlightsCorpReq(Request request, Response response, FileSource files, Parameters parameters,
			String responseBody) {
		String responseBodyLocal = response.getBodyAsString();
		if (request.getUrl().contains("/v1/post-search")) {
			String urlComps[] = request.getAbsoluteUrl().split("&");
			String crId = getQueryParamValue(urlComps, "crId");
			RememberedDataResp rememberedDataResp = new RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
					request.getBodyAsString(), request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
					responseBodyLocal, response.getStatus());
			MockingService.tempRembData.put(crId + "_" + request.getMethod() + "_" + "post_search", rememberedDataResp);
		} else if (request.getUrl().contains("/v1/rkey-info")) {
			String urlComps[] = request.getAbsoluteUrl().split("&");
			String crId = getQueryParamValue(urlComps, "crId");
			RememberedDataResp rememberedDataResp = new RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
					request.getBodyAsString(), request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
					responseBodyLocal, response.getStatus());
			MockingService.tempRembData.put(crId + "_" + request.getMethod() + "_" + "search_rkey", rememberedDataResp);
		} else if (request.getUrl().contains("/v1/calendar")) {
			String urlComps[] = request.getAbsoluteUrl().split("&");
			String crId = getQueryParamValue(urlComps, "crId");
			RememberedDataResp rememberedDataResp = new RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
					request.getBodyAsString(), request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
					responseBodyLocal, response.getStatus());
			MockingService.tempRembData.put(crId + "_" + request.getMethod() + "_" + "calendar", rememberedDataResp);
		} else if (request.getUrl().contains("/v1/forward-flow")) {
			String urlComps[] = request.getAbsoluteUrl().split("&");
			String crId = getQueryParamValue(urlComps, "crId");
			RememberedDataResp rememberedDataResp = new RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
					request.getBodyAsString(), request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
					responseBodyLocal, response.getStatus());
			MockingService.tempRembData.put(crId + "_" + request.getMethod() + "_" + "forward-flow",
					rememberedDataResp);
		} else if (request.getUrl().contains("/v1/post-review")) {
			String urlComps[] = request.getAbsoluteUrl().split("&");
			String crId = getQueryParamValue(urlComps, "crId");
			RememberedDataResp rememberedDataResp = new RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
					request.getBodyAsString(), request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
					responseBodyLocal, response.getStatus());
			MockingService.tempRembData.put(crId + "_" + request.getMethod() + "_" + "post-review", rememberedDataResp);
		}
		return responseBodyLocal;
	}

	/*
	 * String processUserDetailReq(Request request, Response response, FileSource
	 * files, Parameters parameters, String responseBody) {
	 * 
	 * String responseBodyLocal = response.getBodyAsString(); if
	 * (request.getUrl().endsWith("/user/details")) { String email =
	 * JsonPath.read(request.toString(), "$.result.extendedUser.primaryEmailId");
	 * RememberedDataResp rememberedDataResp = new
	 * RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
	 * request.getBodyAsString(), request.getAbsoluteUrl(),
	 * getAllHeadersInMap(response.getHeaders()), responseBodyLocal,
	 * response.getStatus());
	 * MockingService.tempRembData.put(email+"_"+request.getMethod()+
	 * "__user_detail", rememberedDataResp); }
	 * 
	 * if (request.getUrl().endsWith("/user/update")) {
	 * 
	 * 
	 * String headerdata[]=
	 * getAllHeadersInMap(request.getHeaders()).get("User-Identifier").split("\\{");
	 * if(headerdata.length>1) { System.out.println("Len >1"); String email=
	 * headerdata[1].split("\":\"")[2].split("\",\"")[0];
	 * 
	 * for (int i =0;i<email_value.length;i++) { email=email+email_value[i];
	 * 
	 * } RememberedDataResp rememberedDataResp = new
	 * RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
	 * request.getBodyAsString(), request.getAbsoluteUrl(),
	 * getAllHeadersInMap(response.getHeaders()), responseBodyLocal,
	 * response.getStatus());
	 * 
	 * MockingService.tempRembData.put(email+"_"+request.getMethod()+
	 * "__user_update", rememberedDataResp); } else { RememberedDataResp
	 * rememberedDataResp = new
	 * RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
	 * request.getBodyAsString(), request.getAbsoluteUrl(),
	 * getAllHeadersInMap(response.getHeaders()), responseBodyLocal,
	 * response.getStatus()); String a =headerdata[0].split(":")[1];
	 * MockingService.tempRembData.put(a+"_"+request.getMethod()+"__user_update",
	 * rememberedDataResp); }
	 * 
	 * } return responseBodyLocal; }
	 */

	String processUserDetailReq(Request request, Response response, FileSource files, Parameters parameters,
			String responseBody) {
		String responseBodyLocal = response.getBodyAsString();
		if (request.getUrl().endsWith("/user/details")) {
			String mailValue = null;
			try {
				String email = JsonPath.read(responseBodyLocal, "$.result.extendedUser.primaryEmailId");
				mailValue = email.split("@")[0];
			} catch (Exception e) {
			}
			if (null != mailValue) {
				RememberedDataResp rememberedDataResp = new RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
						request.getBodyAsString(), request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
						responseBodyLocal, response.getStatus());
				MockingService.tempRembData.put(mailValue + "_" + request.getMethod() + "_user_detail",
						rememberedDataResp);
			}

		}

		return responseBodyLocal;
	}
	
	String processSearchGoCacheResp(Request request, Response response, FileSource files, Parameters parameters,
			String responseBody) {
		String crId = JsonPath.read(request.getBodyAsString(), "$.searchRequest.requestConfig.correlationId");
		String responseBodyLocal = response.getBodyAsString();
		
			RememberedDataResp rememberedDataResp = new RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
					request.getBodyAsString(), request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
					responseBodyLocal, response.getStatus());
			MockingService.tempRembData.put(crId + "_" + "POST_searchstream" ,
					rememberedDataResp);
		
		return responseBodyLocal;
	}
	
	String processManthanViewAllOffersResp(Request request, Response response, FileSource files, Parameters parameters,
			String responseBody) {
		LinkedHashMap paramsMap = JsonPath.read(request.getBodyAsString(), "$.lobQuery.flightParams.paramsMap");
		List<String> itIdList = new ArrayList<>(paramsMap.keySet());
		String responseBodyLocal = response.getBodyAsString();
			RememberedDataResp rememberedDataResp = new RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
					request.getBodyAsString(), request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
					responseBodyLocal, response.getStatus());
			MockingService.tempRembData.put(itIdList.get(0) + "_" + request.getMethod() + "_viewalloffers",
					rememberedDataResp);
		
		return responseBodyLocal;
	}
	
	String processManthanApplyCouponResp(Request request, Response response, FileSource files, Parameters parameters,
			String responseBody) {
		String promocode = JsonPath.read(request.getBodyAsString(), "$.promoCode");
		String responseBodyLocal = response.getBodyAsString();
			RememberedDataResp rememberedDataResp = new RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
					request.getBodyAsString(), request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
					responseBodyLocal, response.getStatus());
			MockingService.tempRembData.put(promocode + "_" + request.getMethod() + "_manthanapplycoupon",
					rememberedDataResp);
		
		return responseBodyLocal;
	}
	
	private String processManthanConvFeeResp(Request request, Response response, FileSource files,
			Parameters parameters, String responseBody) {
		LinkedHashMap paramsMap = JsonPath.read(request.getBodyAsString(), "$.lobQuery.flightParams.paramsMap");
		List<String> itIdList = new ArrayList<>(paramsMap.keySet());
		String responseBodyLocal = response.getBodyAsString();
			RememberedDataResp rememberedDataResp = new RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
					request.getBodyAsString(), request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
					responseBodyLocal, response.getStatus());
			MockingService.tempRembData.put(itIdList.get(0) + "_" + request.getMethod() + "_convfee_v2",
					rememberedDataResp);
		
		return responseBodyLocal;
	}

	Map<String, String> getAllHeadersInMap(HttpHeaders headers) {
		Map<String, String> header = new HashMap<String, String>();
		headers.keys().forEach(key -> {
			header.put(key, headers.getHeader(key).toString());
		});
		return header;
	}

	String getProccessedPreAppliedINTLOWResponse(Request request, Response response, FileSource files,
			Parameters parameters) {
		String respondeBody = response.getBodyAsString();
		Map<String, JSONArray> journies = JsonPath.read(request.getBodyAsString(), "$.itineraryData");
		int i = 1;
		for (Map.Entry<String, JSONArray> entry : journies.entrySet()) {

			Map<String, String> reKeysINBaseResponse = JsonPath.read(respondeBody,
					"$.discountInfo.FROMCITY" + String.valueOf(i) + "-TOCITY" + String.valueOf(i) + "-DEPTDATE"
							+ String.valueOf(i) + ".discountInfoMap");
			int j = 0;
			JSONArray rKeysInRequest = entry.getValue();
			for (Map.Entry<String, String> entryRkeys : reKeysINBaseResponse.entrySet()) {
				if (j >= reKeysINBaseResponse.size() || j >= rKeysInRequest.size()) {
					break;
				}
				respondeBody = respondeBody.replace(entryRkeys.getKey(), rKeysInRequest.get(j).toString());
				j++;
			}
			String journeyData[] = entry.getKey().split("-");
			respondeBody = respondeBody.replaceAll("FROMCITY" + String.valueOf(i), journeyData[0]);
			respondeBody = respondeBody.replaceAll("TOCITY" + String.valueOf(i), journeyData[1]);
			respondeBody = respondeBody.replaceAll("DEPTDATE" + String.valueOf(i), journeyData[2]);
			i++;

		}

		return respondeBody;

	}

	String transformRTData(String respondeBody, int tripNumber, List<String> FromCities, List<String> ToCities,
			List<Object> deptDates, String tripType, String cabinClass) {

		respondeBody = respondeBody.replaceAll("FROMCITY" + (tripNumber + 1), FromCities.get(tripNumber));
		respondeBody = respondeBody.replaceAll("TOCITY" + (tripNumber + 1), ToCities.get(tripNumber));
		respondeBody = respondeBody.replaceAll("DEPTDATETRIP" + (tripNumber + 1), // YesThis junk will fix with regex
				getDate(deptDates.get(tripNumber), 0, "ddMMyy"));
		respondeBody = respondeBody.replaceAll("DEPTDATEPLUS1TRIP" + (tripNumber + 1),
				getDate(deptDates.get(tripNumber), 1, "ddMMyy"));
		respondeBody = respondeBody.replaceAll("DEPTDATEPLUS2TRIP" + (tripNumber + 1),
				getDate(deptDates.get(tripNumber), 2, "ddMMyy"));
		respondeBody = respondeBody.replaceAll("DEPTDATEPLUS3TRIP" + (tripNumber + 1),
				getDate(deptDates.get(tripNumber), 3, "ddMMyy"));
		respondeBody = respondeBody.replaceAll("DEPTDATEPLUS4TRIP" + (tripNumber + 1),
				getDate(deptDates.get(tripNumber), 4, "ddMMyy"));
		respondeBody = respondeBody.replaceAll("DEPTDATEPLUS5TRIP" + (tripNumber + 1),
				getDate(deptDates.get(tripNumber), 5, "ddMMyy"));
		respondeBody = respondeBody.replaceAll("DEPTDATEPLUS6TRIP" + (tripNumber + 1),
				getDate(deptDates.get(tripNumber), 6, "ddMMyy"));
		respondeBody = respondeBody.replaceAll("DEPTDATEPLUS7TRIP" + (tripNumber + 1),
				getDate(deptDates.get(tripNumber), 7, "ddMMyy"));
		respondeBody = respondeBody.replaceAll("DEPTDATEPLUS8TRIP" + (tripNumber + 1),
				getDate(deptDates.get(tripNumber), 8, "ddMMyy"));

		respondeBody = respondeBody.replaceAll("DEPTDATEPLUS9TRIP" + (tripNumber + 1),
				getDate(deptDates.get(tripNumber), 9, "ddMMyy"));

		respondeBody = respondeBody.replaceAll("DEPTDATEPLUS10TRIP" + (tripNumber + 1),
				getDate(deptDates.get(tripNumber), 10, "ddMMyy"));

		respondeBody = respondeBody.replaceAll("DEPTDATEPLUS11TRIP" + (tripNumber + 1),
				getDate(deptDates.get(tripNumber), 11, "ddMMyy"));

		respondeBody = respondeBody.replaceAll("CABINCLASS", cabinClass);

		return respondeBody;
	}

	String replacePlusDays(String responsedBody) {
		// responsedBody.ma
		return responsedBody;

	}

	String updateEpochTimesForSOASearch(String respondeBody, String deptDate) {
		List<Long> depEpochs = JsonPath.read(respondeBody,
				"$.srchRespMap.*.srcRes.rcomMap.*.[*].jrnyMap.*.fltLst.[*].depDt");
		List<Long> arrEpochs = JsonPath.read(respondeBody,
				"$.srchRespMap.*.srcRes.rcomMap.*.[*].jrnyMap.*.fltLst.[*].arrDt");
		List<Long> techStopArr = JsonPath.read(respondeBody,
				"$.srchRespMap.*.srcRes.rcomMap.*.[*].jrnyMap.*.fltLst.[*].techStpOvrLst.[*].stTim");
		List<Long> techStopDept = JsonPath.read(respondeBody,
				"$.srchRespMap.*.srcRes.rcomMap.*.[*].jrnyMap.*.fltLst.[*].techStpOvrLst.[*].enTime");
		Set<Long> allEpochsSet = new HashSet<Long>();
		allEpochsSet.addAll(depEpochs);
		allEpochsSet.addAll(arrEpochs);
		allEpochsSet.addAll(techStopArr);
		allEpochsSet.addAll(techStopDept);

		return updateEpochTimes(respondeBody, deptDate, allEpochsSet);
	}

	String updateEpochTimes(String respondeBody, String deptDate, Set<Long> allEpochsSet) {
		List<Long> sortedEpocs = new ArrayList<Long>(allEpochsSet);
		Collections.sort(sortedEpocs);
		long firstEpochInSOARespo = sortedEpocs.size() > 0 ? setTimeToMidNight(sortedEpocs.get(0)) : 0;
		Map<Long, Integer> epochDatesWithDiffInDays = new HashMap<Long, Integer>();
		sortedEpocs.forEach((epoc) -> {
			int diff = (int) (((epoc - firstEpochInSOARespo) / (1000 * 60 * 60)) / 24);
			System.out.println(epoc + " -" + firstEpochInSOARespo + " =" + diff);
			epochDatesWithDiffInDays.put(epoc, diff);
		});

		respondeBody = updateAllEpochDatesWithDiff(respondeBody, epochDatesWithDiffInDays,
				getDateFromString(deptDate, "ddMMyy"));
		return respondeBody;
	}

	String updateAllEpochDatesWithDiff(String respondeBody, Map<Long, Integer> epochDatesWithDiffInDays,
			Date firstDeptDate) {
		for (Map.Entry<Long, Integer> entry : epochDatesWithDiffInDays.entrySet())

		{
			Date dateInBaseResponse = new Date(entry.getKey());
			String newEpochValue = getDateInString(firstDeptDate, dateInBaseResponse, entry.getValue());
			// System.out.println("old:"+String.valueOf(entry.getKey())+" new:"+
			// newEpochValue);
			respondeBody = respondeBody.replaceAll(String.valueOf(entry.getKey()), newEpochValue);
		}

		return respondeBody;
	}

	private String getDateInString(Date day, Date baseDate, int days) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Calendar cal = Calendar.getInstance();
		cal.setTime(day);

		cal.set(Calendar.HOUR, baseDate.getHours());
		cal.set(Calendar.MINUTE, baseDate.getMinutes());
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.HOUR_OF_DAY, baseDate.getHours());
		cal.set(Calendar.MILLISECOND, 0);

		cal.add(Calendar.DAY_OF_MONTH, days);
		String dateString = simpleDateFormat.format(cal.getTime());

		return String.valueOf(getDateInLong(getDateFromString(dateString, "yyyy-MM-dd HH:mm"), "yyyy-MM-dd HH:mm"));
	}

	private Date getDateFromString(String day, String format) {

		Date date = null;
		try {
			date = new SimpleDateFormat(format).parse(day);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return date;
	}

	long setTimeToMidNight(long timeTOTakeOff) {
		Date dateToSet = new Date(timeTOTakeOff);
		Calendar cal = Calendar.getInstance();
		cal.setTime(dateToSet);
		cal.set(Calendar.HOUR, 00);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTimeInMillis();
		// return day+1;

	}

	private long getDateInLong(Date day, String format) {

		String localDate = getDateInString(day, format);

		SimpleDateFormat f = new SimpleDateFormat(format);
		try {
			Date d = f.parse(localDate);
			long milliseconds = d.getTime();
			return milliseconds;
		} catch (ParseException e) {
			e.printStackTrace();
			return -1;
		}
	}

	private String getDateInString(Date day, String format) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		Date dateToSet = new Date(day.getTime());
		Calendar cal = Calendar.getInstance();
		cal.setTime(dateToSet);
		/*
		 * cal.set(Calendar.HOUR, 0); cal.set(Calendar.MINUTE, 0);
		 * cal.set(Calendar.SECOND, 0); cal.set(Calendar.HOUR_OF_DAY, 0);
		 * cal.set(Calendar.MILLISECOND, 0);
		 */
		String dateString = simpleDateFormat.format(cal.getTime());
		return dateString;
	}

	String transformFareCalendarData(String respondeBody) {
		if (respondeBody != null) {
			DateFormat dateFormat = new SimpleDateFormat("ddMMyy");
			for (int i = 0; i < 100; i++) {
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DATE, i);
				respondeBody = respondeBody.replaceFirst("testDate", dateFormat.format(cal.getTime()));
			}
			Calendar calc = Calendar.getInstance();
			respondeBody = respondeBody.replaceAll("ActiveDate", dateFormat.format(calc.getTime()));
		}
		return respondeBody;
	}

	String getProcessSOAReivewRes(Request request, Response response, FileSource file, Parameters parameters) {
		List<String> FromCities = JsonPath.read(request.getBodyAsString(), "$.requestCore.itineraryList[*].from");
		List<String> ToCities = JsonPath.read(request.getBodyAsString(), "$.requestCore.itineraryList[*].to");
		String sequenceId = JsonPath.read(request.getBodyAsString(), "$.requestCore.sequenceId");
		List<String> deptDate = JsonPath.read(request.getBodyAsString(), "$.requestCore.itineraryList[*].depDate");
		List<String> reqCredIds = JsonPath.read(request.getBodyAsString(),
				"$.requestCore.itineraryList[*].selectionInfoList[*].cId");
		String respondeBody = response.getBodyAsString();
		for (int i = 0; i < FromCities.size(); i++) {

			respondeBody = transformReivewData(respondeBody, i, FromCities, ToCities, deptDate, reqCredIds, sequenceId);

		}

		List<Long> deptDateInlong = JsonPath.read(respondeBody, "$.journeys.*.segments.*.fltLst[*].depDt");
		List<Long> arvDateInlong = JsonPath.read(respondeBody, "$.journeys.*.segments.*.fltLst[*].arrDt");
		Set<Long> allEpochsSet = new HashSet<Long>();
		allEpochsSet.addAll(deptDateInlong);
		allEpochsSet.addAll(arvDateInlong);

		respondeBody = updateEpochTimes(respondeBody, getDate(deptDate.get(0), 0, "ddMMyy"), allEpochsSet);

		RememberedDataResp rememberedDataResp = new RememberedDataResp(getAllHeadersInMap(request.getHeaders()),
				request.getBodyAsString(), request.getAbsoluteUrl(), getAllHeadersInMap(response.getHeaders()),
				respondeBody, response.getStatus());
		String urlComps[] = request.getAbsoluteUrl().split("\\/");
		MockingService.tempRembData.put(sequenceId + "_" + request.getMethod() + "_" + urlComps[4] + "_" + urlComps[5],
				rememberedDataResp);
		return respondeBody;
	}

	String transformReivewData(String responseBody, int tripNumber, List<String> FromCities, List<String> ToCities,
			List<String> deptDates, List<String> reqCredIds, String sequenceId) {
		responseBody = responseBody.replaceAll("FromCity" + (tripNumber + 1), FromCities.get(tripNumber));
		responseBody = responseBody.replaceAll("ToCity" + (tripNumber + 1), ToCities.get(tripNumber));
		// responseBody = responseBody.replaceAll("DepartDate" + (tripNumber + 1),
		// deptDates.get(tripNumber).toString());
		responseBody = responseBody.replaceAll("REQ_CREDID" + (tripNumber + 1), reqCredIds.get(tripNumber).toString());
		responseBody = responseBody.replaceAll("SEQID_REQ", sequenceId);
		responseBody = responseBody.replaceAll("DepartDate" + (tripNumber + 1),
				getDate(deptDates.get(tripNumber), 0, "ddMMyy"));
		// new one below
		// responseBody = responseBody.replaceAll("ArrivalDate" + (tripNumber + 1),
		// getDate(deptDates.get(tripNumber), 0, "ddMMyy"));
		responseBody = responseBody.replaceAll("Depart" + (tripNumber + 1),
				getDate(deptDates.get(tripNumber), 0, "ddMMyy"));

		return responseBody;

	}

	String getProcessSOAFareCalRes(String responseBody) {
		if (responseBody != null) {
			DateFormat dateFormat = new SimpleDateFormat("ddMMyy");
			for (int i = 0; i < 100; i++) {
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DATE, i);
				responseBody = responseBody.replaceFirst("testDate", dateFormat.format(cal.getTime()));
			}
			/*
			 * Calendar calc = Calendar.getInstance(); responseBody =
			 * responseBody.replaceAll("ActiveDate", dateFormat.format(calc.getTime()));
			 */
		}

		return responseBody;
	}

	String getDate(Object date, int addDays, String format) {
		Object dateToParse = null;
		if (null != date && date.getClass().equals(Long.class)) {
			Date local = new Date((Long) date);
			dateToParse = new Date(local.getTime() + TimeUnit.DAYS.toMillis(addDays));

		} else {
			try {
				dateToParse = new Date(new SimpleDateFormat("yyyy-MM-dd").parse(date.toString()).getTime()
						+ TimeUnit.DAYS.toMillis(addDays));
				;
			} catch (ParseException e) {
				e.printStackTrace();
				return null;
			}
		}
		SimpleDateFormat df2 = new SimpleDateFormat(format);
		String DateToReturn = df2.format(dateToParse);

		return DateToReturn;
	}

}

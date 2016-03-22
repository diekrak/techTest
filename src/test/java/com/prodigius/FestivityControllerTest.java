package com.prodigius;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;

import java.util.Locale;

import org.apache.commons.httpclient.HttpStatus;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.prodigius.model.Festivity;
import com.prodigius.persitence.IFestivity;

@SpringApplicationConfiguration(classes = ProdigiousTechApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
public class FestivityControllerTest {
	@Value("${local.server.port}")
	private int port;

	// ROUTES
	private static final String GET_ALL = "/festivity/getAll";
	private static final String GET_BY_ID = "/festivity/{id}";
	private static final String CREATE = "/festivity/create";
	private static final String UPDATE = "/festivity/{id}";
	private static final String GET_BY_NAME= "/festivity/filter/name";
	private static final String GET_BY_DATE_RANGE= "/festivity/filter/startDateRange";
	private static final String GET_BY_PLACE= "/festivity/filter/place";
	

	// VARIABLES
	private static final String NAME = "UnitTest";
	private static final String PLACE = "Development";
	private static final String START_DATE = "2016-01-06";
	private static final String END_DATE = "2016-02-10";

	private static final String NAME_B = "UnitTestB";
	private static final String PLACE_B = "DevelopmentB";
	private static final String START_DATE_B = "2015-01-06";
	private static final String END_DATE_B = "2015-02-10";

	private static final String UPD_NAME = "UPD_UnitTest";
	private static final String UPD_PLACE = "UPD_Development";
	private static final String UPD_START_DATE = "2016-02-09";
	private static final String UPD_END_DATE = "2016-02-17";

	@Autowired
	IFestivity repository;
	@Autowired
	private MessageSource messageSource;
	Festivity festivityA;

	@Before
	public void setup() {
		RestAssured.port = port;
		repository.deleteAll();
	}

	public void createUserA() {
		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
		DateTime dStart = formatter.parseDateTime(START_DATE);
		dStart.toDateTime(DateTimeZone.UTC);
		DateTime dEnd = formatter.parseDateTime(END_DATE);
		dEnd.toDateTime(DateTimeZone.UTC);

		festivityA = new Festivity();
		festivityA.setName(NAME);
		festivityA.setStartDate(dStart.toDate());
		festivityA.setEndDate(dEnd.toDate());
		festivityA.setPlace(PLACE);
		repository.save(festivityA);
	}

	public void createUserB() {
		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
		DateTime dStart = formatter.parseDateTime(START_DATE_B);
		dStart.toDateTime(DateTimeZone.UTC);
		DateTime dEnd = formatter.parseDateTime(END_DATE_B);
		dEnd.toDateTime(DateTimeZone.UTC);

		Festivity festibityB = new Festivity();
		festibityB.setName(NAME_B);
		festibityB.setStartDate(dStart.toDate());
		festibityB.setEndDate(dEnd.toDate());
		festibityB.setPlace(PLACE_B);
		repository.save(festibityB);
	}

	// GET ALL
	 
	@Test
	public void getAllAsJason() {
		createUserA();
		createUserB();
		RestAssured.
		given()
			.accept("application/json")
		.when()
			.get(GET_ALL)
		.then()
			.statusCode(HttpStatus.SC_OK)
			.contentType(ContentType.JSON)
			.body("festivities.name", hasSize(equalTo(2)))
			.body("festivities.name", hasItems(NAME, NAME_B));
	}

	@Test
	public void getAllAsXML() {

		createUserA();
		createUserB();
		RestAssured
		.given()
			.accept("application/xml")
		.when()
			.get(GET_ALL)
		.then()
			.statusCode(HttpStatus.SC_OK)
			.contentType(ContentType.XML)
			.body("festivityList.festivities.name", hasItems(NAME, NAME_B));
	}

	@Test
	public void getAllWIthNoResultsAsJason() {
		RestAssured
		.given()
			.accept("application/json")
		.when()
			.get(GET_ALL)
		.then()
			.statusCode(HttpStatus.SC_NOT_FOUND);
	}

	// GET BY ID

	@Test
	public void getByIdAsXML() {
		createUserA();
		RestAssured
		.given()
			.accept("application/xml")
		.when()
			.get(GET_BY_ID, festivityA.getId())
		.then()				
			.statusCode(HttpStatus.SC_OK)
			.contentType(ContentType.XML)
			.body("festivity.name", equalTo(NAME));
	}

	@Test
	public void getByIdAsJson() {
		createUserA();
		RestAssured
		.given()
			.accept("application/Json")
		.when()
			.get(GET_BY_ID, festivityA
			.getId())
		.then()				
			.statusCode(HttpStatus.SC_OK)
			.contentType(ContentType.JSON)
			.body("name", equalTo(NAME));
	}

	@Test
	public void getByIdWithNoResultsAsJson() {
		RestAssured
		.given()
			.accept("application/json")
		.when()
			.get(GET_BY_ID, 0L)
		.then()				
			.statusCode(HttpStatus.SC_NOT_FOUND)
			.contentType(ContentType.JSON);
	}

	@Test
	public void getByIdWithNoResultsAsXML() {
		RestAssured
		.given()
			.accept("application/xml")
		.when()
			.get(GET_BY_ID, 0L)
		.then()				
			.statusCode(HttpStatus.SC_NOT_FOUND)
			.contentType(ContentType.XML);
	}

	@Test
	public void getByIdWithIllegalParameterAsJson() {
		RestAssured
		.given()
			.accept("application/json")
		.when()
			.get(GET_BY_ID, "f")
		.then()				
			.statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
			.contentType(ContentType.JSON);
	}

	@Test
	public void getByIdWithIllegalParameterAsXML() {
		RestAssured
		.given()
			.accept("application/xml")
		.when()
			.get(GET_BY_ID, "f")
		.then()				
			.statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
			.contentType(ContentType.XML);
	}

	// Create

	@Test
	public void createFestivityWithJson() {

		RestAssured
		.given()
			.accept("application/json")
			.contentType(ContentType.JSON)				
			.body("{\"name\": \"" + NAME + "\",\"startDate\": \"" + START_DATE + "\",\"endDate\": \"" + END_DATE
									+ "\",\"place\": \"" + PLACE + "\"}")				
		.when()
			.post(CREATE)
		.then()
			.statusCode(HttpStatus.SC_CREATED)
			.contentType(ContentType.JSON)		
			.body("name", equalTo(NAME));		
	}

	@Test
	public void createFestivityWithXML() {
		RestAssured
		.given()
			.accept("application/xml")
			.contentType(ContentType.XML)				
			.body("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><festivity><endDate>" + END_DATE
									+ "</endDate><name>" + NAME + "</name><place>" + PLACE + "</place><startDate>" + START_DATE
									+ "</startDate></festivity>")				
		.when()
			.post(CREATE)
		.then()
			.statusCode(HttpStatus.SC_CREATED)
			.contentType(ContentType.XML)				
			.body("festivity.name", equalTo(NAME));
	}

	@Test
	public void tryToCreateFestivityWithStartDateGreaterThatEndDateInJsonFormat() {

		String respMessage = messageSource.getMessage("error.exception", null, LocaleContextHolder.getLocale())
				+ messageSource.getMessage("rest.rule.invalid.date", null, LocaleContextHolder.getLocale());

		RestAssured
		.given()
			.accept("application/json")
			.contentType(ContentType.JSON)				
			.body("{\"name\": \"" + NAME + "\",\"startDate\": \"" + END_DATE + "\",\"endDate\": \"" + START_DATE
									+ "\",\"place\": \"" + PLACE + "\"}")				
		.when()
			.post(CREATE)
		.then()
			.statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)				
			.contentType(ContentType.JSON)
			.body("message", equalTo(respMessage));
	}

	@Test
	public void tryToCreateFestivityWithStartDateGreaterThatEndDateInXMLFormat() {

		String respMessage = messageSource.getMessage("error.exception", null, LocaleContextHolder.getLocale())
				+ messageSource.getMessage("rest.rule.invalid.date", null, LocaleContextHolder.getLocale());

		RestAssured
		.given()
			.accept("application/xml")
			.contentType(ContentType.XML)				
			.body("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><festivity><endDate>" + START_DATE
									+ "</endDate><name>" + NAME + "</name><place>" + PLACE + "</place><startDate>" + END_DATE
									+ "</startDate></festivity>")				
		.when()
			.post(CREATE)
		.then()
			.statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
			.contentType(ContentType.XML)				
			.body("errorInfo.message", equalTo(respMessage));
	}

	@Test
	public void tryToCreateFestivityWithStartDateGreaterThatEndDateInJsonFormatInSpanish() {

		String respMessage = messageSource.getMessage("error.exception", null, new Locale("es"))
				+ messageSource.getMessage("rest.rule.invalid.date", null, new Locale("es"));
		RestAssured
		.given()
			.accept("application/json")
			.contentType(ContentType.JSON)				
			.body("{\"name\": \"" + NAME + "\",\"startDate\": \"" + END_DATE + "\",\"endDate\": \"" + START_DATE
									+ "\",\"place\": \"" + PLACE + "\"}")				
			.header("Accept-Language", "es")
		.when()
			.post(CREATE)
		.then()				
			.statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
			.contentType(ContentType.JSON)				
			.body("message", equalTo(respMessage));
	}

	@Test
	public void tryToCreateFestivityWithStartDateGreaterThatEndDateInXMLFormatInSpanish() {

		String respMessage = messageSource.getMessage("error.exception", null, new Locale("es"))
				+ messageSource.getMessage("rest.rule.invalid.date", null, new Locale("es"));
		
		RestAssured
		.given()
			.accept("application/xml")
			.contentType(ContentType.XML)				
			.body("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><festivity><endDate>" + START_DATE
									+ "</endDate><name>" + NAME + "</name><place>" + PLACE + "</place><startDate>" + END_DATE
									+ "</startDate></festivity>")				
			.header("Accept-Language", "es")
		.when()
			.post(CREATE)
		.then()				
			.statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
			.contentType(ContentType.XML)
							
			.body("errorInfo.message", equalTo(respMessage));
	}
	
	
	@Test
	public void tryToCreateFestivityWithMissingPlaceParamInJson() {

		RestAssured
		.given()
			.accept("application/json")
			.contentType(ContentType.JSON)				
			.body("{\"name\": \"" + NAME + "\",\"startDate\": \"" + START_DATE + "\",\"endDate\": \"" + END_DATE
									+ "\"}")				
		.when()
			.post(CREATE)
		.then()
			.statusCode(HttpStatus.SC_BAD_REQUEST)
			.contentType(ContentType.JSON)		
			;		
	}


	@Test
	public void tryToCreateFestivityWithMissingNameParamInJson() {

		RestAssured
		.given()
			.accept("application/json")
			.contentType(ContentType.JSON)				
			.body("{\"startDate\": \"" + START_DATE + "\",\"endDate\": \"" + END_DATE
									+ "\",\"place\": \"" + PLACE + "\"}")				
		.when()
			.post(CREATE)
		.then()
			.statusCode(HttpStatus.SC_BAD_REQUEST)
			.contentType(ContentType.JSON)		
			;		
	}


	@Test
	public void tryToCreateFestivityWithMissingStartDateParamInJson() {

		RestAssured
		.given()
			.accept("application/json")
			.contentType(ContentType.JSON)				
			.body("{\"name\": \"" + NAME + "\",\"endDate\": \"" + END_DATE
									+ "\",\"place\": \"" + PLACE + "\"}")				
		.when()
			.post(CREATE)
		.then()
			.statusCode(HttpStatus.SC_BAD_REQUEST)
			.contentType(ContentType.JSON)		
			;		
	}


	@Test
	public void tryToCreateFestivityWithMissingEndDateParamInJson() {

		RestAssured
		.given()
			.accept("application/json")
			.contentType(ContentType.JSON)				
			.body("{\"name\": \"" + NAME + "\",\"startDate\": \"" + START_DATE + "\",\"place\": \"" + PLACE + "\"}")				
		.when()
			.post(CREATE)
		.then()
			.statusCode(HttpStatus.SC_BAD_REQUEST)
			.contentType(ContentType.JSON)		
			;		
	}

	
	// UPDATE

	@Test
	public void updateFestivityWithJson() {
		createUserA();
		RestAssured
		.given()
			.accept("application/json")
			.contentType(ContentType.JSON)
			.body("{\"name\": \"" + UPD_NAME + "\",\"startDate\": \"" + UPD_START_DATE + "\",\"endDate\": \""
						+ UPD_END_DATE + "\",\"place\": \"" + UPD_PLACE + "\"}")
		.when()
			.put(UPDATE, festivityA.getId())
		.then()
			.statusCode(HttpStatus.SC_OK)
			.contentType(ContentType.JSON)
			.body("name", equalTo(UPD_NAME));

		Festivity tmp = repository.findOne(festivityA.getId());
		Assert.assertEquals(tmp.getName(), UPD_NAME);
		Assert.assertEquals(tmp.getPlace(), PLACE);

//		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
//
//		DateTime dateStartUTCToUpdate = new DateTime(formatter.parseDateTime(UPD_START_DATE), DateTimeZone.UTC);
//		DateTime dateStartUTC = new DateTime(tmp.getStartDate(), DateTimeZone.UTC);
//		Assert.assertEquals(dateStartUTC, dateStartUTCToUpdate);
//
//		DateTime dateEndtUTCToUpdate = new DateTime(formatter.parseDateTime(UPD_END_DATE), DateTimeZone.UTC);
//		DateTime dateEndtUTC = new DateTime(tmp.getEndDate(), DateTimeZone.UTC);
//		Assert.assertEquals(dateStartUTC, dateStartUTCToUpdate);
//		Assert.assertEquals(dateEndtUTC, dateEndtUTCToUpdate);
	} 
	
	@Test
	public void updateFestivityWithXML() {
		createUserA();		
		RestAssured
		.given()
			.accept("application/xml")
			.contentType(ContentType.XML)
			.body("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><festivity><endDate>" + UPD_END_DATE
					+ "</endDate><name>" + UPD_NAME + "</name><place>" + UPD_PLACE + "</place><startDate>" + UPD_START_DATE
					+ "</startDate></festivity>")			
		.when()
			.put(UPDATE, festivityA.getId())
		.then()
			.statusCode(HttpStatus.SC_OK)
			.contentType(ContentType.XML)
			.body("festivity.name", equalTo(UPD_NAME));

		Festivity tmp = repository.findOne(festivityA.getId());
		Assert.assertEquals(tmp.getName(), UPD_NAME);
		Assert.assertEquals(tmp.getPlace(), PLACE);

		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");

		DateTime dateStartUTCToUpdate = new DateTime(formatter.parseDateTime(UPD_START_DATE), DateTimeZone.UTC);
		DateTime dateStartUTC = new DateTime(tmp.getStartDate(), DateTimeZone.UTC);
		Assert.assertEquals(dateStartUTC, dateStartUTCToUpdate);

		DateTime dateEndtUTCToUpdate = new DateTime(formatter.parseDateTime(UPD_END_DATE), DateTimeZone.UTC);
		DateTime dateEndtUTC = new DateTime(tmp.getEndDate(), DateTimeZone.UTC);
		Assert.assertEquals(dateStartUTC, dateStartUTCToUpdate);
		Assert.assertEquals(dateEndtUTC, dateEndtUTCToUpdate);
	} 
	
	@Test
	public void tryToUpdateFestivityWithStartDateGreaterThatEndDateInXMLFormat() {
		createUserA();
		String respMessage = messageSource.getMessage("error.exception", null, LocaleContextHolder.getLocale())
				+ messageSource.getMessage("rest.rule.invalid.date", null, LocaleContextHolder.getLocale());		
		RestAssured
		.given()
			.accept("application/xml")
			.contentType(ContentType.XML)
			.body("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><festivity><endDate>" + UPD_START_DATE
					+ "</endDate><name>" + UPD_NAME + "</name><place>" + UPD_PLACE + "</place><startDate>" + UPD_END_DATE
					+ "</startDate></festivity>")			
		.when()
			.put(UPDATE, festivityA.getId())
		.then()
			.statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
			.contentType(ContentType.XML)				
			.body("errorInfo.message", equalTo(respMessage));
	} 

	@Test
	public void tryToUpdateFestivityWithStartDateGreaterThatEndDateInJsonFormat() {
		createUserA();
		String respMessage = messageSource.getMessage("error.exception", null, LocaleContextHolder.getLocale())
				+ messageSource.getMessage("rest.rule.invalid.date", null, LocaleContextHolder.getLocale());		
		RestAssured
		.given()
			.accept("application/json")
			.contentType(ContentType.JSON)
			.body("{\"name\": \"" + UPD_NAME + "\",\"startDate\": \"" + UPD_END_DATE + "\",\"endDate\": \""
					+ UPD_START_DATE + "\",\"place\": \"" + UPD_PLACE + "\"}")
		.when()
			.put(UPDATE, festivityA.getId())
		.then()
			.statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
			.contentType(ContentType.JSON)				
			.body("message", equalTo(respMessage));
	} 

	//Specific Queries
	
	@Test
	public void getByNameAsJson() {
		createUserA();
		createUserB();
		createUserB();
		RestAssured
		.given()
			.accept("application/json")
			.param("name", NAME_B)
		.when()
			.get(GET_BY_NAME)
		.then()
			.statusCode(HttpStatus.SC_OK)
			.contentType(ContentType.JSON)
			.body("festivities.name", hasSize(equalTo(2)))
			;
	}
	
	@Test
	public void getByNameAsXML() {
		createUserA();
		createUserB();
		createUserB();
		RestAssured
		.given()
			.accept("application/xml")
			.param("name", NAME_B)
		.when()
			.get(GET_BY_NAME)
		.then()
			.statusCode(HttpStatus.SC_OK)
			.contentType(ContentType.XML)
			.body("festivityList.festivities.name", hasItems(NAME_B, NAME_B))
			 ;
	}
	
	
	@Test
	public void getByPlaceAsJson() {
		createUserA();
		createUserB();
		createUserB();
		RestAssured
		.given()
			.accept("application/json")
			.param("place", PLACE_B)
		.when()
			.get(GET_BY_PLACE)
		.then()
			.statusCode(HttpStatus.SC_OK)
			.contentType(ContentType.JSON)
			.body("festivities.name", hasSize(equalTo(2)))
			;
	}
	
	@Test
	public void getByPlaceAsXML() {
		createUserA();
		createUserB();
		createUserB();
		RestAssured
		.given()
			.accept("application/xml")
			.param("place", PLACE_B)
		.when()
			.get(GET_BY_PLACE)
		.then()
			.statusCode(HttpStatus.SC_OK)
			.contentType(ContentType.XML)
			.body("festivityList.festivities.name", hasItems(NAME_B, NAME_B))
			 ;
	}
	
	
	@Test
	public void getByDateRangeAsJson() {
		createUserA();
		createUserB();
		createUserB();
		RestAssured
		.given()
			.accept("application/json")
			.param("startDateIni", "2015-01-05")
			.param("startDateEnd", "2015-01-07")
		.when()
			.get(GET_BY_DATE_RANGE)
		.then()
			.statusCode(HttpStatus.SC_OK)
			.contentType(ContentType.JSON)
			.body("festivities.name", hasSize(equalTo(2)))
			;
	}
	
	
	@Test
	public void getByDateRangeAsXML() {
		createUserA();
		createUserB();
		createUserB();
		RestAssured
		.given()
			.accept("application/xml")
			.param("startDateIni", "2015-01-05")
			.param("startDateEnd", "2015-01-07")
		.when()
			.get(GET_BY_DATE_RANGE)
		.then()
			.statusCode(HttpStatus.SC_OK)
			.contentType(ContentType.XML)
			.body("festivityList.festivities.name", hasItems(NAME_B, NAME_B))
			 ;
	}
	
}
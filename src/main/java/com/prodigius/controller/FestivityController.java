package com.prodigius.controller;

import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;

import org.hibernate.exception.ConstraintViolationException;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.prodigius.business.Rule;
import com.prodigius.model.Festivity;
import com.prodigius.persitence.IFestivity;
import com.prodigius.util.FestivityList;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
public class FestivityController {
	@Autowired
	IFestivity fest;
	@Autowired
	private MessageSource messageSource;

	private static final Logger logger = LoggerFactory.getLogger(FestivityController.class);


	@ApiOperation(value = "createFestivity")
	@ApiResponses(value = { @ApiResponse(code = 201, message = "Success", response = Festivity.class),
			@ApiResponse(code = 500, message = "Invalid data or unknown error") ,
			@ApiResponse(code = 400, message = "Missing Parameters") })
	@RequestMapping(value = "/festivity/create", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	@ResponseStatus(HttpStatus.CREATED)
	public Festivity create(@RequestBody Festivity festivity) throws Exception {
		if (festivity.getPlace() == null) {			
			throw new ConstraintViolationException(messageSource.getMessage("missing.place", null, LocaleContextHolder.getLocale()), null, null);
		}
		if (festivity.getName() == null) {
			throw new ConstraintViolationException(messageSource.getMessage("missing.name", null, LocaleContextHolder.getLocale()), null, null);
		}
		if (festivity.getStartDate() == null) {			
			throw new ConstraintViolationException(messageSource.getMessage("missing.start.date", null, LocaleContextHolder.getLocale()), null, null);
		}
		if (festivity.getEndDate()== null) {
			System.out.println("ERRPR");
			throw new ConstraintViolationException(messageSource.getMessage("missing.end.date", null, LocaleContextHolder.getLocale()), null, null);
		}
		
		if (Rule.areDatesValid(festivity.getStartDate(), festivity.getEndDate())) {
			fest.save(festivity);
		} else {
			throw new Exception(
					messageSource.getMessage("rest.rule.invalid.date", null, LocaleContextHolder.getLocale()));
		}
		return festivity;
	}

	
	@RequestMapping(value = "/festivity/{id}", method = RequestMethod.PUT, produces = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE })
	@ResponseStatus(HttpStatus.OK)
	public Festivity update(@PathVariable Long id, @RequestBody Festivity festivity) throws Exception {
		Festivity updFest = fest.findOne(id);
		updFest.setName(festivity.getName());
		updFest.setEndDate(festivity.getEndDate());
		updFest.setStartDate(festivity.getStartDate());
		if (Rule.areDatesValid(festivity.getStartDate(), festivity.getEndDate())) {
			fest.save(updFest);
		} else {
			throw new Exception(
					messageSource.getMessage("rest.rule.invalid.date", null, LocaleContextHolder.getLocale()));
		}
		return updFest;
	}

	@RequestMapping(value = "/festivity/{id}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE })
	@ResponseStatus(HttpStatus.OK)
	public Festivity getFestivity(@PathVariable Long id) {
		Festivity f = fest.findOne(id);
		if (f == null) {
			logger.error("[NO RESULTS FOUND][READ BY ID=" + id + "] ");
			throw new NoResultException(
					messageSource.getMessage("rest.no.data.found", null, LocaleContextHolder.getLocale()));
		}
		return f;
	}

	@RequestMapping(value = "/festivity/getAll", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public FestivityList getAll() {
		FestivityList festListResp = new FestivityList();
		List<Festivity> aux = (List<Festivity>) fest.findAll();
		if (aux.isEmpty()) {
			logger.error("[NO RESULTS FOUND][READ ALL]");
			throw new NoResultException(
					messageSource.getMessage("rest.no.data.found", null, LocaleContextHolder.getLocale()));
		}
		festListResp.setFestivities(aux);
		return festListResp;
	}

	@ApiOperation(value = "Find Festivity By Name")	
	@ApiImplicitParams({
			@ApiImplicitParam(name = "name", value = "Name of the festivity", required = true, dataType = "string", paramType = "query") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success", response = Festivity.class),
			@ApiResponse(code = 500, message = "Invalid data or unknown error"),
			@ApiResponse(code = 404, message = "No data found")})
	@RequestMapping(value = "/festivity/filter/name", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	@ResponseStatus(HttpStatus.OK)
	public FestivityList readByName(String name) {
		FestivityList festListResp = new FestivityList();
		List<Festivity> aux = fest.findByName(name);
		if (aux == null) {
			logger.error("[NO RESULTS FOUND][READ BY NAME=" + name + "]");
			throw new NoResultException(
					messageSource.getMessage("rest.no.data.found", null, LocaleContextHolder.getLocale()));
		}
		festListResp.setFestivities(aux);
		return festListResp;
	}

	@ApiOperation(value = "Find festivities by start date")	
	@ApiImplicitParams({
			@ApiImplicitParam(name = "startDateIni", value = "Initial date to filter", required = true, dataType = "Date(YYYY-MM-DD)", paramType = "query"),
			@ApiImplicitParam(name = "startDateEnd", value = "Final date to filter", required = true, dataType = "Date(YYYY-MM-DD)", paramType= "query")})
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success", response = Festivity.class),
			@ApiResponse(code = 500, message = "Invalid data or unknown error"),
			@ApiResponse(code = 404, message = "No data found")})
	@RequestMapping(value = "/festivity/filter/startDateRange", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	@ResponseStatus(HttpStatus.OK)
	public FestivityList readByDateStarRange(@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDateIni,
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDateEnd) {

		DateTime dateStartA = new DateTime(startDateIni, DateTimeZone.UTC);
		DateTime dateStartB = new DateTime(startDateEnd, DateTimeZone.UTC);
		FestivityList festListResp = new FestivityList();
		List<Festivity> aux = fest.findByStartDateBetween(dateStartA.toDate(), dateStartB.toDate());
		if (aux == null) {
			logger.error("[NO RESULTS FOUND][READ BY DATE RANGE=" + startDateIni + "-" + startDateEnd + "]");
			throw new NoResultException(
					messageSource.getMessage("rest.no.data.found", null, LocaleContextHolder.getLocale()));
		}
		festListResp.setFestivities(aux);
		return festListResp;
	}

	@ApiOperation(value = "Find Festivity By Place")	
	@ApiImplicitParams({
			@ApiImplicitParam(name = "place", value = "Place of the festivity", required = true, dataType = "string", paramType = "query") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success", response = Festivity.class),
			@ApiResponse(code = 500, message = "Invalid data or unknown error"),
			@ApiResponse(code = 404, message = "No data found")})
	@RequestMapping(value = "/festivity/filter/place", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	@ResponseStatus(HttpStatus.OK)
	public FestivityList readByPlace(String place) {
		FestivityList festListResp = new FestivityList();
		List<Festivity> aux = fest.findByPlace(place);
		if (aux == null) {
			logger.error("[NO RESULTS FOUND][READ BY PLACE=" + place + "]");
			throw new NoResultException(
					messageSource.getMessage("rest.no.data.found", null, LocaleContextHolder.getLocale()));
		}
		festListResp.setFestivities(aux);
		return festListResp;
	}

	@RequestMapping(value = "/festivity/{id}", method = RequestMethod.DELETE, produces = {
			MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	@ResponseStatus(HttpStatus.OK)
	public void delete(@PathVariable Long id) {
		fest.delete(id);
	}

}

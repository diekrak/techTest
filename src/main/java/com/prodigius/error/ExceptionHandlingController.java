package com.prodigius.error;

import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ExceptionHandlingController {

	@Autowired
	private MessageSource messageSource;

//	private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlingController.class);

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public ErrorInfo exceptionHandler(Exception ex, HttpServletRequest request) {
		ErrorInfo response = new ErrorInfo();
		response.setUrl(request.getRequestURL().toString());
		response.setMessage(
				messageSource.getMessage("error.exception", null, LocaleContextHolder.getLocale()) + ex.getMessage());
		return response;
	}

	@ExceptionHandler(IllegalStateException.class)
	@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
	@ResponseBody
	public ErrorInfo illegalExceptionHandler(IllegalStateException ex, HttpServletRequest request) {

		ErrorInfo response = new ErrorInfo();
		response.setUrl(request.getRequestURL().toString());
		response.setMessage(messageSource.getMessage("error.illegal.exeption", null, LocaleContextHolder.getLocale())
				+ ex.getMessage());
		return response;
	}

	@ExceptionHandler(NoResultException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ResponseBody
	public ErrorInfo noResultHandler(NoResultException ex, HttpServletRequest request) {
		ErrorInfo response = new ErrorInfo();
		response.setUrl(request.getRequestURL().toString());
		response.setMessage(messageSource.getMessage("error.no.result.exception", null, LocaleContextHolder.getLocale())
				+ ex.getMessage());
		return response;
	}

	@ExceptionHandler(EmptyResultDataAccessException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ResponseBody
	public ErrorInfo emptyException(EmptyResultDataAccessException ex, HttpServletRequest request) {

		ErrorInfo response = new ErrorInfo();
		response.setUrl(request.getRequestURL().toString());
		response.setMessage(
				messageSource.getMessage("error.empty.result.exception", null, LocaleContextHolder.getLocale())
						+ ex.getMessage());
		return response;
	}

}

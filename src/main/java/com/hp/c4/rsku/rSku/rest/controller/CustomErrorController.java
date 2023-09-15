package com.hp.c4.rsku.rSku.rest.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;

import com.hp.c4.rsku.rSku.bean.response.CustomError;

@RestController
public class CustomErrorController implements ErrorController {

	@Autowired
	private ErrorAttributes errorAttributes;

	@GetMapping("/error")
	public CustomError error(HttpServletRequest request, HttpServletResponse response) {
		ErrorAttributeOptions errorAttributes = ErrorAttributeOptions.of(ErrorAttributeOptions.Include.MESSAGE);
		return new CustomError(response.getStatus(),
				this.errorAttributes.getErrorAttributes(new ServletWebRequest(request), errorAttributes));
	}

}

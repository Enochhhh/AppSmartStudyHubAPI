package com.focusedapp.smartstudyhub.controller.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.focusedapp.smartstudyhub.model.custom.AuthenticationDTO;
import com.focusedapp.smartstudyhub.model.custom.Result;
import com.focusedapp.smartstudyhub.service.AuthenticationService;
import com.focusedapp.smartstudyhub.service.UserService;
import com.focusedapp.smartstudyhub.util.enumerate.StatusCode;


@RestController
@RequestMapping("/mobile/v1/auth")
public class AuthenticationController {
	
	@Autowired
	AuthenticationService authenticationService; 
	@Autowired
	UserService userService;

	/**
	 * 
	 * Controller Register User
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/register")
	public ResponseEntity<Result<AuthenticationDTO>> register(@RequestBody AuthenticationDTO request) {
		Result<AuthenticationDTO> result = new Result<>();
		AuthenticationDTO data = authenticationService.register(request);
		
		if (data == null) {
			result.getMeta().setStatusCode(StatusCode.REGISTER_FAILURE.getCode());
			result.getMeta().setMessage(StatusCode.REGISTER_FAILURE.getMessage());
		} else {
			result.setData(data);
			result.getMeta().setStatusCode(StatusCode.REGISTER_SUCCESS.getCode());
			result.getMeta().setMessage(StatusCode.REGISTER_SUCCESS.getMessage());
		}
		
		return ResponseEntity.ok(result);
	}
	
	/**
	 * 
	 * Controller Login User
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/authenticate")
	public ResponseEntity<Result<AuthenticationDTO>> authenticate(@RequestBody AuthenticationDTO request) {
		Result<AuthenticationDTO> result = new Result<>();
		AuthenticationDTO data = authenticationService.authenticate(request);
		
		if (data == null) {
			result.getMeta().setStatusCode(StatusCode.LOGIN_FAILURE.getCode());
			result.getMeta().setMessage(StatusCode.LOGIN_FAILURE.getMessage());
		} else {
			result.setData(data);
			result.getMeta().setStatusCode(StatusCode.LOGIN_SUCCESS.getCode());
			result.getMeta().setMessage(StatusCode.LOGIN_SUCCESS.getMessage());
		}
		
		return ResponseEntity.ok(result);
	}
}

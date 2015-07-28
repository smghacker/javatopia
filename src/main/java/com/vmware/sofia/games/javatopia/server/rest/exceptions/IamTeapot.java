package com.vmware.sofia.games.javatopia.server.rest.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(reason = "I am a teapot", value = HttpStatus.I_AM_A_TEAPOT)
public class IamTeapot extends RuntimeException {
   private static final long serialVersionUID = 1L;

}

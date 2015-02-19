package com.vmware.sofia.games.javatopia.server.rest.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.TOO_MANY_REQUESTS)
public class TooManyRequest extends RuntimeException {
   private static final long serialVersionUID = 1L;

}

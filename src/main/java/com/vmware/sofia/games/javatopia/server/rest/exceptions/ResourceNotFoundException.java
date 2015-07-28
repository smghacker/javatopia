package com.vmware.sofia.games.javatopia.server.rest.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.vmware.sofia.games.javatopia.server.tests.tools.TestSuite;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Valid sectors are between 1 and "
        + TestSuite.SECTOR_COUNT)
public class ResourceNotFoundException extends RuntimeException {
   private static final long serialVersionUID = 1L;

}

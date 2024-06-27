package com.dimata.helpdesk.exception;

public class ForbiddenException extends ServiceBaseException{

    private static final long serialVersionUID = 6825516975507296397L;

    public ForbiddenException(ExceptionCode code) {
        super(code);
    }

    public ForbiddenException(ExceptionCode code, String message) {
        super(code, message);
    }
}
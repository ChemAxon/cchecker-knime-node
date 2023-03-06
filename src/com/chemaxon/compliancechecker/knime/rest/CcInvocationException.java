package com.chemaxon.compliancechecker.knime.rest;

public class CcInvocationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CcInvocationException(String msg) {
        super(msg);
    }

    public CcInvocationException(String msg, Throwable thorwable) {
        super(msg, thorwable);
    }
}

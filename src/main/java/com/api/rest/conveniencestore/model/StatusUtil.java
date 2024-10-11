package com.api.rest.conveniencestore.model;

import com.api.rest.conveniencestore.enums.Status;

public interface StatusUtil {
    void setStatus(Status status);
    Status getStatus();
}

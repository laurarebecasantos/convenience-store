package com.api.rest.conveniencestore.shared.utils;

import com.api.rest.conveniencestore.shared.enums.Status;

public interface StatusUtil {
    void setStatus(Status status);
    Status getStatus();
}


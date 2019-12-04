package com.imperva.apiattacktool.model.tests;

public interface HttpResponseValidator {
    boolean isValidHttpCode(int httpResponseCode);
}

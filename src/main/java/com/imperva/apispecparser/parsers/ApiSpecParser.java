package com.imperva.apispecparser.parsers;

import com.imperva.apispecparser.exceptions.ParseException;
import com.imperva.apispecparser.model.EndpointModel;

import java.util.List;

public interface ApiSpecParser {

    List<EndpointModel> getEndpointModelList(String filePath, ApiSpecFileLocation apiSpecFileLocation) throws ParseException;
}

package com.companies.service;

import com.companies.domain.CompanyDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.HttpGet;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class ApiService {

    private final Logger log = LoggerFactory.getLogger(ApiService.class);

    private Header[] createRequestHeaders() {

        String auth = "FglhKRDhVbZfYaBsRCaOkaHZOtNOybY_HNVCU_P7";

        Header[] headers = {
            new BasicHeader("Authorization", auth)
        };
        return headers;

    }

    public String urlEncodeValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getCause());
        }
    }

    private String callGetRequestAPI(String url) throws IOException {

        String responseString;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(url);

            httpGet.setHeaders(createRequestHeaders());

            log.debug("Request for companies search: {}", httpGet);
            HttpResponse response = httpClient.execute(httpGet);
            org.apache.http.HttpEntity entity = response.getEntity();
            responseString = EntityUtils.toString(entity, "UTF-8");
            log.debug("Response for companiesSearch: {}", responseString);
        }
        return responseString;
    }

    public CompanyDTO getCompanyNumber(String url, String companyName) throws IOException {

        CompanyDTO companyDTO;
        String company = callGetRequestAPI(url);
        try{
            JSONObject resultList = new JSONObject(company);
            for (int i = 0; i < resultList.getJSONArray("items").length(); i++) {
                JSONObject resultItem = resultList.getJSONArray("items").getJSONObject(i);
                if(companyName.equalsIgnoreCase(resultItem.getString("title"))){
                    companyDTO = new CompanyDTO(companyName, resultItem.getString("company_number"), resultItem.getString("address_snippet"));
                    return companyDTO;
                }
            }
        } catch (Exception e) {
            log.error("error");
        }
        return null;
    }


    public List<String> getOfficerList(String url) throws IOException {

        String officer = callGetRequestAPI(url);

        List<String> companyOfficers = new ArrayList<>();
        try{
            JSONObject resultList = new JSONObject(officer);
            for (int i = 0; i < resultList.getJSONArray("items").length(); i++) {
                JSONObject officerItem = resultList.getJSONArray("items").getJSONObject(i);
                companyOfficers.add(officerItem.getString("name"));
            }
        } catch (Exception e) {
            log.error("error");
        }
        return companyOfficers;
    }


    public boolean checkCompanyOfficer(String url, String firstName, String surname) throws IOException {
        List<String> companyOfficersList = getOfficerList(url);
        for(String officerName: companyOfficersList) {
            officerName = officerName.toLowerCase();
            List<String> listOfNames = Arrays.asList(officerName.split("\\s*[,| ]\\s*"));
            if(listOfNames.contains(firstName.toLowerCase()) && listOfNames.contains(surname.toLowerCase())){
                return true;
            }
        }
        return false;
    }

}

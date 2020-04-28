package com.companies.web.rest;

import com.companies.domain.CompanyDTO;
import com.companies.domain.ResponseDTO;
import com.companies.security.SecurityUtils;

import com.companies.service.ApiService;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class AccountResource {

    private static final String COMPANY_API = "https://api.companieshouse.gov.uk/search/companies?q=";
    private static final String COMPANY_OFFICERS_API1 = "https://api.companieshouse.gov.uk/company/";
    private static final String COMPANY_OFFICERS_API2 = "/officers";

    @Autowired
    private final ApiService apiService;

    public AccountResource(ApiService apiService) {
        this.apiService = apiService;
    }

    private static class AccountResourceException extends RuntimeException {
    }


    @GetMapping("/account/test")
    public ResponseDTO test(@RequestParam("companyName") String companyName, @RequestParam("firstName") String firstName,
                       @RequestParam("surname") String surname) throws IOException {

        CompanyDTO companyDTO = apiService.getCompanyNumber(COMPANY_API + apiService.urlEncodeValue(companyName), companyName);
        String companyNotFound = "A record for " + companyName + " could not be found at Companies House";
        String officerNotFound = "A record for " + firstName + " " + surname + " could not be found as an officer of " + companyName;


        if(companyDTO != null){
            if(apiService.checkCompanyOfficer(COMPANY_OFFICERS_API1 + companyDTO.getCompanyNumber() + COMPANY_OFFICERS_API2, firstName, surname)){
                return new ResponseDTO(companyDTO.getCompanyAddress(), false);
            } else {
                return new ResponseDTO(officerNotFound, true);
            }
        } else {
            return new ResponseDTO(companyNotFound, true);
        }
    }

    /**
     * {@code GET  /account} : get the current user.
     *
     * @return the current user.
     * @throws AccountResourceException {@code 500 (Internal Server Error)} if the user couldn't be returned.
     */
    @GetMapping("/account")
    public UserVM getAccount() {
        String login = SecurityUtils.getCurrentUserLogin()
            .orElseThrow(AccountResourceException::new);
        Set<String> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toSet());
        return new UserVM(login, authorities);
    }

    private static class UserVM {
        private String login;
        private Set<String> authorities;

        @JsonCreator
        UserVM(String login, Set<String> authorities) {
            this.login = login;
            this.authorities = authorities;
        }

        public boolean isActivated() {
            return true;
        }

        public Set<String> getAuthorities() {
            return authorities;
        }

        public String getLogin() {
            return login;
        }
    }
}

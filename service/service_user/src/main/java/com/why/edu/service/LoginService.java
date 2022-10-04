package com.why.edu.service;

import javax.servlet.http.HttpServletRequest;

public interface LoginService {

    String getWxCodeUrl();

    String callback(String code, String state, HttpServletRequest request);

}

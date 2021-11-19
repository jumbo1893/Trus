package com.jumbo.trus.web;

import org.json.JSONObject;

public interface IRequestListener {
    void onResponse(JSONObject response);
    void onErrorResponse(String error);
}

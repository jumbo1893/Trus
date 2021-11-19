package com.jumbo.trus.web;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class JsonParser {

    private String URL;
    private String sheetName;
    private int rowsNumber;

    public JsonParser() {
    }
    public JsonParser(JSONObject jsonObject) throws JSONException {
        URL = jsonObject.getString("sheetURL");
        sheetName = jsonObject.getString("sheetName");
        rowsNumber = jsonObject.getInt("rowsNumber");
    }


    public JSONObject convertStatsToJsonObject(String action, List<List<String>> rowList, String footer) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", action);
        JSONObject table = new JSONObject();
        JSONArray rows = new JSONArray(rowList);
        table.put("rows", rows);
        table.put("rowLength", rowList.get(0).size());
        table.put("footer", footer);
        jsonObject.put("table", table);
        return jsonObject;
    }

    public String getURL() {
        return URL;
    }

    public String getSheetName() {
        return sheetName;
    }

    public int getRowsNumber() {
        return rowsNumber;
    }
}

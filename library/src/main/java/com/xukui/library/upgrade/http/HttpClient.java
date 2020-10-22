package com.xukui.library.upgrade.http;

import com.xukui.library.upgrade.builder.RequestVersionBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpClient {

    private static OkHttpClient client;

    public static OkHttpClient getHttpClient() {
        if (client == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(15, TimeUnit.SECONDS);
            client = builder.build();
        }

        return client;
    }

    private static String assembleUrl(String url, HttpParams params) {
        StringBuffer urlBuilder = new StringBuffer(url);
        if (params != null) {
            urlBuilder.append("?");

            for (Map.Entry<String, Object> stringObjectEntry : params.entrySet()) {
                String key = stringObjectEntry.getKey();
                String value = stringObjectEntry.getValue() + "";
                urlBuilder.append(key).append("=").append(value).append("&");
            }

            url = urlBuilder.substring(0, urlBuilder.length() - 1);
        }
        return url;
    }

    private static String getRequestParamsJson(HttpParams params) {
        JSONObject jsonObject = new JSONObject();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            try {
                jsonObject.put(entry.getKey(), entry.getValue());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonObject.toString();
    }

    /**********************************V2.0 Using RequestBuilder ************************************************************************/

    private static <T extends Request.Builder> T assembleHeader(T builder, RequestVersionBuilder versionParams) {
        HttpHeaders headers = versionParams.getHttpHeaders();
        if (headers != null) {
            for (Map.Entry<String, String> stringStringEntry : headers.entrySet()) {
                String key = stringStringEntry.getKey();
                String value = stringStringEntry.getValue();
                builder.addHeader(key, value);
            }
        }
        return builder;
    }

    public static Request.Builder get(RequestVersionBuilder versionParams) {
        Request.Builder builder = new Request.Builder();
        builder = assembleHeader(builder, versionParams);
        builder.url(assembleUrl(versionParams.getRequestUrl(), versionParams.getRequestParams()));

        return builder;
    }

    public static Request.Builder post(RequestVersionBuilder versionParams) {
        FormBody formBody = getRequestParams(versionParams);
        Request.Builder builder = new Request.Builder();
        builder = assembleHeader(builder, versionParams);
        builder.post(formBody).url(versionParams.getRequestUrl());
        return builder;
    }

    public static Request.Builder postJson(RequestVersionBuilder versionParams) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String json = getRequestParamsJson(versionParams.getRequestParams());
        RequestBody body = RequestBody.create(JSON, json);
        Request.Builder builder = new Request.Builder();
        builder = assembleHeader(builder, versionParams);
        builder.post(body).url(versionParams.getRequestUrl());
        return builder;
    }

    private static FormBody getRequestParams(RequestVersionBuilder versionParams) {
        FormBody.Builder builder = new FormBody.Builder();
        HttpParams params = versionParams.getRequestParams();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            builder.add(entry.getKey(), entry.getValue() + "");
        }
        return builder.build();
    }

}
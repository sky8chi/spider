package com.skyzd.framework.http;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.apache.http.Consts;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Parameters implements Cloneable {
    private final Map<String, Map<Integer, Object>> paramHashValues = new LinkedHashMap<String, Map<Integer, Object>>();

    private int limit = -1;
    private int parameterCount = 0;
    private int parameterIndex = 0;
    private String orgParamStr = "";

    public String getOrgParamStr() {
        return orgParamStr;
    }

    public void setOrgParamStr(String orgParamStr) {
        this.orgParamStr = orgParamStr;
    }

    private void addParameter(String key, Object value) {
        parameterCount++;
        if (limit > -1 && parameterCount > limit) {
            // Processing this parameter will push us over the limit. ISE is
            // what Request.parseParts() uses for requests that are too big
            throw new IllegalStateException("parameters.maxCountFail: " + limit);
        }

        Map<Integer, Object> values = paramHashValues.get(key);
        if (values == null) {
            values = Maps.newLinkedHashMap();
            paramHashValues.put(key, values);
        }
        values.put(parameterIndex++, value == null ? "" : value);
    }

    public Parameters add4Encode(String key, Object value) throws IllegalStateException, UnsupportedEncodingException {
        if (key == null) {
            return this;
        }
        if (!checkValuePrimitive(value)) {
            throw new IllegalArgumentException("Please use value which is primitive type like: String,Integer,Long and so on. But not Collection !");
        }
        addParameter(key, URLEncoder.encode(value.toString(), Consts.UTF_8.displayName()));
        return this;
    }

    public Parameters add(String key, Object value) throws IllegalStateException {
        if (key == null) {
            return this;
        }
        if (!checkValuePrimitive(value)) {
            throw new IllegalArgumentException("Please use value which is primitive type like: String,Integer,Long and so on. But not Collection !");
        }
        addParameter(key, value);
        return this;
    }

    private boolean checkValuePrimitive(Object value) {
        return (value == null || value instanceof String || value instanceof Integer
                || value instanceof Long || value instanceof Boolean || value instanceof Float
                || value instanceof Double || value instanceof Character || value instanceof Byte
                || value instanceof Short);
    }

    public String[] getParameterValues(String name) {
        Map<Integer, Object> values = paramHashValues.get(name);
        if (values == null) {
            return null;
        }
        String[] valueArr = new String[values.size()];
        int i = 0;
        for (Object value : values.values()) {
            valueArr[i++] = value.toString();
        }
        return valueArr;
    }

    public Map<Integer, String> getParameterIdxValues(String name) {
        Map<Integer, Object> values = paramHashValues.get(name);
        if (values == null) {
            return null;
        }
        Map<Integer, String> rtValues = new LinkedHashMap<Integer, String>();
        for (Integer idx : values.keySet()) {
            Object valueObj = values.get(idx);
            String value = valueObj == null ? "" : valueObj.toString();
            rtValues.put(idx, value);
        }
        return rtValues;
    }

    public String getParameter(String name) {
        Map<Integer, Object> values = paramHashValues.get(name);
        if (values != null) {
            if(values.size() == 0) {
                return "";
            }
            return values.values().iterator().next().toString();
        } else {
            return null;
        }
    }

    public Set<String> getParameterNames() {
        return paramHashValues.keySet();
    }



    /**
     * Debug purpose
     */
    public String paramsAsString(ParamValueDecorate... decorates) throws IOException {
        if (paramHashValues.isEmpty()) {
            return "";
        }

        ParamValueDecorate decorate = null;
        if (decorates != null && decorates.length > 0) {
            decorate = decorates[0];
        }

        String[] paramArr = new String[parameterIndex];
        for (String name : paramHashValues.keySet()) {
            Map<Integer, Object> values = paramHashValues.get(name);
            for (Integer index : values.keySet()) {
                String value = values.get(index).toString();
                if(decorate != null) {
                    value = decorate.doInValue(value);
                }
                paramArr[index] = name + "=" + value;
            }
        }

        StringBuilder sb = new StringBuilder();
        for (String singleQuery : paramArr) {
            if (!Strings.isNullOrEmpty(singleQuery)) {
                sb.append(singleQuery).append("&");
            }
        }
        return sb.length() > 0 ? sb.subSequence(0, sb.length() - 1).toString() : "";
    }


    public int getParameterCount() {
        return parameterCount;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}

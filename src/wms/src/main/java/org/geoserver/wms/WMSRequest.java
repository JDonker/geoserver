/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections.EnumerationUtils;
import org.geoserver.ows.Dispatcher;
import org.geoserver.ows.util.CaseInsensitiveMap;

import javax.servlet.http.HttpServletRequest;

/**
 * Defines a general Request type and provides accessor methods for universal request information.
 *
 * @author Rob Hranac, TOPP
 * @author Chris Holmes, TOPP
 * @author Gabriel Roldan
 * @author $Author: Alessio Fabiani (alessio.fabiani@gmail.com) $ (last modification)
 * @author $Author: Simone Giannecchini (simboss1@gmail.com) $ (last modification)
 * @version $Id$
 */
public abstract class WMSRequest {

    private String baseUrl;

    private Map<String, String> rawKvp;

    /** flag indicating if the request is get */
    protected boolean get;

    protected String request;

    protected String version;

    private String requestCharset;

    private Map<String, String> httpRequestHeaders;

    /**
     * Creates the new request with the given operation name
     *
     * @param request name of the request, (Example, GetCapabiliites)
     */
    protected WMSRequest(final String request) {
        setRequest(request);
    }

    /**
     * Tells whether the originating request used HTTP GET method or not; may be useful, for
     * example, to determine if client can do HTTP caching and then set the corresponding response
     * headers.
     *
     * @return {@code true} if the originating HTTP request used HTTP GET method, {@code false}
     *     otherwise
     */
    public boolean isGet() {
        return get;
    }

    public void setGet(boolean get) {
        this.get = get;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setRawKvp(Map<String, String> rawKvp) {
        this.rawKvp = rawKvp;
    }

    /** Set by {@link Dispatcher} */
    public void setBaseUrl(final String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getBaseUrl() {
        return this.baseUrl;
    }

    /** Gets the raw kvp parameters which were used to create the request. */
    public Map<String, String> getRawKvp() {
        return rawKvp;
    }

    /** Setter for the 'WMTVER' parameter, which is an alias for 'VERSION'. */
    public void setWmtVer(String version) {
        setVersion(version);
    }

    /** @return the HTTP request charset, may be {@code null} */
    public String getRequestCharset() {
        return requestCharset;
    }

    public void setRequestCharset(String requestCharset) {
        this.requestCharset = requestCharset;
    }

    public String getHttpRequestHeader(String headerName) {
        return httpRequestHeaders == null ? null : httpRequestHeaders.get(headerName);
    }

    public void putHttpRequestHeader(String headerName, String value) {
        if (httpRequestHeaders == null) {
            httpRequestHeaders = new CaseInsensitiveMap<>(new HashMap<>());
        }
        httpRequestHeaders.put(headerName, value);
    }

    public Map<String, String> getHttpRequestHeaders() {
        return this.httpRequestHeaders;
    }

    public void addRequestParameters() {
        HttpServletRequest httpRequest =
                Optional.ofNullable(Dispatcher.REQUEST.get())
                        .map(r -> r.getHttpRequest())
                        .orElse(null);
        if (httpRequest != null) {
            this.setRequestCharset(httpRequest.getCharacterEncoding());
            this.setGet("GET".equalsIgnoreCase(httpRequest.getMethod()));
            List<String> headerNames = EnumerationUtils.toList(httpRequest.getHeaderNames());
            for (String headerName : headerNames) {
                this.putHttpRequestHeader(headerName, httpRequest.getHeader(headerName));
            }
        }
    }

    public void addRequestParameters(Map<String, String> httpRequestHeaders) {
        if (httpRequestHeaders != null) {
            for (Map.Entry<String, String> headerName : httpRequestHeaders.entrySet()) {
                this.putHttpRequestHeader(headerName.getKey(), headerName.getValue());
            }
        }
    }

}

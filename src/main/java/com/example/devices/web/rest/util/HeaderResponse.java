package com.example.devices.web.rest.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;

/**
 * Utility class for HTTP headers creation.
 */
public class HeaderResponse {

    @Value("${spring.application.name}")
    private String applicationName;


    /**
     * Create a alert
     *
     * @param message
     * @param param
     * @return a {@link HttpHeaders} with
     */
    private HttpHeaders createAlert(String message, String param) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-" + applicationName + "-Alert", message);
        try {
            headers.add("X-" + applicationName + "-Parameter", URLEncoder.encode(param, StandardCharsets.UTF_8.toString()));
        } catch (UnsupportedEncodingException e) {
            // StandardCharsets are supported by every Java implementation so this exception will never happen
        }
        return headers;
    }

    /**
     * Create Header Add Alert
     *
     * @param entityName
     * @param param
     * @return
     */
    public HttpHeaders createAddAlert(String entityName, String param) {
        String message = "A new " + entityName + " is created with identifier " + param;
        return createAlert(message, param);
    }

    public HttpHeaders createDeleteAlert(String entityName, String param) {
        String message = "A " + entityName + " is deleted with identifier " + param;
        return createAlert(message, param);
    }

    /**
     * Generate Link pagination
     *
     * @param uriComponentsBuilder  a {@link UriComponentsBuilder} for build the link
     * @param page current page with all information to create a link
     * @param <T> generic type
     * @return a {@link HttpHeaders} with the links: first, last, next and prev
     */
    public static <T> HttpHeaders generatePagination(UriComponentsBuilder uriComponentsBuilder, Page<T> page) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", Long.toString(page.getTotalElements()));
        int pageNumber = page.getNumber();
        int pageSize = page.getSize();
        StringBuilder link = new StringBuilder();
        if (pageNumber < page.getTotalPages()) { // <link>next</link>
            link.append(createLink(uriComponentsBuilder, pageNumber, pageSize, "next")).append(",");
        }

        if (pageNumber > 0) { // <link>prev</link>
            link.append(createLink(uriComponentsBuilder, pageNumber, pageSize, "prev")).append(",");
        }

        link.append(createLink(uriComponentsBuilder, page.getTotalPages() - 1, pageSize, "last"))
                .append(",")
                .append(createLink(uriComponentsBuilder, 0, pageSize, "first"));
        headers.add(HttpHeaders.LINK, link.toString());
        return headers;
    }

    /**
     * Create a link
     *
     * @param builder    a {@link UriComponentsBuilder} for build the link
     * @param pageNumber current page
     * @param pageSize   a integer
     * @param relType    direction of the link: next, first, last or first
     * @return a link as chain
     */
    private static String createLink(UriComponentsBuilder builder, int pageNumber, int pageSize, String relType) {
        return MessageFormat.format("<{0}>; rel\"{1}\"", builder.replaceQueryParam(
                "page", Integer.toString(pageNumber))
                        .replaceQueryParam(
                                "size", Integer.toString(pageSize)
                        ).toUriString()
                        .replace(",", "%2C")
                        .replace(";", "%3B")
                , relType);
    }
}

package com.stephthedev.kankaclient.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;
import com.stephthedev.kanka.generated.entities.KankaEntity;
import com.stephthedev.kankaclient.api.entities.EntitiesRequest;
import com.stephthedev.kankaclient.api.entities.EntitiesResponse;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.tinylog.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

/**
 * Makes requests to kanka and transform result(s) into the correct type
 */
class KankaRestClient {

    static final long UNSET = -1L;

    private enum HttpMethod {
        GET, POST, PATCH, DELETE
    }

    String authToken;
    HttpClient httpClient;
    String host;
    long campaignId;
    ObjectMapper mapper;

    KankaRestClient(String host, String authToken, long campaignId) {
        this.host = host;
        this.authToken = authToken;
        this.campaignId = campaignId;

        this.httpClient = HttpClients.createDefault();
        this.mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    <T extends KankaEntity> T createEntity(String endpoint, Class<T> responseType, Optional<Long> parentId, KankaEntity entity) throws IOException, URISyntaxException {
        Preconditions.checkNotNull(entity, "The kanka entity cannot be null");

        if (parentId.isPresent()) {
            Preconditions.checkArgument(parentId.get() > UNSET,
                    "The parent id should be greater than 0");
            endpoint = String.format(endpoint, parentId.get(), entity.getId());
        }

        String json = makeRequest(HttpMethod.POST, endpoint, entity);
        return responseType.cast(mapper.readValue(getDataFieldFromJsonStr(json), responseType));
    }

    <T extends KankaEntity> T readEntity(String endpoint, Class<T> responseType, Optional<Long> parentId, long id) throws IOException, URISyntaxException {
        Preconditions.checkArgument(id > UNSET, "The kanka entity id must be greater than 0");
        if (parentId.isPresent()) {
            Preconditions.checkArgument(parentId.get() > UNSET,
                    "The parent id should be greater than 0");
            endpoint = String.format(endpoint, parentId.get(), id);
        } else {
            endpoint = String.format(endpoint, id);
        }

        String json = makeRequest(HttpMethod.GET, endpoint, null);
        return responseType.cast(mapper.readValue(getDataFieldFromJsonStr(json), responseType));
    }

    EntitiesResponse readEntities(String endpoint, Class responseType, Optional<Long> parentId, EntitiesRequest request) throws IOException, URISyntaxException {
        if (parentId.isPresent()) {
            Preconditions.checkArgument(parentId.get() > UNSET,
                    "The parent id should be greater than 0");
            endpoint = String.format(endpoint, parentId.get());
        }
        String json = makeRequest(HttpMethod.GET, endpoint, null);
        JavaType entitiesResponseType = mapper.getTypeFactory()
                .constructParametricType(EntitiesResponse.class, responseType);
        return mapper.readValue(json, entitiesResponseType);
    }

    <T extends KankaEntity> T updateEntity(String endpoint, Class<T> responseType, Optional<Long> parentId, KankaEntity entity) throws IOException, URISyntaxException {
        Preconditions.checkNotNull(entity, "The kanka entity cannot be null");
        Preconditions.checkNotNull(entity.getId(), "The entity id cannot be null");

        if (parentId.isPresent()) {
            Preconditions.checkArgument(parentId.get() > UNSET,
                    "The parent id should be greater than 0");
            endpoint = String.format(endpoint, parentId.get(), entity.getId());
        } else {
            endpoint = String.format(endpoint, entity.getId());
        }

        String json = makeRequest(HttpMethod.PATCH, endpoint, entity);
        return responseType.cast(mapper.readValue(getDataFieldFromJsonStr(json), responseType));
    }

    void deleteEntity(String endpoint, Optional<Long> parentId, long id) throws IOException, URISyntaxException {
        Preconditions.checkArgument(id > UNSET, "The kanka entity id must be greater than 0");
        if (parentId.isPresent()) {
            Preconditions.checkArgument(parentId.get() > UNSET,
                    "The parent id should be greater than 0");
            endpoint = String.format(endpoint, parentId.get(), id);
        } else {
            endpoint = String.format(endpoint, id);
        }
        makeRequest(HttpMethod.DELETE, endpoint, null);
    }

    private String makeRequest(HttpMethod httpMethod, String endpoint, KankaEntity kankaEntity)
            throws IOException, URISyntaxException {
        return makeRequest(httpMethod, endpoint, kankaEntity, null);
    }

    /**
     *
     * @param httpMethod The http method to make
     * @param endpoint The partial endpoint to use (i.e. /characters)
     * @param entitiesRequest The optional parameters to use when making this request
     * @return
     * @throws IOException
     */
    private String makeRequest(HttpMethod httpMethod, String endpoint, KankaEntity kankaEntity, EntitiesRequest entitiesRequest)
            throws IOException, URISyntaxException {
        Preconditions.checkNotNull(httpMethod, "The HTTP request cannot be null");
        Preconditions.checkNotNull(endpoint, "The http endpoint cannot be null");

        //Make the request
        HttpResponse response = null;
        URI uri = null;

        if (entitiesRequest == null) {
            uri = new URI(host + endpoint);
        }

        if (entitiesRequest != null) {
            if (entitiesRequest.getLink() != null) {
                uri = new URI(entitiesRequest.getLink());
            } else {
                //Set the host + path
                URIBuilder uriBuilder = new URIBuilder().setHost(host)
                        .setPath(endpoint);
                if (entitiesRequest.getPage() > -1) {
                    uriBuilder = uriBuilder.addParameter("page", entitiesRequest.getPage() + "");
                }
                if (entitiesRequest.getLastSync() != null) {
                    uriBuilder = uriBuilder.addParameter("lastSync", entitiesRequest.getLastSync());
                }
                uri = uriBuilder.build();
            }
        }
        int expectedCode = -1;

        Logger.debug(httpMethod + " " + endpoint);
        Logger.debug("Entity JSON: " + mapper.writeValueAsString(kankaEntity));
        if (HttpMethod.POST.equals(httpMethod) || HttpMethod.PATCH.equals(httpMethod)) {
            HttpEntityEnclosingRequestBase httpReq = null;
            if (HttpMethod.POST.equals(httpMethod)) {
                httpReq = new HttpPost(uri);
                expectedCode = HttpStatus.SC_CREATED;
            } else {
                httpReq = new HttpPatch(uri);
                expectedCode = HttpStatus.SC_OK;
            }
            addCommonHeaders(httpReq);
            httpReq.setEntity(new StringEntity(mapper.writeValueAsString(kankaEntity)));
            response = httpClient.execute(httpReq);
        } else if (HttpMethod.GET.equals(httpMethod) || HttpMethod.DELETE.equals(httpMethod)) {
            HttpUriRequest httpReq = null;
            if (HttpMethod.DELETE.equals(httpMethod)) {
                httpReq = new HttpDelete(uri);
                expectedCode = HttpStatus.SC_NO_CONTENT;
            } else {
                httpReq = new HttpGet(uri);
                expectedCode = HttpStatus.SC_OK;
            }
            addCommonHeaders(httpReq);
            response = httpClient.execute(httpReq);
        } else {
            throw new IOException("Unsupported HTTP request for " + httpMethod);
        }

        //Examine the response
        if (response.getStatusLine().getStatusCode() == expectedCode) {
            if (HttpMethod.DELETE.equals(httpMethod)) {
                //No_content
                return null;
            }

            String json = EntityUtils.toString(response.getEntity());
            Logger.debug("Kanka Response: " + json);
            return json;
        } else {
            StatusLine status = response.getStatusLine();
            String error = "Unexpected status code ({%d}) and error ({%s})";
            String message = String.format(error, status.getStatusCode(), status.getReasonPhrase());
            if (response.getEntity() != null) {
                String json = EntityUtils.toString(response.getEntity());
                Logger.error("Kanka Error Response: " + json);
            }
            throw new IOException(message);
        }
    }

    private void addCommonHeaders(HttpUriRequest httpReq) {
        httpReq.setHeader("Authorization", "Bearer " + authToken);
        httpReq.setHeader("Content-Type", "application/json");
        httpReq.setHeader("Accept", "application/json");
    }

    /**
     * Used for any kanka response that is just {"data" : {}}. Return just the "data" object value
     * @param json
     * @return
     * @throws JsonProcessingException
     */
    private String getDataFieldFromJsonStr(String json) throws JsonProcessingException {
        ObjectNode node = mapper.readValue(json, ObjectNode.class);
        return node.get("data").toString();
    }
}

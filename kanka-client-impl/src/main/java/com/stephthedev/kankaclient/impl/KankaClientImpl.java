package com.stephthedev.kankaclient.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;
import com.stephthedev.kanka.generated.entities.KankaCharacter;
import com.stephthedev.kanka.generated.entities.KankaEntity;
import com.stephthedev.kanka.generated.entities.KankaEntityNote;
import com.stephthedev.kanka.generated.entities.KankaLocation;
import com.stephthedev.kankaclient.api.entities.EntitiesResponse;
import com.stephthedev.kankaclient.api.entities.EntitiesRequest;
import com.stephthedev.kankaclient.api.KankaClient;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Implementation of Kanka client
 * @author sortiz
 */
public class KankaClientImpl implements KankaClient {

	private static final String ENDPOINT_CHARACTER = "/characters/%d";
	private static final String ENDPOINT_CHARACTERS = "/characters";

	private static final String ENDPOINT_LOCATION = "/location/%id";
	private static final String ENDPOINT_LOCATIONS = "/locations";

	private static final String ENDPOINT_ENTITY_NOTE = "/entities/%d/entity_notes/%d";
	private static final String ENDPOINT_ENTITY_NOTES = "/entities/%d/entity_notes";

	private enum HttpMethod {
		GET, POST, PATCH, DELETE
	}

	String authToken;
	HttpClient httpClient;
	String host;
	long campaignId;
	ObjectMapper mapper;

	private KankaClientImpl(String host, String authToken, long campaignId) {
		this.host = host;
		this.authToken = authToken;
		this.campaignId = campaignId;
		this.httpClient = HttpClients.createDefault();
		this.mapper = new ObjectMapper();
	}

	@Override
	public EntitiesResponse<KankaCharacter> getCharacters(EntitiesRequest request) throws IOException, URISyntaxException {
		return readEntities(ENDPOINT_CHARACTERS, KankaCharacter.class, request);
	}

	@Override
	public KankaCharacter getCharacter(long id) throws IOException, URISyntaxException {
		return readEntity(ENDPOINT_CHARACTER, KankaCharacter.class, id);
	}

	@Override
	public KankaCharacter createCharacter(KankaCharacter character) throws IOException, URISyntaxException {
		return createEntity(ENDPOINT_CHARACTERS, KankaCharacter.class, character);
	}

	@Override
	public KankaCharacter updateCharacter(KankaCharacter character) throws IOException, URISyntaxException {
		return updateEntity(ENDPOINT_CHARACTER, KankaCharacter.class, character);
	}

	@Override
	public void deleteCharacter(long id) throws IOException, URISyntaxException {
		deleteEntity(ENDPOINT_CHARACTER, id);
	}

	@Override
	public EntitiesResponse<KankaLocation> getLocations(EntitiesRequest request) throws IOException, URISyntaxException {
		return readEntities(ENDPOINT_LOCATIONS, KankaLocation.class, request);
	}

	@Override
	public KankaLocation getLocation(long id) throws IOException, URISyntaxException {
		return readEntity(ENDPOINT_LOCATION, KankaLocation.class, id);
	}

	@Override
	public KankaLocation createLocation(KankaLocation location) throws IOException, URISyntaxException {
		return createEntity(ENDPOINT_LOCATIONS, KankaLocation.class, location);
	}

	@Override
	public KankaLocation updateLocation(KankaLocation location) throws IOException, URISyntaxException {
		return updateEntity(ENDPOINT_LOCATION, KankaLocation.class, location);
	}

	@Override
	public void deleteLocation(long id) throws IOException, URISyntaxException {
		deleteEntity(ENDPOINT_LOCATION, id);
	}

	@Override
	public EntitiesResponse<KankaEntityNote> getEntityNotes(long parentId) throws IOException, URISyntaxException {
		return readEntities(ENDPOINT_ENTITY_NOTES, KankaEntityNote.class, parentId);
	}

	@Override
	public KankaEntityNote getEntityNote(long parentId, long noteId) throws IOException, URISyntaxException {
		return readEntity(ENDPOINT_ENTITY_NOTE, KankaEntityNote.class, parentId, noteId);
	}

	@Override
	public KankaEntityNote createEntityNote(long parentId, KankaEntityNote note) throws IOException, URISyntaxException {
		return createEntity(ENDPOINT_ENTITY_NOTES, KankaEntityNote.class, parentId, note);
	}

	@Override
	public KankaEntityNote updateEntityNote(long parentId, KankaEntityNote note) throws IOException, URISyntaxException {
		return updateEntity(ENDPOINT_ENTITY_NOTE, KankaEntityNote.class, parentId, note);
	}

	@Override
	public void deleteEntityNote(long parentId, long noteId) throws IOException, URISyntaxException {
		deleteEntity(ENDPOINT_ENTITY_NOTE, parentId, noteId);
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

	private String getURLFromEntityRequest(EntitiesRequest request, String defaultEndpoint) {
		String url = defaultEndpoint;
		if (request != null) {
			if (request.getPage() > -1) {
				//TODO: Pull the query param out
				url = defaultEndpoint + "?page=" + request.getPage();
			} else if (request.getLink() != null) {
				url = request.getLink();
			}
		}
		return url;
	}

	private <T extends KankaEntity> T createEntity(String endpoint, Class<T> type, KankaEntity entity) throws IOException, URISyntaxException {
		Preconditions.checkNotNull(entity, "The kanka entity cannot be null");
		String json = makeRequest(HttpMethod.POST, host + endpoint, entity);
		return type.cast(mapper.readValue(getDataFieldFromJsonStr(json), type));
	}

	private <T extends KankaEntity> T createEntity(String endpoint, Class<T> type, long entityId, KankaEntity entity) throws IOException, URISyntaxException {
		Preconditions.checkArgument(entityId > 0, "The entity id must be greater than 0");
		Preconditions.checkNotNull(entity, "The kanka entity cannot be null");
		String json = makeRequest(HttpMethod.POST, String.format(host + endpoint, entityId), entity);
		return type.cast(mapper.readValue(getDataFieldFromJsonStr(json), type));
	}

	private <T extends KankaEntity> T readEntity(String endpoint, Class<T> type, long id) throws IOException, URISyntaxException {
		Preconditions.checkArgument(id > 0, "The kanka entity id must be greater than 0");
		String json = makeRequest(HttpMethod.GET, String.format(host + endpoint, id), null);
		return type.cast(mapper.readValue(getDataFieldFromJsonStr(json), type));
	}

	private <T extends KankaEntity> T readEntity(String endpoint, Class<T> type, long entityId, long id) throws IOException, URISyntaxException {
		Preconditions.checkArgument(entityId > 0, "The kanka entity id must be greater than 0");
		Preconditions.checkArgument(id > 0, "The id must be greater than 0");

		String json = makeRequest(HttpMethod.GET, String.format(host + endpoint, entityId, id), null);
		return type.cast(mapper.readValue(getDataFieldFromJsonStr(json), type));
	}

	private EntitiesResponse readEntities(String endpoint, Class type, EntitiesRequest request) throws IOException, URISyntaxException {
		Preconditions.checkNotNull(request, "The entity request cannot be null");
		String url = getURLFromEntityRequest(request, host + endpoint);
		String json = makeRequest(HttpMethod.GET, url, null);
		JavaType entitiesResponseType = mapper.getTypeFactory()
				.constructParametricType(EntitiesResponse.class, type);
		return mapper.readValue(json, entitiesResponseType);
	}

	private EntitiesResponse readEntities(String endpoint, Class type, long entityId) throws IOException, URISyntaxException {
		Preconditions.checkArgument(entityId > 0, "The entity id must be greater than 0");
		String json = makeRequest(HttpMethod.GET, String.format(host + endpoint, entityId), null);
		JavaType entitiesResponseType = mapper.getTypeFactory()
				.constructParametricType(EntitiesResponse.class, type);
		return mapper.readValue(json, entitiesResponseType);
	}

	private <T extends KankaEntity> T updateEntity(String endpoint, Class<T> type, KankaEntity entity) throws IOException, URISyntaxException {
		Preconditions.checkNotNull(entity, "The kanka entity cannot be null");
		Preconditions.checkNotNull(entity.getId(), "The kanka entity id cannot be null");

		String json = makeRequest(HttpMethod.PATCH, String.format(host + endpoint, entity.getId()), entity);
		return type.cast(mapper.readValue(getDataFieldFromJsonStr(json), type));
	}

	private <T extends KankaEntity> T updateEntity(String endpoint, Class<T> type, long parentId, KankaEntity entity) throws IOException, URISyntaxException {
		Preconditions.checkArgument(parentId > 0, "The entity id must be greater than 0");
		Preconditions.checkNotNull(entity, "The kanka entity cannot be null");

		String json = makeRequest(HttpMethod.PATCH, String.format(host + endpoint, parentId, entity.getId()), entity);
		return type.cast(mapper.readValue(getDataFieldFromJsonStr(json), type));
	}

	private void deleteEntity(String endpoint, long id) throws IOException, URISyntaxException {
		Preconditions.checkArgument(id > 0, "The kanka entity id must be greater than 0");
		makeRequest(HttpMethod.DELETE, String.format(host + endpoint, id), null);
	}

	private void deleteEntity(String endpoint, long parentId, long id) throws IOException, URISyntaxException {
		Preconditions.checkArgument(parentId > 0, "The kanka entity id must be greater than 0");
		Preconditions.checkArgument(id > 0, "The kanka entity id must be greater than 0");
		makeRequest(HttpMethod.DELETE, String.format(host + endpoint, parentId, id), null);
	}

	/**
	 *
	 * @param httpMethod The http method to make
	 * @param endpoint The partial endpoint to use (i.e. /characters)
	 * @return
	 * @throws IOException
	 */
	private String makeRequest(HttpMethod httpMethod, String endpoint, KankaEntity kankaEntity) throws IOException, URISyntaxException {
		Preconditions.checkNotNull(httpMethod, "The HTTP request cannot be null");
		Preconditions.checkNotNull(endpoint, "The http endpoint cannot be null");

		//Make the request
		HttpResponse response = null;
		URI uri = new URI(endpoint);
		int expectedCode = -1;

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
			return json;
		} else {
			StatusLine status = response.getStatusLine();
			String error = "Unexpected status code ({%d}) and error ({%s})";
			String message = String.format(error, status.getStatusCode(), status.getReasonPhrase());
			throw new IOException(message);
		}
	}

	private void addCommonHeaders(HttpUriRequest httpReq) {
		httpReq.setHeader("Authorization", "Bearer " + authToken);
		httpReq.setHeader("Content-Type", "application/json");
		httpReq.setHeader("Accept", "application/json");
	}

	public static class Builder {

		private static final long UNSET = -1L;
		static final String KANKA_ENDPOINT = "kanka.io/api";
		static final String KANKA_VERSION = "v1";

		String authToken;
		long campaignId = UNSET;
		String kankaAPIEndpoint = KANKA_ENDPOINT;
		String kankaVersion = KANKA_VERSION;

		/**
		 * (Optional) Defaults to "kanka.io/api"
		 * @param kankaAPIEndpoint The kanka api endpoint to use
		 * @return
		 */
		public Builder withKankaApiEndpoint(String kankaAPIEndpoint) {
			this.kankaAPIEndpoint = kankaAPIEndpoint;
			return this;
		}

		/**
		 * (Optional) Defaults to "v1"
		 * @param kankaVersion The kanka version to use
		 * @return
		 */
		public Builder withKankaVersion(String kankaVersion) {
			this.kankaVersion = kankaVersion;
			return this;
		}

		/**
		 * The campaign id to use
		 * @param campaignId
		 * @return
		 */
		public Builder withCampaignId(long campaignId) {
			this.campaignId = campaignId;
			return this;
		}

		/**
		 * The kanka authentication token to use
		 * @param authToken
		 * @return
		 */
		public Builder withAuthToken(String authToken) {
			this.authToken = authToken;
			return this;
		}

		public KankaClient build() {
			String host = String.format("https://%s/%s/campaigns/%d", kankaAPIEndpoint, kankaVersion, campaignId);
			Preconditions.checkNotNull(kankaAPIEndpoint, "The kanka endpoint cannot be null");
			Preconditions.checkNotNull(authToken, "The authentication token cannot be null");
			Preconditions.checkArgument(campaignId > UNSET, "The campaign id is not set");

			return new KankaClientImpl(host, authToken, campaignId);
		}
	}
}

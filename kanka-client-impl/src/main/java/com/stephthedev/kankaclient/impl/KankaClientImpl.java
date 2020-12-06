package com.stephthedev.kankaclient.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.stephthedev.kanka.api.client.GetCharacterResponse;
import com.stephthedev.kanka.api.client.GetCharactersResponse;
import com.stephthedev.kanka.api.entities.CharacterEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClients;

import com.stephthedev.kankaclient.api.EntityRequest;
import com.stephthedev.kankaclient.api.KankaClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Implementation of Kanka client
 * @author sortiz
 */
public class KankaClientImpl implements KankaClient {

	private static final String ENDPOINT_CHARACTERS = "/characters";
	private static final String ENDPOINT_CHARACTER = "/characters/%d";

	private static enum HttpMethod {
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
	public GetCharactersResponse getCharacters(EntityRequest request) throws IOException, URISyntaxException {
		String json = null;
		String url = host + ENDPOINT_CHARACTERS;
		if (request != null) {
			if (request.getPage() > -1) {
				//TODO: Pull the query param out
				url = host + ENDPOINT_CHARACTERS + "?page=" + request.getPage();
			} else if (request.getLink() != null) {
				url = request.getLink();
			}
		}

		json = makeRequest(HttpMethod.GET, url);
		return mapper.readValue(json, GetCharactersResponse.class);
	}

	@Override
	public CharacterEntity getCharacter(long id) throws IOException, URISyntaxException {
		String json = makeRequest(HttpMethod.GET, String.format(ENDPOINT_CHARACTER, id));
		GetCharacterResponse response = mapper.readValue(json, GetCharacterResponse.class);
		return response.getData();
	}

	@Override
	public CharacterEntity createCharacter(CharacterEntity character) throws IOException, URISyntaxException {
		return null;
	}

	@Override
	public CharacterEntity updateCharacter(CharacterEntity character) throws IOException, URISyntaxException {
		return null;
	}

	@Override
	public CharacterEntity deleteCharacter(long id) throws IOException, URISyntaxException {
		return null;
	}

	/**
	 *
	 * @param httpMethod The http method to make
	 * @param endpoint The partial endpoint to use (i.e. /characters)
	 * @return
	 * @throws IOException
	 */
	private String makeRequest(HttpMethod httpMethod, String endpoint) throws IOException, URISyntaxException {
		Preconditions.checkNotNull(httpMethod, "The HTTP request cannot be null");
		Preconditions.checkNotNull(endpoint, "The http endpoint cannot be null");

		HttpUriRequest httpReq = null;
		int expectedCode = -1;
		if (HttpMethod.GET.equals(httpMethod)) {
			httpReq = new HttpGet(new URI(endpoint));
			expectedCode = HttpStatus.SC_OK;
		}

		//Set the headers
		httpReq.setHeader("Authorization", "Bearer " + authToken);
		httpReq.setHeader("Content-Type", "application/json");

		//Make the request
		HttpResponse response = httpClient.execute(httpReq);
		if (response.getStatusLine().getStatusCode() == expectedCode) {
			String json = EntityUtils.toString(response.getEntity());
			return json;
		} else {
			StatusLine status = response.getStatusLine();
			String error = "Unexpected status code ({%d}) and error ({%s})";
			String message = String.format(error, status.getStatusCode(), status.getReasonPhrase());
			throw new IOException(message);
		}
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

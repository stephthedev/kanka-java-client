package com.stephthedev.kankaclient.impl;

import com.google.common.base.Preconditions;
import com.stephthedev.kanka.generated.entities.KankaCharacter;
import com.stephthedev.kanka.generated.entities.KankaEntityNote;
import com.stephthedev.kanka.generated.entities.KankaFamily;
import com.stephthedev.kanka.generated.entities.KankaLocation;
import com.stephthedev.kankaclient.api.KankaClient;
import com.stephthedev.kankaclient.api.entities.EntitiesRequest;
import com.stephthedev.kankaclient.api.entities.EntitiesResponse;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;

/**
 * Implementation of Kanka client
 * @author sortiz
 */
public class KankaClientImpl implements KankaClient {

	private static final String ENDPOINT_CHARACTERS = "/characters";
	private static final String ENDPOINT_CHARACTER = ENDPOINT_CHARACTERS + "/%d";

	private static final String ENDPOINT_LOCATIONS = "/locations";
	private static final String ENDPOINT_LOCATION = ENDPOINT_LOCATIONS + "/%d";

	private static final String ENDPOINT_FAMILIES = "/families";
	private static final String ENDPOINT_FAMILY = ENDPOINT_FAMILIES + "/%d";

	private static final String ENDPOINT_ENTITY_NOTES = "/entities/%d/entity_notes";
	private static final String ENDPOINT_ENTITY_NOTE = ENDPOINT_ENTITY_NOTES + "/%d";

	String authToken;
	String host;
	long campaignId;
	KankaRestClient client;

	private KankaClientImpl(String host, String authToken, long campaignId) {
		this.host = host;
		this.authToken = authToken;
		this.campaignId = campaignId;
		this.client = new KankaRestClient(host, authToken, campaignId);
	}

	@Override
	public EntitiesResponse<KankaCharacter> getCharacters(EntitiesRequest request) throws IOException, URISyntaxException {
		return client.readEntities(ENDPOINT_CHARACTERS, KankaCharacter.class, Optional.empty(), request);
	}

	@Override
	public KankaCharacter getCharacter(long id) throws IOException, URISyntaxException {
		return client.readEntity(ENDPOINT_CHARACTER, KankaCharacter.class, Optional.empty(), id);
	}

	@Override
	public KankaCharacter createCharacter(KankaCharacter character) throws IOException, URISyntaxException {
		return client.createEntity(ENDPOINT_CHARACTERS, KankaCharacter.class, Optional.empty(), character);
	}

	@Override
	public KankaCharacter updateCharacter(KankaCharacter character) throws IOException, URISyntaxException {
		return client.updateEntity(ENDPOINT_CHARACTER, KankaCharacter.class, Optional.empty(), character);
	}

	@Override
	public void deleteCharacter(long id) throws IOException, URISyntaxException {
		client.deleteEntity(ENDPOINT_CHARACTER, Optional.empty(), id);
	}

	@Override
	public EntitiesResponse<KankaLocation> getLocations(EntitiesRequest request) throws IOException, URISyntaxException {
		return client.readEntities(ENDPOINT_LOCATIONS, KankaLocation.class, Optional.empty(), request);
	}

	@Override
	public KankaLocation getLocation(long id) throws IOException, URISyntaxException {
		return client.readEntity(ENDPOINT_LOCATION, KankaLocation.class, Optional.empty(), id);
	}

	@Override
	public KankaLocation createLocation(KankaLocation location) throws IOException, URISyntaxException {
		return client.createEntity(ENDPOINT_LOCATIONS, KankaLocation.class, Optional.empty(), location);
	}

	@Override
	public KankaLocation updateLocation(KankaLocation location) throws IOException, URISyntaxException {
		return client.updateEntity(ENDPOINT_LOCATION, KankaLocation.class, Optional.empty(), location);
	}

	@Override
	public void deleteLocation(long id) throws IOException, URISyntaxException {
		client.deleteEntity(ENDPOINT_LOCATION, Optional.empty(), id);
	}

	@Override
	public EntitiesResponse<KankaFamily> getFamilies(EntitiesRequest request) throws IOException, URISyntaxException {
		return client.readEntities(ENDPOINT_FAMILIES, KankaFamily.class, Optional.empty(), request);
	}

	@Override
	public KankaFamily getFamily(long id) throws IOException, URISyntaxException {
		return client.readEntity(ENDPOINT_FAMILY, KankaFamily.class, Optional.empty(), id);
	}

	@Override
	public KankaFamily createFamily(KankaFamily family) throws IOException, URISyntaxException {
		return client.createEntity(ENDPOINT_FAMILIES, KankaFamily.class, Optional.empty(), family);
	}

	@Override
	public KankaFamily updateFamily(KankaFamily family) throws IOException, URISyntaxException {
		return client.updateEntity(ENDPOINT_FAMILY, KankaFamily.class, Optional.empty(), family);
	}

	@Override
	public void deleteFamily(long id) throws IOException, URISyntaxException {
		client.deleteEntity(ENDPOINT_FAMILY, Optional.empty(), id);
	}

	@Override
	public EntitiesResponse<KankaEntityNote> getEntityNotes(long parentId, EntitiesRequest request) throws IOException, URISyntaxException {
		return client.readEntities(ENDPOINT_ENTITY_NOTES, KankaEntityNote.class, Optional.of(parentId), request);
	}

	@Override
	public KankaEntityNote getEntityNote(long parentId, long noteId) throws IOException, URISyntaxException {
		return client.readEntity(ENDPOINT_ENTITY_NOTE, KankaEntityNote.class, Optional.of(parentId), noteId);
	}

	@Override
	public KankaEntityNote createEntityNote(long parentId, KankaEntityNote note) throws IOException, URISyntaxException {
		return client.createEntity(ENDPOINT_ENTITY_NOTES, KankaEntityNote.class, Optional.of(parentId), note);
	}

	@Override
	public KankaEntityNote updateEntityNote(long parentId, KankaEntityNote note) throws IOException, URISyntaxException {
		return client.updateEntity(ENDPOINT_ENTITY_NOTE, KankaEntityNote.class, Optional.of(parentId), note);
	}

	@Override
	public void deleteEntityNote(long parentId, long noteId) throws IOException, URISyntaxException {
		client.deleteEntity(ENDPOINT_ENTITY_NOTE, Optional.of(parentId), noteId);
	}

	public static class Builder {

		private static final long UNSET = -1L;
		static final String KANKA_VERSION = "1.0";

		String authToken;
		long campaignId = UNSET;
		String kankaVersion = KANKA_VERSION;

		/**
		 * (Optional) Defaults to "1.0"
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
			Preconditions.checkNotNull(authToken, "The authentication token cannot be null");
			Preconditions.checkArgument(campaignId > UNSET, "The campaign id is not set");

			String host = String.format("kanka.io/api/%s/campaigns/%d", kankaVersion, campaignId);
			return new KankaClientImpl(host, authToken, campaignId);
		}
	}
}

package com.stephthedev.kankaclient.api;

import com.stephthedev.kanka.api.client.GetCharactersResponse;
import com.stephthedev.kanka.api.client.GetLocationsResponse;
import com.stephthedev.kanka.api.entities.CharacterEntity;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * REST client for <a href="https://kanka.io/en-US/docs/1.0/">Kanka</a>
 */
public interface KankaClient {

    GetCharactersResponse getCharacters(EntityRequest request) throws IOException, URISyntaxException;

    CharacterEntity getCharacter(long id) throws IOException, URISyntaxException;

    CharacterEntity createCharacter(CharacterEntity character) throws IOException, URISyntaxException;

    CharacterEntity updateCharacter(CharacterEntity character) throws IOException, URISyntaxException;

    void deleteCharacter(long id) throws IOException, URISyntaxException;

    GetLocationsResponse getLocations(EntityRequest request) throws IOException, URISyntaxException;
}

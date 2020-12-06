package com.stephthedev.kankaclient.api;

import com.stephthedev.kanka.generated.api.KankaCharacter;
import com.stephthedev.kanka.generated.api.KankaResponseCharacters;
import com.stephthedev.kanka.generated.api.KankaResponseLocations;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * REST client for <a href="https://kanka.io/en-US/docs/1.0/">Kanka</a>
 */
public interface KankaClient {

    KankaResponseCharacters getCharacters(EntityRequest request) throws IOException, URISyntaxException;

    KankaCharacter getCharacter(long id) throws IOException, URISyntaxException;

    KankaCharacter createCharacter(KankaCharacter character) throws IOException, URISyntaxException;

    KankaCharacter updateCharacter(KankaCharacter character) throws IOException, URISyntaxException;

    void deleteCharacter(long id) throws IOException, URISyntaxException;

    KankaResponseLocations getLocations(EntityRequest request) throws IOException, URISyntaxException;
}

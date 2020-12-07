package com.stephthedev.kankaclient.api;

import com.stephthedev.kanka.generated.api.*;

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
    KankaLocation getLocation(long id) throws IOException, URISyntaxException;
    KankaLocation createLocation(KankaLocation location) throws IOException, URISyntaxException;
    KankaLocation updateLocation(KankaLocation location) throws IOException, URISyntaxException;
    void deleteLocation(long id) throws IOException, URISyntaxException;

    KankaResponseNotes getEntityNotes(long parentId) throws IOException, URISyntaxException;
    KankaEntityNote getEntityNote(long parentId, long noteId) throws IOException, URISyntaxException;
    KankaEntityNote createEntityNote(long parentId, KankaEntityNote note) throws IOException, URISyntaxException;
    KankaEntityNote updateEntityNote(long parentId, KankaEntityNote note) throws IOException, URISyntaxException;
    void deleteEntityNote(long parentId, long noteId) throws IOException, URISyntaxException;
}

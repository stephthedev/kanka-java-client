package com.stephthedev.kankaclient.impl;

import com.stephthedev.kanka.generated.entities.KankaCharacter;
import com.stephthedev.kanka.generated.entities.KankaEntity;
import com.stephthedev.kankaclient.api.entities.EntitiesRequest;
import com.stephthedev.kankaclient.api.entities.EntitiesResponse;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Integration tests that only run if an auth token and campaign id are specified in the environment.
 * Note: This creates/modifies real data in Kanka. Use a dummy campaign.
 */
public class KankaCharacterIT extends BaseIT {

    public void testGetAll() throws Exception {
        EntitiesResponse<KankaCharacter> response = client.getCharacters(new EntitiesRequest.Builder().build());
        assertNotNull(response);
        assertFalse(response.getData().isEmpty());
    }

    @Override
    public void testGet() throws Exception {
        KankaCharacter character = client.getCharacter(310019);
        assertNotNull(character);
        assertEquals("Character1", character.getName());
    }

    @Override
    public void testCreate() throws Exception {
        KankaCharacter character = (KankaCharacter) generateEntity();
        KankaCharacter response = client.createCharacter(character);
        assertNotNull(response);
        assertEquals(character.getName(), response.getName());

        assertNotNull("Character was not successfully created", client.getCharacter(response.getId()));
    }

    @Override
    public void testUpdate() throws Exception {
        KankaCharacter origCharacter = client.getCharacter(310019);
        String origEntry = origCharacter.getEntry();
        String seed = System.currentTimeMillis() + "";
        String updatedEntry = origCharacter.getEntry() + "<br />" + seed;

        //Update character
        origCharacter.setEntry(updatedEntry);
        KankaCharacter response = client.updateCharacter(origCharacter);
        assertNotNull(response);
        assertTrue(response.getEntry().contains(seed));

        //Compare character
        origCharacter = client.getCharacter(response.getId());
        assertTrue(response.getEntry().contains(seed));
    }

    @Override
    public void testDelete() throws Exception {
        KankaCharacter character = (KankaCharacter) generateEntity();
        KankaCharacter createResp = client.createCharacter(character);
        assertNotNull(createResp);

        client.deleteCharacter(createResp.getId());

        try {
           client.getCharacter(createResp.getId());
           fail("No character should exist with that id");
        } catch (IOException e) {
            assertTrue(e.getMessage().contains("404"));
        }
    }

    @Override
    KankaEntity generateEntity() {
        KankaCharacter character = (KankaCharacter) new KankaCharacter.KankaCharacterBuilder<>()
                .withName(testName.getMethodName() + System.nanoTime())
                .withAge("10")
                .withSex("F")
                .withEntry("Line1 \n Line 2")
                .build();
        return character;
    }

}

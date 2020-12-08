package com.stephthedev.kankaclient.impl;

import com.stephthedev.kanka.generated.entities.KankaCharacter;
import com.stephthedev.kankaclient.api.entities.EntitiesResponse;
import com.stephthedev.kankaclient.api.entities.EntitiesRequest;
import com.stephthedev.kankaclient.api.KankaClient;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.Assert.*;

/**
 * Integration tests that only run if an auth token and campaign id are specified in the environment.
 * Note: This creates/modifies real data in Kanka. Use a dummy campaign.
 */
public class KankaClientImplIT {

    KankaClient client;

    @Rule
    public TestName testName = new TestName();

    @Before
    public void setUp() {
        String authToken = System.getenv("auth.token");
        String campaignId = System.getenv("campaign.id");
        Assume.assumeNotNull(authToken);
        Assume.assumeNotNull(campaignId);

        client = new KankaClientImpl.Builder()
                .withAuthToken(authToken)
                .withCampaignId(Integer.parseInt(campaignId))
                .build();
    }

    @Test
    public void testGetAllCharacters() throws IOException, URISyntaxException {
        EntitiesResponse<KankaCharacter> response = client.getCharacters(new EntitiesRequest.Builder().build());
        assertNotNull(response);
        assertFalse(response.getData().isEmpty());
    }

    @Test
    public void testGetCharacter() throws IOException, URISyntaxException {
        KankaCharacter character = client.getCharacter(310019);
        assertNotNull(character);
        assertEquals("Character1", character.getName());
    }

    @Test
    public void testCreateCharacter() throws IOException, URISyntaxException {
        KankaCharacter character = generateCharacter();
        KankaCharacter response = client.createCharacter(character);
        assertNotNull(response);
        assertEquals(character.getName(), response.getName());

        assertNotNull("Character was not successfully created", client.getCharacter(response.getId()));
    }

    @Test
    public void testUpdateCharacter() throws IOException, URISyntaxException {
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

    @Test
    public void testDeleteCharacter() throws IOException, URISyntaxException {
        KankaCharacter character = generateCharacter();
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

    private KankaCharacter generateCharacter() {
        KankaCharacter character = (KankaCharacter) new KankaCharacter.KankaCharacterBuilder<>()
                .withName(testName.getMethodName() + System.nanoTime())
                .withAge("10")
                .withSex("F")
                .withEntry("Line1 \n Line 2")
                .build();
        return character;
    }

}

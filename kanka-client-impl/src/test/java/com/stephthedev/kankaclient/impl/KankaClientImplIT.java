package com.stephthedev.kankaclient.impl;

import com.stephthedev.kanka.api.client.GetCharactersResponse;
import com.stephthedev.kanka.api.entities.CharacterEntity;
import com.stephthedev.kankaclient.api.EntityRequest;
import com.stephthedev.kankaclient.api.KankaClient;
import org.junit.*;
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
        GetCharactersResponse response = client.getCharacters(new EntityRequest.Builder().build());
        assertNotNull(response);
        assertFalse(response.getData().isEmpty());
    }

    @Test
    public void testGetCharacter() throws IOException, URISyntaxException {
        CharacterEntity character = client.getCharacter(310019);
        assertNotNull(character);
        assertEquals("Character1", character.getName());
    }

    @Test
    public void testCreateCharacter() throws IOException, URISyntaxException {
        CharacterEntity character = generateCharacter();
        CharacterEntity response = client.createCharacter(character);
        assertNotNull(response);
        assertEquals(character.getName(), response.getName());

        assertNotNull("Character was not successfully created", client.getCharacter(response.getId()));
    }

    @Test
    public void testUpdateCharacter() throws IOException, URISyntaxException {
        CharacterEntity origCharacter = client.getCharacter(310019);
        String origEntry = origCharacter.getEntry();
        String seed = System.currentTimeMillis() + "";
        String updatedEntry = origCharacter.getEntry() + "<br />" + seed;

        //Update character
        origCharacter.setEntry(updatedEntry);
        CharacterEntity response = client.updateCharacter(origCharacter);
        assertNotNull(response);
        assertTrue(response.getEntry().contains(seed));

        //Compare character
        origCharacter = client.getCharacter(response.getId());
        assertTrue(response.getEntry().contains(seed));
    }

    @Test
    public void testDeleteCharacter() throws IOException, URISyntaxException {
        CharacterEntity character = generateCharacter();
        CharacterEntity createResp = client.createCharacter(character);
        assertNotNull(createResp);

        client.deleteCharacter(createResp.getId());

        try {
           client.getCharacter(createResp.getId());
           fail("No character should exist with that id");
        } catch (IOException e) {
            assertTrue(e.getMessage().contains("404"));
        }
    }

    private CharacterEntity generateCharacter() {
        CharacterEntity character = (CharacterEntity) new CharacterEntity.CharacterEntityBuilder<>()
                .withName(testName.getMethodName() + System.nanoTime())
                .withAge("10")
                .withSex("F")
                .withEntry("Line1 \n Line 2")
                .build();
        return character;
    }

}

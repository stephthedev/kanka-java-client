package com.stephthedev.kankaclient.impl;

import com.stephthedev.kanka.generated.entities.KankaEntity;
import com.stephthedev.kankaclient.api.KankaClient;
import com.stephthedev.kankaclient.api.entities.EntitiesRequest;
import com.stephthedev.kankaclient.api.entities.EntitiesResponse;
import org.junit.*;
import org.junit.rules.TestName;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Base CRUD entity tests that hit the production Kanka API.
 * TODO: These appear fairly inflexible for further testing. Can this be addressed?
 */
public abstract class BaseEntityIT implements KankaCRUDOperations {

    private static final long CAMPAIGN_ID = 46354L;

    KankaClient client;
    private ZonedDateTime startZDT;

    protected KankaEntity knownEntity;


    @Rule
    public TestName testName = new TestName();

    @Before
    public void setUp() {
        String authToken = System.getenv("auth.token");
        Assume.assumeNotNull(authToken);

        client = new KankaClientImpl.Builder()
                .withAuthToken(authToken)
                .withCampaignId(CAMPAIGN_ID)
                .build();

        startZDT = ZonedDateTime.now(TimeZone.getTimeZone("UTC").toZoneId());
    }

    @After
    public void tearDown() {
        deleteTestArtifacts();
    }

    @Test
    public void testGetAllEntities() throws Exception {
        EntitiesResponse<? extends KankaEntity> response = getAllEntities(new EntitiesRequest.Builder().build());
        assertNotNull(response);
        assertFalse(response.getData().isEmpty());
    }

    @Test
    public void testGetSingleEntity() throws Exception {
        KankaEntity entity = getEntity();
        assertNotNull(entity);
        assertEquals("Unexpected entity name", knownEntity.getName(), entity.getName());
    }

    @Test
    public void testCreateEntity() throws Exception {
        KankaEntity entity = generateEntity();
        KankaEntity response = createEntity(entity);
        assertNotNull(response);
        assertEquals(entity.getName(), response.getName());
        assertNotNull(response.getId());
        assertTrue(response.getId() > 0);
    }

    @Test
    public void testUpdateEntity() throws Exception {
        //Retrieve entity
        KankaEntity snapshotEntity = getEntity();
        String seed = System.currentTimeMillis() + "";
        String updatedEntryStr = snapshotEntity.getEntry() + "<br />" + seed;

        //Update character
        snapshotEntity.setEntry(updatedEntryStr);
        KankaEntity response = updateEntity(snapshotEntity);

        //Verify the text matches up
        assertNotNull(response);
        assertTrue(response.getEntry().contains(seed));
    }

    @Test
    public void testDeleteEntity() throws Exception {
        //Create a dummy entity
        KankaEntity entity = generateEntity();
        KankaEntity response = createEntity(entity);
        assertNotNull(response);
        assertNotNull(response.getId());

        //Delete the dummy entity
        deleteEntity(response.getId());

        try {
            //Verify it doesn't exist
            client.getCharacter(response.getId());
            fail("No character should exist with that id");
        } catch (IOException e) {
            assertTrue(e.getMessage().contains("404"));
        }
    }

    private void deleteTestArtifacts() {
        try {
            //Get the artifacts from kanka
            EntitiesResponse<? extends KankaEntity> artifacts =
                    getAllEntities(new EntitiesRequest.Builder().withLastSync(startZDT.toString()).build());

            //Get just the ids
            List<Long> artifactIds = artifacts.getData().stream()
                    .filter(k -> !k.getName().equals(knownEntity.getName()))
                    .map(e -> e.getId())
                    .collect(Collectors.toList());

            //A bulk api would speed this up
            for (Long id : artifactIds) {
                deleteEntity(id);
            }
        } catch (Exception e) {
            //Gobble the exception, it's okay if the artifacts aren't deleted
            e.printStackTrace();
        }
    }

    abstract KankaEntity generateEntity();
}

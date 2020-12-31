package com.stephthedev.kankaclient.impl;

import com.stephthedev.kanka.generated.entities.KankaEntity;
import com.stephthedev.kankaclient.api.KankaClient;
import com.stephthedev.kankaclient.api.entities.EntitiesResponse;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import java.io.IOException;
import java.util.function.Supplier;

import static org.junit.Assert.*;

/**
 * Base CRUD entity tests that hit the production Kanka API.
 * TODO: These appear fairly inflexible for further testing. Can this be addressed?
 */
public abstract class BaseEntityIT {

    KankaClient client;

    protected KankaEntity knownEntity;

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
    public void testGetAllEntities() throws Exception {
        EntitiesResponse<? extends KankaEntity> response = getAllEntitiesSupplier().get();
        assertNotNull(response);
        assertFalse(response.getData().isEmpty());
    }

    @Test
    public void testGetSingleEntity() throws Exception {
        KankaEntity entity = getEntitySupplier().get();
        assertNotNull(entity);
        assertEquals("Unexpected entity name", knownEntity.getName(), entity.getName());
    }

    @Test
    public void testCreateEntity() throws Exception {
        KankaEntity entity = generateEntity();
        KankaEntity response = createEntitySupplier(entity).get();
        assertNotNull(response);
        assertEquals(entity.getName(), response.getName());
        assertNotNull("Entity was not successfully created", response);
        //TODO: Verify other properties
    }

    @Test
    public void testUpdateEntity() throws Exception {
        //Retrieve entity
        KankaEntity snapshotEntity = getEntitySupplier().get();
        String seed = System.currentTimeMillis() + "";
        String updatedEntryStr = snapshotEntity.getEntry() + "<br />" + seed;

        //Update character
        snapshotEntity.setEntry(updatedEntryStr);
        KankaEntity response = updateEntitySupplier(snapshotEntity).get();

        //Verify the text matches up
        assertNotNull(response);
        assertTrue(response.getEntry().contains(seed));
    }

    @Test
    public void testDeleteEntity() throws Exception {
        //Create a dummy entity
        KankaEntity entity = generateEntity();
        KankaEntity response = createEntitySupplier(entity).get();
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

    abstract KankaEntity generateEntity();

    public abstract Supplier<EntitiesResponse<KankaEntity>> getAllEntitiesSupplier() throws Exception;

    public abstract Supplier<? extends KankaEntity> getEntitySupplier() throws Exception;

    public abstract Supplier<? extends KankaEntity> createEntitySupplier(KankaEntity entity) throws Exception;

    public abstract Supplier<? extends KankaEntity> updateEntitySupplier(KankaEntity entity) throws Exception;

    public abstract void deleteEntity(long id) throws Exception;

}

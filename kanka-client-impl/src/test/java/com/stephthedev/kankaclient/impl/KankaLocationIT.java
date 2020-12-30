package com.stephthedev.kankaclient.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stephthedev.kanka.generated.entities.KankaEntity;
import com.stephthedev.kanka.generated.entities.KankaLocation;
import com.stephthedev.kankaclient.api.entities.EntitiesRequest;
import com.stephthedev.kankaclient.api.entities.EntitiesResponse;

import java.io.IOException;

import static org.junit.Assert.*;

public class KankaLocationIT extends BaseIT {

    private static final long KNOWN_ENTITY = 307917;

    @Override
    public void testGetAll() throws Exception {
        EntitiesResponse<KankaLocation> response = client.getLocations(new EntitiesRequest.Builder().build());
        assertNotNull(response);
        assertFalse(response.getData().isEmpty());
    }

    @Override
    public void testGet() throws Exception {
        KankaLocation entity = client.getLocation(KNOWN_ENTITY);
        assertNotNull(entity);
        assertEquals("Location1", entity.getName());
    }

    @Override
    public void testCreate() throws Exception {
        KankaLocation expectedLocation = (KankaLocation) generateEntity();
        KankaLocation actualLocation = client.createLocation(expectedLocation);
        assertNotNull(actualLocation);
        assertEquals(expectedLocation.getName(), actualLocation.getName());
        assertNotNull("Character was not successfully created", client.getLocation(actualLocation.getId()));
    }

    @Override
    public void testUpdate() throws Exception {
        KankaLocation origEntity = client.getLocation(KNOWN_ENTITY);
        String origEntry = origEntity.getEntry();
        String seed = System.currentTimeMillis() + "";
        String updatedEntry = origEntity.getEntry() + "<br />" + seed;

        //Update location
        ObjectMapper mapper = new ObjectMapper();
        origEntity.setEntry(updatedEntry);
        System.out.println(mapper.writeValueAsString(origEntity));
        KankaLocation response = client.updateLocation(origEntity);
        assertNotNull(response);
        assertTrue(response.getEntry().contains(seed));

        //Compare location
        origEntity = client.getLocation(response.getId());
        assertTrue(response.getEntry().contains(seed));
    }

    @Override
    public void testDelete() throws Exception {
        KankaLocation entity = (KankaLocation) generateEntity();
        entity = client.createLocation(entity);
        client.deleteLocation(entity.getId());

        try {
            client.getLocation(entity.getId());
            fail("No character should exist with that id");
        } catch (IOException e) {
            assertTrue(e.getMessage().contains("404"));
        }
    }

    @Override
    KankaEntity generateEntity() {
        KankaLocation entity = (KankaLocation) new KankaLocation.KankaLocationBuilder<>()
                .withName(testName.getMethodName() + System.nanoTime())
                .withType("city")
                .withEntry("Dummy location")
                .build();
        return entity;
    }
}

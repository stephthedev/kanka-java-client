package com.stephthedev.kankaclient.impl;

import com.stephthedev.kanka.generated.entities.KankaEntity;
import com.stephthedev.kanka.generated.entities.KankaLocation;
import com.stephthedev.kankaclient.api.entities.EntitiesRequest;
import com.stephthedev.kankaclient.api.entities.EntitiesResponse;
import org.junit.Before;

public class KankaLocationEntityIT extends BaseEntityIT {

    @Before
    public void setUpKnownEntity() {
        knownEntity = new KankaLocation.KankaLocationBuilder<>()
                .withId(307917L)
                .withName("Location1")
                .build();
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

    @Override
    public EntitiesResponse<KankaLocation> getAllEntities(EntitiesRequest request) throws Exception {
        return client.getLocations(request);
    }

    @Override
    public KankaLocation getEntity() throws Exception {
        return client.getLocation(knownEntity.getId());
    }

    @Override
    public KankaLocation createEntity(KankaEntity entity) throws Exception {
        return client.createLocation((KankaLocation) entity);
    }

    @Override
    public KankaLocation updateEntity(KankaEntity entity) throws Exception {
        return client.updateLocation((KankaLocation) entity);
    }

    @Override
    public void deleteEntity(long id) throws Exception {
        client.deleteLocation(id);
    }
}

package com.stephthedev.kankaclient.impl;

import com.stephthedev.kanka.generated.entities.KankaEntity;
import com.stephthedev.kanka.generated.entities.KankaFamily;
import com.stephthedev.kankaclient.api.entities.EntitiesRequest;
import com.stephthedev.kankaclient.api.entities.EntitiesResponse;
import org.junit.Before;

public class KankaFamilyEntityIT extends BaseEntityIT {

    @Before
    public void setUpKnownEntity() {
        knownEntity = new KankaFamily.KankaFamilyBuilder<>()
                .withId(43506L)
                .withName("Do Not Delete")
                .build();
    }

    @Override
    KankaEntity generateEntity() {
        return new KankaFamily.KankaFamilyBuilder<>()
                .withName(testName.getMethodName() + System.nanoTime())
                .withEntry("Dummy family")
                .build();
    }

    @Override
    public EntitiesResponse<? extends KankaEntity> getAllEntities(EntitiesRequest request) throws Exception {
        return client.getFamilies(request);
    }

    @Override
    public KankaEntity getEntity() throws Exception {
        return client.getFamily(knownEntity.getId());
    }

    @Override
    public KankaEntity createEntity(KankaEntity entity) throws Exception {
        return client.createFamily((KankaFamily) entity);
    }

    @Override
    public KankaEntity updateEntity(KankaEntity entity) throws Exception {
        return client.updateFamily((KankaFamily) entity);
    }

    @Override
    public void deleteEntity(long id) throws Exception {
        client.deleteFamily(id);
    }
}

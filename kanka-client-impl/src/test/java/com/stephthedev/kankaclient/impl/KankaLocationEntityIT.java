package com.stephthedev.kankaclient.impl;

import com.stephthedev.kanka.generated.entities.KankaEntity;
import com.stephthedev.kanka.generated.entities.KankaLocation;
import com.stephthedev.kankaclient.api.entities.EntitiesRequest;
import com.stephthedev.kankaclient.api.entities.EntitiesResponse;
import org.junit.Before;

import java.util.function.Supplier;

public class KankaLocationEntityIT extends BaseEntityIT {

    private static final long KNOWN_ENTITY = 307917;

    @Before
    public void setUpKnownEntity() {
        knownEntity = new KankaLocation.KankaLocationBuilder<>()
                .withId(KNOWN_ENTITY)
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
    public Supplier getAllEntitiesSupplier() throws Exception {
        Supplier<EntitiesResponse<KankaLocation>> s = () -> {
            try {
                return client.getLocations(new EntitiesRequest.Builder().build());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
        return s;
    }

    @Override
    public Supplier<KankaLocation> getEntitySupplier() throws Exception {
        return() -> {
            try {
                return client.getLocation(KNOWN_ENTITY);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    @Override
    public Supplier<KankaLocation> createEntitySupplier(KankaEntity entity) throws Exception {
        return () -> {
            try {
                return client.createLocation((KankaLocation) entity);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    @Override
    public Supplier<KankaLocation> updateEntitySupplier(KankaEntity entity) throws Exception {
        return () -> {
            try {
                return client.updateLocation((KankaLocation) entity);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    @Override
    public void deleteEntity(long id) throws Exception {
        client.deleteLocation(id);
    }
}

package com.stephthedev.kankaclient.impl;

import com.stephthedev.kanka.generated.entities.KankaCharacter;
import com.stephthedev.kanka.generated.entities.KankaEntity;
import com.stephthedev.kankaclient.api.entities.EntitiesRequest;
import com.stephthedev.kankaclient.api.entities.EntitiesResponse;
import org.junit.Before;

import java.util.function.Supplier;

/**
 * Integration tests that only run if an auth token and campaign id are specified in the environment.
 * Note: This creates/modifies real data in Kanka. Use a dummy campaign.
 */
public class KankaCharacterEntityIT extends BaseEntityIT {

    @Before
    public void setUpKnownEntity() {
        knownEntity = new KankaCharacter.KankaCharacterBuilder<>()
                .withId(310019L)
                .withName("Character1")
                .build();
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

    @Override
    public Supplier getAllEntitiesSupplier() throws Exception {
        Supplier<EntitiesResponse<KankaCharacter>> s = () -> {
            try {
                return client.getCharacters(new EntitiesRequest.Builder().build());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
        return s;
    }

    @Override
    public Supplier<KankaCharacter> getEntitySupplier() throws Exception {
        Supplier<KankaCharacter> s = () -> {
            try {
                return client.getCharacter(knownEntity.getId());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
        return s;
    }

    @Override
    public Supplier<KankaCharacter> createEntitySupplier(KankaEntity entity) throws Exception {
        Supplier<KankaCharacter> s = () -> {
            try {
                return client.createCharacter((KankaCharacter) entity);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
        return s;
    }

    @Override
    public Supplier<KankaCharacter> updateEntitySupplier(KankaEntity entity) throws Exception {
        return () -> {
            try {
                return client.updateCharacter((KankaCharacter) entity);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    @Override
    public void deleteEntity(long id) throws Exception {
        client.deleteCharacter(id);
    }

}

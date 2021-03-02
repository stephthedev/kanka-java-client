package com.stephthedev.kankaclient.impl;

import com.stephthedev.kanka.generated.entities.KankaCharacter;
import com.stephthedev.kanka.generated.entities.KankaEntity;
import com.stephthedev.kankaclient.api.entities.EntitiesRequest;
import com.stephthedev.kankaclient.api.entities.EntitiesResponse;
import org.junit.Before;

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
                .withAge("10")
                .withSex("F")
                .withName(testName.getMethodName() + System.nanoTime())
                .withEntry("Line1 \n Line 2")
                .build();
        return character;
    }

    @Override
    public EntitiesResponse<KankaCharacter> getAllEntities(EntitiesRequest request) throws Exception {
        return client.getCharacters(request);
    }

    @Override
    public KankaCharacter getEntity() throws Exception {
        return client.getCharacter(knownEntity.getId());
    }

    @Override
    public KankaCharacter createEntity(KankaEntity entity) throws Exception {
        return client.createCharacter((KankaCharacter) entity);
    }

    @Override
    public KankaCharacter updateEntity(KankaEntity entity) throws Exception {
        return client.updateCharacter((KankaCharacter) entity);
    }

    @Override
    public void deleteEntity(long id) throws Exception {
        client.deleteCharacter(id);
    }
}

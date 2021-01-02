package com.stephthedev.kankaclient.impl;

import com.stephthedev.kanka.generated.entities.KankaEntity;
import com.stephthedev.kanka.generated.entities.KankaEntityNote;
import com.stephthedev.kankaclient.api.entities.EntitiesRequest;
import com.stephthedev.kankaclient.api.entities.EntitiesResponse;
import org.junit.Before;

public class KankaEntityNoteIT extends BaseEntityIT {

    //Kanka character
    private static final long PARENT_ENTITY_ID = 1206199L;

    @Before
    public void setUpKnownEntity() {
        knownEntity = new KankaEntityNote.KankaEntityNoteBuilder<>()
                .withVisibility("admin")
                .withId(51496L)
                .withName("Secrets")
                .withEntityId(PARENT_ENTITY_ID)
                .build();
    }

    @Override
    KankaEntity generateEntity() {
        KankaEntityNote entity = (KankaEntityNote) new KankaEntityNote.KankaEntityNoteBuilder<>()
                .withVisibility("all")
                .withEntityId(PARENT_ENTITY_ID)
                .withName(testName.getMethodName() + System.nanoTime())
                .withEntry("Dummy entity note")
                .build();
        return entity;
    }

    @Override
    public EntitiesResponse<KankaEntityNote> getAllEntities(EntitiesRequest request) throws Exception {
        return client.getEntityNotes(PARENT_ENTITY_ID, request);
    }

    @Override
    public KankaEntityNote getEntity() throws Exception {
        return client.getEntityNote(PARENT_ENTITY_ID, knownEntity.getId());
    }

    @Override
    public KankaEntityNote createEntity(KankaEntity entity) throws Exception {
        return client.createEntityNote(PARENT_ENTITY_ID, (KankaEntityNote) entity);
    }

    @Override
    public KankaEntityNote updateEntity(KankaEntity entity) throws Exception {
        return client.updateEntityNote(PARENT_ENTITY_ID, (KankaEntityNote) entity);
    }

    @Override
    public void deleteEntity(long id) throws Exception {
        client.deleteEntityNote(PARENT_ENTITY_ID, id);
    }
}

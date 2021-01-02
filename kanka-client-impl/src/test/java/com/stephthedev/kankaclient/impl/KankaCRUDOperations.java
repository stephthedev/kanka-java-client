package com.stephthedev.kankaclient.impl;

import com.stephthedev.kanka.generated.entities.KankaEntity;
import com.stephthedev.kankaclient.api.entities.EntitiesRequest;
import com.stephthedev.kankaclient.api.entities.EntitiesResponse;

/**
 * Basic CRUD operations that are common to every entity
 */
public interface KankaCRUDOperations {

    EntitiesResponse<? extends KankaEntity> getAllEntities(EntitiesRequest request) throws Exception;

    KankaEntity getEntity() throws Exception;

    KankaEntity createEntity(KankaEntity entity) throws Exception;

    KankaEntity updateEntity(KankaEntity entity) throws Exception;

    void deleteEntity(long id) throws Exception;
}

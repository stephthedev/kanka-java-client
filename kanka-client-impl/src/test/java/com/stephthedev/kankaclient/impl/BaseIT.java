package com.stephthedev.kankaclient.impl;

import com.stephthedev.kanka.generated.entities.KankaEntity;
import com.stephthedev.kankaclient.api.KankaClient;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public abstract class BaseIT {

    KankaClient client;

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
    public abstract void testGetAll() throws Exception;

    @Test
    public abstract void testGet() throws Exception;

    @Test
    public abstract void testCreate() throws Exception;

    @Test
    public abstract void testUpdate() throws Exception;

    @Test
    public abstract void testDelete() throws Exception;

    abstract KankaEntity generateEntity();
}

package com.stephthedev.kankaclient.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stephthedev.kanka.api.client.GetCharactersResponse;
import com.stephthedev.kanka.api.client.GetLocationsResponse;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class GetLocationsSerializationTest {
    private static final String RESPONSE_FILE = "locations_response.json";

    private static GetLocationsResponse response;

    @BeforeClass
    public static void setUp() throws IOException {
        File file = TestUtil.loadFile(RESPONSE_FILE);

        ObjectMapper mapper = new ObjectMapper();
        response = mapper.readValue(file, GetLocationsResponse.class);
        assertNotNull(response);
    }

    @Test
    public void testParsed() {
        assertEquals(15, response.getData().size());
        assertEquals(38, response.getMeta().getTotal().intValue());
    }
}

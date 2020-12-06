package com.stephthedev.kankaclient.api;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stephthedev.kanka.api.client.GetCharactersResponse;
import com.stephthedev.kanka.api.hateoas.Links;
import com.stephthedev.kanka.api.hateoas.Meta;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class GetCharactersSerializationTest {

    private static final String RESPONSE_FILE = "characters_response.json";

    private static GetCharactersResponse response;

    @BeforeClass
    public static void setUp() throws IOException {
        File file = TestUtil.loadFile(RESPONSE_FILE);

        ObjectMapper mapper = new ObjectMapper();
        response = mapper.readValue(file, GetCharactersResponse.class);
        assertNotNull(response);
    }

    @Test
    public void testMeta() throws IOException {
        assertNotNull(response.getMeta());
        Meta meta = response.getMeta();
        assertEquals(68, meta.getTotal().intValue());
        assertEquals("http://kanka.io/api/1.0/campaigns/13936/characters", meta.getPath());
    }

    @Test
    public void testLinks() throws Exception {
        assertNotNull(response.getLinks());
        Links links = response.getLinks();

        assertNotNull(links.getFirst());
        assertNotNull(links.getNext());
        assertNotNull(links.getLast());
        assertNull(links.getPrevious());
    }
}

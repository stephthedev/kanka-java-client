package com.stephthedev.kankaclient.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stephthedev.kanka.generated.api.KankaLinks;
import com.stephthedev.kanka.generated.api.KankaMeta;
import com.stephthedev.kanka.generated.api.KankaResponseCharacters;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class GetCharactersSerializationTest {

    private static final String RESPONSE_FILE = "characters_response.json";

    private static KankaResponseCharacters response;

    @BeforeClass
    public static void setUp() throws IOException {
        File file = TestUtil.loadFile(RESPONSE_FILE);

        ObjectMapper mapper = new ObjectMapper();
        response = mapper.readValue(file, KankaResponseCharacters.class);
        assertNotNull(response);
    }

    @Test
    public void testMeta() throws IOException {
        assertNotNull(response.getMeta());
        KankaMeta meta = response.getMeta();
        assertEquals(68, meta.getTotal().intValue());
        assertEquals("http://kanka.io/api/1.0/campaigns/13936/characters", meta.getPath());
    }

    @Test
    public void testLinks() throws Exception {
        assertNotNull(response.getLinks());
        KankaLinks links = response.getLinks();

        assertNotNull(links.getFirst());
        assertNotNull(links.getNext());
        assertNotNull(links.getLast());
        assertNull(links.getPrevious());
    }
}

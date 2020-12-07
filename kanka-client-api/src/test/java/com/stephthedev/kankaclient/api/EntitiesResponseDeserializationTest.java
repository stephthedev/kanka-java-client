package com.stephthedev.kankaclient.api;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stephthedev.kanka.generated.entities.KankaCharacter;
import com.stephthedev.kanka.generated.hateoas.KankaLinks;
import com.stephthedev.kanka.generated.hateoas.KankaMeta;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class EntitiesResponseDeserializationTest {

    private static final String RESPONSE_FILE = "characters_response.json";

    private static EntitiesResponse<KankaCharacter> response;

    @BeforeClass
    public static void setUp() throws IOException {
        File file = TestUtil.loadFile(RESPONSE_FILE);
        ObjectMapper mapper = new ObjectMapper();
        JavaType type = mapper.getTypeFactory()
                .constructParametricType(EntitiesResponse.class, KankaCharacter.class);
        response = mapper.readValue(file, type);
        assertNotNull(response);
    }

    @Test
    public void testCharacters() {
        KankaCharacter kankaCharacter = response.getData().get(0);
        assertNotNull(kankaCharacter);
        assertTrue(kankaCharacter instanceof KankaCharacter);
        assertEquals("Gundren Rockseeker", kankaCharacter.getName());
        assertEquals("130", kankaCharacter.getAge());
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

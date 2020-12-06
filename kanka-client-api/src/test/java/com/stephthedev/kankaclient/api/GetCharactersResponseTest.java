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

public class GetCharactersResponseTest {

    private static final String RESPONSE_FILE = "characters_response.json";

    private static GetCharactersResponse responseFirstPage;

    @BeforeClass
    public static void setUp() throws IOException {
        responseFirstPage = loadResponse(RESPONSE_FILE);
    }

    @Test
    public void testMeta() throws IOException {
        assertNotNull(responseFirstPage.getMeta());
        Meta meta = responseFirstPage.getMeta();
        assertEquals(68, meta.getTotal().intValue());
        assertEquals("http://kanka.io/api/1.0/campaigns/13936/characters", meta.getPath());
    }

    @Test
    public void testLinks() throws Exception {
        assertNotNull(responseFirstPage.getLinks());
        Links links = responseFirstPage.getLinks();

        assertNotNull(links.getFirst());
        assertNotNull(links.getNext());
        assertNotNull(links.getLast());
        assertNull(links.getPrevious());
    }

    private static GetCharactersResponse loadResponse(String fileName) throws IOException {
        ClassLoader classLoader = GetCharactersResponseTest.class.getClassLoader();
        File responseFile = new File(classLoader.getResource(fileName).getFile());
        ObjectMapper mapper = new ObjectMapper();
        GetCharactersResponse response = mapper.readValue(responseFile, GetCharactersResponse.class);
        assertNotNull(response);

        return response;
    }
}

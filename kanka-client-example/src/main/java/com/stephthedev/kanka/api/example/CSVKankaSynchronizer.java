package com.stephthedev.kanka.api.example;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.tinylog.Logger;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.stephthedev.kanka.generated.entities.KankaCharacter;
import com.stephthedev.kanka.generated.entities.KankaEntityNote;
import com.stephthedev.kanka.generated.entities.KankaLocation;
import com.stephthedev.kankaclient.api.KankaClient;
import com.stephthedev.kankaclient.api.entities.EntitiesRequest;
import com.stephthedev.kankaclient.api.entities.EntitiesResponse;
import com.stephthedev.kankaclient.impl.KankaClientImpl;

/**
 * Keep a local CSV file in sync with Kanka and vice-versa
 */
public class CSVKankaSynchronizer {

    String absoluteFile;
    KankaClient client;
    private static final String UPDATING_MSG = "Updating %s (id=%d) (name='%s')";
    private static final String CREATING_MSG = "Creating %s (name='%s')";

    CSVKankaSynchronizer(String absoluteFile, String campaignId, String authToken) throws IOException, URISyntaxException {
        this.absoluteFile = absoluteFile;
        this.client = new KankaClientImpl.Builder()
                .withAuthToken(authToken)
                .withCampaignId(Integer.parseInt(campaignId))
                .build();
    }

    private Map<String, KankaCharacter> getAllCharacters() throws IOException, URISyntaxException {
        List<KankaCharacter> characters = new ArrayList<>();

        EntitiesRequest request = new EntitiesRequest.Builder().build();
        for(;;) {
            EntitiesResponse<KankaCharacter> response = client.getCharacters(request);
            characters.addAll(response.getData());

            if (response.getMeta().getCurrentPage() == response.getMeta().getLastPage()) {
                break;
            }

            request = new EntitiesRequest.Builder().withLink(response.getLinks().getNext()).build();
        }

        return characters.stream()
                .collect(Collectors.toMap(KankaCharacter::getName, e -> e));
    }

    private Map<String, KankaLocation> getAllLocations() throws IOException, URISyntaxException {
        List<KankaLocation> locations = new ArrayList<>();

        EntitiesRequest request = new EntitiesRequest.Builder().build();
        for (;;) {
            EntitiesResponse<KankaLocation> response = client.getLocations(request);
            locations.addAll(response.getData());

            if (response.getMeta().getCurrentPage() == response.getMeta().getLastPage()) {
                break;
            }

            request = new EntitiesRequest.Builder().withLink(response.getLinks().getNext()).build();
        }

        return locations.stream()
                .collect(Collectors.toMap(KankaLocation::getName, e -> e));
    }

    private Map<String, CSVCharacter> getCharactersFromCSV() throws IOException {
        // read from file
        MappingIterator<CSVCharacter> csvRowIter = new CsvMapper()
                .readerWithTypedSchemaFor(CSVCharacter.class)
                .readValues(new File(absoluteFile));

        Map<String, CSVCharacter> characters = new HashMap<>();
        int line=0;
        while (csvRowIter.hasNext()) {
        	CSVCharacter character = null;
        	try {
        		character = csvRowIter.next();
        	} catch (Exception e) {
        		if (line != 0) {
        			throw e;
        		}
        		
        	}
            line++;

            //Skip the header and any empty rows
            if ((line == 1) || Strings.isNullOrEmpty(character.getName())) {
                continue;
            }

            characters.put(character.getName(), character);
        }
        
        return characters;
    }

    /**
     * Loads CSV & Kanka content to perform an upsert
     * @throws IOException
     * @throws URISyntaxException
     * @throws InterruptedException
     */
    public void sync() throws IOException, URISyntaxException, InterruptedException {
    	// Load CSV characters
        Map<String, CSVCharacter> csvCharacters = getCharactersFromCSV();
        
        // Load Kanka characters and locations
        Map<String, KankaCharacter> kankaNameCharMap = getAllCharacters();
        Map<String, KankaLocation> kankaNameLocMap = getAllLocations();
        
        // For each csv character, upsert them to kanka
        for (CSVCharacter csvCharacter : csvCharacters.values()) {
        	Logger.info(String.format("Upserting: %s", csvCharacter.getName()));
        	KankaCharacter existingKankaChar = kankaNameCharMap.get(csvCharacter.getName());
            KankaLocation existingKankaLoc = kankaNameLocMap.get(csvCharacter.getLocation());
            upsertCharacter(csvCharacter, existingKankaChar, existingKankaLoc);
            
            Logger.debug("Sleeping 10s to avoid rate limiting");
            Thread.sleep(10000);    //TODO: Find another way to not get rate-limited*/
        }
        
        for (String charName : kankaNameCharMap.keySet()) {
        	if (!csvCharacters.containsKey(charName)) {
        		Logger.debug(String.format("Character '%s' on Kanka but not in CSV", charName));
        	}
        }
    }

    private KankaCharacter upsertCharacter(CSVCharacter csvCharacter, KankaCharacter charFromKanka, KankaLocation kankaLocation) throws IOException, URISyntaxException {
    	KankaCharacter character = (KankaCharacter) new KankaCharacter.KankaCharacterBuilder<>()
    			.withImageUrl(csvCharacter.getImageURL())
                .withAge(csvCharacter.getAge().trim())
                .withSex(csvCharacter.getSex())
                .withLocationId(kankaLocation != null ? kankaLocation.getId() : null)
                .withType("NPC")
                .withName(csvCharacter.getName().trim())
                .withEntry(makeHTMLReady(csvCharacter.getPlayerNotes()))
                .withIsPrivate(csvCharacter.isPrivate())
                .build();

        setPersonalityTraits(character, csvCharacter.getPersonality());
        if (!Strings.isNullOrEmpty(csvCharacter.getImageURL())) {
            character.setImageFull(csvCharacter.getImageURL());
        }

        // Upsert the character
        if (charFromKanka != null) {
        	Logger.info(String.format(UPDATING_MSG, "character", charFromKanka.getId(), charFromKanka.getName()));
            character.setId(charFromKanka.getId());
            charFromKanka = client.updateCharacter(character);
        } else {
        	Logger.info(String.format(CREATING_MSG, "character", csvCharacter.getName()));
            charFromKanka = client.createCharacter(character);
        }
        
        // Upsert the GM notes
        upsertGMCharacterNote(csvCharacter.getGmNotes(),
        		charFromKanka.getEntityId());
        
        return charFromKanka;
    }
    
    private KankaEntityNote upsertGMCharacterNote(String text, long parentId) throws IOException, URISyntaxException {
        final String noteName = "GM Notes";
        KankaEntityNote note = (KankaEntityNote) new KankaEntityNote.KankaEntityNoteBuilder<>()
                .withVisibility(KankaEntityNote.Visibility.ADMIN)
                .withEntry(makeHTMLReady(text))
                .withEntityId(parentId)
                .withName(noteName)
                .build();

        EntitiesResponse<KankaEntityNote> response = client.getEntityNotes(parentId, new EntitiesRequest.Builder().build());
        Optional<KankaEntityNote> opt = response.getData().stream()
                .filter(e -> noteName.equalsIgnoreCase(e.getName()))
                .findFirst();
        if (opt.isPresent()) {
            KankaEntityNote kankaNote = opt.get();
            note.setId(kankaNote.getId());

            Logger.info(String.format(UPDATING_MSG, "entity-note", note.getId(), note.getName()));
            note = client.updateEntityNote(parentId, note);
        } else {
        	Logger.info(String.format(CREATING_MSG, "entity-note", note.getName()));
            note = client.createEntityNote(parentId, note);
        }
        return note;
    }
    
    private String makeHTMLReady(String text) {
    	if (text.contains("•")) {
    		StringBuilder builder = new StringBuilder();
    		String[] lines = text.split("\n");
    		builder.append("<ul>");
    		for (String line : lines) {
    			builder.append("<li>");
    			builder.append(line.replaceAll("•", ""));
    			builder.append("</li>");
    		}
    		builder.append("</ul>");
    		return builder.toString();
    	}
    	return text.replaceAll("\n", "<br />");
    }

    /**
     * Sets personality traits in the form of:
     * <pre>
     * 	Bond: Some bond
     *  Ideal: Some ideal
     *  Flaw: Some flaw
     * </pre>
     * @param kankaCharacter
     * @param personality
     */
    private void setPersonalityTraits(KankaCharacter kankaCharacter, String personality) {
        if (personality != null) {
            String[] lines = personality.split("\n");
            Map<String, String> personalityMap = new HashMap<>();
            for (String line : lines) {
                int index = line.indexOf(":");
                if (index > -1) {
                    String trait = line.substring(0, index);
                    String value = line.substring(index + 1);
                    personalityMap.put(trait.trim(), value.trim());
                }
            }

            kankaCharacter.setPersonalityName(new ArrayList<>(personalityMap.keySet()));
            kankaCharacter.setPersonalityEntry(new ArrayList<>(personalityMap.values()));
        }
    }
    
    private static String getArgument(String propertyName, String arg) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(arg), String.format("The %s property cannot be null", propertyName));
        String[] nameValueArray = arg.split("=");
        Preconditions.checkArgument(nameValueArray.length == 2, "Could not find value for " + propertyName);
        return nameValueArray[1].trim();
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        Preconditions.checkArgument(args != null && args.length > 0, "The arguments cannot be null or empty");

        String csvFile = getArgument("csv.file", args[0]);
        String campaignId = getArgument("campaign.id", args[1]);
        String authToken = getArgument("auth.token", args[2]);

        try {
            CSVKankaSynchronizer synchronizer = new CSVKankaSynchronizer(csvFile, campaignId, authToken);
            synchronizer.sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

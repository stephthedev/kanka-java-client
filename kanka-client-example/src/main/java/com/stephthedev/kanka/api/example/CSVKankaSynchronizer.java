package com.stephthedev.kanka.api.example;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.stephthedev.kanka.generated.entities.KankaCharacter;
import com.stephthedev.kanka.generated.entities.KankaEntityNote;
import com.stephthedev.kanka.generated.entities.KankaLocation;
import com.stephthedev.kankaclient.api.entities.EntitiesResponse;
import com.stephthedev.kankaclient.api.entities.EntitiesRequest;
import com.stephthedev.kankaclient.api.KankaClient;
import com.stephthedev.kankaclient.impl.KankaClientImpl;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Keep a local CSV file in sync with Kanka and vice-versa
 */
public class CSVKankaSynchronizer {

    String absoluteFile;
    KankaClient client;
    private static final String UPDATING_MSG = "Updating %s (id=%d) (name='%s')";
    private static final String CREATING_MSG = "Creating %s (name='%s')";

    Map<String, KankaCharacter> kankaCharacters;
    Map<String, KankaLocation> kankaLocations;

    CSVKankaSynchronizer(String absoluteFile, String campaignId, String authToken) throws IOException, URISyntaxException {
        this.absoluteFile = absoluteFile;
        this.client = new KankaClientImpl.Builder()
                .withAuthToken(authToken)
                .withCampaignId(Integer.parseInt(campaignId))
                .build();

        kankaCharacters = getAllCharacters();
        kankaLocations = getAllLocations();
    }

    Map<String, KankaCharacter> getAllCharacters() throws IOException, URISyntaxException {
        List<KankaCharacter> characters = new ArrayList<>();
        EntitiesResponse<KankaCharacter> response = client.getCharacters(new EntitiesRequest.Builder().build());
        characters.addAll(response.getData());

        while (response.getLinks().getNext() != null) {
            EntitiesRequest request = new EntitiesRequest.Builder()
                    .withLink(response.getLinks().getNext())
                    .build();
            response = client.getCharacters(request);
            characters.addAll(response.getData());
        }

        return characters.stream()
                .collect(Collectors.toMap(KankaCharacter::getName, e -> e));
    }

    Map<String, KankaLocation> getAllLocations() throws IOException, URISyntaxException {
        List<KankaLocation> locations = new ArrayList<>();
        EntitiesResponse<KankaLocation> response = client.getLocations(new EntitiesRequest.Builder().build());
        locations.addAll(response.getData());

        while (response.getLinks().getNext() != null) {
            EntitiesRequest request = new EntitiesRequest.Builder()
                    .withLink(response.getLinks().getNext())
                    .build();
            response = client.getLocations(request);
            locations.addAll(response.getData());
        }
        return locations.stream()
                .collect(Collectors.toMap(KankaLocation::getName, e -> e));
    }

    List<CSVCharacter> getCharactersFromCSV() throws IOException {
        CsvMapper mapper = new CsvMapper();

        // Schema from POJO (usually has @JsonPropertyOrder annotation)
        CsvSchema schema = CsvSchema.builder()
            .addColumn("Name")
            .addColumn("Sex")
            .addColumn("Race")
            .addColumn("Age")
            .addColumn("Location")
            .addColumn("Sublocation")
            .addColumn("Affiliation")
            .addColumn("Page Number")
            .addColumn("Personality")
            .addColumn("Player Notes")
            .addColumn("GM Notes")
            .addColumn("Image URL")
            .build();

        // configure the reader on what bean to read and how we want to write
        // that bean
        ObjectReader oReader = mapper
                .readerWithTypedSchemaFor(CSVCharacter.class);

        // read from file
        MappingIterator<CSVCharacter> csvRowIter = new CsvMapper()
                .readerWithTypedSchemaFor(CSVCharacter.class)
                .readValues(new File(absoluteFile));

        List<CSVCharacter> characters = new ArrayList<>();
        int line=0;
        while (csvRowIter.hasNext()) {
            CSVCharacter character = csvRowIter.next();
            line++;

            //Skip the header and any empty rows
            if ((line == 1) || Strings.isNullOrEmpty(character.getName())) {
                continue;
            }

            characters.add(character);
        }
        return characters;
    }

    public void sync() throws IOException, URISyntaxException {
        List<CSVCharacter> csvCharacters = getCharactersFromCSV();

        for (CSVCharacter csvCharacter : csvCharacters) {
            System.out.println("Upserting " + csvCharacter.getName());
            KankaCharacter kankaCharacter = upsertCharacter(csvCharacter);
            KankaEntityNote kankaEntityNote = upsertCharacterNote(csvCharacter.getGmNotes(),
                    kankaCharacter.getEntityId());
        }
    }

    private KankaEntityNote upsertCharacterNote(String text, long parentId) throws IOException, URISyntaxException {
        final String noteName = "GM Notes";
        KankaEntityNote note = (KankaEntityNote) new KankaEntityNote.KankaEntityNoteBuilder<>()
                .withVisibility("admin")
                .withEntry(text.replaceAll("\n", "<br />"))
                .withEntityId(parentId)
                .withName(noteName)
                .build();

        EntitiesResponse<KankaEntityNote> response = client.getEntityNotes(parentId);
        Optional<KankaEntityNote> opt = response.getData().stream()
                .filter(e -> noteName.equalsIgnoreCase(e.getName()))
                .findFirst();
        if (opt.isPresent()) {
            KankaEntityNote kankaNote = opt.get();
            note.setId(kankaNote.getId());

            System.out.println(String.format(UPDATING_MSG, "entity-note", note.getId(), note.getName()));
            note = client.updateEntityNote(parentId, note);
        } else {
            System.out.println(String.format(CREATING_MSG, "entity-note", note.getName()));
            note = client.createEntityNote(parentId, note);
        }
        return note;
    }

    private KankaCharacter upsertCharacter(CSVCharacter csvCharacter) throws IOException, URISyntaxException {
        KankaCharacter character = (KankaCharacter) new KankaCharacter.KankaCharacterBuilder<>()
                .withName(csvCharacter.getName().trim())
                .withAge(csvCharacter.getAge().trim())
                .withSex(csvCharacter.getSex())
                .withLocationId(kankaLocations.containsKey(csvCharacter.getLocation()) ?
                        kankaLocations.get(csvCharacter.getLocation()).getId() : null)
                .withType("NPC")
                .withEntry(csvCharacter.getPlayerNotes())
                .build();

        setPersonalityTraits(character, csvCharacter.getPersonality());
        if (!Strings.isNullOrEmpty(csvCharacter.getImageURL())) {
            character.setImageFull(csvCharacter.getImageURL());
        }

        KankaCharacter charFromKanka = kankaCharacters.get(character.getName());
        if (charFromKanka != null) {
            System.out.println(String.format(UPDATING_MSG, "character", charFromKanka.getId(), charFromKanka.getName()));
            character.setId(charFromKanka.getId());
            charFromKanka = client.updateCharacter(character);
        } else {
            System.out.println(String.format(CREATING_MSG, "character", charFromKanka.getName()));
            charFromKanka = client.createCharacter(character);
        }
        return charFromKanka;
    }

    private void setPersonalityTraits(KankaCharacter kankaCharacter, String personality) {
        if (personality != null) {
            String[] lines = personality.split("\n");
            Map<String, String> personalityMap = new HashMap<>();
            for (String line : lines) {
                int index = line.indexOf(":");
                String trait = line.substring(0, index);
                String value = line.substring(index + 1);
                personalityMap.put(trait.trim(), value.trim());
            }

            kankaCharacter.setPersonalityName(new ArrayList<>(personalityMap.keySet()));
            kankaCharacter.setPersonalityEntry(new ArrayList<>(personalityMap.values()));
        }
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

    private static String getArgument(String propertyName, String arg) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(arg), String.format("The %s property cannot be null", propertyName));
        String[] nameValueArray = arg.split("=");
        Preconditions.checkArgument(nameValueArray.length == 2, "Could not find value for " + propertyName);
        return nameValueArray[1].trim();
    }
}

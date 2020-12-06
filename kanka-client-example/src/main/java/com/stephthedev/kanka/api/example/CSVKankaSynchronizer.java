package com.stephthedev.kanka.api.example;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.stephthedev.kanka.generated.api.KankaCharacter;
import com.stephthedev.kanka.generated.api.KankaLocation;
import com.stephthedev.kanka.generated.api.KankaResponseCharacters;
import com.stephthedev.kanka.generated.api.KankaResponseLocations;
import com.stephthedev.kankaclient.api.EntityRequest;
import com.stephthedev.kankaclient.api.KankaClient;
import com.stephthedev.kankaclient.impl.KankaClientImpl;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Keep a local CSV file in sync with Kanka and vice-versa
 */
public class CSVKankaSynchronizer {

    String absoluteFile;
    KankaClient client;

    CSVKankaSynchronizer(String absoluteFile, String campaignId, String authToken) {
        this.absoluteFile = absoluteFile;
        this.client = new KankaClientImpl.Builder()
                .withAuthToken(authToken)
                .withCampaignId(Integer.parseInt(campaignId))
                .build();
    }

    List<KankaCharacter> getAllCharacters() throws IOException, URISyntaxException {
        List<KankaCharacter> characters = new ArrayList<>();
        KankaResponseCharacters response = client.getCharacters(new EntityRequest.Builder().build());
        characters.addAll(response.getData());

        while (response.getLinks().getNext() != null) {
            EntityRequest request = new EntityRequest.Builder()
                    .withLink(response.getLinks().getNext())
                    .build();
            response = client.getCharacters(request);
            characters.addAll(response.getData());
        }
        return characters;
    }

    List<KankaLocation> getAllLocations() throws IOException, URISyntaxException {
        List<KankaLocation> locations = new ArrayList<>();
        KankaResponseLocations response = client.getLocations(new EntityRequest.Builder().build());
        locations.addAll(response.getData());

        while (response.getLinks().getNext() != null) {
            EntityRequest request = new EntityRequest.Builder()
                    .withLink(response.getLinks().getNext())
                    .build();
            response = client.getLocations(request);
            locations.addAll(response.getData());
        }
        return locations;
    }

    void sync() throws IOException, URISyntaxException {
        List<KankaCharacter> characters = getAllCharacters();
        List<KankaLocation> locations = getAllLocations();
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        Preconditions.checkArgument(args != null && args.length > 0, "The arguments cannot be null or empty");

        String csvFile = getArgument("csv.file", args[0]);
        String campaignId = getArgument("campaign.id", args[1]);
        String authToken = getArgument("auth.token", args[2]);

        try {
            CSVKankaSynchronizer sync = new CSVKankaSynchronizer(csvFile, campaignId, authToken);
            List<KankaCharacter> characters = sync.getAllCharacters();
            List<KankaLocation> locations = sync.getAllLocations();
            System.out.println(locations.size());
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

package com.stephthedev.kanka.api.example;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.stephthedev.kanka.api.client.GetCharactersResponse;
import com.stephthedev.kanka.api.entities.CharacterEntity;
import com.stephthedev.kankaclient.KankaClientImpl;
import com.stephthedev.kankaclient.api.EntityRequest;
import com.stephthedev.kankaclient.api.KankaClient;

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

    List<CharacterEntity> getAllCharacters() throws IOException, URISyntaxException {
        List<CharacterEntity> characters = new ArrayList<>();
        GetCharactersResponse response = client.getCharacters(new EntityRequest.Builder().build());
        while (response.getLinks().getNext() != null) {
            characters.addAll(response.getData());

            EntityRequest request = new EntityRequest.Builder()
                    .withLink(response.getLinks().getNext())
                    .build();
            response = client.getCharacters(request);
        }
        characters.addAll(response.getData());
        return characters;
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        Preconditions.checkArgument(args != null && args.length > 0, "The arguments cannot be null or empty");

        String csvFile = getArgument("csv.file", args[0]);
        String campaignId = getArgument("campaign.id", args[1]);
        String authToken = getArgument("auth.token", args[2]);

        try {
            CSVKankaSynchronizer sync = new CSVKankaSynchronizer(csvFile, campaignId, authToken);
            sync.getAllCharacters();
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

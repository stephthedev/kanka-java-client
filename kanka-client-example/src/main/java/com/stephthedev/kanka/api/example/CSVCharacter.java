package com.stephthedev.kanka.api.example;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
    "name",
    "sex",
    "race",
    "age",
    "location",
    "sublocation",
    "affiliation",
    "pageNumber",
    "personality",
    "playerNotes",
    "gmNotes",
    "imageURL",
    "isPrivate"
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class CSVCharacter {

    String name;
    String sex;
    String race;
    String age;
    String location;
    String sublocation;
    String affiliation;
    String pageNumber;
    String personality;
    String playerNotes;
    String gmNotes;
    String imageURL;
    
    @JsonProperty("is_private")
    boolean isPrivate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSublocation() {
        return sublocation;
    }

    public void setSublocation(String sublocation) {
        this.sublocation = sublocation;
    }

    public String getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }

    public String getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(String pageNumber) {
        this.pageNumber = pageNumber;
    }

    public String getPersonality() {
        return personality;
    }

    public void setPersonality(String personality) {
        this.personality = personality;
    }

    public String getPlayerNotes() {
        return playerNotes;
    }

    public void setPlayerNotes(String playerNotes) {
        this.playerNotes = playerNotes;
    }

    public String getGmNotes() {
        return gmNotes;
    }

    public void setGmNotes(String gmNotes) {
        this.gmNotes = gmNotes;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    @JsonProperty("is_private")
    public boolean isPrivate() {
		return isPrivate;
	}

    @JsonProperty("is_private")
	public void setPrivate(boolean isPrivate) {
		this.isPrivate = isPrivate;
	}

	@Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Name: " + name);
        return builder.toString();
    }
}

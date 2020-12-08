package com.stephthedev.kankaclient.api.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "current_page",
        "from",
        "last_page",
        "path",
        "per_page",
        "to",
        "total"
})
public class Meta {

    @JsonProperty("current_page")
    private int currentPage;

    @JsonProperty("from")
    private int from;

    @JsonProperty("last_page")
    private int lastPage;

    @JsonProperty("path")
    private String path;

    @JsonProperty("per_page")
    private int perPage;

    @JsonProperty("to")
    private int to;

    @JsonProperty("total")
    private int total;

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(Integer from) {
        this.from = from;
    }

    public int getLastPage() {
        return lastPage;
    }

    public void setLastPage(Integer lastPage) {
        this.lastPage = lastPage;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getPerPage() {
        return perPage;
    }

    public void setPerPage(Integer perPage) {
        this.perPage = perPage;
    }

    public int getTo() {
        return to;
    }

    public void setTo(Integer to) {
        this.to = to;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}

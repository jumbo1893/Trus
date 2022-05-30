package com.jumbo.trus.pkfl;

public class PkflSeason {

    private String url;
    private String name;

    public PkflSeason(String url, String name) {
        this.url = url;
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "PkflSeason{" +
                "url='" + url + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}

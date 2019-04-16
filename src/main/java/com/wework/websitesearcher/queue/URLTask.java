package com.wework.websitesearcher.queue;

public class URLTask implements Task {
    public String getUrl() {
        return url;
    }
    public String getId() {
        return Id;
    }
    private final String url;
    private final String Id;

    public URLTask(String url, String Id){
        this.url = url;
        this.Id = Id;
    }


}

package org.example.models;

import java.time.LocalDateTime; // Import the LocalDateTime class


public class ConfigDTO {
    private String dc;
    private String fromDateTime_UNIX;
    private String toDateTime_UNIX;

    public String getDc() {
        return dc;
    }

    public void setDc(String dc) {
        this.dc = dc;
    }
    public String getFromDateTime() {
        return fromDateTime_UNIX;
    }
    public void setFromDateTime(String fromDateTime) {
        this.fromDateTime_UNIX = fromDateTime;
    }
    public String getToDateTime() {
        return toDateTime_UNIX;
    }
    public void setToDateTime(String toDateTime) {
        this.toDateTime_UNIX = toDateTime;
    }
    @Override
    public String toString() {
        return "dc=" + dc + ", fromDateTime=" + fromDateTime_UNIX + ", toDateTime=" + toDateTime_UNIX;
    }
}

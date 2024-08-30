package org.example;

public class Blog {
    private String title;
    private String body;
    private String author;
    @Override
    public String toString() {
        return "title: " + title + "\n" +
                "body: " + body + "\n" +
                "author: " + author + "\n";
    }
}

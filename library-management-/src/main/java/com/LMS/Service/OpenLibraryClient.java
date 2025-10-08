package com.LMS.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class OpenLibraryClient {

    private final RestTemplate restTemplate = new RestTemplate();

    public AuthorData fetchAuthorByIsbn(String isbn) {
        try {
            String url = "https://openlibrary.org/api/books?bibkeys=ISBN:" + isbn + "&format=json&jscmd=data";
            JsonNode root = restTemplate.getForObject(url, JsonNode.class);

            JsonNode bookNode = root.path("ISBN:" + isbn);
            if (bookNode.isMissingNode()) return null;

            JsonNode authorsNode = bookNode.path("authors");
            if (!authorsNode.isArray() || authorsNode.size() == 0) return null;

            JsonNode firstAuthor = authorsNode.get(0);
            String name = firstAuthor.path("name").asText();
            String key = firstAuthor.path("key").asText(); // e.g., "/authors/OL149084A"

            return new AuthorData(name, "https://openlibrary.org" + key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static class AuthorData {
        private final String name;
        private final String url;

        public AuthorData(String name, String url) {
            this.name = name;
            this.url = url;
        }

        public String getName() { return name; }
        public String getUrl() { return url; }
    }
}

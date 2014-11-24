package imag.quizz.common;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Config {

    private static final URL configUrl = Config.class.getResource("/config.json");

    private final Map<Integer, String> servers;

    public Config() {
        this.servers = new HashMap<>();
        try {
            final Path configFilePath = Paths.get(Config.configUrl.toURI());
            final String configString = new String(Files.readAllBytes(configFilePath), StandardCharsets.UTF_8);
            this.parseJsonConfig(configString);
        } catch (final IOException | URISyntaxException e1) {
            // TODO Error reading file
            e1.printStackTrace();
        } catch (final IllegalArgumentException e2) {
            // TODO Malfomed file
            e2.printStackTrace();
        }
    }

    private void parseJsonConfig(final String json) throws IllegalArgumentException {
        try {
            final JSONObject jsonRoot = (JSONObject) new JSONParser().parse(json);
            final JSONArray serversArray = (JSONArray) jsonRoot.get("servers");
            for (final Object aServersArray : serversArray) {
                final JSONObject serverObject = (JSONObject) aServersArray;
                final int serverId = Integer.parseInt((String) serverObject.get("id"));
                final String serverHost = (String) serverObject.get("host");
                final String serverPort = (String) serverObject.get("port");
                this.servers.put(serverId, serverHost + ':' + serverPort);
            }
        } catch (final ParseException | ClassCastException | NullPointerException | NumberFormatException e) {
            throw new IllegalArgumentException("Malformed configuration file", e);
        }
    }

    public Map<Integer, String> getServers() {
        return this.servers;
    }
}

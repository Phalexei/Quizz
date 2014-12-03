package imag.quizz.common;

import imag.quizz.common.tool.Log;
import org.apache.log4j.Level;
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
import java.util.Map.Entry;

public class Config {

    public final class ServerInfo {
        private final int    id;
        private final String host;
        private final int    serverPort;
        private final int playerPort;

        public ServerInfo(final int id, final String host, final int serverPort, final int playerPort) {
            this.id = id;
            this.host = host;
            this.serverPort = serverPort;
            this.playerPort = playerPort;
        }

        public int getId() {
            return this.id;
        }

        public String getHost() {
            return this.host;
        }

        public int getServerPort() {
            return this.serverPort;
        }

        public int getPlayerPort() {
            return this.playerPort;
        }
    }

    private static final URL configUrl = Config.class.getResource("/config.json");

    private final Map<Integer, ServerInfo> servers;

    public Config() throws IOException {
        this.servers = new HashMap<>();
        try {
            final Path configFilePath = Paths.get(Config.configUrl.toURI());
            final String configString = new String(Files.readAllBytes(configFilePath), StandardCharsets.UTF_8);
            this.parseJsonConfig(configString);
        } catch (final IOException | URISyntaxException | IllegalArgumentException e1) {
            throw new IOException("Error while reading configuration file", e1);
        }
        if (Log.isEnabledFor(Level.DEBUG)) {
            Log.debug("Configuration file parsed. " + this.servers.size() + " servers found:");
            for (final Entry<Integer, ServerInfo> e : this.servers.entrySet()) {
                final ServerInfo info = e.getValue();
                Log.debug("Server n°" + e.getKey() + " : " + info.getHost() + " - " + info.getServerPort() + "/" + info.getPlayerPort());
            }
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
                final int serverPort = Integer.parseInt((String) serverObject.get("serverPort"));
                final int playerPort = Integer.parseInt((String) serverObject.get("playerPort"));
                this.servers.put(serverId, new ServerInfo(serverId, serverHost, serverPort, playerPort));
            }
        } catch (final ParseException | ClassCastException | NullPointerException | NumberFormatException e) {
            throw new IllegalArgumentException("Malformed configuration file", e);
        }
    }

    public Map<Integer, ServerInfo> getServers() {
        return this.servers;
    }
}

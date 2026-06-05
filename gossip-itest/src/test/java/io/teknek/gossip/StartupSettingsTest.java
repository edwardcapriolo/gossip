package io.teknek.gossip;

import io.teknek.gossip.manager.GossipManager;
import io.teknek.gossip.manager.GossipManagerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

/**
 * Tests support of using {@code StartupSettings} and thereby reading
 * setup config from file.
 */
public class StartupSettingsTest {
  private static final Logger log = LoggerFactory.getLogger(StartupSettingsTest.class);
  private static final String CLUSTER = UUID.randomUUID().toString();

  @Test
  public void testUsingSettingsFile() throws IOException, InterruptedException, URISyntaxException {
    File settingsFile = File.createTempFile("gossipTest",".json");
    settingsFile.deleteOnExit();
    writeSettingsFile(settingsFile);
    URI uri = new URI("udp://" + "127.0.0.1" + ":" + 50000);
    GossipSettings firstGossipSettings = new GossipSettings();
    firstGossipSettings.setTransportManagerClass("io.teknek.gossip.transport.UnitTestTransportManager");
    firstGossipSettings.setProtocolManagerClass("io.teknek.gossip.protocol.UnitTestProtocolManager");
    GossipManager firstService = GossipManagerBuilder.newBuilder()
            .cluster(CLUSTER)
            .uri(uri)
            .id("1")
            .gossipSettings(firstGossipSettings).build();
    firstService.init();
    GossipManager manager = GossipManagerBuilder.newBuilder()
            .startupSettings(StartupSettings.fromJSONFile(settingsFile)).build();
    manager.init();
    firstService.shutdown();
    manager.shutdown();
  }

  private void writeSettingsFile( File target ) throws IOException {
    String settings =
            "[{\n" + // It is odd that this is meant to be in an array, but oh well.
            "  \"cluster\":\"" + CLUSTER + "\",\n" +
            "  \"id\":\"" + "2" + "\",\n" +
            "  \"uri\":\"udp://127.0.0.1:50001\",\n" +
            "  \"gossip_interval\":1000,\n" +
            "  \"window_size\":1000,\n" +
            "  \"minimum_samples\":5,\n" +
            "  \"cleanup_interval\":10000,\n" +
            "  \"convict_threshold\":2.6,\n" +
            "  \"distribution\":\"exponential\",\n" +
            "  \"transport_manager_class\":\"io.teknek.gossip.transport.UnitTestTransportManager\",\n" +
            "  \"protocol_manager_class\":\"io.teknek.gossip.protocol.UnitTestProtocolManager\",\n" +
            "  \"properties\":{},\n" +
            "  \"members\":[\n" +
            "    {\"cluster\": \"" + CLUSTER + "\",\"uri\":\"udp://127.0.0.1:5000\"}\n" +
            "  ]\n" +
            "}]";

    log.info("Using settings file with contents of:\n---\n{}\n---", settings);
    FileOutputStream output = new FileOutputStream(target);
    output.write(settings.getBytes());
    output.close();
  }
}

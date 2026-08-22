package io.teknek.gossip.spring;

import com.codahale.metrics.MetricRegistry;
import io.teknek.gossip.StartupSettings;
import io.teknek.gossip.manager.GossipManager;
import io.teknek.gossip.manager.GossipManagerBuilder;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.nio.file.Files;

@AutoConfiguration
@EnableConfigurationProperties(GossipSpringProperties.class)
@ConditionalOnProperty(prefix = "gossip", name = "settings-location")
public class GossipAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public StartupSettings gossipStartupSettings(GossipSpringProperties properties, ResourceLoader resourceLoader) {
    Resource resource = resource(properties.getSettingsLocation(), resourceLoader);
    try {
      StartupSettings settings = StartupSettings.fromJSONFile(file(resource));
      settings.getGossipSettings().setTransportManagerClass(HttpTransportManager.class.getName());
      return settings;
    } catch (IOException e) {
      throw new UncheckedIOException("Unable to read gossip settings " + properties.getSettingsLocation(), e);
    } catch (URISyntaxException e) {
      throw new IllegalArgumentException("Invalid URI in gossip settings " + properties.getSettingsLocation(), e);
    }
  }

  @Bean(initMethod = "init", destroyMethod = "shutdown")
  @ConditionalOnMissingBean
  public GossipManager gossipManager(StartupSettings startupSettings, ObjectProvider<MetricRegistry> registry) {
    return GossipManagerBuilder.newBuilder()
            .startupSettings(startupSettings)
            .registry(registry.getIfAvailable(MetricRegistry::new))
            .build();
  }

  @Bean
  @ConditionalOnMissingBean
  public GossipMessageController gossipMessageController(GossipManager gossipManager) {
    return new GossipMessageController(gossipManager);
  }

  @Bean
  @ConditionalOnMissingBean
  public GossipStatusController gossipStatusController(GossipManager gossipManager) {
    return new GossipStatusController(gossipManager);
  }

  @Bean
  public ApplicationListener<ApplicationReadyEvent> gossipHttpTransportStarter(GossipManager gossipManager) {
    return event -> {
      if (gossipManager.getTransportManager() instanceof HttpTransportManager httpTransportManager) {
        httpTransportManager.startAfterSpringReady();
      }
    };
  }

  private Resource resource(String location, ResourceLoader resourceLoader) {
    if (location.contains(":")) {
      return resourceLoader.getResource(location);
    }
    return new FileSystemResource(location);
  }

  private File file(Resource resource) throws IOException {
    if (!resource.exists()) {
      throw new IOException("Gossip settings resource does not exist: " + resource);
    }
    if (resource.isFile()) {
      return resource.getFile();
    }
    File temp = Files.createTempFile("gossip-settings", ".json").toFile();
    try (var input = resource.getInputStream()) {
      Files.copy(input, temp.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
    }
    temp.deleteOnExit();
    return temp;
  }
}

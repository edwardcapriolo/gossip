package io.teknek.gossip.spring;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("gossip")
public class GossipSpringProperties {

  private String settingsLocation;

  public String getSettingsLocation() {
    return settingsLocation;
  }

  public void setSettingsLocation(String settingsLocation) {
    this.settingsLocation = settingsLocation;
  }
}

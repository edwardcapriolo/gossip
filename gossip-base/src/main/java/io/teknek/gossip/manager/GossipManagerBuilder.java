package io.teknek.gossip.manager;

import com.codahale.metrics.MetricRegistry;
import io.teknek.gossip.GossipSettings;
import io.teknek.gossip.Member;
import io.teknek.gossip.StartupSettings;
import io.teknek.gossip.event.GossipListener;
import io.teknek.gossip.event.GossipState;
import io.teknek.gossip.manager.handlers.MessageHandler;
import io.teknek.gossip.manager.handlers.MessageHandlerFactory;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GossipManagerBuilder {

  public static ManagerBuilder newBuilder() {
    return new ManagerBuilder();
  }

  public static final class ManagerBuilder {
    private String cluster;
    private URI uri;
    private String id;
    private GossipSettings settings;
    private List<Member> gossipMembers;
    private GossipListener listener;
    private MetricRegistry registry;
    private Map<String,String> properties;
    private MessageHandler messageHandler;

    private ManagerBuilder() {}

    private void checkArgument(boolean check, String msg) {
      if (!check) {
        throw new IllegalArgumentException(msg);
      }
    }

    public ManagerBuilder cluster(String cluster) {
      this.cluster = cluster;
      return this;
    }
    
    public ManagerBuilder properties(Map<String,String> properties) {
      this.properties = properties;
      return this;
    }

    public ManagerBuilder id(String id) {
      this.id = id;
      return this;
    }

    public ManagerBuilder gossipSettings(GossipSettings settings) {
      this.settings = settings;
      return this;
    }
    
    public ManagerBuilder startupSettings(StartupSettings startupSettings) {
      this.cluster = startupSettings.getCluster();
      this.id = startupSettings.getId();
      this.settings = startupSettings.getGossipSettings();
      this.gossipMembers = startupSettings.getGossipMembers();
      this.uri = startupSettings.getUri();
      return this;
    }

    public ManagerBuilder gossipMembers(List<Member> members) {
      this.gossipMembers = members;
      return this;
    }

    public ManagerBuilder listener(GossipListener listener) {
      this.listener = listener;
      return this;
    }
    
    public ManagerBuilder registry(MetricRegistry registry) {
      this.registry = registry;
      return this;
    }

    public ManagerBuilder uri(URI uri){
      this.uri = uri;
      return this;
    }

    public ManagerBuilder messageHandler(MessageHandler messageHandler) {
      this.messageHandler = messageHandler;
      return this;
    }

    public GossipManager build() {
      checkArgument(id != null, "You must specify an id");
      checkArgument(cluster != null, "You must specify a cluster name");
      checkArgument(settings != null, "You must specify gossip settings");
      checkArgument(uri != null, "You must specify a uri");
      if (registry == null){
        registry = new MetricRegistry();
      }
      if (properties == null){
        properties = new HashMap<String,String>();
      }
      if (listener == null){
        listener((a,b) -> {});
      }
      if (gossipMembers == null) {
        gossipMembers = new ArrayList<>();
      }
      
      if (messageHandler == null) {
        messageHandler = MessageHandlerFactory.defaultHandler();
      }
      return new GossipManager(cluster, uri, id, properties, settings, gossipMembers, listener, registry, messageHandler) {} ;
    }
  }

}

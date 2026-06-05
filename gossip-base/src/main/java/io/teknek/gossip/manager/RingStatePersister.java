package io.teknek.gossip.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NavigableSet;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.teknek.gossip.LocalMember;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RingStatePersister implements Runnable {

  private static final Logger LOGGER = LoggerFactory.getLogger(RingStatePersister.class);
  private final File path;
  // NOTE: this is a different instance than what gets used for message marshalling.
  private final ObjectMapper objectMapper;
  private final GossipManager manager;
  
  public RingStatePersister(File path, GossipManager manager){
    this.path = path;
    this.objectMapper = new ObjectMapper();
    this.manager = manager;
  }
  
  @Override
  public void run() {
    writeToDisk();
  }
  
  void writeToDisk() {
    NavigableSet<LocalMember> i = manager.getMembers().keySet();
    try (FileOutputStream fos = new FileOutputStream(path)){
      objectMapper.writeValue(fos, i);
    } catch (IOException e) {
      LOGGER.warn("Unable to write ring state", e);
    }
  }

  List<LocalMember> readFromDisk() {
    if (!path.exists()) {
      return new ArrayList<>();
    }
    try (FileInputStream fos = new FileInputStream(path)){
      return objectMapper.readValue(fos, new TypeReference<List<LocalMember>>() { });
    } catch (IOException e) {
      LOGGER.warn("Unable to read ring state", e);
    }
    return new ArrayList<>();
  }
}

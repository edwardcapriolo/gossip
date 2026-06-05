package io.teknek.gossip.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.teknek.gossip.model.PerNodeDataMessage;
import io.teknek.gossip.model.SharedDataMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserDataPersister implements Runnable {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(UserDataPersister.class);
  private final GossipCore gossipCore; 
  
  private final File perNodePath;
  private final File sharedPath;
  private final ObjectMapper objectMapper;
  
  UserDataPersister(GossipCore gossipCore, File perNodePath, File sharedPath) {
    this.gossipCore = gossipCore;
    this.objectMapper = GossipManager.metdataObjectMapper;
    this.perNodePath = perNodePath;
    this.sharedPath = sharedPath;
  }
  
  @SuppressWarnings("unchecked")
  ConcurrentHashMap<String, ConcurrentHashMap<String, PerNodeDataMessage>> readPerNodeFromDisk(){
    if (!perNodePath.exists()) {
      return new ConcurrentHashMap<String, ConcurrentHashMap<String, PerNodeDataMessage>>();
    }
    try (FileInputStream fos = new FileInputStream(perNodePath)){
      return objectMapper.readValue(fos, ConcurrentHashMap.class);
    } catch (IOException e) {
      LOGGER.debug("Unable to read per-node data", e);
    }
    return new ConcurrentHashMap<String, ConcurrentHashMap<String, PerNodeDataMessage>>();
  }
  
  void writePerNodeToDisk(){
    try (FileOutputStream fos = new FileOutputStream(perNodePath)){
      objectMapper.writeValue(fos, gossipCore.getPerNodeData());
    } catch (IOException e) {
      LOGGER.warn("Unable to read shared data", e);
    }
  }
  
  void writeSharedToDisk(){
    try (FileOutputStream fos = new FileOutputStream(sharedPath)){
      objectMapper.writeValue(fos, gossipCore.getSharedData());
    } catch (IOException e) {
      LOGGER.warn("Unable to write per-node data", e);
    }
  }

  @SuppressWarnings("unchecked")
  ConcurrentHashMap<String, SharedDataMessage> readSharedDataFromDisk(){
    if (!sharedPath.exists()) {
      return new ConcurrentHashMap<>();
    }
    try (FileInputStream fos = new FileInputStream(sharedPath)){
      return objectMapper.readValue(fos, ConcurrentHashMap.class);
    } catch (IOException e) {
      LOGGER.debug("Unable to write shared data", e);
    }
    return new ConcurrentHashMap<String, SharedDataMessage>();
  }
  
  /**
   * Writes all pernode and shared data to disk 
   */
  @Override
  public void run() {
    writePerNodeToDisk();
    writeSharedToDisk();
  }
}

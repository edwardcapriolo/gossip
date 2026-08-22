package io.teknek.gossip.spring;

import io.teknek.gossip.manager.GossipManager;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
public class GossipMessageController {

  private final GossipManager gossipManager;

  public GossipMessageController(GossipManager gossipManager) {
    this.gossipManager = gossipManager;
  }

  @PostMapping(value = HttpTransportManager.MESSAGE_PATH, consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public ResponseEntity<Void> receive(@RequestBody byte[] message) {
    try {
      gossipManager.receive(message);
      return ResponseEntity.accepted().build();
    } catch (IOException | RuntimeException e) {
      throw new ResponseStatusException(BAD_REQUEST, "Unable to process gossip message", e);
    }
  }
}

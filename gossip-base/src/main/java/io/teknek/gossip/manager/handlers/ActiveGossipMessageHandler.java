package io.teknek.gossip.manager.handlers;

import io.teknek.gossip.Member;
import io.teknek.gossip.RemoteMember;
import io.teknek.gossip.manager.GossipCore;
import io.teknek.gossip.manager.GossipManager;
import io.teknek.gossip.model.Base;
import io.teknek.gossip.udp.UdpActiveGossipMessage;
import io.teknek.gossip.udp.UdpActiveGossipOk;
import io.teknek.gossip.udp.UdpNotAMemberFault;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class ActiveGossipMessageHandler implements MessageHandler {
  
  /**
   * @param gossipCore context.
   * @param gossipManager context.
   * @param base message reference.
   * @return boolean indicating success.
   */
  @Override
  public boolean invoke(GossipCore gossipCore, GossipManager gossipManager, Base base) {
    List<Member> remoteGossipMembers = new ArrayList<>();
    RemoteMember senderMember = null;
    UdpActiveGossipMessage activeGossipMessage = (UdpActiveGossipMessage) base;
    for (int i = 0; i < activeGossipMessage.getMembers().size(); i++) {
      URI u;
      try {
        u = new URI(activeGossipMessage.getMembers().get(i).getUri());
      } catch (URISyntaxException e) {
        GossipCore.LOGGER.debug("Gossip message with faulty URI", e);
        continue;
      }
      RemoteMember member = new RemoteMember(
              activeGossipMessage.getMembers().get(i).getCluster(),
              u,
              activeGossipMessage.getMembers().get(i).getId(),
              activeGossipMessage.getMembers().get(i).getHeartbeat(),
              activeGossipMessage.getMembers().get(i).getProperties());
      if (i == 0) {
        senderMember = member;
      }
      if (!(member.getClusterName().equals(gossipManager.getMyself().getClusterName()))) {
        UdpNotAMemberFault f = new UdpNotAMemberFault();
        f.setException("Not a member of this cluster " + i);
        f.setUriFrom(activeGossipMessage.getUriFrom());
        f.setUuid(activeGossipMessage.getUuid());
        GossipCore.LOGGER.warn("Received not-a-member fault", f);
        gossipCore.sendOneWay(f, member.getUri());
        continue;
      }
      remoteGossipMembers.add(member);
    }
    UdpActiveGossipOk o = new UdpActiveGossipOk();
    o.setUriFrom(activeGossipMessage.getUriFrom());
    o.setUuid(activeGossipMessage.getUuid());
    gossipCore.sendOneWay(o, senderMember.getUri());
    gossipCore.mergeLists(senderMember, remoteGossipMembers);
    return true;
  }
}

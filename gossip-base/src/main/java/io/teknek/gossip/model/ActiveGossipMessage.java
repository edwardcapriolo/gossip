package io.teknek.gossip.model;

import java.util.ArrayList;
import java.util.List;

public class ActiveGossipMessage extends Base {

  private List<Member> members = new ArrayList<>();
  
  public ActiveGossipMessage(){
    
  }

  public List<Member> getMembers() {
    return members;
  }

  public void setMembers(List<Member> members) {
    this.members = members;
  }
  
}

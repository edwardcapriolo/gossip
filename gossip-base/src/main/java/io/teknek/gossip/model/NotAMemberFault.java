package io.teknek.gossip.model;

public class NotAMemberFault extends Fault {

  public NotAMemberFault(){
    
  }
  
  public NotAMemberFault(String message){
    this.setException(message);
  }
}

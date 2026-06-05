package io.teknek.gossip.model;

import io.teknek.gossip.udp.UdpActiveGossipMessage;
import io.teknek.gossip.udp.UdpActiveGossipOk;
import io.teknek.gossip.udp.UdpPerNodeDataBulkMessage;
import io.teknek.gossip.udp.UdpNotAMemberFault;
import io.teknek.gossip.udp.UdpSharedDataBulkMessage;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;


@JsonTypeInfo(  
        use = JsonTypeInfo.Id.CLASS,  
        include = JsonTypeInfo.As.PROPERTY,  
        property = "type") 
@JsonSubTypes({
        @Type(value = ActiveGossipMessage.class, name = "ActiveGossipMessage"),
        @Type(value = Fault.class, name = "Fault"),
        @Type(value = ActiveGossipOk.class, name = "ActiveGossipOk"),
        @Type(value = UdpActiveGossipOk.class, name = "UdpActiveGossipOk"),
        @Type(value = UdpActiveGossipMessage.class, name = "UdpActiveGossipMessage"),
        @Type(value = UdpNotAMemberFault.class, name = "UdpNotAMemberFault"),
        @Type(value = PerNodeDataMessage.class, name = "PerNodeDataMessage"),
        @Type(value = UdpPerNodeDataBulkMessage.class, name = "UdpPerNodeDataMessage"),
        @Type(value = SharedDataMessage.class, name = "SharedDataMessage"),
        @Type(value = UdpSharedDataBulkMessage.class, name = "UdpSharedDataMessage")
        })
public class Base {

}

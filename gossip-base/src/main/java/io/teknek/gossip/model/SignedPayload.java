package io.teknek.gossip.model;

public class SignedPayload extends Base{
  private byte [] data;
  private byte [] signature;
  public byte[] getData() {
    return data;
  }
  public void setData(byte[] data) {
    this.data = data;
  }
  public byte[] getSignature() {
    return signature;
  }
  public void setSignature(byte[] signature) {
    this.signature = signature;
  }
  
}

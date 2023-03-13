package com.github.simplestaph.kafka.tutorial4.twitteremulator;

import java.util.Date;
import lombok.Data;

@Data
public class TwitterMessage {
  private long messageID;
  private String messageType;

  private User user;

  public TwitterMessage(){
    this.messageID = new Date().getTime();
    this.messageType = "fakeTwit";
    this.user = new User();
  }

}

package com.github.simplestaph.kafka.tutorial4.twitteremulator;

import java.util.Date;
import java.util.Random;
import lombok.Data;
@Data
public class User {
  private long id;
  private Integer followers_count;
  public User(){
    this.id = new Date().getTime();
    this.followers_count = new Random().ints(5, 15)
        .findFirst()
        .getAsInt() * 1000;
  }


}

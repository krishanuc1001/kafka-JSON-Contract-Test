package org.pact;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Message {

  @JsonProperty("id")
  private UUID id;

  @JsonProperty("data")
  private Data data;

}

@Getter
@Setter
class Data {

  @JsonProperty("payload")
  private Payload payload;

}

@Getter
@Setter
class Payload {

  @JsonProperty("name")
  private String name;

  @JsonProperty("employeeDetail")
  private EmployeeDetail employeeDetail;

}

@Getter
@Setter
class EmployeeDetail {

  @JsonProperty("id")
  private String id;

}


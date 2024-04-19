package consumer;

import au.com.dius.pact.consumer.dsl.PactBuilder;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.consumer.junit5.ProviderType;
import au.com.dius.pact.core.model.V4Interaction.AsynchronousMessage;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(
    providerName = "kafka-message-provider",
    providerType = ProviderType.ASYNCH)
public class ConsumerTest {

  @BeforeAll
  public static void setUp() {
    System.setProperty("pact_do_not_track", "true");
  }

  @Pact(consumer = "kafka-message-consumer")
  public V4Pact messageFromKafkaProvider(PactBuilder builder) {
    PactDslJsonBody jsonBody = new PactDslJsonBody();

    Objects.requireNonNull(
            Objects.requireNonNull(jsonBody.uuid("id", "556732b9-ed6c-40dd-81b2-dbd34eaeae0e")
                    .object("data")
                    .object("payload")
                    .stringValue("name", "Thoughtworks")
                    .object("employeeDetail")
                    .stringType("id", "kc:employee:31833")
                    .closeObject())
                .closeObject())
        .closeObject();

    return builder
        .expectsToReceive("A sample message", "core/interaction/message")
        .with(Map.of("message.contents", jsonBody))
        .toPact();
  }

  @Test
  @PactTestFor(pactMethod = "messageFromKafkaProvider")
  void testMessageFromProvider(List<AsynchronousMessage> messages)
      throws JsonProcessingException {
    JsonNode msg = new ObjectMapper().readValue(messages.get(0).contentsAsString(),
        JsonNode.class);

    Assertions.assertNotNull(msg);
  }

}

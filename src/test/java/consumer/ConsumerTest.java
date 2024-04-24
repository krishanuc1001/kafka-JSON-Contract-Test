package consumer;

import au.com.dius.pact.consumer.dsl.PactBuilder;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.junit5.PactConsumerTest;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.consumer.junit5.ProviderType;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.V4Interaction;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(PactConsumerTestExt.class)
@PactConsumerTest
@PactTestFor(
    providerName = "kafka-message-provider",
    providerType = ProviderType.ASYNCH,
    pactVersion = PactSpecVersion.V4)
public class ConsumerTest {

  @BeforeAll
  public static void setUp() {
    System.setProperty("pact_do_not_track", "true");
  }

  @Pact(consumer = "kafka-message-consumer")
  public V4Pact createPact(PactBuilder builder) {
    PactDslJsonBody jsonBody = new PactDslJsonBody();

    Objects.requireNonNull(
            Objects.requireNonNull(jsonBody
                    .stringMatcher("id", "^[a-zA-Z0-9-]*$", "24456732b9-ed6c-40dd-81b2-dggdhdhd12455")
                    .object("data")
                    .object("payload")
                    .stringMatcher("name", "^Krish[0-9]*$", "Krish1001")
                    .object("employeeDetail")
                    .stringType("id", "kc:employee:12345")
                    .closeObject())
                .closeObject())
        .closeObject();

    /**
     * Without using MessageInteractionBuilder
     * Using this approach with PactSpecVersion.V4 generates a pact without regex
     */
//    return builder
//        .expectsToReceive("A sample message", "core/interaction/message")
//        .with(Map.of("message.contents", jsonBody))
//        .toPact();

    /**
     * With MessageInteractionBuilder
     * Using this approach with PactSpecVersion.V4 generates a pact with regex
     */
    return builder
        .expectsToReceiveMessageInteraction(
            "A sample message", messageInteractionBuilder -> {
              return messageInteractionBuilder.withContents(messageContentsBuilder -> {
                return messageContentsBuilder
                    .withContent(jsonBody)
                    .withMetadata(new HashMap<>());
              });
            })
        .toPact();

  }

  @Test
  @PactTestFor(pactMethod = "createPact")
  void testMessageFromProvider(List<V4Interaction.AsynchronousMessage> messages)
      throws JsonProcessingException {
    JsonNode msg = new ObjectMapper().readValue(messages.get(0).contentsAsString(), JsonNode.class);
    Assertions.assertNotNull(msg);
  }

}

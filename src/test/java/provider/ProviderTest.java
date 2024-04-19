package provider;

import au.com.dius.pact.provider.MessageAndMetadata;
import au.com.dius.pact.provider.PactVerifyProvider;
import au.com.dius.pact.provider.junit5.MessageTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.pact.Message;

@Provider("kafka-message-provider")
@PactFolder("target/pacts")
public class ProviderTest {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeAll
  public static void setUp() {
    System.setProperty("pact_do_not_track", "true");
  }

  @BeforeEach
  void before(PactVerificationContext context) {
    context.setTarget(new MessageTestTarget());
  }

  @TestTemplate
  @ExtendWith(PactVerificationInvocationContextProvider.class)
  void pactVerificationTestTemplate(PactVerificationContext context) {
    context.verifyInteraction();
  }

  @PactVerifyProvider("A sample message")
  MessageAndMetadata verifyMessage() throws IOException {
    File file = new File("src/test/resources/kafka-message.json");
    Message message = objectMapper.readValue(file, Message.class);
    return new MessageAndMetadata(objectMapper.writeValueAsBytes(message), Collections.emptyMap());
  }

}

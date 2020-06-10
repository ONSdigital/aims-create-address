package uk.gov.ons;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.cloud.gcp.pubsub.integration.AckMode;
import org.springframework.cloud.gcp.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import org.springframework.cloud.gcp.pubsub.support.BasicAcknowledgeablePubsubMessage;
import org.springframework.cloud.gcp.pubsub.support.GcpPubSubHeaders;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.gov.ons.entities.Message;
import uk.gov.ons.repository.AddressRepository;
import uk.gov.ons.service.AddressService;

@SpringBootApplication
public class CreateAddressApplication {

	private Logger logger = LoggerFactory.getLogger(CreateAddressApplication.class);
	
	@Autowired
	private AddressService addressService;
	
	@Autowired
	private AddressRepository addressRepository;

	@Value("${spring.cloud.gcp.project-id}")
	private String gcpProject;
	
	public static void main(String[] args) {
		SpringApplication.run(CreateAddressApplication.class, args);
	}
	
	// Inbound channel adapter.
	@Bean
	public MessageChannel pubsubInputChannel() {
		return new DirectChannel();
	}
	
	@Bean
	public PubSubInboundChannelAdapter messageChannelAdapter(
			@Qualifier("pubsubInputChannel") MessageChannel inputChannel, PubSubTemplate pubSubTemplate) {
		PubSubInboundChannelAdapter adapter = new PubSubInboundChannelAdapter(pubSubTemplate,
				String.format("projects/%s/subscriptions/new-address-subscription", gcpProject));
		adapter.setOutputChannel(inputChannel);
		adapter.setAckMode(AckMode.MANUAL);

		return adapter;
	}
	
	@Bean
	@ServiceActivator(inputChannel = "pubsubInputChannel")
	public MessageHandler messageReceiver() {
		return message -> {
			logger.info("Message arrived! Payload: " + new String((byte[]) message.getPayload()));
			
			try {
				Message msg = new ObjectMapper().readValue((byte[]) message.getPayload(), Message.class);
				logger.info(String.format("Message: %s", msg.toString()));
				
				// Save the new address to ES
				addressService.createAddressFromMsg(msg.getPayload().getNewAddress().getCollectionCase().getAddress()).subscribe(
						response -> {						
							if (response != null) {
								
								// Show ES Content for new Address - DEMO
								addressRepository.findById(String.valueOf(response.getUprn())).subscribe(address -> {
									logger.info(String.format("ES content for new address with ID: %s = %s", response.getUprn(), address.toString()));
								});
								
								// Send ACK
								BasicAcknowledgeablePubsubMessage originalMessage = message.getHeaders()
										.get(GcpPubSubHeaders.ORIGINAL_MESSAGE, BasicAcknowledgeablePubsubMessage.class);
								originalMessage.ack();	
							}
						});
			} catch (IOException e) {
				logger.info(String.format("Unable to read message: %s", e));
			}
		};
	}
}

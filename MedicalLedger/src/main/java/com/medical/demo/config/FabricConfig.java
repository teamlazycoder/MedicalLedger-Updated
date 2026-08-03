
package com.medical.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "fabric")
public class FabricConfig {

    private String mspId = "Org1MSP";
    private String channelName = "medchannel";
    private String chaincodeName = "healthchaincode";
    private String peerEndpoint = "localhost:7051";
    private String caServerEndpoint = "localhost:7054";
    private String walletPath = "./wallet";
    private String connectionProfilePath = "./connection-profile.json";
}
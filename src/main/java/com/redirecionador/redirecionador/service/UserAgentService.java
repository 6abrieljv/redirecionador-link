package com.redirecionador.redirecionador.service;

import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.springframework.stereotype.Service;

@Service
public class UserAgentService {
    private static final String UNKNOWN = "Unknown";

    private final UserAgentAnalyzer userAgentAnalyzer;

    public UserAgentService(UserAgentAnalyzer userAgentAnalyzer) {
        this.userAgentAnalyzer = userAgentAnalyzer;
    }

    public UserAgentDetails parse(String userAgentHeader) {
        if (userAgentHeader == null || userAgentHeader.isBlank()) {
            return UserAgentDetails.unknown();
        }

        UserAgent agent = userAgentAnalyzer.parse(userAgentHeader);
        return new UserAgentDetails(
                valueOrUnknown(agent.getValue("DeviceClass")),
                valueOrUnknown(agent.getValue("OperatingSystemName")),
                valueOrUnknown(agent.getValue("AgentName")),
                valueOrUnknown(agent.getValue("AgentClass"))
        );
    }

    private String valueOrUnknown(String value) {
        if (value == null || value.isBlank()) {
            return UNKNOWN;
        }
        return value;
    }

    public record UserAgentDetails(
            String deviceClass,
            String osName,
            String agentName,
            String agentClass
    ) {
        public static UserAgentDetails unknown() {
            return new UserAgentDetails(UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN);
        }
    }
}

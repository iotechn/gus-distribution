package com.dobbinsoft.gus.distribution.data.dto.configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfigurationDTO {

    /**
     * 后续使用两套模板
     * colorful: CN特供模板
     *
     */
    private String template;

    private Location location;

    @Getter
    @Setter
    public static class Location {

        private String code;

    }

    public static ConfigurationDTO defaultConfiguration() {
        ConfigurationDTO configurationDTO = new ConfigurationDTO();
        // location
        configurationDTO.setLocation(new Location());
        configurationDTO.getLocation().setCode("");
        return configurationDTO;
    }
}

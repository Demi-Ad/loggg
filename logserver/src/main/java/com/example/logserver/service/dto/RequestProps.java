package com.example.logserver.service.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@Slf4j
public class RequestProps {
    private int maxEditDistance = 0;
    private int exactPrefixLength = 0;
    private int slop = -1;
    private boolean hasProperty = true;


    @JsonCreator
    public static RequestProps requestProps(
            @JsonProperty("maxEditDistance") String maxEditDistance,
            @JsonProperty("exactPrefixLength") String exactPrefixLength,
            @JsonProperty("slop") String slop)
    {

        try {
            RequestProps requestProps = new RequestProps();

            if (StringUtils.hasText(maxEditDistance)) {
                requestProps.setMaxEditDistance(Integer.parseInt(maxEditDistance));
            }

            if (StringUtils.hasText(exactPrefixLength)) {
                requestProps.setExactPrefixLength(Integer.parseInt(exactPrefixLength));
            }

            if (StringUtils.hasText(slop)) {
                requestProps.setSlop(Integer.parseInt(slop));
            }
            return requestProps;

        } catch (Exception e) {
            log.warn("requestProps value exception",e);
            RequestProps requestProps = new RequestProps();
            requestProps.hasProperty = false;
            return requestProps;
        }

    }
}

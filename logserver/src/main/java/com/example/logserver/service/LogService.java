package com.example.logserver.service;

import com.example.logserver.endpoint.ProtocolType;
import com.example.logserver.endpoint.RequestPayload;
import com.example.logserver.entity.Log;
import com.example.logserver.entity.Logger;
import com.example.logserver.repo.LogRepository;
import com.example.logserver.service.dto.LogResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class LogService {

    private final LogRepository logRepository;

    private final LoggerService loggerService;

    private final NamedParameterJdbcTemplate template;


    public Log write(final RequestPayload payload) {

        Logger logger = loggerService.getLogger(payload);
        Log log = new Log(payload);

        log.connectLogger(logger);
        return logRepository.save(log);
    }

    @Transactional(readOnly = true)
    public List<LogResponse> tailLogs(String service, int size) {

        //language=MySQL
        final String sql =
                """
                select c.log,
                c.remote_ip,
                c.timestamp,
                c.protocol,
                (select service from logger where logger.sequence = logger_sequence) as service
                from (select log.log, log.remote_ip, log.timestamp, log.protocol, log.sequence, log.logger_sequence
                      from log
                      where logger_sequence = (select sequence from logger where service = :service)
                      order by sequence desc
                      limit 0,:size) as c
                order by sequence
                """;


        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource(Map.of("service", service, "size", size));
        return template.query(sql, mapSqlParameterSource, (rs, rowNum) -> {
            LogResponse logResponse = new LogResponse();
            logResponse.setLog(rs.getString("log"));
            logResponse.setTimestamp(rs.getString("timestamp"));
            logResponse.setService(rs.getString("service"));
            logResponse.setProtocolType(ProtocolType.valueOf(rs.getString("protocol").toUpperCase()));
            logResponse.setRemoteIp(rs.getString("remote_ip"));
            return logResponse;
        });
    }


}

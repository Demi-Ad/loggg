package com.example.logserver.entity;

import com.example.logserver.endpoint.ProtocolType;
import com.example.logserver.endpoint.RequestPayload;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;
import org.hibernate.search.engine.backend.types.Highlightable;
import org.hibernate.search.engine.backend.types.Projectable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.engine.backend.types.TermVector;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.*;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "log")
@Indexed
public class Log {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @DocumentId
    private Long sequence;

    @Column(name = "log", length = 100000)
    @FullTextField(name = "log", projectable = Projectable.YES, analyzer = "logger_analyzer", termVector = TermVector.YES)
    private String log;

    @Column(name = "timestamp")
    @GenericField(name = "timestamp", sortable = Sortable.YES, projectable = Projectable.YES)
    private long timestamp;

    @Enumerated(EnumType.STRING)
    @Column(name = "protocol")
    @KeywordField(name = "protocol", projectable = Projectable.YES)
    private ProtocolType type;

    @Column(name = "remote_ip")
    @KeywordField(name = "remote_ip", projectable = Projectable.YES)
    private String remoteIp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    @IndexedEmbedded
    private Logger logger;



    public Log(RequestPayload payload) {
        this.log = payload.getLog();
        if (payload.getTimestamp() == 0L) {
            this.timestamp = new Timestamp(System.currentTimeMillis()).getTime();
        } else {
            this.timestamp = payload.getTimestamp();
        }
        this.type = payload.getType();
        this.remoteIp = payload.getRemoteIp();
    }

    public void connectLogger(Logger logger) {
        this.logger = logger;
    }

}

package com.maan.whatsapp.entity.whatsapptemplate;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QWhatsappChatrecipiantMessageMaster is a Querydsl query type for WhatsappChatrecipiantMessageMaster
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QWhatsappChatrecipiantMessageMaster extends EntityPathBase<WhatsappChatrecipiantMessageMaster> {

    private static final long serialVersionUID = -888845199L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QWhatsappChatrecipiantMessageMaster whatsappChatrecipiantMessageMaster = new QWhatsappChatrecipiantMessageMaster("whatsappChatrecipiantMessageMaster");

    public final StringPath apipassword = createString("apipassword");

    public final StringPath apiusername = createString("apiusername");

    public final StringPath commonmsgid = createString("commonmsgid");

    public final StringPath description = createString("description");

    public final DateTimePath<java.util.Date> effectivedate = createDateTime("effectivedate", java.util.Date.class);

    public final DateTimePath<java.util.Date> entrydate = createDateTime("entrydate", java.util.Date.class);

    public final StringPath input_value = createString("input_value");

    public final StringPath iscommonmsg = createString("iscommonmsg");

    public final StringPath isinput = createString("isinput");

    public final StringPath isjobyn = createString("isjobyn");

    public final StringPath remarks = createString("remarks");

    public final StringPath request_key = createString("request_key");

    public final StringPath requeststring = createString("requeststring");

    public final StringPath status = createString("status");

    public final NumberPath<Long> useroptted_messageid = createNumber("useroptted_messageid", Long.class);

    public final StringPath validationapi = createString("validationapi");

    public final QWhatsappChatrecipiantMessageMasterpk whatschatpk;

    public QWhatsappChatrecipiantMessageMaster(String variable) {
        this(WhatsappChatrecipiantMessageMaster.class, forVariable(variable), INITS);
    }

    public QWhatsappChatrecipiantMessageMaster(Path<? extends WhatsappChatrecipiantMessageMaster> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QWhatsappChatrecipiantMessageMaster(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QWhatsappChatrecipiantMessageMaster(PathMetadata metadata, PathInits inits) {
        this(WhatsappChatrecipiantMessageMaster.class, metadata, inits);
    }

    public QWhatsappChatrecipiantMessageMaster(Class<? extends WhatsappChatrecipiantMessageMaster> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.whatschatpk = inits.isInitialized("whatschatpk") ? new QWhatsappChatrecipiantMessageMasterpk(forProperty("whatschatpk")) : null;
    }

}


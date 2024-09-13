package com.maan.whatsapp.entity.whatsapp;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QWhatsappRequestData is a Querydsl query type for WhatsappRequestData
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWhatsappRequestData extends EntityPathBase<WhatsappRequestData> {

    private static final long serialVersionUID = -384577510L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QWhatsappRequestData whatsappRequestData = new QWhatsappRequestData("whatsappRequestData");

    public final DateTimePath<java.util.Date> entry_date = createDateTime("entry_date", java.util.Date.class);

    public final StringPath isprocesscompleted = createString("isprocesscompleted");

    public final StringPath issessionactive = createString("issessionactive");

    public final DateTimePath<java.util.Date> lastupdated_time = createDateTime("lastupdated_time", java.util.Date.class);

    public final StringPath remarks = createString("remarks");

    public final QWhatsappRequestDataPK reqDataPk;

    public final DateTimePath<java.util.Date> request_time = createDateTime("request_time", java.util.Date.class);

    public final DateTimePath<java.util.Date> response_time = createDateTime("response_time", java.util.Date.class);

    public final StringPath status = createString("status");

    public final StringPath wa_response = createString("wa_response");

    public QWhatsappRequestData(String variable) {
        this(WhatsappRequestData.class, forVariable(variable), INITS);
    }

    public QWhatsappRequestData(Path<? extends WhatsappRequestData> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QWhatsappRequestData(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QWhatsappRequestData(PathMetadata metadata, PathInits inits) {
        this(WhatsappRequestData.class, metadata, inits);
    }

    public QWhatsappRequestData(Class<? extends WhatsappRequestData> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.reqDataPk = inits.isInitialized("reqDataPk") ? new QWhatsappRequestDataPK(forProperty("reqDataPk")) : null;
    }

}


package com.maan.whatsapp.entity.whatsapp;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QWhatsappContactData is a Querydsl query type for WhatsappContactData
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWhatsappContactData extends EntityPathBase<WhatsappContactData> {

    private static final long serialVersionUID = -1493774037L;

    public static final QWhatsappContactData whatsappContactData = new QWhatsappContactData("whatsappContactData");

    public final DateTimePath<java.util.Date> entry_date = createDateTime("entry_date", java.util.Date.class);

    public final StringPath language = createString("language");

    public final StringPath remarks = createString("remarks");

    public final StringPath sendername = createString("sendername");

    public final DateTimePath<java.util.Date> session_end_time = createDateTime("session_end_time", java.util.Date.class);

    public final DateTimePath<java.util.Date> session_start_time = createDateTime("session_start_time", java.util.Date.class);

    public final StringPath status = createString("status");

    public final StringPath wa_messageid = createString("wa_messageid");

    public final NumberPath<Long> whatsappid = createNumber("whatsappid", Long.class);

    public QWhatsappContactData(String variable) {
        super(WhatsappContactData.class, forVariable(variable));
    }

    public QWhatsappContactData(Path<? extends WhatsappContactData> path) {
        super(path.getType(), path.getMetadata());
    }

    public QWhatsappContactData(PathMetadata metadata) {
        super(WhatsappContactData.class, metadata);
    }

}


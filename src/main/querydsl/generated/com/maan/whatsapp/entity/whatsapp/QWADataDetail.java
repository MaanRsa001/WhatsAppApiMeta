package com.maan.whatsapp.entity.whatsapp;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QWADataDetail is a Querydsl query type for WADataDetail
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWADataDetail extends EntityPathBase<WADataDetail> {

    private static final long serialVersionUID = 574981138L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QWADataDetail wADataDetail = new QWADataDetail("wADataDetail");

    public final StringPath apivalidationresponse = createString("apivalidationresponse");

    public final DateTimePath<java.util.Date> entrydate = createDateTime("entrydate", java.util.Date.class);

    public final StringPath input_value = createString("input_value");

    public final StringPath isinput = createString("isinput");

    public final StringPath isjobyn = createString("isjobyn");

    public final StringPath messagecontent = createString("messagecontent");

    public final StringPath parentmessageid = createString("parentmessageid");

    public final StringPath remarks = createString("remarks");

    public final StringPath request_key = createString("request_key");

    public final StringPath sessionid = createString("sessionid");

    public final StringPath status = createString("status");

    public final StringPath usermessageid = createString("usermessageid");

    public final StringPath userreply = createString("userreply");

    public final StringPath userreply_msg = createString("userreply_msg");

    public final QWADataDetailPK waddPk;

    public final StringPath wausermessageid = createString("wausermessageid");

    public QWADataDetail(String variable) {
        this(WADataDetail.class, forVariable(variable), INITS);
    }

    public QWADataDetail(Path<? extends WADataDetail> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QWADataDetail(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QWADataDetail(PathMetadata metadata, PathInits inits) {
        this(WADataDetail.class, metadata, inits);
    }

    public QWADataDetail(Class<? extends WADataDetail> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.waddPk = inits.isInitialized("waddPk") ? new QWADataDetailPK(forProperty("waddPk")) : null;
    }

}


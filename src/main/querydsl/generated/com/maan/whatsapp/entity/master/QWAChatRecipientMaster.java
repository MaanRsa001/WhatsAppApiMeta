package com.maan.whatsapp.entity.master;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QWAChatRecipientMaster is a Querydsl query type for WAChatRecipientMaster
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QWAChatRecipientMaster extends EntityPathBase<WAChatRecipientMaster> {

    private static final long serialVersionUID = 1567133276L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QWAChatRecipientMaster wAChatRecipientMaster = new QWAChatRecipientMaster("wAChatRecipientMaster");

    public final StringPath apipassword = createString("apipassword");

    public final StringPath apiusername = createString("apiusername");

    public final QWAChatRecipientMasterPK chatPk;

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

    public QWAChatRecipientMaster(String variable) {
        this(WAChatRecipientMaster.class, forVariable(variable), INITS);
    }

    public QWAChatRecipientMaster(Path<? extends WAChatRecipientMaster> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QWAChatRecipientMaster(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QWAChatRecipientMaster(PathMetadata metadata, PathInits inits) {
        this(WAChatRecipientMaster.class, metadata, inits);
    }

    public QWAChatRecipientMaster(Class<? extends WAChatRecipientMaster> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.chatPk = inits.isInitialized("chatPk") ? new QWAChatRecipientMasterPK(forProperty("chatPk")) : null;
    }

}


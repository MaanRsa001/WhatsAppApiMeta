package com.maan.whatsapp.entity.master;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QWhatsappLossMaster is a Querydsl query type for WhatsappLossMaster
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWhatsappLossMaster extends EntityPathBase<WhatsappLossMaster> {

    private static final long serialVersionUID = -67319948L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QWhatsappLossMaster whatsappLossMaster = new QWhatsappLossMaster("whatsappLossMaster");

    public final StringPath docname = createString("docname");

    public final QWhatsappLossMasterPk pk;

    public final StringPath status = createString("status");

    public QWhatsappLossMaster(String variable) {
        this(WhatsappLossMaster.class, forVariable(variable), INITS);
    }

    public QWhatsappLossMaster(Path<? extends WhatsappLossMaster> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QWhatsappLossMaster(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QWhatsappLossMaster(PathMetadata metadata, PathInits inits) {
        this(WhatsappLossMaster.class, metadata, inits);
    }

    public QWhatsappLossMaster(Class<? extends WhatsappLossMaster> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.pk = inits.isInitialized("pk") ? new QWhatsappLossMasterPk(forProperty("pk")) : null;
    }

}


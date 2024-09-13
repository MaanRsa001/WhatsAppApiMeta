package com.maan.whatsapp.entity.whatsapptemplate;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QWhatsappMessageMaster is a Querydsl query type for WhatsappMessageMaster
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWhatsappMessageMaster extends EntityPathBase<WhatsappMessageMaster> {

    private static final long serialVersionUID = -1448737212L;

    public static final QWhatsappMessageMaster whatsappMessageMaster = new QWhatsappMessageMaster("whatsappMessageMaster");

    public final StringPath commonmsgid = createString("commonmsgid");

    public final DateTimePath<java.util.Date> effectivedate = createDateTime("effectivedate", java.util.Date.class);

    public final DateTimePath<java.util.Date> entrydate = createDateTime("entrydate", java.util.Date.class);

    public final StringPath iscommonmsg = createString("iscommonmsg");

    public final StringPath messagedescar = createString("messagedescar");

    public final StringPath messagedescen = createString("messagedescen");

    public final StringPath messageid = createString("messageid");

    public final StringPath remarks = createString("remarks");

    public final StringPath status = createString("status");

    public QWhatsappMessageMaster(String variable) {
        super(WhatsappMessageMaster.class, forVariable(variable));
    }

    public QWhatsappMessageMaster(Path<? extends WhatsappMessageMaster> path) {
        super(path.getType(), path.getMetadata());
    }

    public QWhatsappMessageMaster(PathMetadata metadata) {
        super(WhatsappMessageMaster.class, metadata);
    }

}


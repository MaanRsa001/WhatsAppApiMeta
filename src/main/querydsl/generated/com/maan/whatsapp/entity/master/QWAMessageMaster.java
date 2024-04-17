package com.maan.whatsapp.entity.master;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QWAMessageMaster is a Querydsl query type for WAMessageMaster
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QWAMessageMaster extends EntityPathBase<WAMessageMaster> {

    private static final long serialVersionUID = -1542760702L;

    public static final QWAMessageMaster wAMessageMaster = new QWAMessageMaster("wAMessageMaster");

    public final StringPath claimMessage = createString("claimMessage");

    public final StringPath claimMessageYn = createString("claimMessageYn");

    public final StringPath commonmsgid = createString("commonmsgid");

    public final DateTimePath<java.util.Date> effectivedate = createDateTime("effectivedate", java.util.Date.class);

    public final DateTimePath<java.util.Date> entrydate = createDateTime("entrydate", java.util.Date.class);

    public final StringPath imageName = createString("imageName");

    public final StringPath imageUrl = createString("imageUrl");

    public final StringPath isButtonMsg = createString("isButtonMsg");

    public final StringPath iscommonmsg = createString("iscommonmsg");

    public final StringPath messagedescar = createString("messagedescar");

    public final StringPath messagedescen = createString("messagedescen");

    public final StringPath messageid = createString("messageid");

    public final StringPath msgButton1 = createString("msgButton1");

    public final StringPath msgButton2 = createString("msgButton2");

    public final StringPath msgButton3 = createString("msgButton3");

    public final StringPath msgButtonSW1 = createString("msgButtonSW1");

    public final StringPath msgButtonSw2 = createString("msgButtonSw2");

    public final StringPath msgButtonSw3 = createString("msgButtonSw3");

    public final StringPath msgFooter = createString("msgFooter");

    public final StringPath msgHeader = createString("msgHeader");

    public final StringPath msgType = createString("msgType");

    public final StringPath remarks = createString("remarks");

    public final StringPath status = createString("status");

    public QWAMessageMaster(String variable) {
        super(WAMessageMaster.class, forVariable(variable));
    }

    public QWAMessageMaster(Path<? extends WAMessageMaster> path) {
        super(path.getType(), path.getMetadata());
    }

    public QWAMessageMaster(PathMetadata metadata) {
        super(WAMessageMaster.class, metadata);
    }

}


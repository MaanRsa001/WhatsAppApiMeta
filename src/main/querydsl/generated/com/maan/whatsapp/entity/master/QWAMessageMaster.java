package com.maan.whatsapp.entity.master;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QWAMessageMaster is a Querydsl query type for WAMessageMaster
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWAMessageMaster extends EntityPathBase<WAMessageMaster> {

    private static final long serialVersionUID = -1542760702L;

    public static final QWAMessageMaster wAMessageMaster = new QWAMessageMaster("wAMessageMaster");

    public final StringPath button_1 = createString("button_1");

    public final StringPath button_1_sw = createString("button_1_sw");

    public final StringPath button_2 = createString("button_2");

    public final StringPath button_2_sw = createString("button_2_sw");

    public final StringPath button_3 = createString("button_3");

    public final StringPath button_3_sw = createString("button_3_sw");

    public final StringPath commonmsgid = createString("commonmsgid");

    public final StringPath ctaButtonName = createString("ctaButtonName");

    public final StringPath ctaButtonNameSw = createString("ctaButtonNameSw");

    public final DateTimePath<java.util.Date> effectivedate = createDateTime("effectivedate", java.util.Date.class);

    public final DateTimePath<java.util.Date> entrydate = createDateTime("entrydate", java.util.Date.class);

    public final StringPath flow_index_screen_name = createString("flow_index_screen_name");

    public final StringPath flowApi = createString("flowApi");

    public final StringPath flowApiAuth = createString("flowApiAuth");

    public final StringPath flowApiMethod = createString("flowApiMethod");

    public final StringPath flowApiRequest = createString("flowApiRequest");

    public final StringPath flowButtonName = createString("flowButtonName");

    public final StringPath flowButtonNameSw = createString("flowButtonNameSw");

    public final StringPath flowId = createString("flowId");

    public final StringPath flowToken = createString("flowToken");

    public final StringPath interactiveButtonYn = createString("interactiveButtonYn");

    public final StringPath iscommonmsg = createString("iscommonmsg");

    public final StringPath locButtonName = createString("locButtonName");

    public final StringPath locButtonNameSw = createString("locButtonNameSw");

    public final StringPath menu_button_name = createString("menu_button_name");

    public final StringPath menu_button_name_sw = createString("menu_button_name_sw");

    public final StringPath messagedescar = createString("messagedescar");

    public final StringPath messagedescen = createString("messagedescen");

    public final StringPath messageid = createString("messageid");

    public final StringPath messageType = createString("messageType");

    public final StringPath remarks = createString("remarks");

    public final StringPath requestdataYn = createString("requestdataYn");

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


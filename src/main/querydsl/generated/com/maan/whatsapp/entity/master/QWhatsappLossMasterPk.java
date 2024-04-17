package com.maan.whatsapp.entity.master;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QWhatsappLossMasterPk is a Querydsl query type for WhatsappLossMasterPk
 */
@Generated("com.querydsl.codegen.EmbeddableSerializer")
public class QWhatsappLossMasterPk extends BeanPath<WhatsappLossMasterPk> {

    private static final long serialVersionUID = -269958001L;

    public static final QWhatsappLossMasterPk whatsappLossMasterPk = new QWhatsappLossMasterPk("whatsappLossMasterPk");

    public final StringPath docId = createString("docId");

    public final NumberPath<Long> lossId = createNumber("lossId", Long.class);

    public QWhatsappLossMasterPk(String variable) {
        super(WhatsappLossMasterPk.class, forVariable(variable));
    }

    public QWhatsappLossMasterPk(Path<? extends WhatsappLossMasterPk> path) {
        super(path.getType(), path.getMetadata());
    }

    public QWhatsappLossMasterPk(PathMetadata metadata) {
        super(WhatsappLossMasterPk.class, metadata);
    }

}


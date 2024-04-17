package com.maan.whatsapp.entity.master;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QWhatsappTemplateMasterPK is a Querydsl query type for WhatsappTemplateMasterPK
 */
@Generated("com.querydsl.codegen.EmbeddableSerializer")
public class QWhatsappTemplateMasterPK extends BeanPath<WhatsappTemplateMasterPK> {

    private static final long serialVersionUID = 802341702L;

    public static final QWhatsappTemplateMasterPK whatsappTemplateMasterPK = new QWhatsappTemplateMasterPK("whatsappTemplateMasterPK");

    public final StringPath agencycode = createString("agencycode");

    public final NumberPath<Long> productid = createNumber("productid", Long.class);

    public final NumberPath<Long> stagecode = createNumber("stagecode", Long.class);

    public final NumberPath<Long> stagesubcode = createNumber("stagesubcode", Long.class);

    public QWhatsappTemplateMasterPK(String variable) {
        super(WhatsappTemplateMasterPK.class, forVariable(variable));
    }

    public QWhatsappTemplateMasterPK(Path<? extends WhatsappTemplateMasterPK> path) {
        super(path.getType(), path.getMetadata());
    }

    public QWhatsappTemplateMasterPK(PathMetadata metadata) {
        super(WhatsappTemplateMasterPK.class, metadata);
    }

}


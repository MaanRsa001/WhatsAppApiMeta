package com.maan.whatsapp.entity.whatsapptemplate;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QWhatsapptemplatePK is a Querydsl query type for WhatsapptemplatePK
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QWhatsapptemplatePK extends BeanPath<WhatsapptemplatePK> {

    private static final long serialVersionUID = -1336046406L;

    public static final QWhatsapptemplatePK whatsapptemplatePK = new QWhatsapptemplatePK("whatsapptemplatePK");

    public final StringPath agency_code = createString("agency_code");

    public final NumberPath<Long> product_id = createNumber("product_id", Long.class);

    public final NumberPath<Long> stage_code = createNumber("stage_code", Long.class);

    public final NumberPath<Long> stagesub_code = createNumber("stagesub_code", Long.class);

    public QWhatsapptemplatePK(String variable) {
        super(WhatsapptemplatePK.class, forVariable(variable));
    }

    public QWhatsapptemplatePK(Path<? extends WhatsapptemplatePK> path) {
        super(path.getType(), path.getMetadata());
    }

    public QWhatsapptemplatePK(PathMetadata metadata) {
        super(WhatsapptemplatePK.class, metadata);
    }

}


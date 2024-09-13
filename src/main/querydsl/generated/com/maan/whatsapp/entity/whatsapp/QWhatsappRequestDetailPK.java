package com.maan.whatsapp.entity.whatsapp;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QWhatsappRequestDetailPK is a Querydsl query type for WhatsappRequestDetailPK
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QWhatsappRequestDetailPK extends BeanPath<WhatsappRequestDetailPK> {

    private static final long serialVersionUID = 1877244540L;

    public static final QWhatsappRequestDetailPK whatsappRequestDetailPK = new QWhatsappRequestDetailPK("whatsappRequestDetailPK");

    public final NumberPath<Long> currentstage = createNumber("currentstage", Long.class);

    public final NumberPath<Long> currentsubstage = createNumber("currentsubstage", Long.class);

    public final NumberPath<Long> mobileno = createNumber("mobileno", Long.class);

    public final NumberPath<Long> productid = createNumber("productid", Long.class);

    public final NumberPath<Long> quoteno = createNumber("quoteno", Long.class);

    public final NumberPath<Long> whatsappcode = createNumber("whatsappcode", Long.class);

    public QWhatsappRequestDetailPK(String variable) {
        super(WhatsappRequestDetailPK.class, forVariable(variable));
    }

    public QWhatsappRequestDetailPK(Path<? extends WhatsappRequestDetailPK> path) {
        super(path.getType(), path.getMetadata());
    }

    public QWhatsappRequestDetailPK(PathMetadata metadata) {
        super(WhatsappRequestDetailPK.class, metadata);
    }

}


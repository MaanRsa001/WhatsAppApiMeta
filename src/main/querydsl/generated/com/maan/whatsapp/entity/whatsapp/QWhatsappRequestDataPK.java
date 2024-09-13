package com.maan.whatsapp.entity.whatsapp;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QWhatsappRequestDataPK is a Querydsl query type for WhatsappRequestDataPK
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QWhatsappRequestDataPK extends BeanPath<WhatsappRequestDataPK> {

    private static final long serialVersionUID = -211797099L;

    public static final QWhatsappRequestDataPK whatsappRequestDataPK = new QWhatsappRequestDataPK("whatsappRequestDataPK");

    public final NumberPath<Long> currentstage = createNumber("currentstage", Long.class);

    public final NumberPath<Long> mobileno = createNumber("mobileno", Long.class);

    public final NumberPath<Long> productid = createNumber("productid", Long.class);

    public final NumberPath<Long> quoteno = createNumber("quoteno", Long.class);

    public final NumberPath<Long> whatsappcode = createNumber("whatsappcode", Long.class);

    public QWhatsappRequestDataPK(String variable) {
        super(WhatsappRequestDataPK.class, forVariable(variable));
    }

    public QWhatsappRequestDataPK(Path<? extends WhatsappRequestDataPK> path) {
        super(path.getType(), path.getMetadata());
    }

    public QWhatsappRequestDataPK(PathMetadata metadata) {
        super(WhatsappRequestDataPK.class, metadata);
    }

}


package com.maan.whatsapp.entity.master;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QWhatsappMessageMenuMaster is a Querydsl query type for WhatsappMessageMenuMaster
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QWhatsappMessageMenuMaster extends EntityPathBase<WhatsappMessageMenuMaster> {

    private static final long serialVersionUID = -121652967L;

    public static final QWhatsappMessageMenuMaster whatsappMessageMenuMaster = new QWhatsappMessageMenuMaster("whatsappMessageMenuMaster");

    public final NumberPath<Integer> displayOrder = createNumber("displayOrder", Integer.class);

    public final DateTimePath<java.util.Date> entryDate = createDateTime("entryDate", java.util.Date.class);

    public final StringPath messageId = createString("messageId");

    public final StringPath optionDesc = createString("optionDesc");

    public final NumberPath<Integer> optionId = createNumber("optionId", Integer.class);

    public final NumberPath<Integer> optionNo = createNumber("optionNo", Integer.class);

    public final StringPath optionTitle = createString("optionTitle");

    public final StringPath status = createString("status");

    public QWhatsappMessageMenuMaster(String variable) {
        super(WhatsappMessageMenuMaster.class, forVariable(variable));
    }

    public QWhatsappMessageMenuMaster(Path<? extends WhatsappMessageMenuMaster> path) {
        super(path.getType(), path.getMetadata());
    }

    public QWhatsappMessageMenuMaster(PathMetadata metadata) {
        super(WhatsappMessageMenuMaster.class, metadata);
    }

}


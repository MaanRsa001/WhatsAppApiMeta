package com.maan.whatsapp.entity.master;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QWhatsappClaimDocumentSetupPk is a Querydsl query type for WhatsappClaimDocumentSetupPk
 */
@Generated("com.querydsl.codegen.EmbeddableSerializer")
public class QWhatsappClaimDocumentSetupPk extends BeanPath<WhatsappClaimDocumentSetupPk> {

    private static final long serialVersionUID = 601330992L;

    public static final QWhatsappClaimDocumentSetupPk whatsappClaimDocumentSetupPk = new QWhatsappClaimDocumentSetupPk("whatsappClaimDocumentSetupPk");

    public final NumberPath<Long> docId = createNumber("docId", Long.class);

    public final NumberPath<Long> mobNo = createNumber("mobNo", Long.class);

    public final NumberPath<Long> tranId = createNumber("tranId", Long.class);

    public QWhatsappClaimDocumentSetupPk(String variable) {
        super(WhatsappClaimDocumentSetupPk.class, forVariable(variable));
    }

    public QWhatsappClaimDocumentSetupPk(Path<? extends WhatsappClaimDocumentSetupPk> path) {
        super(path.getType(), path.getMetadata());
    }

    public QWhatsappClaimDocumentSetupPk(PathMetadata metadata) {
        super(WhatsappClaimDocumentSetupPk.class, metadata);
    }

}


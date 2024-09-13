package com.maan.whatsapp.entity.master;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QWhatsappClaimDocumentSetup is a Querydsl query type for WhatsappClaimDocumentSetup
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWhatsappClaimDocumentSetup extends EntityPathBase<WhatsappClaimDocumentSetup> {

    private static final long serialVersionUID = 2096712789L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QWhatsappClaimDocumentSetup whatsappClaimDocumentSetup = new QWhatsappClaimDocumentSetup("whatsappClaimDocumentSetup");

    public final StringPath claimNo = createString("claimNo");

    public final QWhatsappClaimDocumentSetupPk claimPk;

    public final StringPath docDesc = createString("docDesc");

    public final StringPath docName = createString("docName");

    public final DateTimePath<java.util.Date> entryDate = createDateTime("entryDate", java.util.Date.class);

    public final StringPath fileYn = createString("fileYn");

    public final StringPath lossId = createString("lossId");

    public final StringPath partyId = createString("partyId");

    public final StringPath processYn = createString("processYn");

    public final StringPath remarks = createString("remarks");

    public final StringPath sentYn = createString("sentYn");

    public final StringPath status = createString("status");

    public QWhatsappClaimDocumentSetup(String variable) {
        this(WhatsappClaimDocumentSetup.class, forVariable(variable), INITS);
    }

    public QWhatsappClaimDocumentSetup(Path<? extends WhatsappClaimDocumentSetup> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QWhatsappClaimDocumentSetup(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QWhatsappClaimDocumentSetup(PathMetadata metadata, PathInits inits) {
        this(WhatsappClaimDocumentSetup.class, metadata, inits);
    }

    public QWhatsappClaimDocumentSetup(Class<? extends WhatsappClaimDocumentSetup> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.claimPk = inits.isInitialized("claimPk") ? new QWhatsappClaimDocumentSetupPk(forProperty("claimPk")) : null;
    }

}


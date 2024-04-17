package com.maan.whatsapp.entity.whatsapptemplate;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QWhatsappChatrecipiantMessageMasterpk is a Querydsl query type for WhatsappChatrecipiantMessageMasterpk
 */
@Generated("com.querydsl.codegen.EmbeddableSerializer")
public class QWhatsappChatrecipiantMessageMasterpk extends BeanPath<WhatsappChatrecipiantMessageMasterpk> {

    private static final long serialVersionUID = 518259244L;

    public static final QWhatsappChatrecipiantMessageMasterpk whatsappChatrecipiantMessageMasterpk = new QWhatsappChatrecipiantMessageMasterpk("whatsappChatrecipiantMessageMasterpk");

    public final StringPath messageid = createString("messageid");

    public final StringPath parentmessageid = createString("parentmessageid");

    public QWhatsappChatrecipiantMessageMasterpk(String variable) {
        super(WhatsappChatrecipiantMessageMasterpk.class, forVariable(variable));
    }

    public QWhatsappChatrecipiantMessageMasterpk(Path<? extends WhatsappChatrecipiantMessageMasterpk> path) {
        super(path.getType(), path.getMetadata());
    }

    public QWhatsappChatrecipiantMessageMasterpk(PathMetadata metadata) {
        super(WhatsappChatrecipiantMessageMasterpk.class, metadata);
    }

}


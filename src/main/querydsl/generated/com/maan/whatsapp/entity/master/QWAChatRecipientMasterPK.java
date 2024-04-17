package com.maan.whatsapp.entity.master;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QWAChatRecipientMasterPK is a Querydsl query type for WAChatRecipientMasterPK
 */
@Generated("com.querydsl.codegen.EmbeddableSerializer")
public class QWAChatRecipientMasterPK extends BeanPath<WAChatRecipientMasterPK> {

    private static final long serialVersionUID = -1518440105L;

    public static final QWAChatRecipientMasterPK wAChatRecipientMasterPK = new QWAChatRecipientMasterPK("wAChatRecipientMasterPK");

    public final StringPath messageid = createString("messageid");

    public final StringPath parentmessageid = createString("parentmessageid");

    public QWAChatRecipientMasterPK(String variable) {
        super(WAChatRecipientMasterPK.class, forVariable(variable));
    }

    public QWAChatRecipientMasterPK(Path<? extends WAChatRecipientMasterPK> path) {
        super(path.getType(), path.getMetadata());
    }

    public QWAChatRecipientMasterPK(PathMetadata metadata) {
        super(WAChatRecipientMasterPK.class, metadata);
    }

}


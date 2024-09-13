package com.maan.whatsapp.entity.whatsapp;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QWADataDetailPK is a Querydsl query type for WADataDetailPK
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QWADataDetailPK extends BeanPath<WADataDetailPK> {

    private static final long serialVersionUID = -1493905011L;

    public static final QWADataDetailPK wADataDetailPK = new QWADataDetailPK("wADataDetailPK");

    public final NumberPath<Long> waid = createNumber("waid", Long.class);

    public final StringPath wamessageid = createString("wamessageid");

    public QWADataDetailPK(String variable) {
        super(WADataDetailPK.class, forVariable(variable));
    }

    public QWADataDetailPK(Path<? extends WADataDetailPK> path) {
        super(path.getType(), path.getMetadata());
    }

    public QWADataDetailPK(PathMetadata metadata) {
        super(WADataDetailPK.class, metadata);
    }

}


package com.maan.whatsapp.entity.master;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QPreinspectionImageDetail is a Querydsl query type for PreinspectionImageDetail
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QPreinspectionImageDetail extends EntityPathBase<PreinspectionImageDetail> {

    private static final long serialVersionUID = 1770260434L;

    public static final QPreinspectionImageDetail preinspectionImageDetail = new QPreinspectionImageDetail("preinspectionImageDetail");

    public final DateTimePath<java.util.Date> entry_date = createDateTime("entry_date", java.util.Date.class);

    public final StringPath imageFilePath = createString("imageFilePath");

    public final StringPath imageName = createString("imageName");

    public final StringPath status = createString("status");

    public final NumberPath<Long> tranId = createNumber("tranId", Long.class);

    public QPreinspectionImageDetail(String variable) {
        super(PreinspectionImageDetail.class, forVariable(variable));
    }

    public QPreinspectionImageDetail(Path<? extends PreinspectionImageDetail> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPreinspectionImageDetail(PathMetadata metadata) {
        super(PreinspectionImageDetail.class, metadata);
    }

}


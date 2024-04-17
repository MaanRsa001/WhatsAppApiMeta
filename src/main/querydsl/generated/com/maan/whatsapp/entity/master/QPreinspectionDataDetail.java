package com.maan.whatsapp.entity.master;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QPreinspectionDataDetail is a Querydsl query type for PreinspectionDataDetail
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QPreinspectionDataDetail extends EntityPathBase<PreinspectionDataDetail> {

    private static final long serialVersionUID = 419340693L;

    public static final QPreinspectionDataDetail preinspectionDataDetail = new QPreinspectionDataDetail("preinspectionDataDetail");

    public final StringPath chassisNo = createString("chassisNo");

    public final DateTimePath<java.util.Date> entry_date = createDateTime("entry_date", java.util.Date.class);

    public final StringPath mobileNo = createString("mobileNo");

    public final StringPath registrationNo = createString("registrationNo");

    public final StringPath status = createString("status");

    public final NumberPath<Long> tranId = createNumber("tranId", Long.class);

    public QPreinspectionDataDetail(String variable) {
        super(PreinspectionDataDetail.class, forVariable(variable));
    }

    public QPreinspectionDataDetail(Path<? extends PreinspectionDataDetail> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPreinspectionDataDetail(PathMetadata metadata) {
        super(PreinspectionDataDetail.class, metadata);
    }

}


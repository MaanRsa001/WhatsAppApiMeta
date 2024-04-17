package com.maan.whatsapp.claimintimation;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QInalipaIntimatedTable is a Querydsl query type for InalipaIntimatedTable
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QInalipaIntimatedTable extends EntityPathBase<InalipaIntimatedTable> {

    private static final long serialVersionUID = -1150730809L;

    public static final QInalipaIntimatedTable inalipaIntimatedTable = new QInalipaIntimatedTable("inalipaIntimatedTable");

    public final DateTimePath<java.util.Date> accidentDate = createDateTime("accidentDate", java.util.Date.class);

    public final StringPath claimId = createString("claimId");

    public final StringPath claimNo = createString("claimNo");

    public final StringPath ClaimType = createString("ClaimType");

    public final DateTimePath<java.util.Date> intimatedDate = createDateTime("intimatedDate", java.util.Date.class);

    public final NumberPath<Integer> intimatedMobileNo = createNumber("intimatedMobileNo", Integer.class);

    public final NumberPath<Long> mobileNo = createNumber("mobileNo", Long.class);

    public final DateTimePath<java.util.Date> policyEndDate = createDateTime("policyEndDate", java.util.Date.class);

    public final StringPath policyNo = createString("policyNo");

    public final DateTimePath<java.util.Date> policyStartDate = createDateTime("policyStartDate", java.util.Date.class);

    public QInalipaIntimatedTable(String variable) {
        super(InalipaIntimatedTable.class, forVariable(variable));
    }

    public QInalipaIntimatedTable(Path<? extends InalipaIntimatedTable> path) {
        super(path.getType(), path.getMetadata());
    }

    public QInalipaIntimatedTable(PathMetadata metadata) {
        super(InalipaIntimatedTable.class, metadata);
    }

}


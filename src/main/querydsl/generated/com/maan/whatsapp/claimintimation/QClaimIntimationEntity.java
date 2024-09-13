package com.maan.whatsapp.claimintimation;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QClaimIntimationEntity is a Querydsl query type for ClaimIntimationEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QClaimIntimationEntity extends EntityPathBase<ClaimIntimationEntity> {

    private static final long serialVersionUID = 612796053L;

    public static final QClaimIntimationEntity claimIntimationEntity = new QClaimIntimationEntity("claimIntimationEntity");

    public final StringPath accidentDate = createString("accidentDate");

    public final StringPath apiType = createString("apiType");

    public final StringPath botOptionNo = createString("botOptionNo");

    public final StringPath branchCode = createString("branchCode");

    public final StringPath chassisNo = createString("chassisNo");

    public final StringPath civilId = createString("civilId");

    public final StringPath claimRefNo = createString("claimRefNo");

    public final StringPath claimStatus = createString("claimStatus");

    public final StringPath code = createString("code");

    public final StringPath codeDesc = createString("codeDesc");

    public final StringPath contactPersonName = createString("contactPersonName");

    public final StringPath divnCode = createString("divnCode");

    public final DateTimePath<java.util.Date> entryDate = createDateTime("entryDate", java.util.Date.class);

    public final StringPath insuranceId = createString("insuranceId");

    public final StringPath manufactureYear = createString("manufactureYear");

    public final StringPath mobileNo = createString("mobileNo");

    public final StringPath plateNo = createString("plateNo");

    public final StringPath policyFrom = createString("policyFrom");

    public final StringPath policyNo = createString("policyNo");

    public final StringPath policyTo = createString("policyTo");

    public final StringPath product = createString("product");

    public final StringPath regionCode = createString("regionCode");

    public final NumberPath<Long> serialNo = createNumber("serialNo", Long.class);

    public final StringPath status = createString("status");

    public final StringPath sumInsured = createString("sumInsured");

    public final StringPath vehiModel = createString("vehiModel");

    public final StringPath vehiType = createString("vehiType");

    public final StringPath vehRegNo = createString("vehRegNo");

    public QClaimIntimationEntity(String variable) {
        super(ClaimIntimationEntity.class, forVariable(variable));
    }

    public QClaimIntimationEntity(Path<? extends ClaimIntimationEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QClaimIntimationEntity(PathMetadata metadata) {
        super(ClaimIntimationEntity.class, metadata);
    }

}


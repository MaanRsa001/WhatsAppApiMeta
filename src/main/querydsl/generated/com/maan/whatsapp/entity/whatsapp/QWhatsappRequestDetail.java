package com.maan.whatsapp.entity.whatsapp;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QWhatsappRequestDetail is a Querydsl query type for WhatsappRequestDetail
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWhatsappRequestDetail extends EntityPathBase<WhatsappRequestDetail> {

    private static final long serialVersionUID = -208102207L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QWhatsappRequestDetail whatsappRequestDetail = new QWhatsappRequestDetail("whatsappRequestDetail");

    public final StringPath apiMessageText = createString("apiMessageText");

    public final DateTimePath<java.util.Date> entry_date = createDateTime("entry_date", java.util.Date.class);

    public final StringPath file_path = createString("file_path");

    public final StringPath file_yn = createString("file_yn");

    public final StringPath formpageUrl = createString("formpageUrl");

    public final StringPath formpageYn = createString("formpageYn");

    public final StringPath isapicall = createString("isapicall");

    public final StringPath isButtonMsg = createString("isButtonMsg");

    public final StringPath isdocuplyn = createString("isdocuplyn");

    public final StringPath isjobyn = createString("isjobyn");

    public final StringPath isprocesscompleted = createString("isprocesscompleted");

    public final StringPath isread = createString("isread");

    public final StringPath isreplyyn = createString("isreplyyn");

    public final StringPath isReponseYn = createString("isReponseYn");

    public final StringPath isResMsg = createString("isResMsg");

    public final StringPath isResMsgApi = createString("isResMsgApi");

    public final StringPath isResponseYnSent = createString("isResponseYnSent");

    public final StringPath isResSaveApi = createString("isResSaveApi");

    public final StringPath issent = createString("issent");

    public final StringPath isskipped = createString("isskipped");

    public final StringPath isskipyn = createString("isskipyn");

    public final StringPath isTemplateMsg = createString("isTemplateMsg");

    public final StringPath isvalid = createString("isvalid");

    public final StringPath isvalidationapi = createString("isvalidationapi");

    public final StringPath locwa_userfilepath = createString("locwa_userfilepath");

    public final StringPath message = createString("message");

    public final StringPath remarks = createString("remarks");

    public final QWhatsappRequestDetailPK reqDetPk;

    public final DateTimePath<java.util.Date> request_time = createDateTime("request_time", java.util.Date.class);

    public final StringPath requestkey = createString("requestkey");

    public final StringPath requeststring = createString("requeststring");

    public final DateTimePath<java.util.Date> response_time = createDateTime("response_time", java.util.Date.class);

    public final StringPath saveApiRes = createString("saveApiRes");

    public final StringPath sessionid = createString("sessionid");

    public final NumberPath<Long> stage_order = createNumber("stage_order", Long.class);

    public final StringPath stageDesc = createString("stageDesc");

    public final StringPath status = createString("status");

    public final StringPath userreply = createString("userreply");

    public final StringPath validationmessage = createString("validationmessage");

    public final StringPath wa_filepath = createString("wa_filepath");

    public final StringPath wa_messageid = createString("wa_messageid");

    public final StringPath wa_response = createString("wa_response");

    public final StringPath wa_userfilepath = createString("wa_userfilepath");

    public final StringPath wausermessageid = createString("wausermessageid");

    public final NumberPath<Long> whatsappid = createNumber("whatsappid", Long.class);

    public QWhatsappRequestDetail(String variable) {
        this(WhatsappRequestDetail.class, forVariable(variable), INITS);
    }

    public QWhatsappRequestDetail(Path<? extends WhatsappRequestDetail> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QWhatsappRequestDetail(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QWhatsappRequestDetail(PathMetadata metadata, PathInits inits) {
        this(WhatsappRequestDetail.class, metadata, inits);
    }

    public QWhatsappRequestDetail(Class<? extends WhatsappRequestDetail> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.reqDetPk = inits.isInitialized("reqDetPk") ? new QWhatsappRequestDetailPK(forProperty("reqDetPk")) : null;
    }

}


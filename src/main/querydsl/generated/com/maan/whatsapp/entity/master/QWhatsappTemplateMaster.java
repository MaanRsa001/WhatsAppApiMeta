package com.maan.whatsapp.entity.master;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QWhatsappTemplateMaster is a Querydsl query type for WhatsappTemplateMaster
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QWhatsappTemplateMaster extends EntityPathBase<WhatsappTemplateMaster> {

    private static final long serialVersionUID = 590778379L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QWhatsappTemplateMaster whatsappTemplateMaster = new QWhatsappTemplateMaster("whatsappTemplateMaster");

    public final StringPath apiauth = createString("apiauth");

    public final StringPath apimethod = createString("apimethod");

    public final StringPath apiurl = createString("apiurl");

    public final StringPath button1 = createString("button1");

    public final StringPath button2 = createString("button2");

    public final StringPath button3 = createString("button3");

    public final StringPath buttonBody = createString("buttonBody");

    public final StringPath buttonFooter = createString("buttonFooter");

    public final StringPath buttonHeader = createString("buttonHeader");

    public final StringPath buttonSw1 = createString("buttonSw1");

    public final StringPath buttonSw2 = createString("buttonSw2");

    public final StringPath buttonSw3 = createString("buttonSw3");

    public final DateTimePath<java.util.Date> entry_date = createDateTime("entry_date", java.util.Date.class);

    public final StringPath errorResponseStrTzs = createString("errorResponseStrTzs");

    public final StringPath errorrespstring = createString("errorrespstring");

    public final StringPath file_path = createString("file_path");

    public final StringPath file_yn = createString("file_yn");

    public final StringPath formpageUrl = createString("formpageUrl");

    public final StringPath formpageYn = createString("formpageYn");

    public final StringPath imageName = createString("imageName");

    public final StringPath imageUrl = createString("imageUrl");

    public final StringPath isapicall = createString("isapicall");

    public final StringPath isButtonMsg = createString("isButtonMsg");

    public final StringPath ischatyn = createString("ischatyn");

    public final StringPath isdocuplyn = createString("isdocuplyn");

    public final StringPath isreplyyn = createString("isreplyyn");

    public final StringPath isReponseYn = createString("isReponseYn");

    public final StringPath isResMsg = createString("isResMsg");

    public final StringPath isResMsgApi = createString("isResMsgApi");

    public final StringPath isResSaveApi = createString("isResSaveApi");

    public final StringPath isskipyn = createString("isskipyn");

    public final StringPath isTemplateMsg = createString("isTemplateMsg");

    public final StringPath isvalidationapi = createString("isvalidationapi");

    public final StringPath message_content_ar = createString("message_content_ar");

    public final StringPath message_content_en = createString("message_content_en");

    public final StringPath message_regards_ar = createString("message_regards_ar");

    public final StringPath message_regards_en = createString("message_regards_en");

    public final StringPath msgType = createString("msgType");

    public final StringPath remarks = createString("remarks");

    public final StringPath requestkey = createString("requestkey");

    public final StringPath requeststring = createString("requeststring");

    public final StringPath response_keys = createString("response_keys");

    public final StringPath responsestring = createString("responsestring");

    public final StringPath responseStringAr = createString("responseStringAr");

    public final StringPath stage_desc = createString("stage_desc");

    public final NumberPath<Long> stage_order = createNumber("stage_order", Long.class);

    public final StringPath stagesub_desc = createString("stagesub_desc");

    public final StringPath status = createString("status");

    public final StringPath templateName = createString("templateName");

    public final QWhatsappTemplateMasterPK tempMasterPk;

    public QWhatsappTemplateMaster(String variable) {
        this(WhatsappTemplateMaster.class, forVariable(variable), INITS);
    }

    public QWhatsappTemplateMaster(Path<? extends WhatsappTemplateMaster> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QWhatsappTemplateMaster(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QWhatsappTemplateMaster(PathMetadata metadata, PathInits inits) {
        this(WhatsappTemplateMaster.class, metadata, inits);
    }

    public QWhatsappTemplateMaster(Class<? extends WhatsappTemplateMaster> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.tempMasterPk = inits.isInitialized("tempMasterPk") ? new QWhatsappTemplateMasterPK(forProperty("tempMasterPk")) : null;
    }

}


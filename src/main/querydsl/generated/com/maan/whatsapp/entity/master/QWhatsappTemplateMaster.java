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

    public final StringPath button_1 = createString("button_1");

    public final StringPath button_1_sw = createString("button_1_sw");

    public final StringPath button_2 = createString("button_2");

    public final StringPath button_2_sw = createString("button_2_sw");

    public final StringPath button_3 = createString("button_3");

    public final StringPath button_3_sw = createString("button_3_sw");

    public final StringPath ctaButtonKeys = createString("ctaButtonKeys");

    public final StringPath ctaButtonName = createString("ctaButtonName");

    public final StringPath ctaButtonNameSw = createString("ctaButtonNameSw");

    public final StringPath ctaButtonUrl = createString("ctaButtonUrl");

    public final DateTimePath<java.util.Date> entry_date = createDateTime("entry_date", java.util.Date.class);

    public final StringPath errorResponseStrTzs = createString("errorResponseStrTzs");

    public final StringPath errorrespstring = createString("errorrespstring");

    public final StringPath file_path = createString("file_path");

    public final StringPath file_yn = createString("file_yn");

    public final StringPath flow_index_screen_name = createString("flow_index_screen_name");

    public final StringPath flowApi = createString("flowApi");

    public final StringPath flowApiAuth = createString("flowApiAuth");

    public final StringPath flowApiMethod = createString("flowApiMethod");

    public final StringPath flowApiRequest = createString("flowApiRequest");

    public final StringPath flowButtonName = createString("flowButtonName");

    public final StringPath flowButtonNameSw = createString("flowButtonNameSw");

    public final StringPath flowId = createString("flowId");

    public final StringPath flowToken = createString("flowToken");

    public final StringPath formpageUrl = createString("formpageUrl");

    public final StringPath formpageYn = createString("formpageYn");

    public final StringPath interactiveButtonYn = createString("interactiveButtonYn");

    public final StringPath isapicall = createString("isapicall");

    public final StringPath ischatyn = createString("ischatyn");

    public final StringPath isCtaDynamicYn = createString("isCtaDynamicYn");

    public final StringPath isdocuplyn = createString("isdocuplyn");

    public final StringPath isreplyyn = createString("isreplyyn");

    public final StringPath isReponseYn = createString("isReponseYn");

    public final StringPath isResMsg = createString("isResMsg");

    public final StringPath isResMsgApi = createString("isResMsgApi");

    public final StringPath isResSaveApi = createString("isResSaveApi");

    public final StringPath isskipyn = createString("isskipyn");

    public final StringPath isvalidationapi = createString("isvalidationapi");

    public final StringPath locButtonName = createString("locButtonName");

    public final StringPath locButtonNameSw = createString("locButtonNameSw");

    public final StringPath menu_button_name = createString("menu_button_name");

    public final StringPath menu_button_name_sw = createString("menu_button_name_sw");

    public final StringPath message_content_ar = createString("message_content_ar");

    public final StringPath message_content_en = createString("message_content_en");

    public final StringPath message_regards_ar = createString("message_regards_ar");

    public final StringPath message_regards_en = createString("message_regards_en");

    public final StringPath messageType = createString("messageType");

    public final StringPath remarks = createString("remarks");

    public final StringPath requestdataYn = createString("requestdataYn");

    public final StringPath requestkey = createString("requestkey");

    public final StringPath requeststring = createString("requeststring");

    public final StringPath response_keys = createString("response_keys");

    public final StringPath responsestring = createString("responsestring");

    public final StringPath responseStringAr = createString("responseStringAr");

    public final StringPath stage_desc = createString("stage_desc");

    public final NumberPath<Long> stage_order = createNumber("stage_order", Long.class);

    public final StringPath stagesub_desc = createString("stagesub_desc");

    public final StringPath status = createString("status");

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


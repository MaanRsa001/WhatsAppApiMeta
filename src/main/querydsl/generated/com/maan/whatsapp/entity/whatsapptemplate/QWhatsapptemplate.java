package com.maan.whatsapp.entity.whatsapptemplate;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QWhatsapptemplate is a Querydsl query type for Whatsapptemplate
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWhatsapptemplate extends EntityPathBase<Whatsapptemplate> {

    private static final long serialVersionUID = 1057826431L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QWhatsapptemplate whatsapptemplate = new QWhatsapptemplate("whatsapptemplate");

    public final StringPath apiauth = createString("apiauth");

    public final StringPath apimethod = createString("apimethod");

    public final StringPath apiurl = createString("apiurl");

    public final StringPath button1 = createString("button1");

    public final StringPath button2 = createString("button2");

    public final StringPath button3 = createString("button3");

    public final StringPath buttonBody = createString("buttonBody");

    public final StringPath buttonFooter = createString("buttonFooter");

    public final StringPath buttonHeader = createString("buttonHeader");

    public final DateTimePath<java.util.Date> entry_date = createDateTime("entry_date", java.util.Date.class);

    public final StringPath errorrespstring = createString("errorrespstring");

    public final StringPath file_path = createString("file_path");

    public final StringPath file_yn = createString("file_yn");

    public final StringPath imageName = createString("imageName");

    public final StringPath imageUrl = createString("imageUrl");

    public final StringPath isapicall = createString("isapicall");

    public final StringPath isButtonMsg = createString("isButtonMsg");

    public final StringPath ischatyn = createString("ischatyn");

    public final StringPath isdocuplyn = createString("isdocuplyn");

    public final StringPath isreplyyn = createString("isreplyyn");

    public final StringPath isReponseYn = createString("isReponseYn");

    public final StringPath isskipyn = createString("isskipyn");

    public final StringPath isvalidationapi = createString("isvalidationapi");

    public final StringPath message_content_ar = createString("message_content_ar");

    public final StringPath message_content_en = createString("message_content_en");

    public final StringPath message_regards_ar = createString("message_regards_ar");

    public final StringPath message_regards_en = createString("message_regards_en");

    public final StringPath msgType = createString("msgType");

    public final StringPath remarks = createString("remarks");

    public final StringPath request_key = createString("request_key");

    public final StringPath request_string = createString("request_string");

    public final StringPath responsestring = createString("responsestring");

    public final StringPath responsestring_ar = createString("responsestring_ar");

    public final StringPath stage_desc = createString("stage_desc");

    public final NumberPath<Long> stage_order = createNumber("stage_order", Long.class);

    public final StringPath stagesub_desc = createString("stagesub_desc");

    public final StringPath status = createString("status");

    public final QWhatsapptemplatePK Whatsapptemplatepk;

    public QWhatsapptemplate(String variable) {
        this(Whatsapptemplate.class, forVariable(variable), INITS);
    }

    public QWhatsapptemplate(Path<? extends Whatsapptemplate> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QWhatsapptemplate(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QWhatsapptemplate(PathMetadata metadata, PathInits inits) {
        this(Whatsapptemplate.class, metadata, inits);
    }

    public QWhatsapptemplate(Class<? extends Whatsapptemplate> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.Whatsapptemplatepk = inits.isInitialized("Whatsapptemplatepk") ? new QWhatsapptemplatePK(forProperty("Whatsapptemplatepk")) : null;
    }

}


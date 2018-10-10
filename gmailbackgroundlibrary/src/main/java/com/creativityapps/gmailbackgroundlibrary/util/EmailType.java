package com.creativityapps.gmailbackgroundlibrary.util;

import android.support.annotation.StringDef;

import static com.creativityapps.gmailbackgroundlibrary.util.EmailType.HTML;
import static com.creativityapps.gmailbackgroundlibrary.util.EmailType.PLAIN;

@StringDef({PLAIN, HTML})
public @interface EmailType {

    String PLAIN = "text/plain";
    String HTML = "text/html";

}

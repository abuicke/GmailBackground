package ie.moses.gmailbackgroundlibrary.util;

import android.support.annotation.StringDef;

import static ie.moses.gmailbackgroundlibrary.util.EmailType.HTML;
import static ie.moses.gmailbackgroundlibrary.util.EmailType.PLAIN;

@StringDef({PLAIN, HTML})
public @interface EmailType {

    String PLAIN = "text/plain";
    String HTML = "text/html";

}

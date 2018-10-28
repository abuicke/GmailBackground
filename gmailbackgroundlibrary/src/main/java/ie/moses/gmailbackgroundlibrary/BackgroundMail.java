package ie.moses.gmailbackgroundlibrary;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.RequiresPermission;
import android.text.TextUtils;
import android.util.Log;

import ie.moses.gmailbackgroundlibrary.util.EmailType;
import ie.moses.gmailbackgroundlibrary.util.GmailSender;

import java.util.ArrayList;
import java.util.Arrays;

import ie.moses.caimito.Callback;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.INTERNET;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static ie.moses.caimito.android.PermissionUtils.checkPermission;

public class BackgroundMail {

    private static final String TAG = BackgroundMail.class.getSimpleName();

    private Context _context;

    private String _username;
    private String _password;
    private String _mailto;
    private String _subject;
    private String _body;
    private String _type;
    private String _progressMessage;
    private boolean _showProgressDialog = true;
    private final ArrayList<String> _attachments = new ArrayList<>();

    private Callback<Void> _onSuccessCallback;
    private Callback<Void> _onFailCallback;

    private BackgroundMail(Context context) {
        _context = context;
    }

    public void send() {
        checkState(!TextUtils.isEmpty(_username), "You didn't set a Gmail username");
        checkState(!TextUtils.isEmpty(_password), "You didn't set a Gmail password");
        checkState(!TextUtils.isEmpty(_mailto), "You didn't set a Gmail recipient");
        checkState(!TextUtils.isEmpty(_body), "You didn't set a body");
        checkState(!TextUtils.isEmpty(_subject), "You didn't set a subject");
        SendEmailTask sendEmailTask = new SendEmailTask();
        sendEmailTask.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private class SendEmailTask extends AsyncTask<String, Void, Boolean> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (_showProgressDialog) {
                progressDialog = new ProgressDialog(_context);
                progressDialog.setMessage(_progressMessage);
                progressDialog.setCancelable(false);
                progressDialog.show();
            }
        }

        @Override
        protected Boolean doInBackground(String... arg0) {
            try {
                GmailSender sender = new GmailSender(_username, _password);
                for (String attachment : _attachments) {
                    if (attachment.isEmpty()) {
                        sender.addAttachment(attachment);
                    }
                }
                sender.sendMail(_subject, _body, _username, _mailto, _type);
            } catch (Exception e) {
                Log.e(TAG, "send email failed", e);
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (_showProgressDialog) {
                progressDialog.dismiss();
                if (result) {
                    if (_onSuccessCallback != null) {
                        _onSuccessCallback.call(null);
                    }
                } else {
                    if (_onFailCallback != null) {
                        _onFailCallback.call(null);
                    }
                }
            }
        }
    }

    public static final class Builder {

        private final BackgroundMail _instance;

        @RequiresPermission(allOf = {ACCESS_NETWORK_STATE, INTERNET})
        public Builder(Context context) {
            checkPermission(context, ACCESS_NETWORK_STATE);
            checkPermission(context, INTERNET);
            _instance = new BackgroundMail(context);
        }

        public Builder withUsername(String username) {
            checkNotNull(username);
            _instance._username = username;
            return this;
        }

        public Builder withPassword(String password) {
            checkNotNull(password);
            _instance._password = password;
            return this;
        }

        public Builder withMailto(String mailto) {
            checkNotNull(mailto);
            _instance._mailto = mailto;
            return this;
        }

        public Builder withSubject(String subject) {
            checkNotNull(subject);
            _instance._subject = subject;
            return this;
        }

        public Builder withType(@EmailType String type) {
            checkNotNull(type);
            _instance._type = type;
            return this;
        }

        public Builder withBody(String body) {
            checkNotNull(body);
            _instance._body = body;
            return this;
        }

        public Builder withAttachments(ArrayList<String> attachments) {
            checkNotNull(attachments);
            _instance._attachments.addAll(attachments);
            return this;
        }

        public Builder withAttachments(String... attachments) {
            checkNotNull(attachments);
            _instance._attachments.addAll(Arrays.asList(attachments));
            return this;
        }

        public Builder withProgressMessage(String progressMessage) {
            checkNotNull(progressMessage);
            _instance._progressMessage = progressMessage;
            return this;
        }

        public Builder withProgressDialog(boolean showProgressDialog) {
            _instance._showProgressDialog = showProgressDialog;
            return this;
        }

        public Builder withOnSuccessCallback(Callback<Void> onSuccessCallback) {
            checkNotNull(onSuccessCallback);
            _instance._onSuccessCallback = onSuccessCallback;
            return this;
        }

        public Builder withOnFailCallback(Callback<Void> onFailCallback) {
            checkNotNull(onFailCallback);
            _instance._onFailCallback = onFailCallback;
            return this;
        }

        public BackgroundMail build() {
            return _instance;
        }

        public void send() {
            build().send();
        }
    }

}

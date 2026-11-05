package jp.u15.scout;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.content.pm.PackageManager;
import android.webkit.*;
import android.view.View;
import android.widget.Toast;
import android.util.Base64;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class MainActivity extends Activity {
    private WebView webView;
    private PermissionRequest pendingRequest;
    private static final int CAMERA_REQUEST = 71;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        webView = new WebView(this);
        setContentView(webView);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        webView.addJavascriptInterface(new AndroidBridge(), "AndroidBridge");
        webView.setWebChromeClient(new WebChromeClient() {
            @Override public void onPermissionRequest(PermissionRequest request) {
                runOnUiThread(() -> {
                    if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        request.grant(request.getResources());
                    } else {
                        pendingRequest = request;
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST);
                    }
                });
            }
            @Override public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
                return super.onJsConfirm(view, url, message, result);
            }
        });
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("file:///android_asset/index.html");
    }

    @Override public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST && pendingRequest != null) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pendingRequest.grant(pendingRequest.getResources());
            } else {
                pendingRequest.deny();
                Toast.makeText(this, "カメラ権限が必要です", Toast.LENGTH_LONG).show();
            }
            pendingRequest = null;
        }
    }

    public class AndroidBridge {
        @JavascriptInterface public void saveTextFile(String name, String content, String mime) {
            try {
                File dir = new File(getExternalFilesDir(null), "exports");
                if (!dir.exists()) dir.mkdirs();
                File out = new File(dir, safeName(name));
                try (FileOutputStream fos = new FileOutputStream(out)) {
                    fos.write(content.getBytes(StandardCharsets.UTF_8));
                }
                runOnUiThread(() -> Toast.makeText(MainActivity.this,
                    "保存しました: " + out.getAbsolutePath(), Toast.LENGTH_LONG).show());
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "保存に失敗しました", Toast.LENGTH_SHORT).show());
            }
        }
        @JavascriptInterface public void saveDataUrl(String name, String dataUrl) {
            try {
                String encoded = dataUrl.substring(dataUrl.indexOf(",") + 1);
                byte[] bytes = Base64.decode(encoded, Base64.DEFAULT);
                File dir = new File(getExternalFilesDir(null), "exports");
                if (!dir.exists()) dir.mkdirs();
                File out = new File(dir, safeName(name));
                try (FileOutputStream fos = new FileOutputStream(out)) { fos.write(bytes); }
                runOnUiThread(() -> Toast.makeText(MainActivity.this,
                    "静止画をアプリ内に保存しました", Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "画像保存に失敗しました", Toast.LENGTH_SHORT).show());
            }
        }
    }

    private String safeName(String name) {
        return name == null ? "export.dat" : name.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    @Override public void onBackPressed() {
        if (webView != null && webView.canGoBack()) webView.goBack();
        else super.onBackPressed();
    }
}

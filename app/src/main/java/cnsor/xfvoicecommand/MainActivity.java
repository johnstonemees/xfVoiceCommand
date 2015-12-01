package cnsor.xfvoicecommand;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.view.View.OnClickListener;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.GrammarListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SpeechConstant;

public class MainActivity extends AppCompatActivity {
    private TextView resultView;
    private Button sumbitButton;
    private int ret;
    private String grammarIdFinal;
    //1.创建SpeechRecognizer对象
    private SpeechRecognizer mAsr = SpeechRecognizer.createRecognizer(MainActivity.this, null);
    //构建语法监听器
    private GrammarListener grammarListener = new GrammarListener() {
        @Override
        public void onBuildFinish(String grammarId, SpeechError error) {
            if (error == null) {
                if (!TextUtils.isEmpty(grammarId)) {
                    //构建语法成功，请保存grammarId用于识别
                    grammarIdFinal = grammarId;
                } else {
                    Log.d(MainActivity.ACTIVITY_SERVICE, "语法构建失败,错误码：" + error.getErrorCode());
                }
            }
        }
    };

    private RecognizerListener mRecognizerListener = new RecognizerListener(){
        @Override
        public void onBeginOfSpeech() {
            // TODO Auto-generated method stub

        }
        @Override
        public void onEndOfSpeech() {
            // TODO Auto-generated method stub

        }
        @Override
        public void onError(SpeechError arg0) {
            // TODO Auto-generated method stub

        }
        @Override
        public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
            // TODO Auto-generated method stub

        }
        @Override
        public void onResult(RecognizerResult arg0, boolean arg1) {
            // TODO Auto-generated method stub
            Log.d("语音识别结果为：",arg0.getResultString());

        }

        @Override
        public void onVolumeChanged(int i) {

        }
    };
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // 初始化讯飞接口
        SpeechUtility.createUtility(MainActivity.this, SpeechConstant.APPID + "=56516737");
        initResource();
        initAmr();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void initResource() {
        resultView = (TextView) findViewById(R.id.textView);
        sumbitButton = (Button) findViewById(R.id.button);


        sumbitButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ret = mAsr.startListening(mRecognizerListener);
                if (ret != ErrorCode.SUCCESS) {
                    Log.d(MainActivity.ACTIVITY_SERVICE, "识别失败,错误码: " + ret);
                }
            }
        });
    }

    public void initAmr(){
        //云端语法识别：如需本地识别请参照本地识别

        // ABNF语法示例，可以说”北京到上海”
        String mCloudGrammar = "#ABNF 1.0 UTF-8;" +
                "languagezh-CN;mode voice;" +
                "root $main;$main = $place1 到$place2 ;" +
                "$place1 = 北京 | 武汉 | 南京 | 天津 | 天京 | 东京;" +
                "$place2 = 上海 | 合肥; ";
        //2.构建语法文件
        mAsr.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
        ret = mAsr.buildGrammar("abnf", mCloudGrammar , grammarListener);
        if (ret != ErrorCode.SUCCESS){
            Log.d(MainActivity.ACTIVITY_SERVICE, "语法构建失败,错误码：" + ret);
        }else {
            Log.d(MainActivity.ACTIVITY_SERVICE,"语法构建成功");
        }
        //3.开始识别,设置引擎类型为云端
        mAsr.setParameter(SpeechConstant.ENGINE_TYPE, "cloud");
        //设置grammarId
        mAsr.setParameter(SpeechConstant.CLOUD_GRAMMAR, grammarIdFinal);

    }



    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://cnsor.xfvoicecommand/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://cnsor.xfvoicecommand/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}

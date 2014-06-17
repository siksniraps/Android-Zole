package lv.pineapple.zole;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class GameActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        TextView output = (TextView) findViewById(R.id.output);
        TextView hand = (TextView) findViewById(R.id.myHand);

    }

}

package lv.pineapple.zole;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button newGameButton = (Button) findViewById(R.id.newGame);
        newGameButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.newGame: {
                Intent intent;
                intent = new Intent(this, GameActivity.class);
                startActivity(intent);
            }
        }
    }


}

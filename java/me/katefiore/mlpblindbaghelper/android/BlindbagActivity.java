package me.katefiore.mlpblindbaghelper.android;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import me.katefiore.mlpblindbaghelper.R;
import me.katefiore.mlpblindbaghelper.base.Blindbag;
import me.katefiore.mlpblindbaghelper.base.BlindbagCollectionParser;
import me.katefiore.mlpblindbaghelper.base.Static;

import java.io.IOException;

/**
 * @author cab404
 */
public class BlindbagActivity extends Activity {
	public static final String BLINDBAG_INDEX = "blindbag_index";
	Blindbag blindbag;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.blindbag_info);

		int index = getIntent().getIntExtra(BLINDBAG_INDEX, 0);

		blindbag = Static.index.get(index);


		/* Выставляем всякую фигню. */
        setTitle(blindbag.name);
        ((TextView) findViewById(R.id.id))
				.setText(blindbag.id.toUpperCase());
		((TextView) findViewById(R.id.wave))
				.setText(getString(R.string.wave) + " " + blindbag.wave);

		for (String img : blindbag.images) {
			addImage(BlindbagCollectionParser.IMAGES + "/" + img);
		}

	}

	private void addImage(String path) {
		ImageView image = new ImageView(this);
		((LinearLayout) findViewById(R.id.images)).addView(image);
		image.getLayoutParams().width =
				image.getLayoutParams().height =
						getResources().getDimensionPixelSize(R.dimen.blindbag_image_height);

		try {
			image.setImageDrawable(
					new BitmapDrawable(getResources(),
							BitmapFactory.decodeStream(
									getAssets()
											.open(path)
							)
					)
			);
		} catch (IOException e) {
				/* если картинка не найдена, ставим иконку приложения. Лол.*/
			Log.w("SearchAdapter", "Не найдена картинка для " + blindbag.wave + "." + blindbag.id + ":" + path);
			image.setImageResource(R.drawable.ic_launcher);
		}
	}
}

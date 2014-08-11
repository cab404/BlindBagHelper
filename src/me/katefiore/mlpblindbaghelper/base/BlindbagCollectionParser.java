package me.katefiore.mlpblindbaghelper.base;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Парсирует блайнбэги из asset-ов.
 *
 * @author cab404
 */
public class BlindbagCollectionParser {
	public final static String WAVES = "waves", INDEX_FILE = "index";

	private Context context;
	public BlindbagCollectionParser(Context context) {
		this.context = context;
	}

	public List<Blindbag> parse() {
		List<Blindbag> blindbags = new ArrayList<Blindbag>();
		AssetManager assets = context.getResources().getAssets();


		try {

			for (String wave : assets.list(WAVES)) {

				BufferedReader reader = new BufferedReader(
						new InputStreamReader(
								assets.open(WAVES + "/" + wave + "/" + INDEX_FILE, AssetManager.ACCESS_BUFFER
								)
						)
				);

				int wave_color = Color.parseColor(reader.readLine());

				String line;
				while ((line = reader.readLine()) != null) {
					int split = line.indexOf(':');
					Blindbag blindbag = new Blindbag(
							Integer.parseInt(wave),
							line.substring(0, split).trim(),
							line.substring(split + 1).trim(),
							wave_color);
					blindbags.add(blindbag);
				}

			}

		} catch (IOException e) {
			throw new RuntimeException("Ошибка импорта блайнбэгов!", e);
		}

		Log.v("BlindbagCollectionParser", "Импортировано блайндбэгов: " + blindbags.size() + "");

		return blindbags;
	}
}

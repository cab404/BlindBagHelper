package me.katefiore.mlpblindbaghelper.base;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
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

		String[] waves;

		try {
			waves = assets.list(WAVES);
		} catch (IOException e) {
			throw new RuntimeException("Невозможно получить список приложенных ресурсов!");
		}

		for (String wave : waves) {

			try {
				BufferedReader reader;
				try {
					reader = new BufferedReader(
							new InputStreamReader(
									assets.open(WAVES + "/" + wave + "/" + INDEX_FILE, AssetManager.ACCESS_BUFFER)
							)
					);
				} catch (FileNotFoundException e) {
					Log.w("BlindbagCollectionParser", "Найдена папка " + wave + ", но в ней нет index-файла. Пропускаю.");
					continue;
				}


				StringBuilder text = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null)
					text.append(line);

				JSONObject json = new JSONObject(text.toString());

				int wave_color = Color.parseColor(json.getString("color"));
				JSONArray entries = (JSONArray) json.get("entries");

				for (int i = 0; i < entries.length(); i++) {
					JSONObject entry = (JSONObject) entries.get(i);
					JSONArray images = (JSONArray) entry.get("images");
					String[] images_array = new String[images.length()];

					for (int j = 0; j < images.length(); j++) {
						images_array[j] = images.getString(i);
					}

					blindbags.add(new Blindbag(
							Integer.parseInt(wave),
							entry.getString("id"),
							entry.getString("name"),
							wave_color,
							images_array
					));
				}

			} catch (IOException | NullPointerException | JSONException e) {
				Log.e("BlindbagCollectionParser", "Ошибка импорта блайнбэгов из папки " + wave, e);
			}

		}


		Log.v("BlindbagCollectionParser", "Импортировано блайндбэгов: " + blindbags.size() + "");

		return blindbags;
	}
}

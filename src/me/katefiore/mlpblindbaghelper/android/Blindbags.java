package me.katefiore.mlpblindbaghelper.android;

import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import me.katefiore.mlpblindbaghelper.R;
import me.katefiore.mlpblindbaghelper.base.Blindbag;
import me.katefiore.mlpblindbaghelper.base.BlindbagCollectionParser;
import me.katefiore.mlpblindbaghelper.base.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Blindbags extends Activity {

	SearchAdapter search_adapter;
	List<String> waves;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		ListView list = (ListView) findViewById(R.id.results);

		try {
			waves = Arrays.asList(getAssets().list(BlindbagCollectionParser.WAVES));
		} catch (IOException e) {
			throw new RuntimeException("OK, no assets.", e);
		}

		/* Выбиралка волны */
		ListView select_wave = (ListView) findViewById(R.id.wave_menu).findViewById(R.id.waves);
		select_wave.setAdapter(new WaveListAdapter(waves));

		search_adapter = new SearchAdapter();
		list.setAdapter(search_adapter);

		TextView textView = new TextView(this);

		list.setEmptyView(textView);

		/* Ищем при изменении запроса. */
		((EditText) findViewById(R.id.request)).addTextChangedListener(new TextWatcher() {
			@Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			@Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
			@Override public void afterTextChanged(Editable s) {
				if (s.length() == 0) {
					findViewById(R.id.wave_menu).setVisibility(View.VISIBLE);
					search_adapter.clear();
				} else {
					findViewById(R.id.wave_menu).setVisibility(View.GONE);
					search_adapter.search(s.toString());
				}
			}
		});


	}

	public void help(View view) {
		startActivity(new Intent(this, Help.class));
	}

	private void search(String term) {
		((EditText) findViewById(R.id.request)).setText(term);
		search_adapter.search(term);
	}

	/**
	 * Занимается всяческим поиском.
	 */
	private class SearchAdapter extends BaseAdapter {
		List<Blindbag> query;
		List<Blindbag> reduced;

		public SearchAdapter() {
			query = new BlindbagCollectionParser(Blindbags.this).parse();
			reduced = new ArrayList<Blindbag>();
		}

		public void search(String request) {
			reduced.clear();

			search:
			for (Blindbag blindbag : query) {
				for (String part : StringUtils.splitToArray(request, ' '))
					if (!blindbag.matchesPattern(part))
						continue search;
				reduced.add(blindbag);
			}

			Collections.sort(reduced);
			notifyDataSetChanged();
		}

		public void clear() {
			reduced.clear();
			notifyDataSetChanged();
		}

		@Override public int getCount() {
			return reduced.size();
		}

		@Override public Object getItem(int position) {
			return reduced.get(position);
		}

		@Override public long getItemId(int position) {
			return position;
		}

		@Override public View getView(int position, View convertView, ViewGroup parent) {
			Blindbag blindbag = reduced.get(position);

			if (convertView == null) {
				LayoutInflater inflater = LayoutInflater.from(parent.getContext());
				convertView = inflater.inflate(R.layout.blindbag, parent, false);
			}

			/* Выставляем всякую фигню. */
			((TextView) convertView.findViewById(R.id.id)).setText(blindbag.id);
			((TextView) convertView.findViewById(R.id.title)).setText(blindbag.name);
			((TextView) convertView.findViewById(R.id.wave)).setText(getString(R.string.wave) + " " + blindbag.wave);

			/* Пытаемся достать картинку из asset-ов и выставить. */
			try {
				((ImageView) convertView.findViewById(R.id.image))
						.setImageDrawable(
								new BitmapDrawable(getResources(),
										BitmapFactory.decodeStream(
												getAssets()
														.open(
																BlindbagCollectionParser.WAVES + "/"
																		+ blindbag.wave + "/"
																		+ blindbag.id + ".png"
														)
										)
								)
						);
			} catch (IOException e) {
				/* если картинка не найдена, ставим иконку приложения. Лол.*/
				Log.w("SearchAdapter", "Не найдена картинка для " + blindbag.wave + "." + blindbag.id);
				((ImageView) convertView.findViewById(R.id.image)).setImageResource(R.drawable.ic_launcher);
			}

			convertView.findViewById(R.id.wave)
					.getBackground().setColorFilter(blindbag.wave_color, PorterDuff.Mode.MULTIPLY);

			return convertView;
		}

		@Override public void registerDataSetObserver(DataSetObserver observer) {
			if (observer != null)
				super.registerDataSetObserver(observer);
		}

		@Override public void unregisterDataSetObserver(DataSetObserver observer) {
			if (observer != null)
				super.unregisterDataSetObserver(observer);
		}

	}

	/**
	 * Выьиралка волны.
	 */
	private class WaveListAdapter extends BaseAdapter {
		private List<String> list;
		public WaveListAdapter(List<String> list) {
			this.list = list;
		}
		@Override public int getCount() {
			return list.size();
		}
		@Override public Object getItem(int position) {
			return list.get(position);
		}
		@Override public long getItemId(int position) {
			return position;
		}
		@Override public View getView(int position, View convertView, ViewGroup parent) {

			final String wave = list.get(position);
			if (convertView == null)
				convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.wave, parent, false);

			((TextView) convertView.findViewById(R.id.title)).setText(getString(R.string.wave) + " " + wave);

			/* Пытаемся достать картинку из asset-ов и выставить. */
			try {
				((ImageView) convertView.findViewById(R.id.image))
						.setImageDrawable(
								new BitmapDrawable(getResources(),
										BitmapFactory.decodeStream(
												getAssets()
														.open(
																BlindbagCollectionParser.WAVES + "/"
																		+ wave + "/"
																		+ "bag.png"
														)
										)
								)
						);
			} catch (IOException e) {
				/* если картинка не найдена, ставим иконку приложения. Лол.*/
				Log.w("WaveListAdapter", "Не найдена картинка для " + wave);
				((ImageView) convertView.findViewById(R.id.image)).setImageResource(R.drawable.ic_launcher);
			}

			convertView.setOnClickListener(new View.OnClickListener() {
				@Override public void onClick(View v) {
					search("wave:" + wave);
				}
			});

			return convertView;
		}
	}


}

package me.katefiore.mlpblindbaghelper.android;

import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.BitmapFactory;
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
import me.katefiore.mlpblindbaghelper.base.Static;
import me.katefiore.mlpblindbaghelper.base.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Blindbags extends Activity {

	SearchAdapter search_adapter;
	List<Integer> waves;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		ListView list = (ListView) findViewById(R.id.results);

		Static.index = new BlindbagCollectionParser(Blindbags.this).parse();
		;

		/* Поисковик по блайндбэгам */
		search_adapter = new SearchAdapter();
		list.setAdapter(search_adapter);

		/* Выбиралка волны */
		waves = new ArrayList();
		for (Blindbag bag : Static.index)
			if (!waves.contains(bag.wave))
				waves.add(bag.wave);
		Collections.sort(waves);

		ListView select_wave = (ListView) findViewById(R.id.wave_menu).findViewById(R.id.waves);
		select_wave.setAdapter(new WaveListAdapter(waves));

		/* Включаем поиск при изменении запроса. */
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
	}

	/**
	 * Занимается всяческим поиском.
	 */
	private class SearchAdapter extends BaseAdapter {
		List<Blindbag> reduced;

		public SearchAdapter() {
			reduced = new ArrayList();
		}

		public void search(String request) {
			reduced.clear();

			search:
			for (Blindbag blindbag : Static.index) {
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
			final Blindbag blindbag = reduced.get(position);

			if (convertView == null) {
				LayoutInflater inflater = LayoutInflater.from(parent.getContext());
				convertView = inflater.inflate(R.layout.blindbag, parent, false);
			}

			convertView.setOnClickListener(new View.OnClickListener() {
				@Override public void onClick(View v) {
					startActivity(
							new Intent(Blindbags.this, BlindbagActivity.class)
									.putExtra(
											BlindbagActivity.BLINDBAG_INDEX,
											Static.index.indexOf(blindbag)
									)
					);
				}
			});

			/* Выставляем всякую фигню. */
			((TextView) convertView.findViewById(R.id.id))
					.setText(blindbag.id);
			((TextView) convertView.findViewById(R.id.title))
					.setText(blindbag.name);
			((TextView) convertView.findViewById(R.id.wave))
					.setText(getString(R.string.wave) + " " + blindbag.wave);

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

//			convertView.findViewById(R.id.wave)
//					.getBackground().setColorFilter(blindbag.wave_color, PorterDuff.Mode.MULTIPLY);

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

	@Override public void onBackPressed() {
		if (((EditText) findViewById(R.id.request)).getText().length() == 0)
			super.onBackPressed();
		else
			((EditText) findViewById(R.id.request)).setText("");
	}
	/**
	 * Выбиралка волны.
	 */
	private class WaveListAdapter extends BaseAdapter {
		private List<Integer> list;
		public WaveListAdapter(List<Integer> list) {
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

			final int wave = list.get(position);
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

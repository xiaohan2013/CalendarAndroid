package com.example.calendar_02;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Bundle;
import android.R.integer;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.widget.AbsListView.LayoutParams;

public class MainActivity extends Activity implements OnGestureListener {
	
	private ViewFlipper flipper1 = null;
	private GridView gridView = null;
	private GestureDetector gestureDetector = null;
	private int year_c = 0;
	private int month_c = 0;
	private int day_c = 0;//当天日期
	private int week_c = 0;
	private int week_num = 0;
	private String currentDate = "";
	private static int jumpWeek = 0;
	private static int jumpMonth = 0;
	private static int jumpYear = 0;
	private DateAdapter dateAdapter;
	private int daysOfMonth = 0; // 某月的天数
	private int dayOfWeek = 0; // 具体某月的第一天是星期几
	private int weeksOfMonth = 0;
	private SpecialCalendar sc = null;
	private boolean isLeapyear = false; // 是否为闰年
	private int selectPostion = 0;
	private String dayNumbers[] = new String[7];
	private TextView tvDate;
	private int currentYear;
	private int currentMonth;
	private int currentWeek;
	private int currentDay;
	private int currentNum;
	private boolean isStart;// 是否是交接的月初

	public MainActivity() {
		
		
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
		currentDate = sdf.format(date);
		
		
		year_c = Integer.parseInt(currentDate.split("-")[0]);
		month_c = Integer.parseInt(currentDate.split("-")[1]);
		day_c = Integer.parseInt(currentDate.split("-")[2]);//9
		
		
		currentYear = year_c;
		currentMonth = month_c;
		currentDay = day_c;
		
		sc = new SpecialCalendar();
		
		getCalendar(year_c, month_c);
		
		week_num = getWeeksOfMonth();//12月份week_num是5
	
		currentNum = week_num;//5
		
		if (dayOfWeek == 7) {
			
			week_c = day_c / 7 + 1;
			
		} else {
			if (day_c <= (7 - dayOfWeek)) {
				week_c = 1;
			} else {
				if ((day_c - (7 - dayOfWeek)) % 7 == 0) {
					
					week_c = (day_c - (7 - dayOfWeek)) / 7 + 1;
					
					Log.i("life", "day_c = "+ day_c );
					
				} else {
					week_c = (day_c - (7 - dayOfWeek)) / 7 + 2;
				}
			}
		}
		
		currentWeek = week_c;
		
		Log.i("life", " currentWeek = "+currentWeek );
		
		getCurrent();

	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		
		tvDate = (TextView) findViewById(R.id.tv_date);
		tvDate.setText(year_c + "年" + month_c + "月" + day_c + "日");
		
		
		gestureDetector = new GestureDetector(this);
		flipper1 = (ViewFlipper) findViewById(R.id.flipper1);
		dateAdapter = new DateAdapter(this, getResources(), currentYear,
				currentMonth, currentWeek, currentNum, selectPostion,
				currentWeek == 1 ? true : false);
		addGridView();
		dayNumbers = dateAdapter.getDayNumbers();
		gridView.setAdapter(dateAdapter);
		selectPostion = dateAdapter.getTodayPosition();
		gridView.setSelection(selectPostion);
		flipper1.addView(gridView, 0);
	}
	

	/**
	 * 判断某年某月所有的星期数
	 * 
	 * @param year
	 * @param month
	 */
	public int getWeeksOfMonth(int year, int month) {
		// 先判断某月的第一天为星期几
		int preMonthRelax = 0;
		int dayFirst = getWhichDayOfWeek(year, month);
		int days = sc.getDaysOfMonth(sc.isLeapYear(year), month);
		if (dayFirst != 7) {
			preMonthRelax = dayFirst;
		}
		if ((days + preMonthRelax) % 7 == 0) {
			weeksOfMonth = (days + preMonthRelax) / 7;
		} else {
			weeksOfMonth = (days + preMonthRelax) / 7 + 1;
		}
		return weeksOfMonth;

	}

	/**
	 * 判断某年某月的第一天为星期几
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public int getWhichDayOfWeek(int year, int month) {
		return sc.getWeekdayOfMonth(year, month);

	}

	/**
	 * 
	 * @param year
	 * @param month
	 */
	public int getLastDayOfWeek(int year, int month) {
		
		return sc.getWeekDayOfLastMonth(year, month,
				sc.getDaysOfMonth(isLeapyear, month));
	}

	public void getCalendar(int year, int month) {
		isLeapyear = sc.isLeapYear(year); // 是否为闰年
		daysOfMonth = sc.getDaysOfMonth(isLeapyear, month); // 某月的总天数
		dayOfWeek = sc.getWeekdayOfMonth(year, month); // 某月第一天为星期几
		
		Log.i("life", " dayOfWeek = "+dayOfWeek );
	}

	public int getWeeksOfMonth() {
		
		int preMonthRelax = 0;
		if (dayOfWeek != 7) {//2014年的12月1号是星期一，所以preMonthRelax的值是1
			preMonthRelax = dayOfWeek;
		}
		
		
		
		if ((daysOfMonth + preMonthRelax) % 7 == 0) {
			weeksOfMonth = (daysOfMonth + preMonthRelax) / 7;
		} else {
			weeksOfMonth = (daysOfMonth + preMonthRelax) / 7 + 1;//所以2014年的12月的weeksOfMonth值是1
		}
		
		Log.i("life", " weeksOfMonth = "+weeksOfMonth );
		
		return weeksOfMonth;
	}

	

	private void addGridView() {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		gridView = new GridView(this);
		gridView.setNumColumns(7);
		gridView.setGravity(Gravity.CENTER_VERTICAL);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		gridView.setVerticalSpacing(1);
		gridView.setHorizontalSpacing(1);
		gridView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return MainActivity.this.gestureDetector.onTouchEvent(event);
			}
		});

		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
			
				selectPostion = position;
				dateAdapter.setSeclection(position);
				dateAdapter.notifyDataSetChanged();
				tvDate.setText(dateAdapter.getCurrentYear(selectPostion) + "年"
						+ dateAdapter.getCurrentMonth(selectPostion) + "月"
						+ dayNumbers[position] + "日");
			}
		});
		gridView.setLayoutParams(params);
	}

	
	@Override
	protected void onPause() {
		super.onPause();
		jumpWeek = 0;
	}

	@Override
	public boolean onDown(MotionEvent e) {

		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {

	}

	/**
	 * 重新计算当前的年月
	 */
	public void getCurrent() {
		
		if (currentWeek > currentNum) {
			if (currentMonth + 1 <= 12) {
				currentMonth++;
			} else {
				currentMonth = 1;
				currentYear++;
			}
			currentWeek = 1;
			currentNum = getWeeksOfMonth(currentYear, currentMonth);
		} else if (currentWeek == currentNum) {
			if (getLastDayOfWeek(currentYear, currentMonth) == 6) {
			} else {
				if (currentMonth + 1 <= 12) {
					currentMonth++;
				} else {
					currentMonth = 1;
					currentYear++;
				}
				currentWeek = 1;
				currentNum = getWeeksOfMonth(currentYear, currentMonth);
			}

		} else if (currentWeek < 1) {
			if (currentMonth - 1 >= 1) {
				currentMonth--;
			} else {
				currentMonth = 12;
				currentYear--;
			}
			currentNum = getWeeksOfMonth(currentYear, currentMonth);
			currentWeek = currentNum - 1;
		}
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		int gvFlag = 0;
		if (e1.getX() - e2.getX() > 80) {
			// 向左滑
			addGridView();
			currentWeek++;
			getCurrent();
			dateAdapter = new DateAdapter(this, getResources(), currentYear,
					currentMonth, currentWeek, currentNum, selectPostion,
					currentWeek == 1 ? true : false);
			dayNumbers = dateAdapter.getDayNumbers();
			gridView.setAdapter(dateAdapter);
			tvDate.setText(dateAdapter.getCurrentYear(selectPostion) + "年"
					+ dateAdapter.getCurrentMonth(selectPostion) + "月"
					+ dayNumbers[selectPostion] + "日");
			gvFlag++;
			flipper1.addView(gridView, gvFlag);
			dateAdapter.setSeclection(selectPostion);
			this.flipper1.setInAnimation(AnimationUtils.loadAnimation(this,
					R.anim.push_left_in));
			this.flipper1.setOutAnimation(AnimationUtils.loadAnimation(this,
					R.anim.push_left_out));
			this.flipper1.showNext();
			flipper1.removeViewAt(0);
			return true;

		} else if (e1.getX() - e2.getX() < -80) {
			addGridView();
			currentWeek--;
			getCurrent();
			dateAdapter = new DateAdapter(this, getResources(), currentYear,
					currentMonth, currentWeek, currentNum, selectPostion,
					currentWeek == 1 ? true : false);
			dayNumbers = dateAdapter.getDayNumbers();
			gridView.setAdapter(dateAdapter);
			tvDate.setText(dateAdapter.getCurrentYear(selectPostion) + "年"
					+ dateAdapter.getCurrentMonth(selectPostion) + "月"
					+ dayNumbers[selectPostion] + "日");
			gvFlag++;
			flipper1.addView(gridView, gvFlag);
			dateAdapter.setSeclection(selectPostion);
			this.flipper1.setInAnimation(AnimationUtils.loadAnimation(this,
					R.anim.push_right_in));
			this.flipper1.setOutAnimation(AnimationUtils.loadAnimation(this,
					R.anim.push_right_out));
			this.flipper1.showPrevious();
			flipper1.removeViewAt(0);
			return true;
			// }
		}
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return this.gestureDetector.onTouchEvent(event);
	}

}

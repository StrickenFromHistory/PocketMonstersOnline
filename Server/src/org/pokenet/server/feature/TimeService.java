package org.pokenet.server.feature;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Calendar;
import java.util.Random;
import java.util.StringTokenizer;

import org.pokenet.server.battle.mechanics.statuses.field.FieldEffect;
import org.pokenet.server.battle.mechanics.statuses.field.HailEffect;
import org.pokenet.server.battle.mechanics.statuses.field.RainEffect;
import org.pokenet.server.battle.mechanics.statuses.field.SandstormEffect;

/**
 * Handles game time and weather
 * @author shadowkanji
 *
 */
public class TimeService implements Runnable {
	private boolean m_isRunning;
	private long m_lastWeatherUpdate;
	private int m_forcedWeather=0;
	private Thread m_thread;
	private static int m_hour;
	private static int m_minutes;
	private static int m_day = 0;
	private static Weather m_weather;
	
	/*
	 * NOTE: HAIL = SNOW
	 */
	public enum Weather { NORMAL, RAIN, HAIL, SANDSTORM, FOG }
	
	/**
	 * Default constructor
	 */
	public TimeService() {
		/*
		 * Generate random weather
		 */
		int weather = new Random().nextInt(4);
		switch(weather) {
		case 0:
			m_weather = Weather.NORMAL;
			break;
		case 1:
			m_weather = Weather.RAIN;
			break;
		case 2:
			m_weather = Weather.HAIL;
			break;
		case 3:
			m_weather = Weather.FOG;
			break;
		case 4:
			m_weather = Weather.SANDSTORM;
			break;
		default:
			m_weather = Weather.NORMAL;
		}
		m_lastWeatherUpdate = System.currentTimeMillis();
		m_thread = new Thread(this);
	}
	
	/**
	 * Called by m_thread.start()
	 */
	public void run() {
		try {
			/*
			 * Parses time from a common server.
			 * The webpage should just have text (no html tags) in the form:
			 * DAY HOUR MINUTES
			 * where day is a number from 0 - 6
			 */
			URL url = new URL("http://pokedev.org/time.php");
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			StringTokenizer s = new StringTokenizer(in.readLine());
			m_day = Integer.parseInt(s.nextToken());
			m_hour = Integer.parseInt(s.nextToken());
			m_minutes = Integer.parseInt(s.nextToken());
			in.close();
		} catch (Exception e) {
			System.out.println("ERROR: Cannot reach time server, reverting to local time");
			/* Can't reach website, base time on local */
			Calendar cal = Calendar.getInstance();
			m_hour = cal.get(Calendar.HOUR_OF_DAY);
			m_minutes = 0;
			m_day = 0;
		}
		while(m_isRunning) {
			//Update the time. Time moves 4 times faster.
			m_minutes = m_minutes == 59 ? 0 : m_minutes + 1;
			if(m_minutes == 0) {
				if(m_hour == 23) {
					incrementDay();
					m_hour = 0;
				} else {
					m_hour += 1;
				}
			}
				m_hour = m_hour == 23 ? 0 : m_hour + 1;
			//Check if weather should be updated
			if(System.currentTimeMillis() - m_lastWeatherUpdate >= 3600000) {
				generateWeather();
				m_lastWeatherUpdate = System.currentTimeMillis();
			}
			try {
				Thread.sleep(60000);
			} catch (Exception e) {}
		}
		System.out.println("INFO: Time Service stopped");
	}
	
	/**
	 * Increments the day on the server
	 */
	public void incrementDay() {
		m_day = m_day == 6 ? 0 : m_day + 1;
	}
	
	/**
	 * Starts this Time Service
	 */
	public void start() {
		m_isRunning = true;
		m_thread.start();
		System.out.println("INFO: Time Service started");
	}

	/**
	 * Stops this Time Service
	 */
	public void stop() {
		m_isRunning = false;
	}
	
	/**
	 * Generates a new weather status
	 */
	private void generateWeather() {
		/*
		 * Generate random weather
		 */
		int weather = m_forcedWeather;
		if(weather == 9)
			weather = new Random().nextInt(4);
		switch(weather) {
		case 0:
			m_weather = Weather.NORMAL;
			break;
		case 1:
			m_weather = Weather.RAIN;
			break;
		case 2:
			m_weather = Weather.HAIL;
			break;
		case 3:
			m_weather = Weather.FOG;
			break;
		case 4:
			m_weather = Weather.SANDSTORM;
			break;
		default:
			m_weather = Weather.NORMAL;
		}
	}
	
	/**
	 * Returns the field effect based on current weather
	 * @return
	 */
	public static FieldEffect getWeatherEffect() {
		switch(m_weather) {
		case NORMAL:
			return null;
		case RAIN:
			return new RainEffect();
		case HAIL:
			return new HailEffect();
		case SANDSTORM:
			return new SandstormEffect();
		case FOG:
			return null;
		default:
			return null;
		}
	}
	
	/**
	 * Returns a string representation of the current time, e.g. 1201
	 * @return
	 */
	public static String getTime() {
		return "" + (m_hour < 10 ? "0" + m_hour : m_hour) + (m_minutes < 10 ? "0" + m_minutes : m_minutes);
	}
	
	/**
	 * Returns the hour of the day (game time)
	 * @return
	 */
	public static int getHourOfDay() {
		return m_hour;
	}
	
	/**
	 * Returns the current minute (game time)
	 * @return
	 */
	public static int getMinuteOfDay() {
		return m_minutes;
	}
	
	/**
	 * Returns true if it is night time
	 * @return
	 */
	public static boolean isNight() {
		return m_hour >= 20 || m_hour < 6;
	}
	
	/**
	 * Returns the current Weather. 
	 * @return
	 */
	public int getForcedWeather() {
		return m_forcedWeather;
	}
	
	/**
	 * Sets the weather. 
	 * 0: Weather.NORMAL;
	 * 1: Weather.RAIN;
	 * 2: Weather.HAIL;
	 * 3: Weather.FOG;
	 * 9: Weather.RANDOM;
	 * @return
	 */
	public void setForcedWeather(int mForcedWeather) {
		m_forcedWeather = mForcedWeather;
		m_lastWeatherUpdate = 0;
	}

	/**
	 * Returns the id of the weather
	 * @return
	 */
	public static int getWeatherId() {
		switch(m_weather) {
		case NORMAL:
			return 0;
		case RAIN:
			return 1;
		case HAIL:
			return 2;
		case SANDSTORM:
			return 3;
		case FOG:
			return 4;
		default:
			return 0;
		}
	}
}

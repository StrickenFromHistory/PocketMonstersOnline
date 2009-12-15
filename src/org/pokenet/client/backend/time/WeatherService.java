package org.pokenet.client.backend.time;

import java.io.File;

import org.newdawn.slick.loading.LoadingList;
import org.newdawn.slick.particles.ParticleIO;
import org.newdawn.slick.particles.ParticleSystem;

/**
 * Handles rendering of weather
 * @author shadowkanji
 *
 */
public class WeatherService {
	public enum Weather { NORMAL, RAIN, HAIL, SANDSTORM, FOG }
	private Weather m_weather;
	private boolean m_isEnabled = true;
	private ParticleSystem [] m_systems;
	
	/**
	 * Default constructor, loads all particle systems
	 */
	public WeatherService() {
		m_weather = Weather.NORMAL;
		/*
		 * Load a particle system for each weather.
		 * Changing one system proves difficult
		 */
		m_systems = new ParticleSystem[4];
		
		String respath = System.getProperty("res.path");
		if(respath==null)
			respath="";
		
		try {
			LoadingList.setDeferredLoading(true);
			File f;
			//Rain
			f = new File(respath+"res/effects/rain/rain.xml");
			m_systems[0] = ParticleIO.loadConfiguredSystem(f);
			//Hail
			f = new File(respath+"res/effects/hail/snow.xml");
			m_systems[1] = ParticleIO.loadConfiguredSystem(f);
			//Sandstorm
			f = new File(respath+"res/effects/sandstorm/sand.xml");
			m_systems[2] = ParticleIO.loadConfiguredSystem(f);
			//Fog
			f = new File(respath+"res/effects/fog/fog.xml");
			m_systems[3] = ParticleIO.loadConfiguredSystem(f);
			LoadingList.setDeferredLoading(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns a particle system based on the current weather
	 * @return
	 */
	public ParticleSystem getParticleSystem() {
		switch(m_weather) {
		case RAIN:
			return m_systems[0];
		case HAIL:
			return m_systems[1];
		case SANDSTORM:
			return m_systems[2];
		case FOG:
			return m_systems[3];
		case NORMAL:
			return null;
		default:
			return null;
		}
	}
	
	/**
	 * Returns the current weather
	 * @return
	 */
	public Weather getWeather() {
		return m_weather;
	}

	/**
	 * Sets the current weather
	 * @param w
	 */
	public void setWeather(Weather w) {
		m_weather = w;
	}
	
	/**
	 * Returns if the user has weather effects enabled
	 * @return
	 */
	public boolean isEnabled() {
		return m_isEnabled;
	}
	
	/**
	 * Sets if weather effects are enabled
	 * @param b
	 */
	public void setEnabled(boolean b) {
		m_isEnabled = b;
	}
}

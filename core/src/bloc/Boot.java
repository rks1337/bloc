package bloc;

import route.Assets;
import route.Audio;
import screen.MainMenu;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

public class Boot extends Game {

	public static Assets assets;
	private String sfx_ = Base64Coder.encodeString("6huj52g6");
	private String bgm_ = Base64Coder.encodeString("fgh65jsd");
	private String beer = Base64Coder.encodeString("hgdftrwe");

	@Override
	public void create() {
		//Gdx.app.log(TITLE, "started..");

		setScreen(new Splash());
		Timer.schedule(new Task(){
			@Override
			public void run() {
				Preferences prefs = Gdx.app.getPreferences("Bloc");
				assets = new Assets();
				assets.load();
				assets.manager.finishLoading();

				Audio.create();
				prefs.putString("CAUTION: ", "DO NOT EDIT THIS FILE! DOING SO WILL LEAD TO LOST OR NO GAME FUNCTIONALITY!");
				prefs.flush();

				if (!Base64Coder.decodeString(prefs.getString(sfx_)).equals("69696969")) {
					prefs.putString(sfx_, beer);
					prefs.flush();
				}
				if (!Base64Coder.decodeString(prefs.getString(bgm_)).equals("69696969")) {
					Audio.bgm.play();
					Audio.bgm.setLooping(true);
					prefs.putString(bgm_, beer);
					prefs.flush();
				}
				setScreen(new MainMenu());
			}
		}, 2f);

		//		// Debug mode
		//		Preferences prefs = Gdx.app.getPreferences("Bloc");
		//		assets = new Assets();
		//		assets.load();
		//		assets.manager.finishLoading();
		//		Audio.create();
		//
		//		prefs.putString("CAUTION: ", "DO NOT EDIT THIS FILE! DOING SO WILL LEAD TO LOST OR NO GAME FUNCTIONALITY!");
		//		prefs.flush();
		//
		//		if (!Base64Coder.decodeString(prefs.getString(sfx_)).equals("69696969")) {
		//			prefs.putString(sfx_, beer);
		//			prefs.flush();
		//		}
		//		if (!Base64Coder.decodeString(prefs.getString(bgm_)).equals("69696969")) {
		//			Audio.bgm.play();
		//			Audio.bgm.setLooping(true);
		//			prefs.putString(bgm_, beer);
		//			prefs.flush();
		//		}
		//
		//		setScreen(new Splash());
	}

	@Override
	public void dispose() {
		super.dispose();
		//Gdx.app.log(TITLE, "dispose()");
	}

	@Override
	public void render() {
		super.render();
		//Gdx.app.log(TITLE, "render()");
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		//Gdx.app.log(TITLE, "resize(..)");
	}

	@Override
	public void pause(){
		super.pause();
		//Gdx.app.log(TITLE, "pause()");
	}

	@Override
	public void resume(){
		super.resume();
		//Gdx.app.log(TITLE, "resume()");
	}

	public Assets getAssets() {
		return assets;
	}
}
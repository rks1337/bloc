package route;

import bloc.Boot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class Audio {

	public static Music bgm;
	public static Sound bloc;
	public static Sound freeze;
	public static Sound reset;

	private final static Boot game = (Boot) Gdx.app.getApplicationListener();
	private final static Assets assets = game.getAssets();

	public static void create() {
		bgm = assets.manager.get(Assets.bgm);
		bloc = assets.manager.get(Assets.bloc);
		freeze = assets.manager.get(Assets.freeze);
		reset = assets.manager.get(Assets.reset);
	}

	public static void dispose() {
		bgm.dispose();
		bloc.dispose();
		freeze.dispose();
		reset.dispose();
	}
}

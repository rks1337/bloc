package bloc.bloc.screen;

import java.text.DecimalFormat;

import bloc.bloc.bloc.Boot;
import bloc.bloc.route.Assets;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Stats implements Screen {
	private static final float TIMESTEP = 1 / 60f;
	private static final int VELOCITYITERATIONS = 1, POSITIONITERATIONS = 6;

	private World world;
	private Box2DDebugRenderer debugRenderer;
	private SpriteBatch batch;
	private OrthographicCamera camera;

	private Stage hud;
	private Table table0;
	private Table table1;
	private Table table2;

	private BitmapFont font;

	private final Boot game = (Boot) Gdx.app.getApplicationListener();
	private final Assets assets = game.getAssets();

	private Preferences prefs = Gdx.app.getPreferences("Bloc");

	private int best_medi_height;

	private String best_medi = Base64Coder.encodeString("fe5yhh44");

	@Override
	public void show() {

		// first load
		if (prefs.getString(best_medi) == "") {
			prefs.putString(best_medi, Base64Coder.encodeString(Float.toString(0)));
			prefs.flush();
		}

		boolean naughty = false;
		try {
			best_medi_height = (int) Float.parseFloat(Base64Coder.decodeString(prefs.getString(best_medi)));
		} catch (NumberFormatException n) {
			//System.out.println(n + "..resetting stats");
			naughty = true;
		} catch (IllegalArgumentException i) {
			//System.out.println(i + "..resetting stats");
			naughty = true;
		}

		if (naughty) {
			best_medi_height = 0;
			prefs.putString(best_medi, Base64Coder.encodeString(Float.toString(0)));
			prefs.flush();
			naughty = false;
		}

		table0 = new Table();
		table0.setFillParent(true);

		table1 = new Table();
		table1.setFillParent(true);

		table2 = new Table();
		table2.setFillParent(true);

		world = new World(new Vector2(0f, -25.81f), true);
		debugRenderer = new Box2DDebugRenderer();
		batch  = new SpriteBatch();
		camera = new OrthographicCamera();

		hud = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), new OrthographicCamera()), batch);

		hud.addActor(table0);
		hud.addActor(table1);
		hud.addActor(table2);

		font = assets.manager.get("galax_250.ttf", BitmapFont.class);
		TextButtonStyle style = new TextButtonStyle();
		style.font = font;

		DecimalFormat df = new DecimalFormat("####");
		TextButton medium_build = new TextButton(df.format(best_medi_height) + "m", style);
		medium_build.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
			}
		});
		medium_build.pad(15);

		// table
		table0.top().center();
		table0.add(medium_build);

		// DEBUG
		//table0.debug();

		Gdx.input.setInputProcessor(hud);

	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0f/255f, 0f/255f, 0f/255f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glCullFace(GL20.GL_BACK);

		world.step(TIMESTEP, VELOCITYITERATIONS, POSITIONITERATIONS);

		camera.position.set(-10f, -50f, 0f);
		camera.update();

		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.end();

		batch.setProjectionMatrix(hud.getCamera().combined);

		hud.act(delta);
		hud.draw();

		// Debug sprite lines
		//debugRenderer.render(world, camera.combined);

		// FPS
		// System.out.println(Gdx.graphics.getFramesPerSecond());

		// PC / Andriod controls
		Gdx.input.setCatchBackKey(true);
		if (Gdx.input.isKeyPressed(Input.Keys.BACK) || Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
			((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenu());
		}
	}

	@Override
	public void resize(int width, int height) {
		camera.viewportWidth = width / 15f; // 10 pc 15 andriod
		camera.viewportHeight = height / 15f;
		hud.getViewport().update(width, height);
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {
		dispose();
	}

	@Override
	public void dispose() {
		batch.dispose();
		hud.dispose();
		world.dispose();
		debugRenderer.dispose();
	}
}

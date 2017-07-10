package screen;

import route.Assets;
import bloc.Boot;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Credits implements Screen {
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

//	private float crt_timer = 0;

	@Override
	public void show() {

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

		// free type font
		font = assets.manager.get("galax_120.ttf", BitmapFont.class);
		// hud buttons
		TextButtonStyle style = new TextButtonStyle();
		style.font = font;

		TextButton title = new TextButton("Credits", style);
		title.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				((Game) Gdx.app.getApplicationListener()).setScreen(new Credits());
				return true;
			}
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
			}
		});
		title.pad(15);

		font = assets.manager.get("galax_80_scarlet.ttf", BitmapFont.class);
		style.font = font;

		TextButton SFX = new TextButton("SFX", style);
		SFX.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
			}
		});
		SFX.pad(15);

		TextButton music = new TextButton("Music", style);
		music.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
			}
		});
		music.pad(15);

		font = assets.manager.get("galax_48.ttf", BitmapFont.class);
		style.font = font;

		TextButton sfx_desc = new TextButton("Sound effects from freesound \n by suonho \nwww.freesound.org/people/suonho/", style);
		sfx_desc.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
			}
		});
		sfx_desc.pad(15);

		TextButton music_desc = new TextButton("Music by Eric Matyas \nwww.soundimage.org", style);
		music_desc.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
			}
		});
		music_desc.pad(15);


		// table
		table0.center().top();
		table0.add(title);

		table1.center();
		table1.row();
		table1.add(music);
		table1.row();
		table1.add(music_desc);
		table1.row();
		table1.add(SFX);
		table1.row();
		table1.add(sfx_desc);

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

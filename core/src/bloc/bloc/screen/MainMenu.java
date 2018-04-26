package bloc.bloc.screen;

import java.util.LinkedList;
import java.util.Random;

import bloc.bloc.bloc.Boot;
import bloc.bloc.route.Assets;
import bloc.bloc.route.Audio;
import bloc.bloc.route.Square;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class MainMenu implements Screen {
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

	private Body ground;
	private Array<Body> bodies = new Array<Body>();

	private BitmapFont font;

	private final Boot game = (Boot) Gdx.app.getApplicationListener();
	private final Assets assets = game.getAssets();

	private Preferences prefs = Gdx.app.getPreferences("Bloc");

	private String sfx_ = Base64Coder.encodeString("6huj52g6");
	private String bgm_ = Base64Coder.encodeString("fgh65jsd");

	private String beer = Base64Coder.encodeString("hgdftrwe");
	private String whisky = Base64Coder.encodeString("69696969");

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

		font = assets.manager.get("galax_250.ttf", BitmapFont.class);

		final TextButtonStyle style = new TextButtonStyle();
		style.font = font;

		TextButton title = new TextButton("bLoc", style);
		title.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
			}
		});
		title.pad(15);

		font = assets.manager.get("galax_120_thick.ttf", BitmapFont.class);
		style.font = font;

		final TextButton play = new TextButton("play", style);
		play.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				((Game) Gdx.app.getApplicationListener()).setScreen(new Play());
			}
		});
		play.pad(25);

		TextButton highscore = new TextButton("high", style);
		highscore.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				((Game) Gdx.app.getApplicationListener()).setScreen(new Stats());
				return true;
			}
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
			}
		});
		highscore.pad(25);

		TextButton credits = new TextButton("credit", style);
		credits.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				((Game) Gdx.app.getApplicationListener()).setScreen(new Credits());
			}
		});
		credits.pad(25);

		font = assets.manager.get("galax_80.ttf", BitmapFont.class);
		style.font = font;

		final TextButton sfx = new TextButton("SFX", style);
		sfx.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				if (sfx.getColor().a == .5f) {
					sfx.setColor(0, 0, 0, 1f);
					prefs.putString(sfx_, beer);
					prefs.flush();

				} else {
					sfx.setColor(0, 0, 0, .5f);
					prefs.putString(sfx_, whisky);
					prefs.flush();
				}
				return true;
			}
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
			}
		});
		sfx.pad(15);
		if (Base64Coder.decodeString(prefs.getString(sfx_)).equals("hgdftrwe")) {
			sfx.setColor(0, 0, 0, 1f);
		} else {
			sfx.setColor(0, 0, 0, .5f);
		}

		final TextButton bgm = new TextButton("BGM", style);
		bgm.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				if (bgm.getColor().a == .5f) {
					bgm.setColor(0, 0, 0, 1f);
					Audio.bgm.play();
					Audio.bgm.setLooping(true);
					prefs.putString(bgm_, beer);
					prefs.flush();
				} else {
					bgm.setColor(0, 0, 0, .5f);
					Audio.bgm.stop();
					prefs.putString(bgm_, whisky);
					prefs.flush();
				}
				return true;
			}
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
			}
		});
		bgm.pad(15);
		if (Base64Coder.decodeString(prefs.getString(bgm_)).equals("hgdftrwe")) {
			bgm.setColor(0, 0, 0, 1f);
		} else {
			bgm.setColor(0, 0, 0, .5f);
		}

		// table
		table0.center().top();
		table0.add(title);

		table1.center().padTop(50);
		table1.add(play);
		table1.row();
		table1.add(highscore);
		table1.row();
		table1.add(credits);

		table2.top().left();
		table2.add(sfx).padBottom(10);
		table2.row();
		table2.add(bgm).padBottom(10);

		// DEBUG
		//table0.debug();
		//table1.debug();
		//table2.debug();

		Square bottom_wall = new Square(-10, -74, 75, 6, -1);
		bottom_wall.bodyDef.type = BodyType.StaticBody;
		ground = world.createBody(bottom_wall.bodyDef);
		ground.createFixture(bottom_wall.fixtureDef);
		ground.setUserData(bottom_wall.sprite);
		bottom_wall.shape.dispose();

		Square left_wall = new Square(-80, -57.5f, 5, 70, -1);
		left_wall.bodyDef.type = BodyType.StaticBody;
		ground = world.createBody(left_wall.bodyDef);
		ground.createFixture(left_wall.fixtureDef);
		ground.setUserData(left_wall.sprite);
		left_wall.shape.dispose();

		Square right_wall = new Square(60, -57.5f, 5, 70, -1);
		right_wall.bodyDef.type = BodyType.StaticBody;
		ground = world.createBody(right_wall.bodyDef);
		ground.createFixture(right_wall.fixtureDef);
		ground.setUserData(right_wall.sprite);
		right_wall.shape.dispose();

		LinkedList<Square> c1 = new LinkedList<Square>();
		c1.add(new Square(-30, 0, 5, 5, rng(1), 1));
		c1.add(new Square(-25, 0, 5, 5, rng(1), 1));
		c1.add(new Square(-20, 0, 5, 5, rng(1), 1));
		c1.add(new Square(-15, 0, 5, 5, rng(1), 1));
		c1.add(new Square(-10, 0, 5, 5, rng(1), 1));
		c1.add(new Square(-5, 0, 5, 5, rng(1), 1));
		c1.add(new Square(0, 0, 5, 5, rng(1), 1));
		c1.add(new Square(5, 0, 5, 5, rng(1), 1));
		c1.add(new Square(10, 0, 5, 5, rng(1), 1));
		c1.add(new Square(15, 0, 5, 5, rng(1), 1));
		c1.add(new Square(20, 0, 5, 5, rng(1), 1));
		c1.add(new Square(25, 0, 5, 5, rng(1), 1));
		c1.add(new Square(30, 0, 5, 5, rng(1), 1));

		c1.add(new Square(-30, 0, 5, 5, rng(1), 1));
		c1.add(new Square(-25, 0, 5, 5, rng(1), 1));
		c1.add(new Square(-20, 0, 5, 5, rng(1), 1));
		c1.add(new Square(-15, 0, 5, 5, rng(1), 1));
		c1.add(new Square(-10, 0, 5, 5, rng(1), 1));
		c1.add(new Square(-5, 0, 5, 5, rng(1), 1));
		c1.add(new Square(0, 0, 5, 5, rng(1), 1));
		c1.add(new Square(5, 0, 5, 5, rng(1), 1));
		c1.add(new Square(10, 0, 5, 5, rng(1), 1));
		c1.add(new Square(15, 0, 5, 5, rng(1), 1));
		c1.add(new Square(20, 0, 5, 5, rng(1), 1));
		c1.add(new Square(25, 0, 5, 5, rng(1), 1));
		c1.add(new Square(30, 0, 5, 5, rng(1), 1));

		c1.add(new Square(-30, 0, 5, 5, rng(1), 1));
		c1.add(new Square(-25, 0, 5, 5, rng(1), 1));
		c1.add(new Square(-20, 0, 5, 5, rng(1), 1));
		c1.add(new Square(-15, 0, 5, 5, rng(1), 1));
		c1.add(new Square(-10, 0, 5, 5, rng(1), 1));
		c1.add(new Square(-5, 0, 5, 5, rng(1), 1));
		c1.add(new Square(0, 0, 5, 5, rng(1), 1));
		c1.add(new Square(5, 0, 5, 5, rng(1), 1));
		c1.add(new Square(10, 0, 5, 5, rng(1), 1));
		c1.add(new Square(15, 0, 5, 5, rng(1), 1));
		c1.add(new Square(20, 0, 5, 5, rng(1), 1));
		c1.add(new Square(25, 0, 5, 5, rng(1), 1));
		c1.add(new Square(30, 0, 5, 5, rng(1), 1));

		c1.add(new Square(-30, 0, 5, 5, rng(1), 1));
		c1.add(new Square(-25, 0, 5, 5, rng(1), 1));
		c1.add(new Square(-20, 0, 5, 5, rng(1), 1));
		c1.add(new Square(-15, 0, 5, 5, rng(1), 1));
		c1.add(new Square(-10, 0, 5, 5, rng(1), 1));
		c1.add(new Square(-5, 0, 5, 5, rng(1), 1));
		c1.add(new Square(0, 0, 5, 5, rng(1), 1));
		c1.add(new Square(5, 0, 5, 5, rng(1), 1));
		c1.add(new Square(10, 0, 5, 5, rng(1), 1));
		c1.add(new Square(15, 0, 5, 5, rng(1), 1));
		c1.add(new Square(20, 0, 5, 5, rng(1), 1));
		c1.add(new Square(25, 0, 5, 5, rng(1), 1));
		c1.add(new Square(30, 0, 5, 5, rng(1), 1));

		c1.add(new Square(-30, 0, 5, 5, rng(1), 1));
		c1.add(new Square(-25, 0, 5, 5, rng(1), 1));
		c1.add(new Square(-20, 0, 5, 5, rng(1), 1));
		c1.add(new Square(-15, 0, 5, 5, rng(1), 1));
		c1.add(new Square(-10, 0, 5, 5, rng(1), 1));
		c1.add(new Square(-5, 0, 5, 5, rng(1), 1));
		c1.add(new Square(0, 0, 5, 5, rng(1), 1));
		c1.add(new Square(5, 0, 5, 5, rng(1), 1));
		c1.add(new Square(10, 0, 5, 5, rng(1), 1));
		c1.add(new Square(15, 0, 5, 5, rng(1), 1));
		c1.add(new Square(20, 0, 5, 5, rng(1), 1));
		c1.add(new Square(25, 0, 5, 5, rng(1), 1));
		c1.add(new Square(30, 0, 5, 5, rng(1), 1));

		c1.add(new Square(-30, 0, 5, 5, rng(1), 1));
		c1.add(new Square(-25, 0, 5, 5, rng(1), 1));
		c1.add(new Square(-20, 0, 5, 5, rng(1), 1));
		c1.add(new Square(-15, 0, 5, 5, rng(1), 1));
		c1.add(new Square(-10, 0, 5, 5, rng(1), 1));
		c1.add(new Square(-5, 0, 5, 5, rng(1), 1));
		c1.add(new Square(0, 0, 5, 5, rng(1), 1));
		c1.add(new Square(5, 0, 5, 5, rng(1), 1));
		c1.add(new Square(10, 0, 5, 5, rng(1), 1));
		c1.add(new Square(15, 0, 5, 5, rng(1), 1));
		c1.add(new Square(20, 0, 5, 5, rng(1), 1));
		c1.add(new Square(25, 0, 5, 5, rng(1), 1));
		c1.add(new Square(30, 0, 5, 5, rng(1), 1));

		for (Square i : c1) {
			ground = world.createBody(i.bodyDef);
			ground.createFixture(i.fixtureDef);
			ground.setUserData(i.sprite);
			i.shape.dispose();
		}

		Gdx.input.setInputProcessor(hud);

	}

	private int rng(int j) {
		Random random = new Random();
		if (j == 1) {
			int i = random.nextInt(4) + 1;
			return i;
		}
		if (j == 2) {
			int i = random.nextInt(2) + 1;
			return i;
		}
		return -1;
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
		world.getBodies(bodies);
		for (Body body : bodies)
			if (body.getUserData() instanceof Sprite) {
				Sprite sprite = (Sprite) body.getUserData();
				sprite.setPosition(body.getPosition().x - sprite.getWidth() / 2, body.getPosition().y - sprite.getHeight() / 2);
				sprite.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
				sprite.draw(batch);
			}
		batch.end();

		batch.setProjectionMatrix(hud.getCamera().combined);
		hud.act(delta);
		hud.draw();

		// Debug sprite lines
		//debugRenderer.render(world, camera.combined);

		// FPS
		// System.out.println(Gdx.graphics.getFramesPerSecond());

		//	PC / Andriod controls
		//		Gdx.input.setCatchBackKey(true);
		//		if (Gdx.input.isKeyPressed(Input.Keys.BACK) || Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
		//		}
	}

	@Override
	public void resize(int width, int height) {
		camera.viewportWidth = width / 30f;
		camera.viewportHeight = height / 30f;
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

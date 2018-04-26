package bloc.bloc.screen;

import java.util.Random;

import bloc.bloc.bloc.Boot;
import bloc.bloc.route.Assets;
import bloc.bloc.route.Audio;
import bloc.bloc.route.Gesture;
import bloc.bloc.route.Square;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Play implements Screen {
	private static final float TIMESTEP = 1 / 30f;
	private static final int VELOCITYITERATIONS = 1, POSITIONITERATIONS = 6;
	private World world;
	private Box2DDebugRenderer debugRenderer;
	private SpriteBatch batch;
	private OrthographicCamera camera;
	private Stage hud;
	private Table table0;
	private Table table1;
	private Table table2;
	private Table table3;
	private Body ground;
	private Array<Body> bodies = new Array<Body>();
	private BitmapFont font;
	private Skin skin;

	private int stack_counter = 0;
	private int freeze_counter = 0;
	private float height_counter = 0;

	private boolean reset;
	private int r1;
	private int r2;
	private Square square;
	private ShapeRenderer sr;
	private float y_camera = 20;

	private boolean freeze;
	private boolean touching;

	private TextButton freeze_button;

	private final Boot game = (Boot) Gdx.app.getApplicationListener();
	private final Assets assets = game.getAssets();

	private Preferences prefs = Gdx.app.getPreferences("Bloc");

	private TextButton freeze_count_display;
	private TextButton height_display;

	private float best_medi_height;
	private int bloc_placed;

	private String best_medi = Base64Coder.encodeString("fe5yhh44");
	private String placed_blocs = Base64Coder.encodeString("56uhns69");

	private String sfx_ = Base64Coder.encodeString("6huj52g6");

	@Override
	public void show() {

		// first load
		if (prefs.getString(best_medi) == "") {
			prefs.putString(best_medi, Base64Coder.encodeString(Float.toString(0)));
			prefs.flush();
		}
		if (prefs.getString(placed_blocs) == "") {
			prefs.putString(placed_blocs, Base64Coder.encodeString(Integer.toString(0)));
			prefs.flush();
		}

		boolean naughty = false;
		try {
			best_medi_height = Float.parseFloat(Base64Coder.decodeString(prefs.getString(best_medi)));
			bloc_placed = Integer.parseInt(Base64Coder.decodeString(prefs.getString(placed_blocs)));
		} catch (NumberFormatException n) {
			//System.out.println(n + "..resetting stats");
			naughty = true;
		} catch (IllegalArgumentException i){
			//System.out.println(i + "..resetting stats");
			naughty = true;
		}

		if (naughty) {
			best_medi_height = 0;
			bloc_placed = 0;

			prefs.putString(best_medi, Base64Coder.encodeString(Float.toString(0)));
			prefs.putString(placed_blocs, Base64Coder.encodeString(Integer.toString(0)));
			prefs.flush();
			naughty = false;
		}

		skin = assets.manager.get(Assets.uiSkin);
		table0 = new Table(skin);
		table1 = new Table(skin);
		table2 = new Table(skin);
		table3 = new Table(skin);

		table0.setFillParent(true);
		table1.setFillParent(true);
		table2.setFillParent(true);
		table3.setFillParent(true);

		world = new World(new Vector2(0f, -25.81f), true);
		debugRenderer = new Box2DDebugRenderer();
		batch  = new SpriteBatch();
		camera = new OrthographicCamera();

		hud = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), new OrthographicCamera()), batch);

		hud.addActor(table0);
		hud.addActor(table1);
		hud.addActor(table2);
		hud.addActor(table3);

		// hud buttons
		TextButtonStyle style = new TextButtonStyle();

		font = assets.manager.get("galax_80.ttf", BitmapFont.class);
		style.font = font;

		freeze_count_display = new TextButton("", style);
		freeze_count_display.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
			}
		});
		freeze_count_display.pad(15);

		font = assets.manager.get("galax_120.ttf", BitmapFont.class);
		style.font = font;

		height_display = new TextButton("", style);
		height_display.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
			}
		});
		height_display.pad(15);

		TextButton m = new TextButton("m", style);
		m.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
			}
		});

		font = assets.manager.get("fauxsnow.ttf", BitmapFont.class);
		style.font = font;

		freeze_button = new TextButton("\"", style);
		freeze_button.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				if (freeze_counter > 0) {
					freeze = true;
					freeze_counter -= 1;
				}
				return true;
			}
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
			}
		});
		freeze_button.pad(15);
		freeze_button.setColor(0, 0, 0, .3f);

		font = assets.manager.get("galax_120.ttf", BitmapFont.class);
		style.font = font;

		TextButton next_button = new TextButton("next", style);
		next_button.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
			}
		});
		next_button.pad(15);

		TextButton reset_camera_buton = new TextButton("top", style);
		reset_camera_buton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				if (height_counter < 20) {
					y_camera = 20;
				} else {
					y_camera = height_counter;
				}
				return true;
			}
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
			}
		});
		reset_camera_buton.pad(15);

		TextButton quit_msg = new TextButton("leave game?", style);
		quit_msg.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
			}
		});
		quit_msg.pad(15);

		TextButton yes = new TextButton("yes", style);
		yes.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenu());
				return true;
			}
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
			}
		});
		yes.pad(15);

		TextButton no = new TextButton("no", style);
		no.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
			}
		});
		no.pad(15);

		font = assets.manager.get("arrows.ttf", BitmapFont.class);
		style.font = font;

		TextButton up_button = new TextButton("S", style);
		up_button.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				y_camera += 10;
				return true;
			}
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
			}
		});
		up_button.pad(15);

		TextButton down_button = new TextButton("T", style);
		down_button.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				if (y_camera > 21f) {
					y_camera -= 10;
				}
				return true;
			}
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
			}
		});
		down_button.pad(15);

		final Square bottom_wall = new Square(0, 10, 13, .5f, rng(0));
		bottom_wall.bodyDef.type = BodyType.StaticBody;
		bottom_wall.bodyDef.gravityScale = -20f;
		ground = world.createBody(bottom_wall.bodyDef);
		ground.createFixture(bottom_wall.fixtureDef);
		ground.setUserData(bottom_wall.sprite);
		bottom_wall.shape.dispose();

		r1 = rng(1);
		r2 = rng(1);

		// table
		table0.top().left();
		table0.add(reset_camera_buton);
		table0.row();
		table0.add(up_button).padRight(50).padTop(100);
		table0.row();
		table0.add(down_button).padRight(50).padTop(100);

		sr = new ShapeRenderer();

		table1.addActor(new Actor() {
			@Override
			public void draw(Batch batch, float parentAlpha) {
				batch.end();
				sr.begin(ShapeType.Filled);
				sr.setColor(Color.WHITE);
				float y = 4;
				// /20 x: 21 y: 7
				sr.rect(14, y += y_camera, r1/.9f, r2/.9f);
				sr.end();
				batch.begin();
				super.draw(batch, parentAlpha);
			}	
		});

		table1.top().right();
		table1.add(next_button).padBottom(250);
		table1.row();
		table1.add(freeze_button);
		table1.row();
		table1.add(freeze_count_display);

		table2.top();
		table2.add(height_display);
		table2.add(m);

		table3.center();
		table3.add(quit_msg);
		table3.row();
		table3.add(yes);
		table3.row();
		table3.add(no);
		table3.setVisible(false);

		// table debug
		//table0.debug();
		//table1.debug();
		//table2.debug();
		//table3.debug();
		//table4.debug();

		InputProcessor input_0 = new GestureDetector(new Gesture() {
			@Override
			public boolean tap(float x, float y, int count, int button) {
				Vector3 mouse = new Vector3();
				camera.unproject(mouse.set(Gdx.input.getX(), Gdx.input.getY(), 0));

				//System.out.println(mouse.x + " " + mouse.y);

				if (!table3.isVisible()) {	
					if (!touching) {
						if (mouse.x > -15f && mouse.x < 13f) {
							if (mouse.y > (height_counter - 5)) {
								square = new Square(mouse.x, mouse.y, r1, r2, rng(0));
								square.bodyDef.gravityScale = .25f;
								ground = world.createBody(square.bodyDef);
								ground.createFixture(square.fixtureDef);
								ground.setUserData(square.sprite);
								square.shape.dispose();

								if (height_counter > 150) {
									r1 = rng(3);
									r2 = rng(3);
								} else if (height_counter > 75) {
									r1 = rng(2);
									r2 = rng(2);
								} else {
									r1 = rng(1);
									r2 = rng(1);
								}

								if (square.bodyDef.linearVelocity.y < 0.05f 
										&& square.bodyDef.linearVelocity.y > -1) {
									stack_counter++;
								}

								if (bloc_placed == 0) {
									bloc_placed = 1;
									prefs.putString(placed_blocs, Base64Coder.encodeString(Integer.toString(bloc_placed)));
									prefs.flush();
								} else {
									bloc_placed++;
									prefs.putString(placed_blocs, Base64Coder.encodeString(Integer.toString(bloc_placed)));
									prefs.flush();
								}

								if (Base64Coder.decodeString(prefs.getString(sfx_)).equals("hgdftrwe")) {
									Audio.bloc.play();
								}	
							}
						}
					}
				}

				table3.setVisible(false);
				return super.tap(x, y, count, button);
			}

		}) {
			@Override
			public boolean touchUp(float x, float y, int pointer, int button) {
				touching = false;
				return super.touchUp(x, y, pointer, button);
			}

		};

		InputProcessor input_1 = hud;
		InputMultiplexer input_multiplexer = new InputMultiplexer();
		input_multiplexer.addProcessor(input_0);
		input_multiplexer.addProcessor(input_1);
		Gdx.input.setInputProcessor(input_multiplexer);

	}

	private int rng(int j) {
		Random random = new Random();
		// colors
		if (j == 0) {
			int i = random.nextInt(4) + 1;
			return i;
		}
		// bloc w/h
		if (j == 1) {
			int i = random.nextInt(3) + 2;
			return i;
		}
		if (j == 2) {
			int i = random.nextInt(3) + 1;
			return i;
		}
		if (j == 3) {
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

		//System.out.println(y_camera);

		camera.position.set(0, y_camera, 0);
		camera.update();

		batch.setProjectionMatrix(camera.combined);
		sr.setProjectionMatrix(batch.getProjectionMatrix());

		batch.begin();
		world.getBodies(bodies);
		for (Body body : bodies) {
			Sprite sprite = (Sprite) body.getUserData();
			if (body.getUserData() instanceof Sprite) {
				sprite.setPosition(body.getPosition().x - sprite.getWidth() / 2, body.getPosition().y - sprite.getHeight() / 2);
				sprite.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
				sprite.draw(batch);
			}

			if (body.getPosition().y < -40) {
				reset = true;
			}
		}

		batch.end();
		batch.setProjectionMatrix(hud.getCamera().combined);
		hud.act(delta);
		hud.draw();

		if (stack_counter == 5) {
			freeze_counter++;
			stack_counter = 0;
		}

		if (freeze_counter > 0) {
			freeze_button.setColor(0, 0, 0, 1f);
		} else {
			freeze_button.setColor(0, 0, 0, .3f);
		}

		if (freeze) {
			if (Base64Coder.decodeString(prefs.getString(sfx_)).equals("hgdftrwe")) {
				Audio.freeze.play();
			}		
			for (Body body : bodies) {
				//System.out.println(body.getLinearVelocity().y);
				if (body.getLinearVelocity().y > -.3f && body.getLinearVelocity().y < .3f) {
					if (body.getGravityScale() != -20f) {

						Sprite old_sprite = (Sprite) (body.getUserData());
						float width = old_sprite.getWidth();
						float height = old_sprite.getHeight();

						Sprite sprite = new Sprite(assets.manager.get(Assets.blue));
						sprite.setSize(width, height);
						sprite.setOrigin(width / 2, height / 2);

						body.setUserData(sprite);
						body.setType(BodyType.StaticBody);

						if (body.getPosition().y > height_counter) {
							height_counter = body.getPosition().y;
						}
					}
				}
			}

			if (best_medi_height < height_counter) {
				prefs.putString(best_medi, Base64Coder.encodeString(Float.toString(height_counter)));
				prefs.flush();
			}
			freeze = false;
		}

		if (reset) {
			stack_counter = 0;
			y_camera = 20;

			height_counter = 0;
			freeze_counter = 0;

			if (Base64Coder.decodeString(prefs.getString(sfx_)).equals("hgdftrwe")) {
				Audio.reset.play();
			}	

			for (Body body : bodies) {
				if (body.getGravityScale() == .25f) {
					world.destroyBody(body);
					body.setUserData(null);
					body = null;
				}
			}
			reset = false;
		}

		// set text for freeze and height counters
		freeze_count_display.setText(Integer.toString(freeze_counter));
		height_display.setText(Integer.toString((int)height_counter));

		// Debug sprite lines
		//debugRenderer.render(world, camera.combined);

		// FPS
		// System.out.println(Gdx.graphics.getFramesPerSecond());

		// PC / Andriod controls
		Gdx.input.setCatchBackKey(true);
		if (Gdx.input.isKeyPressed(Input.Keys.BACK) || Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
			table3.setVisible(true);
		}
	}

	@Override
	public void resize(int width, int height) {
		camera.viewportWidth = 1200 / 30;
		camera.viewportHeight = 800 / 30;
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
		sr.dispose();
		batch.dispose();
		hud.dispose();
		world.dispose();
		debugRenderer.dispose();
	}
}

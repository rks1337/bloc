package bloc;

import bloc.Boot;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.addIcon("ui/bloc_icon.png", Files.FileType.Internal);
		config.title = "bloc";
		config.vSyncEnabled = true;
		config.width = 1280;
		config.height = 720;
		new LwjglApplication(new Boot(), config);
	}
}
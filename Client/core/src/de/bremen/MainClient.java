package de.bremen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import de.bremen.service.CommunicationService;
import de.bremen.screens.LoginScreen;
import lombok.Setter;
import lombok.Getter;
public class MainClient extends Game {
	@Setter
	@Getter
	private AssetManager assetManager;

	public LoginScreen loginScreen;
	private SpriteBatch batch;

	public CommunicationService communicationService;

	@Override
	public void create () {


		assetManager = new AssetManager();
		assetManager.finishLoading();
		loginScreen = new LoginScreen(this);


		batch = new SpriteBatch();
		setScreen(loginScreen);

	}
	@Override
	public void dispose() {
		super.dispose();
		assetManager.dispose();
		batch.dispose();
	}

	public SpriteBatch getBatch() {
		return batch;

	}
	public AssetManager getAssetmanager() {
		return assetManager;
	}

}

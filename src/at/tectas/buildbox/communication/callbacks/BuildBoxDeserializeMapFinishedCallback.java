package at.tectas.buildbox.communication.callbacks;

import at.tectas.buildbox.BuildBoxMainActivity;
import at.tectas.buildbox.library.communication.callbacks.DeserializeMapFinishedCallback;
import at.tectas.buildbox.library.download.DownloadActivity;

public class BuildBoxDeserializeMapFinishedCallback extends DeserializeMapFinishedCallback {

	private BuildBoxDeserializeMapFinishedCallback(DownloadActivity activity) {
		super(activity);
	}
	
	public BuildBoxDeserializeMapFinishedCallback(BuildBoxMainActivity activity) {
		super(activity);
	}
	
	@Override
	public void mapDeserializedCallback() {
		if (this.activity instanceof BuildBoxMainActivity) {
			BuildBoxMainActivity buildBoxActivity = (BuildBoxMainActivity) this.activity;
			
			buildBoxActivity.refreshDownloadsView();
		}
		
		super.mapDeserializedCallback();
	}
}

package examples.android.puc;

import java.util.ArrayList;
import java.util.List;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class IOverlay extends ItemizedOverlay<OverlayItem> {

	private Handler handler;
	private ArrayList<OverlayItem> overlays = new ArrayList<OverlayItem>();

	public IOverlay(Drawable defaultMarker, Handler h) {
		super(boundCenterBottom(defaultMarker));
		handler = h;
		populate();
	}

	public void addOverlay(OverlayItem overlay) {
		while(!overlays.isEmpty()) {
			overlays.remove(0);
		}
		overlays.add(overlay);
		populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return overlays.get(i);
	}

	@Override
	public int size() {
		return overlays.size();
	}

	@Override
	public boolean onTap(GeoPoint p, MapView map) {
		Message message = new Message();
		Bundle data = new Bundle();
		data.putInt("latitude", p.getLatitudeE6());
		data.putInt("longitude", p.getLongitudeE6());
		message.setData(data);
		handler.sendMessage(message);
		return super.onTap(p, map);
	}

}

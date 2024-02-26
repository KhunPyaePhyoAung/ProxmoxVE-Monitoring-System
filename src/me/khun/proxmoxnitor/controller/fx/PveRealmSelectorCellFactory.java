package me.khun.proxmoxnitor.controller.fx;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import me.khun.proxmoxnitor.pve.PveRealm;

public class PveRealmSelectorCellFactory implements Callback<ListView<PveRealm>, ListCell<PveRealm>> {

	@Override
	public ListCell<PveRealm> call(ListView<PveRealm> list) {
		return new ListCell<PveRealm>() {
			
			@Override
			protected void updateItem(PveRealm item, boolean empty) {
				super.updateItem(item, empty);
				if (!empty && item != null) {
					setText(item.getComment());
				} else {
					setGraphic(null);
				}
			}
		};
	}

}

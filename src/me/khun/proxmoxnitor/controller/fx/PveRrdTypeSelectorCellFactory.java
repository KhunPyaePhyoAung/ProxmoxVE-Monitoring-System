package me.khun.proxmoxnitor.controller.fx;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import me.khun.proxmoxnitor.pve.PveRrdDataType;

public class PveRrdTypeSelectorCellFactory implements Callback<ListView<PveRrdDataType>, ListCell<PveRrdDataType>> {

	@Override
	public ListCell<PveRrdDataType> call(ListView<PveRrdDataType> param) {
		return new ListCell<PveRrdDataType>() {
			@Override
			protected void updateItem(PveRrdDataType item, boolean empty) {
				super.updateItem(item, empty);
				if (!empty && item != null) {
					String frame = item.getTimeFrame().substring(0, 1).toUpperCase() + item.getTimeFrame().substring(1);
					String cf = "";
					if (item.getCf().equals("AVERAGE")) {
						cf = "average";
					} else if (item.getCf().equals("MAX")) {
						cf = "maximum";
					}
					
					setText(String.format("%s (%s)", frame, cf));
				} else {
					setGraphic(null);
				}
			}
		};
	}

}

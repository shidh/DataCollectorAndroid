package de.mpg.mpdl.www.datacollector.app.Event;

import java.util.List;

import de.mpg.mpdl.www.datacollector.app.Model.DataItem;

/**
 * Created by allen on 23/04/15.
 */
public class GetNewItemFromUserEvent {

    public final List<DataItem> itemList;
    public GetNewItemFromUserEvent(List<DataItem> itemList) {
        this.itemList = itemList;
    }
}

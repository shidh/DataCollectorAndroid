package de.mpg.mpdl.www.datacollector.app.Workflow;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.squareup.otto.Produce;

import java.util.ArrayList;
import java.util.List;

import de.mpg.mpdl.www.datacollector.app.Event.MetadataIsReadyEvent;
import de.mpg.mpdl.www.datacollector.app.Event.OttoSingleton;
import de.mpg.mpdl.www.datacollector.app.R;

/**
 * Created by allen on 21/04/15.
 */
public class AskMetadataFragment extends DialogFragment {
    public List mSelectedItems;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        mSelectedItems = new ArrayList();  // Where we track the selected items


        builder.setTitle(R.string.dialog_title)
               .setMultiChoiceItems(R.array.tags_array, null,
                       new DialogInterface.OnMultiChoiceClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which,
                                               boolean isChecked) {
                               if (isChecked) {
                                   // If the user checked the item, add it to the selected items
                                   mSelectedItems.add(which);
                               } else if (mSelectedItems.contains(which)) {
                                   // Else, if the item is already in the array, remove it
                                   mSelectedItems.remove(Integer.valueOf(which));
                               }
                           }
                       })
               .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                   @Produce
                   public void onClick(DialogInterface dialog, int id) {
                       // User clicked OK button
                       String[]  mTagsArray = getResources().getStringArray(R.array.tags_array);

                       List<String> tags = new ArrayList<String>();
                       for (Object item : mSelectedItems) {
                           tags.add(mTagsArray[Integer.parseInt(item.toString())]);
                       }

                       MetadataIsReadyEvent event = new MetadataIsReadyEvent(tags);
                       OttoSingleton.getInstance().post(event);
                   }
               })
               .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

                   @Produce
                   public void onClick(DialogInterface dialog, int id) {
                       // User cancelled the dialog
                       mSelectedItems.clear();
                   }
               });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
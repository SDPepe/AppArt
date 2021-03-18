package ch.epfl.sdp.appart.scrolling.ad;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Button;

import androidx.fragment.app.DialogFragment;

import ch.epfl.sdp.appart.R;
import ch.epfl.sdp.appart.user.User;


public class ContactInfoDialogFragment extends DialogFragment {

    public static ContactInfoDialogFragment newInstance(Bundle args){
        ContactInfoDialogFragment contactFrag = new ContactInfoDialogFragment();
        contactFrag.setArguments(args);
        return contactFrag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstances){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.contactdialog_layout, null))
            .setNegativeButton(R.string.dialog_close, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dismiss();
            }
        });

        return builder.create();
    }

}

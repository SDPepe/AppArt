package ch.epfl.sdp.appart.scrolling.ad;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import ch.epfl.sdp.appart.R;
import ch.epfl.sdp.appart.user.User;


public class ContactInfoDialogFragment extends DialogFragment {

    public static ContactInfoDialogFragment newInstance(Bundle args){
        ContactInfoDialogFragment contactFrag = new ContactInfoDialogFragment();
        contactFrag.setArguments(args);
        return contactFrag;
    }

    /*
     * creates the dialog, inflates the layout and sets the values using the Bundle given to
     * newInstance.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstances){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialog = inflater.inflate(R.layout.contactdialog_layout, null);
        TextView username = dialog.findViewById(R.id.usernameTextView);
        username.setText(savedInstances.getString("name"));
        TextView phone = dialog.findViewById(R.id.phoneField);
        phone.setText(savedInstances.getString("phone"));
        TextView email = dialog.findViewById(R.id.emailField);
        email.setText(savedInstances.getString("email"));
        builder.setView(dialog)
            .setNegativeButton(R.string.dialog_close, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dismiss();
            }
        });

        return builder.create();
    }

}

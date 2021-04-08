package ch.epfl.sdp.appart.ad;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import org.jetbrains.annotations.NotNull;

import ch.epfl.sdp.appart.R;

/**
 * Fragment for a dialog showing information to contact a user.
 * <p>
 * It displays a Dialog containing the user name, phone number and email address.
 */
public class ContactInfoDialogFragment extends DialogFragment {

    /**
     * Creates and returns a new instance of ContactInfoDialogFragment.
     *
     * @return an instance of the dialog fragment
     */
    public static ContactInfoDialogFragment newInstance() {
        return new ContactInfoDialogFragment();
    }

    /**
     * Build the dialog fetching the contact info from the ViewModel for the AnnounceActivity in
     * which this dialog is displayed.
     */
    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstances) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialog = inflater.inflate(R.layout.contactdialog_layout, null);

        AdViewModel mViewModel = new ViewModelProvider(requireActivity()).get(AdViewModel.class);

        // fetch info about the user and set content
        TextView username = dialog.findViewById(R.id.username_ContactInfo_textView);
        username.setText(mViewModel.getAdvertiser().getValue());
        TextView phone = dialog.findViewById(R.id.phone_ContactInfo_textView);
        phone.setText(mViewModel.getPhoneNumber().getValue());
        TextView email = dialog.findViewById(R.id.email_ContactInfo_textView);
        email.setText(mViewModel.getEmailAddress().getValue());

        // Return button
        builder.setView(dialog)
                .setNegativeButton(R.string.dialog_close, (dialog1, id) -> dismiss());

        return builder.create();
    }

}

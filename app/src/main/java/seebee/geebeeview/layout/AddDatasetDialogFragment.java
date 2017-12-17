package seebee.geebeeview.layout;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import seebee.geebeeview.R;
import seebee.geebeeview.model.account.Dataset;

/**
 * Created by Joy on 9/28/2017.
 */

public class AddDatasetDialogFragment extends DialogFragment {
    public static final String TAG = "AddDatasetDialogFragment";

    private Spinner spAddDataset;
    private ArrayList<Dataset> datasetList;
    private int selectedDatasetIndex;
    private AddDatasetDialogListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // get layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // inflate and set layout for the dialog
        View view = inflater.inflate(R.layout.dialog_add_dataset, null);
        // pass null because it is a dialog
        builder.setView(view)
                .setTitle(R.string.add_dataset)
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onDialogPositiveClick(AddDatasetDialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        spAddDataset = (Spinner) view.findViewById(R.id.sp_dataset);
        prepareSpinner();

        return builder.create();
    }

    private void prepareSpinner() {
        List<String> setList = new ArrayList<>();
        for(int i = 0; i < datasetList.size(); i++) {
            setList.add(datasetList.get(i).getSchoolName() + " ("+datasetList.get(i).getDate()+")");
        }
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(),
                R.layout.support_simple_spinner_dropdown_item, setList);
        spinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spAddDataset.setAdapter(spinnerAdapter);

        spAddDataset.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDatasetIndex = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void setDatasetList(ArrayList<Dataset> datasetList) {
        this.datasetList = datasetList;
    }

    public int getSelectedDatasetIndex() {
        return selectedDatasetIndex;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (AddDatasetDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    public interface AddDatasetDialogListener {
        void onDialogPositiveClick(AddDatasetDialogFragment dialog);
    }

}

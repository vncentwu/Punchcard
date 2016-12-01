package menu;

import android.app.FragmentManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.scardox.vncentwu.punchcard3.BrowseAdapter;
import com.scardox.vncentwu.punchcard3.CompanyEntry;
import com.scardox.vncentwu.punchcard3.ContentManager;
import com.scardox.vncentwu.punchcard3.R;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.button;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ExpandedFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ExpandedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExpandedFragment extends Fragment implements ContentManager.EnterCodeListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ContentFragment fragment;

    public Button submit;
    public EditText edit;

    public RecyclerView recyclerView;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ExpandedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ExpandedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExpandedFragment newInstance(String param1, String param2) {
        ExpandedFragment fragment = new ExpandedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment



        View v =  inflater.inflate(R.layout.fragment_expanded, container, false);



        submit = (Button) v.findViewById(R.id.submit_button);
        edit = (EditText) v.findViewById(R.id.code_edit);
        final ExpandedFragment frag = this;
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(edit.getWindowToken(), 0);
                ContentManager.getInstance().enterCode(edit.getText().toString(), frag);
                edit.setText("");
            }
        });



        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void EnterCodeCallBack(String company, int points) {
        String message = "";
        if(points == -1){
            message = "Oh no! " + company;
        }
        else{
            message = "Nice! Looks like you got " + points + " punches from " + company + "!";
        }
        Snackbar snackbar = Snackbar
                .make(getView(), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

package menu;

import android.app.FragmentManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.scardox.vncentwu.punchcard3.BrowseAdapter;
import com.scardox.vncentwu.punchcard3.CompanyEntry;
import com.scardox.vncentwu.punchcard3.ContentManager;
import com.scardox.vncentwu.punchcard3.R;

import java.util.ArrayList;
import java.util.List;

import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ContentFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ContentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContentFragment extends Fragment implements ContentManager.GetCompanyEntryListener, ContentManager.GetPointListener{
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    public RecyclerView recyclerView;


    private String mParam1;
    private String mParam2;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    public List<CompanyEntry> myDataset;
    protected DatabaseReference userDB;

    private OnFragmentInteractionListener mListener;

    public ContentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContentFragment newInstance(String param1, String param2) {
        ContentFragment fragment = new ContentFragment();
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

        myDataset = new ArrayList<>();

//        CompanyEntry entry3 = new CompanyEntry("Chipotle", 5);
//        entry3.addPromotion("Free bowl!", 7);
//        entry3.addPromotion("Free drink with purchase", 2);
//        entry3.setPicture(R.drawable.chipotle);
//        myDataset.add(entry3);
//
//        CompanyEntry entry2 = new CompanyEntry("Tapioca House", 7);
//        entry2.addPromotion("Free drink!", 10);
//        entry2.setPicture(R.drawable.tap_house);
//        myDataset.add(entry2);
//
//        CompanyEntry entry4 = new CompanyEntry("Coco's", 10);
//        entry4.addPromotion("Free drink!", 10);
//        entry4.setPicture(R.drawable.cocos);
//        myDataset.add(entry4);
//
//        CompanyEntry entry5 = new CompanyEntry("Pho Thaison", 0);
//        entry5.addPromotion("Free entree", 20);
//        entry5.setPicture(R.drawable.pho_thaison);
//        myDataset.add(entry5);
//
//        CompanyEntry entry6 = new CompanyEntry("3 Woks Down", 0);
//        entry6.addPromotion("Free popping boba with purchase", 0);
//        entry6.addPromotion("50% off!", 10);
//        entry6.setPicture(R.drawable.three_woks_down);
//        myDataset.add(entry6);
//
//        for(int i = 0; i < 5; i++)
//        {
//            CompanyEntry entry = new CompanyEntry("Company " + i, i);
//            entry.addPromotion("This is promotion #" + i, i + 5);
//            entry.addPromotion("This is promotion #A" + i, i + 5);
//            myDataset.add(entry);
//        }





        View v =  inflater.inflate(R.layout.fragment_content, container, false);
        v.setPadding(0, 10, 0, 10);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.my_recycler_view);


        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                //Remove swiped item from list and notify the
                myDataset.remove(viewHolder.getAdapterPosition());
                mAdapter.notifyDataSetChanged();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);


        mAdapter = new BrowseAdapter(myDataset);
        mRecyclerView.setAdapter(mAdapter);

        fetchData();

        return v;
    }

    public void fetchData(){
        ContentManager manager = ContentManager.getInstance();
        manager.getCompanyEntries(this);


    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        fetchData();
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
    public void GetCompanyEntryCallback(List<CompanyEntry> entries) {
        myDataset.clear();
        myDataset.addAll(entries);
        mAdapter.notifyDataSetChanged();

        ContentManager manager = ContentManager.getInstance();
        manager.getPoints(this);

    }

    @Override
    public void GetPointCallback(List<ContentManager.PointPair> points) {
        for(ContentManager.PointPair pair: points){
            String company = pair.companyName;
            int pointage = pair.points;



            CompanyEntry entry = null;
            for(CompanyEntry companyEntry: myDataset){
                if(companyEntry.companyName.equals(company)){
                    Log.d("wefw", "Updating " + company + " with " + pointage + "points");
                    companyEntry.points = pointage;
                    break;
                }
            }
        }
        mAdapter.notifyDataSetChanged();
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

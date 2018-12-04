package whosthere.whosthere;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


public class SearchFriendsFragment extends Fragment {
    private static final String TAG = "SearchFragment";

    private ArrayList<Friend> mFriendsList;
    private SearchFriendAdapter mAdapter;
    private ListView mListView;
    private LayoutInflater mLayoutInflater;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mDatabase;
    private FirebaseUser mUser;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //friendsList = (ArrayList)getIntent().getParcelableArrayListExtra("mFriendList");
        //this.mFriendsList = ((NavigationBarActivity)getActivity()).getmFriendsList();
        this.mFriendsList = new ArrayList<Friend>();
        this.mLayoutInflater = LayoutInflater.from(getContext());

        mAdapter = new SearchFriendAdapter(getActivity(), mFriendsList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_friends, container, false);
        this.mListView = v.findViewById(R.id.list);
        this.mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Friend friend = mFriendsList.get(position);
                // Link to map ... somehow

            }
        });

        RelativeLayout searchView = ( RelativeLayout)mLayoutInflater.inflate(R.layout.searchnew_view, null);
        final EditText searchText = (EditText)searchView.findViewById(R.id.search_header);
        Button searchButton = searchView.findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Clicked Search Button!!!");

                mAdapter.getFilter().filter(searchText.getText().toString().toString());
            }
        });
        mListView.addHeaderView(searchView);

        getActivity().setTitle("Search For Friends");

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}

package whosthere.whosthere;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;


public class FriendsFragment extends Fragment {

    private ArrayList<Friend> mFriendsList;
    private FriendAdapter mAdapter;
    private ListView mListView;
    private LayoutInflater mLayoutInflater;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //friendsList = (ArrayList)getIntent().getParcelableArrayListExtra("mFriendList");
        this.mFriendsList = ((NavigationBarActivity)getActivity()).getmFriendsList();
        this.mLayoutInflater = LayoutInflater.from(getContext());

        mAdapter = new FriendAdapter(getActivity(), mFriendsList);


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

        EditText searchView = (EditText)mLayoutInflater.inflate(R.layout.search_view, null);

        searchView.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Call back the Adapter with current character to Filter
                mAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mListView.addHeaderView(searchView);
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

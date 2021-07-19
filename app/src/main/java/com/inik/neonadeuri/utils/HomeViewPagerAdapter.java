package com.inik.neonadeuri.utils;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.inik.neonadeuri.AddFeedFragment;
import com.inik.neonadeuri.FeedListFragment;

import com.inik.neonadeuri.ProfileFragment;
import com.inik.neonadeuri.SearchFragment;

import java.util.ArrayList;

public class HomeViewPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> fragments = new ArrayList<>();

    public HomeViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);

        fragments.add(new FeedListFragment());
        fragments.add(new SearchFragment());
        fragments.add(new AddFeedFragment());
        fragments.add(new ProfileFragment());
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}

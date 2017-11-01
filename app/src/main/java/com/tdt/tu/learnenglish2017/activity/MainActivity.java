package com.tdt.tu.learnenglish2017.activity;

import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.tdt.tu.learnenglish2017.R;
import com.tdt.tu.learnenglish2017.fragment.Tab1Fragment;
import com.tdt.tu.learnenglish2017.fragment.Tab2Fragment;
import com.tdt.tu.learnenglish2017.fragment.Tab3Fragment;
import com.tdt.tu.learnenglish2017.fragment.Tab4Fragment;
import com.tdt.tu.learnenglish2017.fragment.Tab5Fragment;
import com.tdt.tu.learnenglish2017.helper.SectionsPagerAdapter;

public class MainActivity extends AppCompatActivity {
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private AHBottomNavigation bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
        addBottomNavigationItems();
        setupBottomNavigationStyle();
        bottomNavigation.setCurrentItem(0);

        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                if (!wasSelected)
                    mViewPager.setCurrentItem(position);
                return true;
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Tab1Fragment());
        adapter.addFragment(new Tab2Fragment());
        adapter.addFragment(new Tab3Fragment());
        adapter.addFragment(new Tab4Fragment());
        adapter.addFragment(new Tab5Fragment());
        viewPager.setAdapter(adapter);
    }

    private void setupBottomNavigationStyle() {
        bottomNavigation.setDefaultBackgroundColor(fetchColor(R.color.colorTeal));
        bottomNavigation.setAccentColor(fetchColor(R.color.colorWhite));
        bottomNavigation.setInactiveColor(fetchColor(R.color.colorGrey));
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
    }

    private void addBottomNavigationItems() {
        AHBottomNavigationItem summaryTab = new AHBottomNavigationItem("Summary", R.drawable.ic_dashboard_black_24dp);
        AHBottomNavigationItem mineTab = new AHBottomNavigationItem("Mine", R.drawable.ic_play_circle_outline_black_24dp);
        AHBottomNavigationItem featuredTab = new AHBottomNavigationItem("Featured", R.drawable.ic_star_border_black_24dp);
        AHBottomNavigationItem searchTab = new AHBottomNavigationItem("Search", R.drawable.ic_search_black_24dp);
        AHBottomNavigationItem favoriteTab = new AHBottomNavigationItem("Favorite", R.drawable.ic_favorite_border_black_24dp);

        bottomNavigation.addItem(summaryTab);
        bottomNavigation.addItem(mineTab);
        bottomNavigation.addItem(featuredTab);
        bottomNavigation.addItem(searchTab);
        bottomNavigation.addItem(favoriteTab);
    }

    private int fetchColor(@ColorRes int color) {
        return ContextCompat.getColor(this, color);
    }
}

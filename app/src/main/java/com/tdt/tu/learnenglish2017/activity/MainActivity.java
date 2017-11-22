package com.tdt.tu.learnenglish2017.activity;

import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.tdt.tu.learnenglish2017.R;
import com.tdt.tu.learnenglish2017.fragment.Tab1Fragment;
import com.tdt.tu.learnenglish2017.fragment.Tab2Fragment;
import com.tdt.tu.learnenglish2017.fragment.Tab3Fragment;
import com.tdt.tu.learnenglish2017.fragment.Tab4Fragment;
import com.tdt.tu.learnenglish2017.fragment.Tab5Fragment;
import com.tdt.tu.learnenglish2017.helper.SectionsPagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    SectionsPagerAdapter sectionsPagerAdapter;
    SectionsPagerAdapter adapter;
    @BindView(R.id.container)
    ViewPager viewPager;
    @BindView(R.id.bottomNavigation)
    AHBottomNavigation bottomNavigation;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        setupViewPager(viewPager);
        addBottomNavigationItems();
        setupBottomNavigationStyle();
        navigationTabHandler();

    }

    private void navigationTabHandler() {
        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                if (!wasSelected) {
                    viewPager.setCurrentItem(position);
                    switchTabColor(position);
                }
                return true;
            }
        });
    }


    private void init() {
        ButterKnife.bind(this);
        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        bottomNavigation.setCurrentItem(0);
    }

    private void switchTabColor(int position) {
        if (position == 0)
            toolbar.setBackgroundColor(fetchColor(R.color.colorRed));
        else if (position == 1)
            toolbar.setBackgroundColor(fetchColor(R.color.colorOrange));
        else if (position == 2)
            toolbar.setBackgroundColor(fetchColor(R.color.colorYellow));
        else if (position == 3)
            toolbar.setBackgroundColor(fetchColor(R.color.colorGreen));
        else if (position == 4)
            toolbar.setBackgroundColor(fetchColor(R.color.colorBlue));
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter.addFragment(new Tab1Fragment());
        adapter.addFragment(new Tab2Fragment());
        adapter.addFragment(new Tab3Fragment());
        adapter.addFragment(new Tab4Fragment());
        adapter.addFragment(new Tab5Fragment());
        viewPager.setAdapter(adapter);
    }

    private void setupBottomNavigationStyle() {
        bottomNavigation.setColoredModeColors(fetchColor(R.color.colorWhite), fetchColor(R.color.colorSpaceGrey));
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        bottomNavigation.setColored(true);
    }

    private void addBottomNavigationItems() {
        AHBottomNavigationItem summaryTab = new AHBottomNavigationItem(R.string.summary, R.drawable.ic_summary, R.color.colorRed);
        AHBottomNavigationItem mineTab = new AHBottomNavigationItem(R.string.mine, R.drawable.ic_mine, R.color.colorOrange);
        AHBottomNavigationItem featuredTab = new AHBottomNavigationItem(R.string.featured, R.drawable.ic_featured, R.color.colorYellow);
        AHBottomNavigationItem searchTab = new AHBottomNavigationItem(R.string.search, R.drawable.ic_search, R.color.colorGreen);
        AHBottomNavigationItem favoriteTab = new AHBottomNavigationItem(R.string.favorite, R.drawable.ic_favorite, R.color.colorBlue);

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
